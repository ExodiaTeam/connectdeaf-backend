package com.connectdeaf.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.connectdeaf.controllers.dtos.requests.AppointmentRequestDTO;
import com.connectdeaf.controllers.dtos.response.AppointmentResponseDTO;
import com.connectdeaf.controllers.dtos.response.ProfessionalResponseDTO;
import com.connectdeaf.controllers.dtos.response.ServiceResponseDTO;
import com.connectdeaf.controllers.dtos.response.UserResponseDTO;
import com.connectdeaf.services.AppointmentService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AppointmentService mockAppointmentService;

    @Test
    void testCreateAppointment() throws Exception {
        final UUID customerId = UUID.fromString("c0a80100-5487-11eb-ae93-0242ac130002");
        final UUID professionalId = UUID.fromString("c0a80100-5487-11eb-ae93-0242ac130003");
        final UUID serviceId = UUID.fromString("c0a80100-5487-11eb-ae93-0242ac130004");

        final AppointmentRequestDTO appointmentRequestDTO = new AppointmentRequestDTO(
                "2024/09/01",
                "14:00:00"
        );

        final UserResponseDTO customerResponseDTO = new UserResponseDTO(
                customerId,
                "Jane Doe",
                "jane.doe@example.com",
                "+1234567890"
        );

        final ProfessionalResponseDTO professionalResponseDTO = new ProfessionalResponseDTO(
                professionalId,
                "John Smith",
                "john.smith@example.com",
                "+0987654321",
                "Consultant",
                "Business Strategy"
        );

        final ServiceResponseDTO serviceResponseDTO = new ServiceResponseDTO(
                serviceId,
                "Business Consulting",
                "Providing expert business advice",
                1200.00,
                professionalResponseDTO
        );

        final AppointmentResponseDTO appointmentResponseDTO = new AppointmentResponseDTO(
                UUID.fromString("c0a80100-5487-11eb-ae93-0242ac130005"),
                customerResponseDTO,
                professionalResponseDTO,
                serviceResponseDTO,
                LocalDate.of(2024, 9, 1),
                LocalTime.of(14, 0),
                "Scheduled"
        );

        when(mockAppointmentService.createAppointment(eq(customerId), eq(professionalId), eq(serviceId), any(AppointmentRequestDTO.class)))
                .thenReturn(appointmentResponseDTO);

        mockMvc.perform(post("/api/appointments")
                        .param("customerId", customerId.toString())
                        .param("professionalId", professionalId.toString())
                        .param("serviceId", serviceId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(appointmentResponseDTO)))
                .andExpect(header().string("Location", "/api/appointments/" + appointmentResponseDTO.id()));

        verify(mockAppointmentService).createAppointment(eq(customerId), eq(professionalId), eq(serviceId), any(AppointmentRequestDTO.class));
    }

    @Test
    void testGetAppointment() throws Exception {
        final UUID appointmentId = UUID.fromString("c0a80100-5487-11eb-ae93-0242ac130005");

        final UserResponseDTO customerResponseDTO = new UserResponseDTO(
                UUID.fromString("c0a80100-5487-11eb-ae93-0242ac130002"),
                "Jane Doe",
                "jane.doe@example.com",
                "+1234567890"
        );

        final ProfessionalResponseDTO professionalResponseDTO = new ProfessionalResponseDTO(
                UUID.fromString("c0a80100-5487-11eb-ae93-0242ac130003"),
                "John Smith",
                "john.smith@example.com",
                "+0987654321",
                "Consultant",
                "Business Strategy"
        );

        final ServiceResponseDTO serviceResponseDTO = new ServiceResponseDTO(
                UUID.fromString("c0a80100-5487-11eb-ae93-0242ac130004"),
                "Business Consulting",
                "Providing expert business advice",
                1200.00,
                professionalResponseDTO
        );

        final AppointmentResponseDTO appointmentResponseDTO = new AppointmentResponseDTO(
                appointmentId,
                customerResponseDTO,
                professionalResponseDTO,
                serviceResponseDTO,
                LocalDate.of(2024, 9, 1),
                LocalTime.of(14, 0),
                "Scheduled"
        );

        when(mockAppointmentService.findAppointmentById(eq(appointmentId))).thenReturn(appointmentResponseDTO);

        mockMvc.perform(get("/api/appointments/{appointment_id}", appointmentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(appointmentResponseDTO)));

        verify(mockAppointmentService).findAppointmentById(eq(appointmentId));
    }

    @Test
    void testGetAllAppointments() throws Exception {
        final UserResponseDTO customerResponseDTO1 = new UserResponseDTO(
                UUID.fromString("c0a80100-5487-11eb-ae93-0242ac130002"),
                "Jane Doe",
                "jane.doe@example.com",
                "+1234567890"
        );

        final ProfessionalResponseDTO professionalResponseDTO1 = new ProfessionalResponseDTO(
                UUID.fromString("c0a80100-5487-11eb-ae93-0242ac130003"),
                "John Smith",
                "john.smith@example.com",
                "+0987654321",
                "Consultant",
                "Business Strategy"
        );

        final ServiceResponseDTO serviceResponseDTO1 = new ServiceResponseDTO(
                UUID.fromString("c0a80100-5487-11eb-ae93-0242ac130004"),
                "Business Consulting",
                "Providing expert business advice",
                1200.00,
                professionalResponseDTO1
        );

        final AppointmentResponseDTO appointmentResponseDTO1 = new AppointmentResponseDTO(
                UUID.fromString("c0a80100-5487-11eb-ae93-0242ac130005"),
                customerResponseDTO1,
                professionalResponseDTO1,
                serviceResponseDTO1,
                LocalDate.of(2024, 9, 1),
                LocalTime.of(14, 0),
                "Scheduled"
        );

        final UserResponseDTO customerResponseDTO2 = new UserResponseDTO(
                UUID.fromString("c0a80100-5487-11eb-ae93-0242ac130006"),
                "Alice Brown",
                "alice.brown@example.com",
                "+1234567891"
        );

        final ProfessionalResponseDTO professionalResponseDTO2 = new ProfessionalResponseDTO(
                UUID.fromString("c0a80100-5487-11eb-ae93-0242ac130007"),
                "Michael Johnson",
                "michael.johnson@example.com",
                "+0987654322",
                "Financial Advisor",
                "Investment Planning"
        );

        final ServiceResponseDTO serviceResponseDTO2 = new ServiceResponseDTO(
                UUID.fromString("c0a80100-5487-11eb-ae93-0242ac130008"),
                "Financial Planning",
                "Providing financial advice and investment strategies",
                1500.00,
                professionalResponseDTO2
        );

        final AppointmentResponseDTO appointmentResponseDTO2 = new AppointmentResponseDTO(
                UUID.fromString("c0a80100-5487-11eb-ae93-0242ac130009"),
                customerResponseDTO2,
                professionalResponseDTO2,
                serviceResponseDTO2,
                LocalDate.of(2024, 9, 2),
                LocalTime.of(10, 0),
                "Confirmed"
        );

        final List<AppointmentResponseDTO> appointmentResponseDTOList = List.of(appointmentResponseDTO1, appointmentResponseDTO2);

        when(mockAppointmentService.findAllAppointments()).thenReturn(appointmentResponseDTOList);

        mockMvc.perform(get("/api/appointments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(appointmentResponseDTOList)));

        verify(mockAppointmentService).findAllAppointments();
    }

    @Test
    void testGetAllAppointments_AppointmentServiceReturnsNoItems() throws Exception {
        when(mockAppointmentService.findAllAppointments()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/appointments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(mockAppointmentService).findAllAppointments();
    }

    @Test
    void testDeleteAppointment() throws Exception {
        final UUID appointmentId = UUID.fromString("c0a80100-5487-11eb-ae93-0242ac130005");

        mockMvc.perform(delete("/api/appointments/{appointment_id}", appointmentId))
                .andExpect(status().isNoContent());

        verify(mockAppointmentService).deleteAppointment(eq(appointmentId));
    }
}
