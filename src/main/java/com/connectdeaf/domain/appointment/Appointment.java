package com.connectdeaf.domain.appointment;

import java.util.UUID;

import com.connectdeaf.domain.professional.Professional;
import com.connectdeaf.domain.schedule.Schedule;
import com.connectdeaf.domain.service.ServiceEntity;
import com.connectdeaf.domain.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TB_APPOINTMENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "professional_id", nullable = false)
    @JsonBackReference
    private Professional professional;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "service_id", nullable = false)
    @JsonBackReference
    private ServiceEntity service;

    // Adicionando uma referência a Schedule
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "schedule_id", nullable = true) // Alterado para true
    private Schedule schedule;

    @NotBlank
    private String status;  // Pendente, Aprovada, Recusada, etc.
}

