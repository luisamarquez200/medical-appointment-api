package com.medisalud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MediSalud — Sistema de Agendamiento de Citas Médicas
 *
 * <p>Arquitectura: Clean Architecture con separación estricta de capas:
 * <pre>
 *   Presentation → Application → Domain ← Infrastructure
 * </pre>
 *
 * <p>El dominio NO depende de Spring, JPA, ni HTTP.
 * La inversión de dependencias se logra mediante interfaces de repositorio
 * definidas en el dominio e implementadas en la infraestructura.
 */
@SpringBootApplication
public class MedisaludApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedisaludApplication.class, args);
    }
}
