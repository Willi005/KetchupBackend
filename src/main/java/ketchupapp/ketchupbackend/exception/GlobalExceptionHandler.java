package ketchupapp.ketchupbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // NUEVO: Excepciónes para la orden
    //Insuficiencia de stock
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<?> insufficientStockException(InsufficientStockException ex, WebRequest request) {
        // Devuelve el mensaje (ej: "Stock insuficiente para: comida") y un 400
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }
    //Insuficiencia de dinero
    @ExceptionHandler(InsufficientPaymentException.class)
    public ResponseEntity<?> insufficientPaymentException(InsufficientPaymentException ex, WebRequest request) {
        // Devuelve el mensaje (ej: "Monto de pago insuficiente") y un 400
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    // Excepciones para la orden


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalExceptionHandler(Exception ex, WebRequest request) {
        return new ResponseEntity<>("Error interno del servidor: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}