package com.sms.studentmanagement.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access
    .AccessDeniedException;
import org.springframework.security.authentication
    .BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation
    .ExceptionHandler;
import org.springframework.web.bind.annotation
    .RestControllerAdvice;
import org.springframework.web.bind
    .MethodArgumentNotValidException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle general errors
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>>
            handleRuntimeException(RuntimeException ex) {
        return buildResponse(
            HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Handle wrong username/password
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>>
            handleBadCredentials(
                BadCredentialsException ex) {
        return buildResponse(
            HttpStatus.UNAUTHORIZED,
            "Invalid username or password");
    }

    // Handle access denied
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>>
            handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(
            HttpStatus.FORBIDDEN,
            "Access denied: insufficient permissions");
    }

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>>
            handleValidationErrors(
                MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
            .getAllErrors()
            .forEach(error -> {
                String fieldName =
                    ((FieldError) error).getField();
                errors.put(fieldName,
                    error.getDefaultMessage());
            });
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp",
            LocalDateTime.now().toString());
        body.put("status",
            HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Failed");
        body.put("fieldErrors", errors);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(body);
    }

    // Build error response
    private ResponseEntity<Map<String, Object>>
            buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp",
            LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
