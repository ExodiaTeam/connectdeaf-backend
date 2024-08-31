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

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ProfessionalRepository professionalRepository;
    private final ServiceRepository serviceRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, UserRepository userRepository, ProfessionalRepository professionalRepository, ServiceRepository serviceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.professionalRepository = professionalRepository;
        this.serviceRepository = serviceRepository;
    }

    @Transactional
    public AppointmentResponseDTO createAppointment(UUID userId, UUID professionalId, UUID serviceId, AppointmentRequestDTO appointmentRequestDTO) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException());

        Professional professional = professionalRepository.findById(professionalId)
            .orElseThrow(() -> new ProfessionalNotFoundException());

        ServiceEntity service = serviceRepository.findById(serviceId)
            .orElseThrow(() -> new ServiceNotFoundException());

        LocalDate date = LocalDate.parse(appointmentRequestDTO.date(), DateTimeFormatter.ISO_LOCAL_DATE);
        LocalTime time = LocalTime.parse(appointmentRequestDTO.time(), DateTimeFormatter.ISO_LOCAL_TIME);

        Appointment appointment = new Appointment();
        appointment.setDate(date);
        appointment.setTime(time);
        appointment.setStatus("PENDING");
        appointment.setCustomer(user);
        appointment.setProfessional(professional);
        appointment.setService(service);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return new AppointmentResponseDTO(
            savedAppointment.getId(),
            mapToUserResponseDTO(savedAppointment.getCustomer()),
            mapToProfessionalResponseDTO(savedAppointment.getProfessional()),
            mapToServiceResponseDTO(savedAppointment.getService()),
            savedAppointment.getDate(),
            savedAppointment.getTime(),
            savedAppointment.getStatus()
        );
    }

    public AppointmentResponseDTO findAppointmentById(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException());

        return new AppointmentResponseDTO(
            appointment.getId(),
            mapToUserResponseDTO(appointment.getCustomer()),
            mapToProfessionalResponseDTO(appointment.getProfessional()),
            mapToServiceResponseDTO(appointment.getService()),
            appointment.getDate(),
            appointment.getTime(),
            appointment.getStatus()
        );
    }

    public List<AppointmentResponseDTO> findAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(appointment -> new AppointmentResponseDTO(
                    appointment.getId(),
                    mapToUserResponseDTO(appointment.getCustomer()),
                    mapToProfessionalResponseDTO(appointment.getProfessional()),
                    mapToServiceResponseDTO(appointment.getService()),
                    appointment.getDate(),
                    appointment.getTime(),
                    appointment.getStatus()
                ))
                .collect(Collectors.toList());
    }

    public void deleteAppointment(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException());
        appointmentRepository.delete(appointment);
    }

    private UserResponseDTO mapToUserResponseDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getPhoneNumber());
    }

    private ProfessionalResponseDTO mapToProfessionalResponseDTO(Professional professional) {
        return new ProfessionalResponseDTO(professional.getId(), professional.getUser().getName(), 
            professional.getUser().getEmail(), professional.getUser().getPhoneNumber(), 
            professional.getQualification(), professional.getAreaOfExpertise());
    }

    private ServiceResponseDTO mapToServiceResponseDTO(ServiceEntity service) {
        return new ServiceResponseDTO(service.getId(), service.getName(), service.getDescription(), service.getValue(),
            mapToProfessionalResponseDTO(service.getProfessional()));
    }
}



