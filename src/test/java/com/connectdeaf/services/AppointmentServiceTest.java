package com.connectdeaf.services;

import com.connectdeaf.controllers.dtos.requests.AppointmentRequestDTO;
import com.connectdeaf.controllers.dtos.response.AppointmentResponseDTO;
import com.connectdeaf.domain.appointment.Appointment;
import com.connectdeaf.domain.professional.Professional;
import com.connectdeaf.domain.service.ServiceEntity;
import com.connectdeaf.domain.user.User;
import com.connectdeaf.exceptions.AppointmentNotFoundException;
import com.connectdeaf.repositories.AppointmentRepository;
import com.connectdeaf.repositories.ProfessionalRepository;
import com.connectdeaf.repositories.ServiceRepository;
import com.connectdeaf.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfessionalRepository professionalRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAppointment() {
        UUID userId = UUID.randomUUID();
        UUID professionalId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();

        AppointmentRequestDTO appointmentRequestDTO = new AppointmentRequestDTO(
                "2024-09-01", 
                "14:00:00"
        );

        User user = new User();
        user.setId(userId);
        user.setName("Jane Doe");
        user.setEmail("jane.doe@example.com");
        user.setPhoneNumber("+1234567890");

        Professional professional = new Professional();
        professional.setId(professionalId);
        professional.setQualification("Consultant");
        professional.setAreaOfExpertise("Business Strategy");

        ServiceEntity service = new ServiceEntity();
        service.setId(serviceId);
        service.setName("Business Consulting");
        service.setDescription("Providing expert business advice");
        service.setValue(1200.00);
        service.setProfessional(professional);

        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setDate(LocalDate.of(2024, 9, 1));
        appointment.setTime(LocalTime.of(14, 0));
        appointment.setStatus("PENDING");
        appointment.setCustomer(user);
        appointment.setProfessional(professional);
        appointment.setService(service);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(professionalRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        AppointmentResponseDTO responseDTO = appointmentService.createAppointment(userId, professionalId, serviceId, appointmentRequestDTO);

        assertNotNull(responseDTO);
        assertEquals(user.getName(), responseDTO.customer().name());
        assertEquals(professional.getQualification(), responseDTO.professional().qualification());
        assertEquals(service.getName(), responseDTO.service().name());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void testFindAppointmentById() {
        UUID appointmentId = UUID.randomUUID();

        User user = new User();
        user.setName("Jane Doe");

        Professional professional = new Professional();
        professional.setQualification("Consultant");

        ServiceEntity service = new ServiceEntity();
        service.setName("Business Consulting");

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setCustomer(user);
        appointment.setProfessional(professional);
        appointment.setService(service);
        appointment.setDate(LocalDate.of(2024, 9, 1));
        appointment.setTime(LocalTime.of(14, 0));
        appointment.setStatus("PENDING");

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        AppointmentResponseDTO responseDTO = appointmentService.findAppointmentById(appointmentId);

        assertNotNull(responseDTO);
        assertEquals("Jane Doe", responseDTO.customer().name());
        assertEquals("Consultant", responseDTO.professional().qualification());
        assertEquals("Business Consulting", responseDTO.service().name());
    }

    @Test
    void testFindAppointmentById_NotFound() {
        UUID appointmentId = UUID.randomUUID();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        assertThrows(AppointmentNotFoundException.class, () -> appointmentService.findAppointmentById(appointmentId));
    }

    @Test
    void testFindAllAppointments() {
        User user = new User();
        user.setName("Jane Doe");

        Professional professional = new Professional();
        professional.setQualification("Consultant");

        ServiceEntity service = new ServiceEntity();
        service.setName("Business Consulting");

        Appointment appointment1 = new Appointment();
        appointment1.setId(UUID.randomUUID());
        appointment1.setCustomer(user);
        appointment1.setProfessional(professional);
        appointment1.setService(service);
        appointment1.setDate(LocalDate.of(2024, 9, 1));
        appointment1.setTime(LocalTime.of(14, 0));
        appointment1.setStatus("PENDING");

        Appointment appointment2 = new Appointment();
        appointment2.setId(UUID.randomUUID());
        appointment2.setCustomer(user);
        appointment2.setProfessional(professional);
        appointment2.setService(service);
        appointment2.setDate(LocalDate.of(2024, 9, 2));
        appointment2.setTime(LocalTime.of(15, 0));
        appointment2.setStatus("CONFIRMED");

        when(appointmentRepository.findAll()).thenReturn(List.of(appointment1, appointment2));

        List<AppointmentResponseDTO> responseDTOList = appointmentService.findAllAppointments();

        assertEquals(2, responseDTOList.size());
        verify(appointmentRepository, times(1)).findAll();
    }

    @Test
    void testDeleteAppointment() {
        UUID appointmentId = UUID.randomUUID();

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        appointmentService.deleteAppointment(appointmentId);

        verify(appointmentRepository, times(1)).delete(appointment);
    }

    @Test
    void testDeleteAppointment_NotFound() {
        UUID appointmentId = UUID.randomUUID();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        assertThrows(AppointmentNotFoundException.class, () -> appointmentService.deleteAppointment(appointmentId));
    }
}
