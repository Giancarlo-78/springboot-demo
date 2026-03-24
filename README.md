# Spring Boot Demo — Progetto Didattico

Progetto **Spring Boot 3 / Java 21** con API REST CRUD, test con MockMvc/JUnit 5
e pipeline CI con GitHub Actions.

---

## Struttura del progetto

```
src/
├── main/java/com/example/demo/
│   ├── DemoApplication.java          # Entry point Spring Boot
│   ├── model/
│   │   └── Product.java              # Record Java (DTO + validazione)
│   ├── service/
│   │   └── ProductService.java       # Business logic (storage in-memory)
│   └── controller/
│       ├── ProductController.java    # Endpoint REST
│       └── GlobalExceptionHandler.java # Gestione errori centralizzata
│
└── test/java/com/example/demo/
    ├── service/
    │   └── ProductServiceTest.java   # Unit test (senza Spring)
    └── controller/
        └── ProductControllerTest.java # Integrazione con MockMvc
```

---

## Concetti chiave appresi

| Concetto | Dove |
|---|---|
| `@RestController` / `@RequestMapping` | `ProductController` |
| `@Service` | `ProductService` |
| `ResponseEntity<T>` + status code | `ProductController` |
| `record` Java 16+ | `Product` |
| Validation (`@NotBlank`, `@Min`, `@Valid`) | `Product` + `ProductController` |
| `@RestControllerAdvice` | `GlobalExceptionHandler` |
| Unit test senza Spring | `ProductServiceTest` |
| `@WebMvcTest` + `@MockBean` + MockMvc | `ProductControllerTest` |
| GitHub Actions (build, test, OWASP) | `.github/workflows/ci.yml` |

---

## Come avviare

```bash
# Prerequisiti: JDK 21, Maven 3.9+

mvn spring-boot:run
# App in ascolto su http://localhost:8080
```

---

## Endpoint REST

### GET /api/products
```bash
curl http://localhost:8080/api/products
```

### GET /api/products/{id}
```bash
curl http://localhost:8080/api/products/1
```

### POST /api/products
```bash
curl -X POST http://localhost:8080/api/products \
     -H "Content-Type: application/json" \
     -d '{"name": "Monitor", "price": 349.99}'
```

### PUT /api/products/{id}
```bash
curl -X PUT http://localhost:8080/api/products/1 \
     -H "Content-Type: application/json" \
     -d '{"name": "Laptop Pro", "price": 1299.99}'
```

### DELETE /api/products/{id}
```bash
curl -X DELETE http://localhost:8080/api/products/1
```

---

## Eseguire i test

```bash
mvn test                    # esegue tutti i test
mvn test -pl . -Dtest=ProductServiceTest     # solo unit test
mvn test -pl . -Dtest=ProductControllerTest  # solo test MockMvc
```

---

## GitHub Actions

Il workflow `.github/workflows/ci.yml` esegue **3 job in sequenza**:

```
push/PR
   │
   ▼
[build-and-test]  → compila + esegue tutti i test
   │
   ├──▶ [code-quality]  → compile check + report JaCoCo coverage
   │
   └──▶ [security-scan] → OWASP Dependency Check (solo su main)
```

Tutti i report (test, coverage, OWASP) vengono pubblicati come
**Artifacts scaricabili** dalla tab *Actions* su GitHub.
