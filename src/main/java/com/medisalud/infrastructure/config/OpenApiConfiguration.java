package com.medisalud.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI medisaludOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("MediSalud — API de Agendamiento de Citas Médicas")
                        .description("""
                                API REST para el sistema de agendamiento digital de MediSalud.
                                
                                **Arquitectura:** Clean Architecture con separación estricta de capas.
                                
                                **Reglas de negocio implementadas:**
                                - RN-01: Franjas horarias laborales (L-V 08:00-18:00, Sáb 08:00-13:00)
                                - RN-02: No duplicidad de citas por médico y franja
                                - RN-03: Validación de fecha de nacimiento del paciente
                                - RN-04: No duplicidad de citas por paciente, médico y franja
                                - RN-05: Penalización por cancelación tardía (<2h) — bloqueo a 3 penalizaciones/30 días
                                - RN-06: Reprogramación = cancelar + crear nueva cita
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("MediSalud Dev Team")
                                .email("dev@medisalud.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
