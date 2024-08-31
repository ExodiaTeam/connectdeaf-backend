package com.connectdeaf.domain.appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.connectdeaf.domain.professional.Professional;
import com.connectdeaf.domain.service.ServiceEntity;
import com.connectdeaf.domain.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    
    @NotNull
    private LocalDate date;

    @NotNull
    private LocalTime time;

    @NotBlank
    private String status;
}
