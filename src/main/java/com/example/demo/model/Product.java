package com.example.demo.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Record Java 16+: immutabile, con getter automatici, equals/hashCode/toString.
 * Ideale per DTO e modelli semplici.
 *
 * @param id    identificativo del prodotto
 * @param name  nome del prodotto (non vuoto)
 * @param price prezzo (>= 0)
 */
public record Product(
        Long id,

        @NotBlank(message = "Il nome non può essere vuoto, regola d'ora")
        String name,

        @Min(value = 0, message = "Il prezzo non può essere negativo")
        double price
) {}
