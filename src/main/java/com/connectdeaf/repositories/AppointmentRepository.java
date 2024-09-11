package com.connectdeaf.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.connectdeaf.domain.appointment.Appointment;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByProfessionalId(UUID professionalId);

    List<Appointment> findByCustomerId(UUID customerId);
}