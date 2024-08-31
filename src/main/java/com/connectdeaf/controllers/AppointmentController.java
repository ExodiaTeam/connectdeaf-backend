package com.connectdeaf.controllers;

import java.util.List;
import java.util.UUID;
import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.connectdeaf.controllers.dtos.requests.AppointmentRequestDTO;
import com.connectdeaf.controllers.dtos.response.AppointmentResponseDTO;
import com.connectdeaf.services.AppointmentService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(
            @RequestParam UUID customerId, 
            @RequestParam UUID professionalId, 
            @RequestParam UUID serviceId, 
            @RequestBody @Valid AppointmentRequestDTO appointmentRequestDTO) {
        AppointmentResponseDTO createdAppointment = appointmentService.createAppointment(customerId, professionalId, serviceId, appointmentRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{appointment_id}")
                .buildAndExpand(createdAppointment.id())
                .toUri();
        return ResponseEntity.created(location).body(createdAppointment);
    }

    @GetMapping("/{appointment_id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointment(@PathVariable("appointment_id") UUID appointmentId) {
        AppointmentResponseDTO appointment = appointmentService.findAppointmentById(appointmentId);
        return ResponseEntity.ok(appointment);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        List<AppointmentResponseDTO> appointments = appointmentService.findAllAppointments();
        return ResponseEntity.ok(appointments);
    }

    @DeleteMapping("/{appointment_id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable("appointment_id") UUID appointmentId) {
        appointmentService.deleteAppointment(appointmentId);
        return ResponseEntity.noContent().build();
    }
}

