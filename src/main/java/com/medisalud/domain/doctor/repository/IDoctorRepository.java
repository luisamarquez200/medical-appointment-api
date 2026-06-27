package com.medisalud.domain.doctor.repository;

import com.medisalud.domain.doctor.Doctor;
import com.medisalud.domain.doctor.DoctorId;

import java.util.Optional;

/**
 * Puerto de salida (Output Port) del dominio para persistencia de médicos.
 *
 * <p>Definido en el dominio, implementado en infraestructura (DoctorRepositoryImpl).
 * Esta es la inversión de dependencias (DIP) en acción: el dominio define el contrato,
 * la infraestructura se adapta a él.
 *
 * <p>No extiende JpaRepository ni ninguna interfaz de framework.
 * Eso mantiene el dominio agnóstico de la tecnología de persistencia.
 */
public interface IDoctorRepository {

    /**
     * Persiste un médico (inserción o actualización).
     *
     * @param doctor el agregado a persistir
     * @return el doctor persistido (puede incluir campos calculados por DB)
     */
    Doctor save(Doctor doctor);

    /**
     * Busca un médico por su identificador.
     *
     * @param id el identificador del médico
     * @return el médico si existe, vacío si no
     */
    Optional<Doctor> findById(DoctorId id);

    /**
     * Verifica la existencia de un médico sin cargarlo completo.
     * Más eficiente que findById cuando solo se necesita confirmar existencia.
     */
    boolean existsById(DoctorId id);
}
