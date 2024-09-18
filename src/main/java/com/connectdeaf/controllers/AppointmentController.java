package com.connectdeaf.controllers;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.connectdeaf.controllers.dtos.requests.AppointmentRequestDTO;
import com.connectdeaf.controllers.dtos.response.AppointmentResponseDTO;
import com.connectdeaf.services.AppointmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "http://localhost:5173") 
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(
            @RequestBody @Valid AppointmentRequestDTO appointmentRequestDTO) {
        AppointmentResponseDTO createdAppointment = appointmentService.createAppointment(appointmentRequestDTO);
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

    @PatchMapping("/{appointment_id}/approve")
    public ResponseEntity<AppointmentResponseDTO> approveAppointment(
            @PathVariable("appointment_id") UUID appointmentId) {
        AppointmentResponseDTO updatedAppointment = appointmentService.updateStatus(appointmentId, "APPROVED");
        return ResponseEntity.ok(updatedAppointment);
    }

    @PatchMapping("/{appointment_id}/reject")
    public ResponseEntity<AppointmentResponseDTO> rejectAppointment(
            @PathVariable("appointment_id") UUID appointmentId) {
        AppointmentResponseDTO updatedAppointment = appointmentService.updateStatus(appointmentId, "REJECTED");
        return ResponseEntity.ok(updatedAppointment);
    }

    @PatchMapping("/{appointment_id}/cancel")
    public ResponseEntity<AppointmentResponseDTO> cancelAppointment(
            @PathVariable("appointment_id") UUID appointmentId) {
        AppointmentResponseDTO updatedAppointment = appointmentService.updateStatus(appointmentId, "CANCELLED");
        return ResponseEntity.ok(updatedAppointment);
    }

    @PatchMapping("/{appointment_id}/finish")
    public ResponseEntity<AppointmentResponseDTO> finishAppointment(
            @PathVariable("appointment_id") UUID appointmentId) {
        AppointmentResponseDTO updatedAppointment = appointmentService.updateStatus(appointmentId, "FINISHED");
        return ResponseEntity.ok(updatedAppointment);
    }

    @GetMapping("/professional/{professional_id}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByProfessional(
            @PathVariable("professional_id") UUID professionalId) {
        List<AppointmentResponseDTO> appointments = appointmentService.findAppointmentsByProfessional(professionalId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/customer/{customer_id}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByCustomer(
            @PathVariable("customer_id") UUID customerId) {
        List<AppointmentResponseDTO> appointments = appointmentService.findAppointmentsByCustomer(customerId);
        return ResponseEntity.ok(appointments);
    }
}

