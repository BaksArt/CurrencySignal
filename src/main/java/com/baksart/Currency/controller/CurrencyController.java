package com.baksart.Currency.controller;

import com.baksart.Currency.model.Currency;
import com.baksart.Currency.model.CurrencyRequest;
import com.baksart.Currency.repository.CurrencyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CurrencyController {

    private final CurrencyRepository currencyRepository;

    public CurrencyController(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @GetMapping("/currencies")
    public ResponseEntity<List<Currency>> getCurrencies() {
        return ResponseEntity.ok(currencyRepository.findAll());
    }

    @PostMapping("/currencies")
    public ResponseEntity<Currency> addCurrency(@RequestBody CurrencyRequest request) {
        Currency currency = new Currency();
        currency.setName(request.getName());
        currency.setBaseCurrency(request.getBaseCurrency());
        currency.setPriceChangeRange(request.getPriceChangeRange());
        currency.setDescription(request.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(currencyRepository.save(currency));
    }

    @GetMapping("/currencies/{id}")
    public ResponseEntity<Currency> getCurrency(@PathVariable String id) {
        return currencyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/currencies/{id}")
    public ResponseEntity<Void> updateCurrency(
            @PathVariable String id,
            @RequestBody CurrencyRequest request) {
        return currencyRepository.findById(id)
                .map(existing -> {
                    existing.setName(request.getName());
                    existing.setBaseCurrency(request.getBaseCurrency());
                    existing.setPriceChangeRange(request.getPriceChangeRange());
                    existing.setDescription(request.getDescription());
                    currencyRepository.save(existing);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/currencies/{id}")
    public ResponseEntity<Void> deleteCurrency(@PathVariable String id) {
        if (currencyRepository.existsById(id)) {
            currencyRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}