package com.medisalud.domain.shared.exception;

/**
 * Excepción base para todas las reglas de negocio del dominio.
 *
 * <p>Propósito: Distinguir errores de negocio de errores técnicos.
 * Todo lo que hereda de esta clase representa una violación de una
 * regla del dominio, no un error inesperado del sistema.
 *
 * <p>Principio SOLID: Open/Closed — se puede extender con subclases
 * específicas sin modificar el manejador global de excepciones.
 */
public abstract class BusinessException extends RuntimeException {

    protected BusinessException(String message) {
        super(message);
    }

    protected BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
