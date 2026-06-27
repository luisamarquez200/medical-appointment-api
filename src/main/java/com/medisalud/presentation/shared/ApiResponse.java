package com.medisalud.presentation.shared;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Wrapper genérico para todas las respuestas de la API.
 *
 * <p>Garantiza consistencia en el formato de respuesta (D09):
 * <pre>
 * // Éxito:
 * { "success": true, "message": "...", "data": { ... }, "timestamp": "..." }
 *
 * // Sin datos (204):
 * { "success": true, "message": "...", "timestamp": "..." }
 * </pre>
 *
 * @param <T> el tipo del objeto de datos
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        LocalDateTime timestamp
) {
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now());
    }

    public static ApiResponse<Void> noContent(String message) {
        return new ApiResponse<>(true, message, null, LocalDateTime.now());
    }
}
