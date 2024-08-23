package com.connectdeaf.domain.address;

import com.connectdeaf.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "TB_ADDRESS")
@Getter
@Setter
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NotBlank private String cep;
    @NotBlank private String street;
    @NotBlank private String number;
    private String complement;
    @NotBlank private String neighborhood;
    @NotBlank private String city;
    @NotBlank private String state;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
