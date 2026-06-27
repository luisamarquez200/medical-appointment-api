package com.medisalud.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/**
 * Registra el bean Clock con la zona horaria de Colombia (America/Bogota).
 *
 * <p>Por qué un bean en lugar de LocalDateTime.now():
 * - Inyectable: los Use Cases reciben el Clock por constructor → testeable.
 * - En tests, se reemplaza por Clock.fixed(...) → resultados determinísticos.
 * - Correcto: nunca usar LocalDateTime.now() sin zona horaria explícita.
 *
 * <p>Decisión D03: America/Bogota (UTC-5).
 */
@Configuration
public class ClockConfiguration {

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("America/Bogota"));
    }
}
