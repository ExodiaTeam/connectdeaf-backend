package com.connectdeaf.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.connectdeaf.controllers.dtos.requests.AppointmentRequestDTO;
import com.connectdeaf.controllers.dtos.response.AppointmentResponseDTO;
import com.connectdeaf.controllers.dtos.response.ProfessionalResponseDTO;
import com.connectdeaf.controllers.dtos.response.ScheduleResponseDTO;
import com.connectdeaf.controllers.dtos.response.ServiceResponseDTO;
import com.connectdeaf.controllers.dtos.response.UserResponseDTO;
import com.connectdeaf.domain.appointment.Appointment;
import com.connectdeaf.domain.professional.Professional;
import com.connectdeaf.domain.schedule.Schedule;
import com.connectdeaf.domain.service.ServiceEntity;
import com.connectdeaf.domain.user.User;
import com.connectdeaf.exceptions.AppointmentNotFoundException;
import com.connectdeaf.exceptions.ProfessionalNotFoundException;
import com.connectdeaf.exceptions.ServiceNotFoundException;
import com.connectdeaf.exceptions.UserNotFoundException;
import com.connectdeaf.repositories.AppointmentRepository;
import com.connectdeaf.repositories.ProfessionalRepository;
import com.connectdeaf.repositories.ScheduleRepository;
import com.connectdeaf.repositories.ServiceRepository;
import com.connectdeaf.repositories.UserRepository;
import com.connectdeaf.controllers.dtos.response.AddressResponseDTO;

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
                                .orElseThrow(() -> new UserNotFoundException());

                Professional professional = professionalRepository.findById(appointmentRequestDTO.professionalId())
                                .orElseThrow(() -> new ProfessionalNotFoundException());

                ServiceEntity service = serviceRepository.findById(appointmentRequestDTO.serviceId())
                                .orElseThrow(() -> new ServiceNotFoundException());

                LocalDate date = appointmentRequestDTO.date();
                LocalTime startTime = appointmentRequestDTO.startTime();
                LocalTime endTime = appointmentRequestDTO.endTime();

                Schedule schedule = new Schedule(
                                null,
                                professional,
                                date,
                                startTime,
                                endTime);

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

        public AppointmentResponseDTO findAppointmentById(UUID appointmentId) {
                Appointment appointment = appointmentRepository.findById(appointmentId)
                                .orElseThrow(() -> new AppointmentNotFoundException());

                Schedule schedule = appointment.getSchedule(); // Obter o horário relacionado

                return new AppointmentResponseDTO(
                                appointment.getId(),
                                mapToUserResponseDTO(appointment.getCustomer()),
                                mapToProfessionalResponseDTO(appointment.getProfessional()),
                                mapToServiceResponseDTO(appointment.getService()),
                                mapToScheduleResponseDTO(schedule), // Passar o schedule corretamente
                                appointment.getStatus());
        }

        public List<AppointmentResponseDTO> findAllAppointments() {
                return appointmentRepository.findAll().stream()
                                .map(appointment -> {
                                        Schedule schedule = appointment.getSchedule(); // Obter o horário relacionado

                                        return new AppointmentResponseDTO(
                                                        appointment.getId(),
                                                        mapToUserResponseDTO(appointment.getCustomer()),
                                                        mapToProfessionalResponseDTO(appointment.getProfessional()),
                                                        mapToServiceResponseDTO(appointment.getService()),
                                                        mapToScheduleResponseDTO(schedule), // Passar o schedule
                                                                                            // corretamente
                                                        appointment.getStatus());
                                })
                                .collect(Collectors.toList());
        }

        public void deleteAppointment(UUID appointmentId) {
                Appointment appointment = appointmentRepository.findById(appointmentId)
                                .orElseThrow(() -> new AppointmentNotFoundException());
                appointmentRepository.delete(appointment);
        }

        public AppointmentResponseDTO updateStatus(UUID appointmentId, String status) {
                Appointment appointment = appointmentRepository.findById(appointmentId)
                                .orElseThrow(() -> new AppointmentNotFoundException());

                appointment.setStatus(status);
                Appointment savedAppointment = appointmentRepository.save(appointment);

                Schedule schedule = savedAppointment.getSchedule(); // Obter o horário relacionado

                return new AppointmentResponseDTO(
                                savedAppointment.getId(),
                                mapToUserResponseDTO(savedAppointment.getCustomer()),
                                mapToProfessionalResponseDTO(savedAppointment.getProfessional()),
                                mapToServiceResponseDTO(savedAppointment.getService()),
                                mapToScheduleResponseDTO(schedule), // Passar o schedule corretamente
                                savedAppointment.getStatus());
        }

        public List<AppointmentResponseDTO> findAppointmentsByProfessional(UUID professionalId) {
                // Implementação para buscar appointments por professionalId
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

        public List<AppointmentResponseDTO> findAppointmentsByCustomer(UUID customerId) {
                // Implementação para buscar appointments por customerId
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
                return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getPhoneNumber());
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
