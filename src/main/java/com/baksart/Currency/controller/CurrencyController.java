package com.baksart.Currency.controller;

import com.baksart.Currency.model.Currency;
import com.baksart.Currency.model.CurrencyRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CurrencyController {

    @GetMapping("/currencies")
    public ResponseEntity<List<Currency>> getCurrencies() {
        return ResponseEntity.ok(Collections.emptyList());
    }

    @PostMapping("/currencies")
    public ResponseEntity<Void> addCurrency(@RequestBody CurrencyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/currencies/{id}")
    public ResponseEntity<Currency> getCurrency(@PathVariable String id) {
        Currency currency = new Currency();
        currency.setId(id);
        return ResponseEntity.ok(currency);
    }

    @PutMapping("/currencies/{id}")
    public ResponseEntity<Void> updateCurrency(
            @PathVariable String id,
            @RequestBody CurrencyRequest request) {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/currencies/{id}")
    public ResponseEntity<Void> deleteCurrency(@PathVariable String id) {
        return ResponseEntity.noContent().build();
    }
}