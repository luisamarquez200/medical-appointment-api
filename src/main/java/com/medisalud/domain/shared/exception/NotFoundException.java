package com.medisalud.domain.shared.exception;

/**
 * Excepción para recursos que no existen en el sistema.
 *
 * <p>Corresponde a HTTP 404 Not Found.
 * Las subclases especializan el mensaje con el tipo de entidad buscada.
 */
public abstract class NotFoundException extends BusinessException {

    protected NotFoundException(String message) {
        super(message);
    }
}
