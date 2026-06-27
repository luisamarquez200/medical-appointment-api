package com.medisalud.domain.shared.exception;

/**
 * Excepción para violaciones de unicidad o conflictos de estado.
 *
 * <p>Corresponde a HTTP 409 Conflict.
 * Ejemplos: médico con cita duplicada, paciente bloqueado,
 * documento de identidad ya registrado.
 */
public abstract class ConflictException extends BusinessException {

    protected ConflictException(String message) {
        super(message);
    }
}
