package com.baksart.Currency.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class CbrResponse {
    @JsonProperty("Valute")
    private Map<String, CurrencyRate> valute;

    @Data
    public static class CurrencyRate {
        @JsonProperty("ID")
        private String id;

        @JsonProperty("NumCode")
        private String numCode;

        @JsonProperty("CharCode")
        private String charCode;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("Value")
        private double value;

        @JsonProperty("Previous")
        private double previous;
    }
}