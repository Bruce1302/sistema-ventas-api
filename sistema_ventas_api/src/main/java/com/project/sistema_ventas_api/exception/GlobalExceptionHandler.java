package com.project.sistema_ventas_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarRecursoNoEncontrado(RecursoNoEncontradoException ex)
    {
        // Creamos JSON personalizado
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("status", 404);
        respuesta.put("timestamp", java.time.LocalDateTime.now());
        respuesta.put("error", ex.getMessage()); // Extraemos el mensaje

        // Retornamos el JSON con el código HTTP 404 (Not Found)
        return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarPeticionVentaFallida(IllegalArgumentException ex)
    {
        // Creamos JSON personalizado
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("status", 400);
        respuesta.put("timestamp", java.time.LocalDateTime.now());
        respuesta.put("error", ex.getMessage()); // Extraemos el mensaje

        // Retornamos el JSON con el código HTTP 404 (Not Found)
        return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
    }

}
