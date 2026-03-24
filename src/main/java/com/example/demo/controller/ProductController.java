package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller: gestisce le richieste HTTP e delega al Service.
 * NON contiene logica di business.
 *
 * Endpoint esposti:
 *   GET    /api/products          -> lista tutti i prodotti
 *   GET    /api/products/{id}     -> prodotto per ID
 *   POST   /api/products          -> crea un prodotto
 *   PUT    /api/products/{id}     -> aggiorna un prodotto
 *   DELETE /api/products/{id}     -> elimina un prodotto
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    // Dependency Injection via costruttore (best practice rispetto a @Autowired sul campo)
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)  ----         // 200 OK
                .orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody Product product) {
        // @Valid attiva le constraint di validazione definite nel record Product
        Product created = productService.create(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id,
                                          @Valid @RequestBody Product product) {
        return productService.update(id, product)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (productService.delete(id)) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.notFound().build();      // 404 Not Found
    }
}
