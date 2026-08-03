package com.backend.AltaPinta.Config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getDefaultMessage())
                .orElse("Datos inválidos");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", mensaje));
    }

    /**
     * Validacion de parametros y variables de ruta (@Validated en la clase).
     *
     * Estos no pasan por MethodArgumentNotValidException, que solo cubre el
     * cuerpo de la peticion: sin este manejador, una cantidad de 0 en el
     * carrito salia como 500 y parecia una averia del servidor en lugar de
     * un dato mal enviado.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        String mensaje = ex.getConstraintViolations().stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse("Datos inválidos");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", mensaje));
    }

    /**
     * Cuerpo ilegible: JSON con la sintaxis rota, o un texto donde se espera
     * un numero. Sin este manejador el cliente recibia la pagina de error por
     * defecto de Spring, con la traza dentro.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleCuerpoIlegible(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "El cuerpo de la petición no tiene un formato válido"));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, String>> handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "Otro cliente compró este producto justo antes, intenta de nuevo"));
    }

    /**
     * AccessDeniedException hereda de RuntimeException, asi que sin este
     * manejador la capturaba el generico de abajo y un acceso denegado por
     * @PreAuthorize se devolvia como 400 (datos invalidos) en lugar de 403.
     * El acceso quedaba bloqueado igualmente, pero el cliente no podia
     * distinguir "no tienes permiso" de "enviaste mal los datos".
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "No tienes permiso para realizar esta acción"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage() != null ? ex.getMessage() : "Error inesperado"));
    }
}
