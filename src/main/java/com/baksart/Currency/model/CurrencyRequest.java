package com.baksart.Currency.model;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CurrencyRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String baseCurrency = "RUB";

    @NotBlank
    private String priceChangeRange;

    private String description;
}