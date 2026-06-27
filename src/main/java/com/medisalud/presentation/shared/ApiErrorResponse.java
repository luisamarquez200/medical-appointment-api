package com.medisalud.presentation.shared;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Estructura de respuesta de error consistente para toda la API.
 *
 * <pre>
 * {
 *   "success": false,
 *   "status": 409,
 *   "error": "Conflict",
 *   "message": "El médico ya tiene una cita en esa franja.",
 *   "path": "/api/v1/appointments",
 *   "timestamp": "2026-06-10T12:00:00",
 *   "validationErrors": [...]  // solo para errores de validación
 * }
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        boolean success,
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp,
        List<String> validationErrors
) {
    public static ApiErrorResponse of(int status, String error, String message, String path) {
        return new ApiErrorResponse(false, status, error, message, path, LocalDateTime.now(), null);
    }

    public static ApiErrorResponse ofValidation(int status, String error, String message, String path, List<String> errors) {
        return new ApiErrorResponse(false, status, error, message, path, LocalDateTime.now(), errors);
    }
}
