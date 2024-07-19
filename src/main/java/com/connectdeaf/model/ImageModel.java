package com.connectdeaf.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "TB_IMAGES")
public class ImageModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID imageId;

    @NotBlank
    private String imageUrl;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_user" )
    private CustomerModel customerModel;
}
