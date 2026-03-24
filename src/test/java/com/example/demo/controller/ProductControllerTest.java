package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test di integrazione del Controller con MockMvc.
 *
 * @WebMvcTest: carica solo il layer Web (Controller + MVC config).
 *              NON avvia il server HTTP reale -> veloce.
 *              Il Service viene sostituito con un mock Mockito (@MockBean).
 *
 * Cosa testiamo qui:
 *  - Routing HTTP (URL, metodi)
 *  - Serializzazione/deserializzazione JSON
 *  - Status code HTTP corretti
 *  - Validazione input (@Valid)
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; // client HTTP simulato

    @MockBean
    private ProductService productService; // sostituisce il bean reale con un mock

    @Autowired
    private ObjectMapper objectMapper; // serializza oggetti Java -> JSON

    // ── GET /api/products ──────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/products -> 200 con lista prodotti")
    void getAll_returns200WithList() throws Exception {
        List<Product> products = List.of(
                new Product(1L, "Laptop", 999.99),
                new Product(2L, "Mouse",   29.90)
        );
        Mockito.when(productService.findAll()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    // ── GET /api/products/{id} ─────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/products/1 -> 200 con prodotto trovato")
    void getById_existing_returns200() throws Exception {
        Mockito.when(productService.findById(1L))
               .thenReturn(Optional.of(new Product(1L, "Laptop", 999.99)));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(999.99));
    }

    @Test
    @DisplayName("GET /api/products/999 -> 404 se non trovato")
    void getById_missing_returns404() throws Exception {
        Mockito.when(productService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    // ── POST /api/products ─────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/products -> 201 Created")
    void create_validInput_returns201() throws Exception {
        Product request = new Product(null, "Monitor", 349.99);
        Product response = new Product(4L,  "Monitor", 349.99);

        Mockito.when(productService.create(any(Product.class))).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.name").value("Monitor"));
    }

    @Test
    @DisplayName("POST /api/products con nome vuoto -> 400 Bad Request")
    void create_blankName_returns400() throws Exception {
        Product invalid = new Product(null, "", 99.99); // nome vuoto: viola @NotBlank

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());
    }

    // ── DELETE /api/products/{id} ──────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/products/1 -> 204 No Content")
    void delete_existing_returns204() throws Exception {
        Mockito.when(productService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/products/999 -> 404 se non trovato")
    void delete_missing_returns404() throws Exception {
        Mockito.when(productService.delete(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound());
    }
}
