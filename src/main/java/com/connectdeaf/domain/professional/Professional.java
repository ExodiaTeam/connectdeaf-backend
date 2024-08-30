package com.connectdeaf.domain.professional;

import com.connectdeaf.domain.appointment.Appointment;
import com.connectdeaf.domain.user.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

import java.util.List;

@Entity
@Table(name = "TB_PROFESSIONAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Professional {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    private String qualification;

    @NotBlank
    private String areaOfExpertise;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "professional", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Appointment> appointments;
}
