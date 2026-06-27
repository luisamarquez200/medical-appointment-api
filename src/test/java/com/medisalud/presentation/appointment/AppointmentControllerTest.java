package com.medisalud.presentation.appointment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medisalud.application.appointment.*;
import com.medisalud.application.shared.dto.appointment.AppointmentResult;
import com.medisalud.application.shared.dto.appointment.CreateAppointmentCommand;
import com.medisalud.presentation.appointment.request.CreateAppointmentRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppointmentController.class)
@DisplayName("AppointmentController API E2E")
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateAppointmentUseCase createAppointmentUseCase;
    
    @MockBean
    private CancelAppointmentUseCase cancelAppointmentUseCase;
    
    @MockBean
    private RescheduleAppointmentUseCase rescheduleAppointmentUseCase;
    
    @MockBean
    private ListAppointmentsUseCase listAppointmentsUseCase;

    @Test
    @DisplayName("Debería retornar 201 Created al crear una cita exitosamente")
    void testCreateAppointmentSuccess() throws Exception {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        LocalDateTime dateTime = LocalDateTime.of(2026, 6, 15, 10, 0);

        CreateAppointmentRequest request = new CreateAppointmentRequest(patientId, doctorId, dateTime);
        AppointmentResult mockResult = new AppointmentResult(UUID.randomUUID(), patientId, doctorId, dateTime, dateTime.plusMinutes(30), "PROGRAMADA", null, LocalDateTime.now(), LocalDateTime.now());

        Mockito.when(createAppointmentUseCase.execute(any(CreateAppointmentCommand.class))).thenReturn(mockResult);

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cita agendada exitosamente."))
                .andExpect(jsonPath("$.data.status").value("PROGRAMADA"));
    }

    @Test
    @DisplayName("Debería retornar 400 Bad Request si faltan campos obligatorios")
    void testCreateAppointmentBadRequest() throws Exception {
        // dateTime es null
        CreateAppointmentRequest request = new CreateAppointmentRequest(UUID.randomUUID(), UUID.randomUUID(), null);

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
