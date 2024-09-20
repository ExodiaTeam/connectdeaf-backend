package com.connectdeaf.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.connectdeaf.controllers.dtos.requests.AppointmentRequestDTO;
import com.connectdeaf.controllers.dtos.response.*;
import com.connectdeaf.domain.appointment.Appointment;
import com.connectdeaf.domain.professional.Professional;
import com.connectdeaf.domain.schedule.Schedule;
import com.connectdeaf.domain.service.ServiceEntity;
import com.connectdeaf.domain.user.User;
import com.connectdeaf.exceptions.*;
import com.connectdeaf.repositories.*;

import jakarta.transaction.Transactional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ProfessionalRepository professionalRepository;
    private final ServiceRepository serviceRepository;
    private final ScheduleRepository scheduleRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, UserRepository userRepository,
                              ProfessionalRepository professionalRepository, ServiceRepository serviceRepository,
                              ScheduleRepository scheduleRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.professionalRepository = professionalRepository;
        this.serviceRepository = serviceRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO appointmentRequestDTO) {
        User customer = userRepository.findById(appointmentRequestDTO.customerId())
                .orElseThrow(UserNotFoundException::new);

        Professional professional = professionalRepository.findById(appointmentRequestDTO.professionalId())
                .orElseThrow(ProfessionalNotFoundException::new);

        ServiceEntity service = serviceRepository.findById(appointmentRequestDTO.serviceId())
                .orElseThrow(ServiceNotFoundException::new);

        LocalDate date = appointmentRequestDTO.date();
        LocalTime startTime = appointmentRequestDTO.startTime();
        LocalTime endTime = appointmentRequestDTO.endTime();

        Schedule schedule = new Schedule(null, professional, date, startTime, endTime);
        scheduleRepository.save(schedule);

        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setProfessional(professional);
        appointment.setService(service);
        appointment.setSchedule(schedule);
        appointment.setStatus("PENDING");

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return new AppointmentResponseDTO(
                savedAppointment.getId(),
                mapToUserResponseDTO(savedAppointment.getCustomer()),
                mapToProfessionalResponseDTO(savedAppointment.getProfessional()),
                mapToServiceResponseDTO(savedAppointment.getService()),
                mapToScheduleResponseDTO(savedAppointment.getSchedule()),
                savedAppointment.getStatus());
    }

    @Transactional
    public AppointmentResponseDTO findAppointmentById(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(AppointmentNotFoundException::new);

        Schedule schedule = appointment.getSchedule();

        return new AppointmentResponseDTO(
                appointment.getId(),
                mapToUserResponseDTO(appointment.getCustomer()),
                mapToProfessionalResponseDTO(appointment.getProfessional()),
                mapToServiceResponseDTO(appointment.getService()),
                mapToScheduleResponseDTO(schedule),
                appointment.getStatus());
    }

    @Transactional
    public List<AppointmentResponseDTO> findAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(appointment -> {
                    Schedule schedule = appointment.getSchedule();
                    return new AppointmentResponseDTO(
                            appointment.getId(),
                            mapToUserResponseDTO(appointment.getCustomer()),
                            mapToProfessionalResponseDTO(appointment.getProfessional()),
                            mapToServiceResponseDTO(appointment.getService()),
                            mapToScheduleResponseDTO(schedule),
                            appointment.getStatus());
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAppointment(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(AppointmentNotFoundException::new);
        appointmentRepository.delete(appointment);
    }

    @Transactional
    public AppointmentResponseDTO updateStatus(UUID appointmentId, String status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(AppointmentNotFoundException::new);

        appointment.setStatus(status);
        Appointment savedAppointment = appointmentRepository.save(appointment);

        Schedule schedule = savedAppointment.getSchedule();

        return new AppointmentResponseDTO(
                savedAppointment.getId(),
                mapToUserResponseDTO(savedAppointment.getCustomer()),
                mapToProfessionalResponseDTO(savedAppointment.getProfessional()),
                mapToServiceResponseDTO(savedAppointment.getService()),
                mapToScheduleResponseDTO(schedule),
                savedAppointment.getStatus());
    }

    @Transactional
    public List<AppointmentResponseDTO> findAppointmentsByProfessional(UUID professionalId) {
        return appointmentRepository.findByProfessionalId(professionalId).stream()
                .map(appointment -> new AppointmentResponseDTO(
                        appointment.getId(),
                        mapToUserResponseDTO(appointment.getCustomer()),
                        mapToProfessionalResponseDTO(appointment.getProfessional()),
                        mapToServiceResponseDTO(appointment.getService()),
                        mapToScheduleResponseDTO(appointment.getSchedule()),
                        appointment.getStatus()))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<AppointmentResponseDTO> findAppointmentsByCustomer(UUID customerId) {
        return appointmentRepository.findByCustomerId(customerId).stream()
                .map(appointment -> new AppointmentResponseDTO(
                        appointment.getId(),
                        mapToUserResponseDTO(appointment.getCustomer()),
                        mapToProfessionalResponseDTO(appointment.getProfessional()),
                        mapToServiceResponseDTO(appointment.getService()),
                        mapToScheduleResponseDTO(appointment.getSchedule()),
                        appointment.getStatus()))
                .collect(Collectors.toList());
    }

    private UserResponseDTO mapToUserResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAddresses().stream().map(address -> new AddressResponseDTO(
                        address.getStreet(),
                        address.getCity(),
                        address.getState(),
                        address.getCep()))
                        .collect(Collectors.toList()));
    }

    private ProfessionalResponseDTO mapToProfessionalResponseDTO(Professional professional) {
        return new ProfessionalResponseDTO(
                professional.getId(),
                professional.getUser().getName(),
                professional.getUser().getEmail(),
                professional.getUser().getPhoneNumber(),
                professional.getQualification(),
                professional.getAreaOfExpertise(),
                professional.getWorkStartTime(),
                professional.getWorkEndTime(),
                professional.getBreakDuration(),
                professional.getUser().getAddresses().stream().map(
                        address -> new AddressResponseDTO(
                                address.getStreet(),
                                address.getCity(),
                                address.getState(),
                                address.getCep()))
                        .collect(Collectors.toList()));
    }

    private ServiceResponseDTO mapToServiceResponseDTO(ServiceEntity service) {
        return new ServiceResponseDTO(service.getId(), service.getName(), service.getDescription(),
                service.getValue(),
                mapToProfessionalResponseDTO(service.getProfessional()));
    }

    private ScheduleResponseDTO mapToScheduleResponseDTO(Schedule schedule) {
        return new ScheduleResponseDTO(schedule.getId(), schedule.getProfessional().getId(), schedule.getDate(),
                schedule.getStartTime(), schedule.getEndTime());
    }
}