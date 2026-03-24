package com.example.demo.service;

import com.example.demo.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service: contiene la logica di business.
 * Separato dal Controller per rispettare il principio Single Responsibility.
 *
 * Usa una lista in-memory (nessun DB) per semplicità didattica.
 */
@Service
public class ProductService {

    // Generatore thread-safe di ID incrementale
    private final AtomicLong idCounter = new AtomicLong(1);

    // "Database" in memoria
    private final List<Product> products = new ArrayList<>(List.of(
            new Product(idCounter.getAndIncrement(), "Laptop",  999.99),
            new Product(idCounter.getAndIncrement(), "Mouse",    29.90),
            new Product(idCounter.getAndIncrement(), "Tastiera", 59.50)
    ));

    public List<Product> findAll() {
        return List.copyOf(products); // copia difensiva: il chiamante non può modificare la lista interna
    }

    public Optional<Product> findById(Long id) {
        return products.stream()
                .filter(p -> p.id().equals(id))
                .findFirst();
    }

    public Product create(Product product) {
        // Assegna un nuovo ID, ignorando quello eventualmente passato dal client
        Product saved = new Product(idCounter.getAndIncrement(), product.name(), product.price());
        products.add(saved);
        return saved;
    }

    public Optional<Product> update(Long id, Product updated) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).id().equals(id)) {
                Product replaced = new Product(id, updated.name(), updated.price());
                products.set(i, replaced);
                return Optional.of(replaced);
            }
        }
        return Optional.empty(); // 404
    }

    public boolean delete(Long id) {
        return products.removeIf(p -> p.id().equals(id));
    }
}
