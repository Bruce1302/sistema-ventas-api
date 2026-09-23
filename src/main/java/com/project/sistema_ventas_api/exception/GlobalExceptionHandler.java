package com.project.sistema_ventas_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //Esta validacion no es muy prciosa como las demas, por eso se usa una clase aparte
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

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> manejarCredencialesInvalidas(BadCredentialsException ex)
    {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("status", 401);
        respuesta.put("timestamp", java.time.LocalDateTime.now());
        respuesta.put("error", "Usuario o contraseña incorrectos");
        return new ResponseEntity<>(respuesta, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidaciones(MethodArgumentNotValidException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("status", 400);
        respuesta.put("timestamp", java.time.LocalDateTime.now());
        respuesta.put("error", "Datos invalidos");

        // Extraemos los mensajes específicos de cada campo que falló
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }
        respuesta.put("Problemas", errores);

        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

}
