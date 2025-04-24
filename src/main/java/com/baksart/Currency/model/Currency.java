package com.baksart.Currency.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "currencies")
public class Currency {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(name = "base_currency", nullable = false)
    private String baseCurrency = "RUB";

    @Column(name = "price_change_range")
    private String priceChangeRange;

    private String description;

    @PrePersist
    public void generateId() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
}