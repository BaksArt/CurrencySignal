package com.baksart.Currency.service;

import com.baksart.Currency.dto.CbrResponse;
import com.baksart.Currency.model.Currency;
import com.baksart.Currency.repository.CurrencyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class CbrService {
    private final WebClient webClient;
    private final CurrencyRepository currencyRepository;
    private final ObjectMapper objectMapper;

    @Scheduled(cron = "0 * * * *")
    public void checkCurrencyChanges() {
        log.info("Starting currency rates check...");

        webClient.get()
                .uri("/daily_json.js")
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), response -> {
                    return response.bodyToMono(String.class)
                            .flatMap(body -> {
                                log.error("API Error: {} - {}", response.statusCode(), body);
                                return Mono.error(new RuntimeException("API Error"));
                            });
                })
                .bodyToMono(String.class)
                .doOnNext(raw -> log.debug("Raw response: {}", raw.substring(0, 100) + "..."))
                .flatMap(this::parseResponseSafely)
                .doOnSuccess(response -> {
                    if (response.getValute() == null || response.getValute().isEmpty()) {
                        log.warn("Empty valute data in response");
                    } else {
                        log.debug("Received {} currencies", response.getValute().size());
                        processResponse(response);
                    }
                })
                .doOnError(error -> log.error("API request failed: {}", error.getMessage()))
                .subscribe();
    }

    private Mono<CbrResponse> parseResponseSafely(String json) {
        return Mono.fromCallable(() -> objectMapper.readValue(json, CbrResponse.class))
                .onErrorResume(e -> {
                    log.error("Failed to parse API response: {}", e.getMessage());
                    return Mono.empty();
                });
    }

    private void processResponse(CbrResponse response) {
        List<Currency> trackedCurrencies = currencyRepository.findAll();

        if (trackedCurrencies.isEmpty()) {
            log.warn("No currencies found in database to track!");
            return;
        }

        trackedCurrencies.forEach(currency -> {
            log.debug("Checking currency: {} (looking for code: {})",
                    currency.getName(), currency.getName());

            CbrResponse.CurrencyRate rate = response.getValute().get(currency.getName());
            if (rate != null) {
                double changePercent = calculateChangePercent(rate.getValue(), rate.getPrevious());
                log.debug("Currency {} change: {}% (threshold: {}%)",
                        currency.getName(), changePercent, currency.getPriceChangeRange());
                checkThreshold(currency, changePercent);
            } else {
                log.warn("Currency code '{}' not found in CBR response. Available codes: {}",
                        currency.getName(),
                        String.join(", ", response.getValute().keySet()));
            }
        });
    }

    private double calculateChangePercent(double current, double previous) {
        return ((current - previous) / previous) * 100;
    }

    private void checkThreshold(Currency currency, double actualChange) {
        try {
            String range = currency.getPriceChangeRange().replace("%", "");
            double threshold = Double.parseDouble(range);

            if (Math.abs(actualChange) >= Math.abs(threshold)) {
                String message = String.format("%s: %s (Изменение: %.2f%%)",
                        currency.getName(),
                        currency.getDescription(),
                        actualChange);
                log.info(message);
            }
        } catch (NumberFormatException e) {
            log.error("Invalid priceChangeRange format for currency {}: {}",
                    currency.getName(), currency.getPriceChangeRange());
        }
    }
}