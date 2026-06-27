package com.medisalud.presentation.appointment;

import com.medisalud.application.appointment.*;
import com.medisalud.application.shared.dto.appointment.*;
import com.medisalud.presentation.appointment.request.CreateAppointmentRequest;
import com.medisalud.presentation.appointment.request.RescheduleAppointmentRequest;
import com.medisalud.presentation.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Controller de citas médicas.
 * Implementa RF-03, RF-05, RF-06 y RN-06.
 */
@RestController
@RequestMapping("/api/v1/appointments")
@Tag(name = "Appointments", description = "Gestión de citas médicas")
public class AppointmentController {

    private final CreateAppointmentUseCase createAppointmentUseCase;
    private final CancelAppointmentUseCase cancelAppointmentUseCase;
    private final RescheduleAppointmentUseCase rescheduleAppointmentUseCase;
    private final ListAppointmentsUseCase listAppointmentsUseCase;

    public AppointmentController(
            CreateAppointmentUseCase createAppointmentUseCase,
            CancelAppointmentUseCase cancelAppointmentUseCase,
            RescheduleAppointmentUseCase rescheduleAppointmentUseCase,
            ListAppointmentsUseCase listAppointmentsUseCase) {
        this.createAppointmentUseCase = createAppointmentUseCase;
        this.cancelAppointmentUseCase = cancelAppointmentUseCase;
        this.rescheduleAppointmentUseCase = rescheduleAppointmentUseCase;
        this.listAppointmentsUseCase = listAppointmentsUseCase;
    }

    @PostMapping
    @Operation(summary = "Reservar una cita", description = "RF-03: Crea una nueva cita médica aplicando RN-01 a RN-05")
    public ResponseEntity<ApiResponse<AppointmentResult>> create(
            @Valid @RequestBody CreateAppointmentRequest request) {
        AppointmentResult result = createAppointmentUseCase.execute(
                new CreateAppointmentCommand(request.patientId(), request.doctorId(), request.dateTime()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Cita agendada exitosamente.", result));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancelar una cita", description = "RF-05: Cancela una cita y aplica penalización si corresponde")
    public ResponseEntity<ApiResponse<AppointmentResult>> cancel(@PathVariable UUID id) {
        AppointmentResult result = cancelAppointmentUseCase.execute(new CancelAppointmentCommand(id));
        return ResponseEntity.ok(ApiResponse.ok("Cita cancelada exitosamente.", result));
    }

    @PostMapping("/{id}/reschedule")
    @Operation(summary = "Reprogramar una cita", description = "RN-06: Cancela la cita actual y crea una nueva en el nuevo horario")
    public ResponseEntity<ApiResponse<AppointmentResult>> reschedule(
            @PathVariable UUID id,
            @Valid @RequestBody RescheduleAppointmentRequest request) {
        AppointmentResult result = rescheduleAppointmentUseCase.execute(
                new RescheduleAppointmentCommand(id, request.newDateTime()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Cita reprogramada exitosamente.", result));
    }

    @GetMapping
    @Operation(summary = "Listar citas", description = "RF-06: Lista citas con filtros opcionales por médico, paciente, estado y rango de fechas")
    public ResponseEntity<ApiResponse<List<AppointmentResult>>> list(
            @RequestParam(required = false) UUID doctorId,
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<AppointmentResult> results = listAppointmentsUseCase.execute(
                new ListAppointmentsQuery(doctorId, patientId, status, fechaInicio, fechaFin));
        return ResponseEntity.ok(ApiResponse.ok(
                "Se encontraron " + results.size() + " cita(s).", results));
    }
}
