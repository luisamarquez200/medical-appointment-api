package com.medisalud.presentation.doctor;

import com.medisalud.application.doctor.GetAvailableSlotsUseCase;
import com.medisalud.application.doctor.GetDoctorByIdUseCase;
import com.medisalud.application.doctor.RegisterDoctorUseCase;
import com.medisalud.application.shared.dto.doctor.CreateDoctorCommand;
import com.medisalud.application.shared.dto.doctor.DoctorResult;
import com.medisalud.application.shared.dto.doctor.TimeSlotResult;
import com.medisalud.presentation.doctor.request.CreateDoctorRequest;
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

/**
 * Controller de médicos.
 */
@RestController
@RequestMapping("/api/v1/doctors")
@Tag(name = "Doctors", description = "Gestión de médicos del sistema")
public class DoctorController {

    private final RegisterDoctorUseCase registerDoctorUseCase;
    private final GetDoctorByIdUseCase getDoctorByIdUseCase;
    private final GetAvailableSlotsUseCase getAvailableSlotsUseCase;

    public DoctorController(
            RegisterDoctorUseCase registerDoctorUseCase,
            GetDoctorByIdUseCase getDoctorByIdUseCase,
            GetAvailableSlotsUseCase getAvailableSlotsUseCase) {
        this.registerDoctorUseCase = registerDoctorUseCase;
        this.getDoctorByIdUseCase = getDoctorByIdUseCase;
        this.getAvailableSlotsUseCase = getAvailableSlotsUseCase;
    }

    @PostMapping
    @Operation(summary = "Registrar un médico", description = "RF-01: Crea un nuevo médico en el sistema")
    public ResponseEntity<ApiResponse<DoctorResult>> register(@Valid @RequestBody CreateDoctorRequest request) {
        CreateDoctorCommand command = new CreateDoctorCommand(
                request.fullName(), request.specialty(), request.phone(), request.email());
        DoctorResult result = registerDoctorUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Médico registrado exitosamente.", result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener médico por ID")
    public ResponseEntity<ApiResponse<DoctorResult>> getById(@PathVariable String id) {
        DoctorResult result = getDoctorByIdUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.ok("Médico encontrado.", result));
    }

    @GetMapping("/{id}/available-slots")
    @Operation(summary = "Consultar franjas disponibles", description = "RF-04: Devuelve franjas de 30 min disponibles en el rango")
    public ResponseEntity<ApiResponse<List<TimeSlotResult>>> getAvailableSlots(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<TimeSlotResult> slots = getAvailableSlotsUseCase.execute(id, fechaInicio, fechaFin);
        return ResponseEntity.ok(ApiResponse.ok(
                "Se encontraron " + slots.size() + " franja(s) disponible(s).", slots));
    }
}
