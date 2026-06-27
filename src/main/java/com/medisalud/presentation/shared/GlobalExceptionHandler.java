package com.medisalud.presentation.shared;

import com.medisalud.domain.shared.exception.BusinessException;
import com.medisalud.domain.shared.exception.ConflictException;
import com.medisalud.domain.shared.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones.
 *
 * <p>Centraliza el mapeo de excepciones del dominio a respuestas HTTP.
 * Un único punto donde se decide qué código HTTP y qué estructura de respuesta devolver.
 *
 * <p>Jerarquía de manejo:
 * - NotFoundException → 404
 * - ConflictException → 409
 * - BusinessException (base) → 400
 * - MethodArgumentNotValidException → 400 (Bean Validation)
 * - IllegalArgumentException (UUID inválido) → 400
 * - Exception (catch-all) → 500
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.of(404, "Not Found", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(ConflictException ex, HttpServletRequest request) {
        log.warn("Conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.of(409, "Conflict", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {
        log.warn("Business rule violation: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of(400, "Bad Request", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        log.warn("Validation failed: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.ofValidation(
                        400, "Validation Error",
                        "La solicitud contiene " + errors.size() + " error(es) de validación.",
                        request.getRequestURI(),
                        errors));
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            Exception ex, HttpServletRequest request) {
        log.warn("Invalid argument: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of(400, "Bad Request",
                        "Parámetro inválido: " + ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.of(500, "Internal Server Error",
                        "Ocurrió un error interno. Por favor contacte al administrador.",
                        request.getRequestURI()));
    }
}
