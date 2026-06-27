package com.medisalud.presentation.patient;

import com.medisalud.application.patient.GetPatientByIdUseCase;
import com.medisalud.application.patient.RegisterPatientUseCase;
import com.medisalud.application.shared.dto.patient.CreatePatientCommand;
import com.medisalud.application.shared.dto.patient.PatientResult;
import com.medisalud.presentation.patient.request.CreatePatientRequest;
import com.medisalud.presentation.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
@Tag(name = "Patients", description = "Gestión de pacientes del sistema")
public class PatientController {

    private final RegisterPatientUseCase registerPatientUseCase;
    private final GetPatientByIdUseCase getPatientByIdUseCase;

    public PatientController(
            RegisterPatientUseCase registerPatientUseCase,
            GetPatientByIdUseCase getPatientByIdUseCase) {
        this.registerPatientUseCase = registerPatientUseCase;
        this.getPatientByIdUseCase = getPatientByIdUseCase;
    }

    @PostMapping
    @Operation(summary = "Registrar un paciente", description = "RF-02: Crea un nuevo paciente en el sistema")
    public ResponseEntity<ApiResponse<PatientResult>> register(@Valid @RequestBody CreatePatientRequest request) {
        CreatePatientCommand command = new CreatePatientCommand(
                request.fullName(), request.documentId(),
                request.phone(), request.email(), request.birthDate());
        PatientResult result = registerPatientUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Paciente registrado exitosamente.", result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener paciente por ID")
    public ResponseEntity<ApiResponse<PatientResult>> getById(@PathVariable String id) {
        PatientResult result = getPatientByIdUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.ok("Paciente encontrado.", result));
    }
}
