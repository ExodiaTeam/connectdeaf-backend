package com.connectdeaf.services;

import com.connectdeaf.controllers.dtos.requests.AppointmentRequestDTO;
import com.connectdeaf.controllers.dtos.response.AppointmentResponseDTO;
import com.connectdeaf.controllers.dtos.response.ProfessionalResponseDTO;
import com.connectdeaf.controllers.dtos.response.ServiceResponseDTO;
import com.connectdeaf.controllers.dtos.response.UserResponseDTO;
import com.connectdeaf.domain.appointment.Appointment;
import com.connectdeaf.domain.professional.Professional;
import com.connectdeaf.domain.service.ServiceEntity;
import com.connectdeaf.domain.user.User;
import com.connectdeaf.exceptions.AppointmentNotFoundException;
import com.connectdeaf.exceptions.ProfessionalNotFoundException;
import com.connectdeaf.exceptions.ServiceNotFoundException;
import com.connectdeaf.exceptions.UserNotFoundException;
import com.connectdeaf.repositories.AppointmentRepository;
import com.connectdeaf.repositories.ProfessionalRepository;
import com.connectdeaf.repositories.ServiceRepository;
import com.connectdeaf.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository mockAppointmentRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private ProfessionalRepository mockProfessionalRepository;
    @Mock
    private ServiceRepository mockServiceRepository;

    private AppointmentService appointmentServiceUnderTest;

    @BeforeEach
    void setUp() {
        appointmentServiceUnderTest = new AppointmentService(mockAppointmentRepository, mockUserRepository,
                mockProfessionalRepository, mockServiceRepository);
    }

    @Test
    void testCreateAppointment() {
        // Setup
        final String validDate = "2024-09-01"; // Data no formato yyyy-MM-dd
        final String validTime = "14:00"; // Hora no formato HH:mm
        final AppointmentRequestDTO appointmentRequestDTO = new AppointmentRequestDTO(validDate, validTime);

        final AppointmentResponseDTO expectedResult = new AppointmentResponseDTO(
                UUID.fromString("ea5b1116-0949-49b6-a415-768ecb1547fc"),
                new UserResponseDTO(UUID.fromString("b41b9fd8-7388-41fd-8c31-20e4c307d6aa"), "name", "email", "phoneNumber"),
                new ProfessionalResponseDTO(UUID.fromString("9ce02099-a143-4ab6-b762-b729a6d86417"), "name", "email", "phoneNumber", "qualification", "areaOfExpertise"),
                new ServiceResponseDTO(UUID.fromString("729de0c1-9d2a-4740-babe-6753fa5e6755"), "name", "description", 0.0, new ProfessionalResponseDTO(UUID.fromString("9ce02099-a143-4ab6-b762-b729a6d86417"), "name", "email", "phoneNumber", "qualification", "areaOfExpertise")),
                LocalDate.of(2024, 9, 1),
                LocalTime.of(14, 0),
                "status"
        );

        // Configure UserRepository.findById(...).
        final Optional<User> userOptional = Optional.of(new User("name", "email", "password", "phoneNumber"));
        when(mockUserRepository.findById(UUID.fromString("fa49ca11-f5a8-47f8-88c8-2782156718ea"))).thenReturn(userOptional);

        // Configure ProfessionalRepository.findById(...).
        final Professional professional1 = new Professional();
        professional1.setId(UUID.fromString("9ce02099-a143-4ab6-b762-b729a6d86417"));
        professional1.setQualification("qualification");
        professional1.setAreaOfExpertise("areaOfExpertise");
        final User user = new User();
        user.setId(UUID.fromString("b41b9fd8-7388-41fd-8c31-20e4c307d6aa"));
        user.setName("name");
        user.setEmail("email");
        user.setPhoneNumber("phoneNumber");
        professional1.setUser(user);
        final Optional<Professional> professional = Optional.of(professional1);
        when(mockProfessionalRepository.findById(UUID.fromString("f15a8fe7-8aee-4e6b-9591-36e2befc5a8e"))).thenReturn(professional);

        // Configure ServiceRepository.findById(...).
        final ServiceEntity serviceEntity1 = new ServiceEntity();
        serviceEntity1.setId(UUID.fromString("729de0c1-9d2a-4740-babe-6753fa5e6755"));
        final Professional professional2 = new Professional();
        professional2.setId(UUID.fromString("9ce02099-a143-4ab6-b762-b729a6d86417"));
        professional2.setQualification("qualification");
        professional2.setAreaOfExpertise("areaOfExpertise");
        final User user1 = new User();
        user1.setId(UUID.fromString("b41b9fd8-7388-41fd-8c31-20e4c307d6aa"));
        user1.setName("name");
        user1.setEmail("email");
        user1.setPhoneNumber("phoneNumber");
        professional2.setUser(user1);
        serviceEntity1.setProfessional(professional2);
        serviceEntity1.setValue(0.0);
        serviceEntity1.setName("name");
        serviceEntity1.setDescription("description");
        final Optional<ServiceEntity> serviceEntity = Optional.of(serviceEntity1);
        when(mockServiceRepository.findById(UUID.fromString("f2bd0ce8-f9d2-4bc8-b052-27a9a790bdc2"))).thenReturn(serviceEntity);

        // Configure AppointmentRepository.save(...).
        final Appointment appointment = new Appointment();
        appointment.setId(UUID.fromString("ea5b1116-0949-49b6-a415-768ecb1547fc"));
        final User customer = new User();
        customer.setId(UUID.fromString("b41b9fd8-7388-41fd-8c31-20e4c307d6aa"));
        customer.setName("name");
        customer.setEmail("email");
        customer.setPhoneNumber("phoneNumber");
        appointment.setCustomer(customer);

        final Professional professional3 = new Professional();
        professional3.setId(UUID.fromString("9ce02099-a143-4ab6-b762-b729a6d86417"));
        professional3.setQualification("qualification");
        professional3.setAreaOfExpertise("areaOfExpertise");

        final User user2 = new User();
        user2.setId(UUID.fromString("b41b9fd8-7388-41fd-8c31-20e4c307d6aa"));
        user2.setName("name");
        user2.setEmail("email");
        user2.setPhoneNumber("phoneNumber");
        professional3.setUser(user2);
        appointment.setProfessional(professional3);

        final ServiceEntity service = new ServiceEntity();
        service.setId(UUID.fromString("729de0c1-9d2a-4740-babe-6753fa5e6755"));

        final Professional professional4 = new Professional();
        professional4.setId(UUID.fromString("9ce02099-a143-4ab6-b762-b729a6d86417"));
        professional4.setQualification("qualification");
        professional4.setAreaOfExpertise("areaOfExpertise");

        final User user3 = new User();
        user3.setId(UUID.fromString("b41b9fd8-7388-41fd-8c31-20e4c307d6aa"));
        user3.setName("name");
        user3.setEmail("email");
        user3.setPhoneNumber("phoneNumber");
        professional4.setUser(user3);
        service.setProfessional(professional4);
        service.setValue(0.0);
        service.setName("name");
        service.setDescription("description");
        appointment.setService(service);
        appointment.setDate(LocalDate.of(2024, 9, 1));
        appointment.setTime(LocalTime.of(14, 0));
        appointment.setStatus("status");
        when(mockAppointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        // Run the test
        final AppointmentResponseDTO result = appointmentServiceUnderTest.createAppointment(
                UUID.fromString("fa49ca11-f5a8-47f8-88c8-2782156718ea"),
                UUID.fromString("f15a8fe7-8aee-4e6b-9591-36e2befc5a8e"),
                UUID.fromString("f2bd0ce8-f9d2-4bc8-b052-27a9a790bdc2"), appointmentRequestDTO);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }


    @Test
    void testCreateAppointment_UserRepositoryReturnsAbsent() {
        // Setup
        final AppointmentRequestDTO appointmentRequestDTO = new AppointmentRequestDTO("date", "time");
        when(mockUserRepository.findById(UUID.fromString("fa49ca11-f5a8-47f8-88c8-2782156718ea")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> appointmentServiceUnderTest.createAppointment(
                UUID.fromString("fa49ca11-f5a8-47f8-88c8-2782156718ea"),
                UUID.fromString("f15a8fe7-8aee-4e6b-9591-36e2befc5a8e"),
                UUID.fromString("f2bd0ce8-f9d2-4bc8-b052-27a9a790bdc2"), appointmentRequestDTO))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testCreateAppointment_ProfessionalRepositoryReturnsAbsent() {
        // Setup
        final AppointmentRequestDTO appointmentRequestDTO = new AppointmentRequestDTO("date", "time");

        // Configure UserRepository.findById(...).
        final Optional<User> userOptional = Optional.of(new User("name", "email", "password", "phoneNumber"));
        when(mockUserRepository.findById(UUID.fromString("fa49ca11-f5a8-47f8-88c8-2782156718ea")))
                .thenReturn(userOptional);

        when(mockProfessionalRepository.findById(UUID.fromString("f15a8fe7-8aee-4e6b-9591-36e2befc5a8e")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> appointmentServiceUnderTest.createAppointment(
                UUID.fromString("fa49ca11-f5a8-47f8-88c8-2782156718ea"),
                UUID.fromString("f15a8fe7-8aee-4e6b-9591-36e2befc5a8e"),
                UUID.fromString("f2bd0ce8-f9d2-4bc8-b052-27a9a790bdc2"), appointmentRequestDTO))
                .isInstanceOf(ProfessionalNotFoundException.class);
    }

    @Test
    void testCreateAppointment_ServiceRepositoryReturnsAbsent() {
        // Setup
        final AppointmentRequestDTO appointmentRequestDTO = new AppointmentRequestDTO("date", "time");

        // Configure UserRepository.findById(...).
        final Optional<User> userOptional = Optional.of(new User("name", "email", "password", "phoneNumber"));
        when(mockUserRepository.findById(UUID.fromString("fa49ca11-f5a8-47f8-88c8-2782156718ea")))
                .thenReturn(userOptional);

        // Configure ProfessionalRepository.findById(...).
        final Professional professional1 = new Professional();
        professional1.setId(UUID.fromString("9ce02099-a143-4ab6-b762-b729a6d86417"));
        professional1.setQualification("qualification");
        professional1.setAreaOfExpertise("areaOfExpertise");
        final User user = new User();
        user.setId(UUID.fromString("b41b9fd8-7388-41fd-8c31-20e4c307d6aa"));
        user.setName("name");
        user.setEmail("email");
        user.setPhoneNumber("phoneNumber");
        professional1.setUser(user);
        final Optional<Professional> professional = Optional.of(professional1);
        when(mockProfessionalRepository.findById(UUID.fromString("f15a8fe7-8aee-4e6b-9591-36e2befc5a8e")))
                .thenReturn(professional);

        when(mockServiceRepository.findById(UUID.fromString("f2bd0ce8-f9d2-4bc8-b052-27a9a790bdc2")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> appointmentServiceUnderTest.createAppointment(
                UUID.fromString("fa49ca11-f5a8-47f8-88c8-2782156718ea"),
                UUID.fromString("f15a8fe7-8aee-4e6b-9591-36e2befc5a8e"),
                UUID.fromString("f2bd0ce8-f9d2-4bc8-b052-27a9a790bdc2"), appointmentRequestDTO))
                .isInstanceOf(ServiceNotFoundException.class);
    }

    @Test
    void testFindAppointmentById() {
        // Setup
        final AppointmentResponseDTO expectedResult = new AppointmentResponseDTO(
                UUID.fromString("ea5b1116-0949-49b6-a415-768ecb1547fc"),
                new UserResponseDTO(
                        UUID.fromString("b41b9fd8-7388-41fd-8c31-20e4c307d6aa"),
                        "name",
                        "email@.com",
                        "phoneNumber"),
                new ProfessionalResponseDTO(
                        UUID.fromString("9ce02099-a143-4ab6-b762-b729a6d86417"),
                        "name", "email",
                        "phoneNumber",
                        "qualification",
                        "areaOfExpertise"),
                new ServiceResponseDTO(UUID.fromString("729de0c1-9d2a-4740-babe-6753fa5e6755"), "name", "description",
                        0.0,
                        new ProfessionalResponseDTO(UUID.fromString("9ce02099-a143-4ab6-b762-b729a6d86417"), "name",
                                "email", "phoneNumber", "qualification", "areaOfExpertise")), LocalDate.of(2020, 1, 1),
                LocalTime.of(0, 0, 0), "status");

        // Configure AppointmentRepository.findById(...).
        final Appointment appointment1 = new Appointment();
        appointment1.setId(UUID.fromString("ea5b1116-0949-49b6-a415-768ecb1547fc"));

        final User customer = new User();
        customer.setId(UUID.fromString("b41b9fd8-7388-41fd-8c31-20e4c307d6aa"));
        customer.setName("name");
        customer.setEmail("email");
        customer.setPhoneNumber("phoneNumber");
        appointment1.setCustomer(customer);

        final Professional professional = new Professional();
        professional.setId(UUID.fromString("9ce02099-a143-4ab6-b762-b729a6d86417"));
        professional.setQualification("qualification");
        professional.setAreaOfExpertise("areaOfExpertise");

        final User user = new User();
        user.setId(UUID.fromString("b41b9fd8-7388-41fd-8c31-20e4c307d6aa"));
        user.setName("name");
        user.setEmail("email");
        user.setPhoneNumber("phoneNumber");
        professional.setUser(user);
        appointment1.setProfessional(professional);

        final ServiceEntity service = new ServiceEntity();
        service.setId(UUID.fromString("729de0c1-9d2a-4740-babe-6753fa5e6755"));

        final Professional professional1 = new Professional();
        professional1.setId(UUID.fromString("9ce02099-a143-4ab6-b762-b729a6d86417"));
        professional1.setQualification("qualification");
        professional1.setAreaOfExpertise("areaOfExpertise");

        final User user1 = new User();
        user1.setId(UUID.fromString("b41b9fd8-7388-41fd-8c31-20e4c307d6aa"));
        user1.setName("name");
        user1.setEmail("email");
        user1.setPhoneNumber("phoneNumber");
        professional1.setUser(user1);

        service.setProfessional(professional1);
        service.setValue(0.0);
        service.setName("name");
        service.setDescription("description");
        appointment1.setService(service);
        appointment1.setDate(LocalDate.of(2020, 1, 1));
        appointment1.setTime(LocalTime.of(0, 0, 0));
        appointment1.setStatus("status");

        final Optional<Appointment> appointment = Optional.of(appointment1);
        when(mockAppointmentRepository.findById(UUID.fromString("ea5b1116-0949-49b6-a415-768ecb1547fc")))
                .thenReturn(appointment);

        // Run the test
        final AppointmentResponseDTO result = appointmentServiceUnderTest.findAppointmentById(
                UUID.fromString("ea5b1116-0949-49b6-a415-768ecb1547fc"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testFindAppointmentById_AppointmentRepositoryReturnsAbsent() {
        // Setup
        when(mockAppointmentRepository.findById(UUID.fromString("3989c521-c3ba-4bfe-a62b-d09b5355eacf")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> appointmentServiceUnderTest.findAppointmentById(
                UUID.fromString("3989c521-c3ba-4bfe-a62b-d09b5355eacf")))
                .isInstanceOf(AppointmentNotFoundException.class);
    }

    @Test
    void testFindAllAppointments() {
        // Setup
        final List<AppointmentResponseDTO> expectedResult = List.of(
                new AppointmentResponseDTO(UUID.fromString("ea5b1116-0949-49b6-a415-768ecb1547fc"),
                        new UserResponseDTO(UUID.fromString("b41b9fd8-7388-41fd-8c31-20e4c307d6aa"), "name", "email",
                                "phoneNumber"),
                        new ProfessionalResponseDTO(UUID.fromString("9ce02099-a143-4ab6-b762-b729a6d86417"), "name",
                                "email", "phoneNumber", "qualification", "areaOfExpertise"),
                        new ServiceResponseDTO(UUID.fromString("729de0c1-9d2a-4740-babe-6753fa5e6755"), "name",
                                "description", 0.0,
                                new ProfessionalResponseDTO(UUID.fromString("9ce02099-a143-4ab6-b762-b729a6d86417"),
                                        "name", "email", "phoneNumber", "qualification", "areaOfExpertise")),
                        LocalDate.of(2020, 1, 1), LocalTime.of(0, 0, 0), "status"));

        // Configure AppointmentRepository.findAll(...).
        final Appointment appointment = new Appointment();
        appointment.setId(UUID.fromString("ea5b1116-0949-49b6-a415-768ecb1547fc"));

        final User customer = new User();
        customer.setId(UUID.fromString("b41b9fd8-7388-41fd-8c31-20e4c307d6aa"));
        customer.setName("name");
        customer.setEmail("email");
        customer.setPhoneNumber("phoneNumber");
        appointment.setCustomer(customer);

        final Professional professional = new Professional();
        professional.setId(UUID.fromString("9ce02099-a143-4ab6-b762-b729a6d86417"));
        professional.setQualification("qualification");
        professional.setAreaOfExpertise("areaOfExpertise");

        final User user = new User();
        user.setId(UUID.fromString("b41b9fd8-7388-41fd-8c31-20e4c307d6aa"));
        user.setName("name");
        user.setEmail("email");
        user.setPhoneNumber("phoneNumber");
        professional.setUser(user);
        appointment.setProfessional(professional);

        final ServiceEntity service = new ServiceEntity();
        service.setId(UUID.fromString("729de0c1-9d2a-4740-babe-6753fa5e6755"));

        final Professional professional1 = new Professional();
        professional1.setId(UUID.fromString("9ce02099-a143-4ab6-b762-b729a6d86417"));
        professional1.setQualification("qualification");
        professional1.setAreaOfExpertise("areaOfExpertise");

        final User user1 = new User();
        user1.setId(UUID.fromString("b41b9fd8-7388-41fd-8c31-20e4c307d6aa"));
        user1.setName("name");
        user1.setEmail("email");
        user1.setPhoneNumber("phoneNumber");
        professional1.setUser(user1);
        service.setProfessional(professional1);
        service.setValue(0.0);
        service.setName("name");
        service.setDescription("description");
        appointment.setService(service);
        appointment.setDate(LocalDate.of(2020, 1, 1));
        appointment.setTime(LocalTime.of(0, 0, 0));
        appointment.setStatus("status");

        final List<Appointment> appointments = List.of(appointment);
        when(mockAppointmentRepository.findAll()).thenReturn(appointments);

        // Run the test
        final List<AppointmentResponseDTO> result = appointmentServiceUnderTest.findAllAppointments();

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }


    @Test
    void testFindAllAppointments_AppointmentRepositoryReturnsNoItems() {
        // Setup
        when(mockAppointmentRepository.findAll()).thenReturn(Collections.emptyList());

        // Run the test
        final List<AppointmentResponseDTO> result = appointmentServiceUnderTest.findAllAppointments();

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testDeleteAppointment() {
        // Setup
        // Configure AppointmentRepository.findById(...).
        final Appointment appointment1 = new Appointment();
        appointment1.setId(UUID.fromString("ea5b1116-0949-49b6-a415-768ecb1547fc"));
        final User customer = new User();
        customer.setId(UUID.fromString("b41b9fd8-7388-41fd-8c31-20e4c307d6aa"));
        customer.setName("name");
        customer.setEmail("email");
        customer.setPhoneNumber("phoneNumber");
        appointment1.setCustomer(customer);
        final Professional professional = new Professional();
        professional.setId(UUID.fromString("9ce02099-a143-4ab6-b762-b729a6d86417"));
        professional.setQualification("qualification");
        professional.setAreaOfExpertise("areaOfExpertise");
        final User user = new User();
        user.setId(UUID.fromString("b41b9fd8-7388-41fd-8c31-20e4c307d6aa"));
        user.setName("name");
        user.setEmail("email");
        user.setPhoneNumber("phoneNumber");
        professional.setUser(user);
        appointment1.setProfessional(professional);
        final ServiceEntity service = new ServiceEntity();
        service.setId(UUID.fromString("729de0c1-9d2a-4740-babe-6753fa5e6755"));
        final Professional professional1 = new Professional();
        professional1.setId(UUID.fromString("9ce02099-a143-4ab6-b762-b729a6d86417"));
        professional1.setQualification("qualification");
        professional1.setAreaOfExpertise("areaOfExpertise");
        final User user1 = new User();
        user1.setId(UUID.fromString("b41b9fd8-7388-41fd-8c31-20e4c307d6aa"));
        user1.setName("name");
        user1.setEmail("email");
        user1.setPhoneNumber("phoneNumber");
        professional1.setUser(user1);
        service.setProfessional(professional1);
        service.setValue(0.0);
        service.setName("name");
        service.setDescription("description");
        appointment1.setService(service);
        appointment1.setDate(LocalDate.of(2020, 1, 1));
        appointment1.setTime(LocalTime.of(0, 0, 0));
        appointment1.setStatus("status");
        final Optional<Appointment> appointment = Optional.of(appointment1);
        when(mockAppointmentRepository.findById(UUID.fromString("082fc8a8-32c5-4ea8-836f-67d23bb06e50")))
                .thenReturn(appointment);

        // Run the test
        appointmentServiceUnderTest.deleteAppointment(UUID.fromString("082fc8a8-32c5-4ea8-836f-67d23bb06e50"));

        // Verify the results
        verify(mockAppointmentRepository).delete(any(Appointment.class));
    }

    @Test
    void testDeleteAppointment_AppointmentRepositoryFindByIdReturnsAbsent() {
        // Setup
        when(mockAppointmentRepository.findById(UUID.fromString("082fc8a8-32c5-4ea8-836f-67d23bb06e50")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> appointmentServiceUnderTest.deleteAppointment(
                UUID.fromString("082fc8a8-32c5-4ea8-836f-67d23bb06e50")))
                .isInstanceOf(AppointmentNotFoundException.class);
    }
}
