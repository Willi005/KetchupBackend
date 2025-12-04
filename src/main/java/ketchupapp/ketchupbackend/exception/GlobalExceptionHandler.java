package ketchupapp.ketchupbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Helper para generar respuestas JSON consistentes
    private ResponseEntity<Map<String, String>> buildJsonResponse(String message, HttpStatus status) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return buildJsonResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String, String>> insufficientStockException(InsufficientStockException ex, WebRequest request) {
        return buildJsonResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InsufficientPaymentException.class)
    public ResponseEntity<Map<String, String>> insufficientPaymentException(InsufficientPaymentException ex, WebRequest request) {
        return buildJsonResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // NUEVO: Manejo específico para errores de Login
    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleAuthenticationException(Exception ex) {
        return buildJsonResponse("Usuario o contraseña incorrectos", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> globalExceptionHandler(Exception ex, WebRequest request) {
        ex.printStackTrace(); // Importante para ver el error real en la consola del backend
        return buildJsonResponse("Error interno del servidor: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}