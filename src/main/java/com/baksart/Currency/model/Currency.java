package com.baksart.Currency.model;
import lombok.Data;

@Data
public class Currency {

    private String id;
    private String name;
    private String baseCurrency = "RUB";
    private String priceChangeRange;
    private String description;

}
