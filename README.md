# MediSalud API — Sistema de Agendamiento de Citas Médicas

Este proyecto es un **MVP funcional** (API REST) para la digitalización del agendamiento de citas médicas de la clínica MediSalud. Se ha diseñado con un enfoque estricto en buenas prácticas, escalabilidad y mantenimiento a largo plazo.

## 🚀 Tecnologías Principales

- **Java 21**
- **Spring Boot 3.3**
- **PostgreSQL** (Persistencia relacional)
- **Flyway** (Control de versiones de base de datos)
- **MapStruct** (Mapeo de objetos)
- **JUnit 5 / Mockito / AssertJ** (Testing)
- **Swagger / OpenAPI 3** (Documentación de API)

---

## 🏗️ Arquitectura y Diseño (Clean Architecture)

El proyecto implementa **Clean Architecture** (Arquitectura Limpia) inspirada en los principios de Robert C. Martin y Domain-Driven Design (DDD). El objetivo es lograr una **separación estricta de responsabilidades** donde el dominio sea el núcleo, completamente agnóstico de frameworks, bases de datos o interfaces web.

### Estructura de Capas

1. **`domain` (Núcleo de Negocio)**
   - **Contiene**: Aggregates (`Appointment`, `Doctor`, `Patient`, `Penalty`), Value Objects (`TimeSlot`, IDs fuertemente tipados), excepciones de negocio y puertos (interfaces de repositorios).
   - **Justificación**: Todo el comportamiento rico y las invariantes del negocio viven aquí. No hay referencias a Spring ni JPA. Esto permite probar la lógica de negocio sin levantar contextos de aplicación.

2. **`application` (Casos de Uso)**
   - **Contiene**: La orquestación de la lógica (ej. `CreateAppointmentUseCase`). Toma comandos (`CreateAppointmentCommand`), delega reglas al dominio y persiste mediante los puertos.
   - **Justificación**: Mantiene las reglas de negocio protegidas de la presentación HTTP. Implementa el principio DRY orquestando de manera atómica (ej. `RescheduleAppointmentUseCase`).

3. **`infrastructure` (Detalles Técnicos)**
   - **Contiene**: Entidades JPA (`AppointmentJpaEntity`), repositorios de Spring Data, mappers (MapStruct), migraciones de Flyway y la inyección de dependencias de Spring.
   - **Justificación**: Aísla los detalles de la base de datos (PostgreSQL) y del framework (Spring). Si en el futuro cambiamos la base de datos, el dominio y la aplicación no se modifican (Principio de Inversión de Dependencias).

4. **`presentation` (Capa Externa)**
   - **Contiene**: Controladores REST (`AppointmentController`), DTOs de validación (Bean Validation) y el `GlobalExceptionHandler`.
   - **Justificación**: Mantiene los Endpoints limpios de lógica de negocio, encargándose únicamente de recibir, validar sintácticamente (seguridad de input) y devolver respuestas estandarizadas (`ApiResponse` y `ApiErrorResponse`).

---

## 🔒 Seguridad, Manejo de Errores y Calidad de Código

- **Validación de Inputs**: Se utiliza Bean Validation (`@Valid`, `@NotNull`, `@Pattern`) en la capa de presentación para detener datos corruptos antes de que lleguen a los Casos de Uso.
- **Prevención de Inyección SQL**: El uso de Spring Data JPA garantiza el uso de *Prepared Statements*.
- **Manejo Consistente de Errores**: El `GlobalExceptionHandler` intercepta excepciones de dominio (`NotFoundException`, `ConflictException`, `BusinessException`) y las traduce en respuestas JSON estandarizadas y códigos HTTP semánticos (404, 409, 400).
- **Defensa en Profundidad**: A pesar de validar la disponibilidad (RN-02) en el código, la base de datos cuenta con índices y restricciones `UNIQUE (doctor_id, appointment_date_time)` para prevenir condiciones de carrera.

---

## ⚙️ Reglas de Negocio Implementadas

- **RN-01 (Horarios Laborales)**: Validadas en `TimeSlotValidatorService`. Garantiza franjas exactas de 30 min (L-V: 08:00 a 18:00, Sáb: 08:00 a 13:00).
- **RN-02 & RN-04 (Conflictos de Horario)**: Validado en base de datos mediante queries específicas en `IAppointmentRepository`.
- **RN-03 (Validación de Edad/Nacimiento)**: La entidad `Patient` valida que no existan fechas futuras. Si se omite, se asume edad 0 (MVP).
- **RN-05 (Penalizaciones)**: La regla de las 2 horas está encapsulada en la entidad `Appointment`. `PenaltyDomainService` bloquea el agendamiento si existen 3 cancelaciones tardías en los últimos 30 días.
- **RN-06 (Reprogramación)**: Tratado atómicamente orquestando Casos de Uso existentes para no duplicar código (DRY).

---

## 🧪 Pruebas Unitarias

La suite de pruebas se enfoca en el comportamiento y casos borde de la lógica de negocio, no en el framework.
- Uso de `Clock.fixed` para testing determinista de tiempos y penalizaciones.
- Pruebas exhaustivas para `TimeSlotValidatorService` (RN-01), evaluando cada límite de horario.
- Uso de `Mockito` para aislar los Casos de Uso (orquestación pura).

---

## 🛠️ Cómo ejecutar el proyecto localmente

### Prerrequisitos
- **Java 21**
- **Maven**
- **Docker** y **Docker Compose** (Para levantar PostgreSQL de forma rápida)

### Pasos

1. **Levantar PostgreSQL con Docker**:
   ```bash
   docker run --name medisalud-db -e POSTGRES_USER=medisalud -e POSTGRES_PASSWORD=medisalud -e POSTGRES_DB=medisalud -p 5432:5432 -d postgres:15
   ```

2. **Compilar y Correr Pruebas**:
   ```bash
   mvn clean test
   ```

3. **Ejecutar la API**:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```
   *Al iniciar, Flyway creará las tablas e insertará los 3 médicos iniciales indicados en los requerimientos.*

4. **Explorar la API (Swagger UI)**:

