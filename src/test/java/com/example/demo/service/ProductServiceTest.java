package com.example.demo.service;

import com.example.demo.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test del Service.
 * NON usa Spring context: testa la classe in isolamento (veloce).
 *
 * Usa AssertJ per asserzioni fluenti e leggibili.
 */
class ProductServiceTest {

    private ProductService service;

    @BeforeEach
    void setUp() {
        // Ricrea il service prima di ogni test: stato pulito e prevedibile
        service = new ProductService();
    }

    @Test
    @DisplayName("findAll() restituisce i prodotti pre-caricati")
    void findAll_returnsPreloadedProducts() {
        List<Product> products = service.findAll();

        assertThat(products).hasSize(3);
        assertThat(products).extracting(Product::name)
                .containsExactlyInAnyOrder("Laptop", "Mouse", "Tastiera");
    }

    @Test
    @DisplayName("findById() con ID esistente restituisce il prodotto")
    void findById_existingId_returnsProduct() {
        Optional<Product> result = service.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("Laptop");
    }

    @Test
    @DisplayName("findById() con ID inesistente restituisce Optional vuoto")
    void findById_missingId_returnsEmpty() {
        Optional<Product> result = service.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("create() aggiunge il prodotto e assegna un ID")
    void create_addsProductWithGeneratedId() {
        Product newProduct = new Product(null, "Monitor", 349.99);

        Product created = service.create(newProduct);

        assertThat(created.id()).isNotNull();
        assertThat(created.name()).isEqualTo("Monitor");
        assertThat(service.findAll()).hasSize(4);
    }

    @Test
    @DisplayName("update() con ID esistente aggiorna e restituisce il prodotto")
    void update_existingId_updatesProduct() {
        Product updated = new Product(null, "Laptop Pro", 1299.99);

        Optional<Product> result = service.update(1L, updated);

        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("Laptop Pro");
        assertThat(result.get().price()).isEqualTo(1299.99);
    }

    @Test
    @DisplayName("update() con ID inesistente restituisce Optional vuoto")
    void update_missingId_returnsEmpty() {
        Product updated = new Product(null, "Fantasma", 0);

        Optional<Product> result = service.update(999L, updated);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("delete() con ID esistente rimuove il prodotto")
    void delete_existingId_removesProduct() {
        boolean removed = service.delete(1L);

        assertThat(removed).isTrue();
        assertThat(service.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("delete() con ID inesistente restituisce false")
    void delete_missingId_returnsFalse() {
        boolean removed = service.delete(999L);

        assertThat(removed).isFalse();
    }
}
