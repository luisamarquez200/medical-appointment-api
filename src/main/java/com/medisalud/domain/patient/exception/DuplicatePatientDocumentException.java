package com.medisalud.domain.patient.exception;

import com.medisalud.domain.shared.exception.ConflictException;

public class DuplicatePatientDocumentException extends ConflictException {

    public DuplicatePatientDocumentException(String documentId) {
        super("Ya existe un paciente registrado con el documento: " + documentId);
    }
}
