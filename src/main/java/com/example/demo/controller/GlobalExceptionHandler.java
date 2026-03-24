package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

/**
 * Gestione centralizzata delle eccezioni.
 * Invece di lasciare che Spring restituisca stacktrace grezzi,
 * intercettiamo le eccezioni e restituiamo JSON leggibili.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Cattura gli errori di validazione (@Valid fallito).
     * Restituisce 400 Bad Request con la lista dei campi errati.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", 400,
                        "errors", errors
                ));
    }

    /**
     * Cattura qualsiasi altra eccezione non gestita.
     * Evita che il client veda lo stacktrace interno.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "status", 500,
                        "message", "Errore interno del server"
                ));
    }
}
