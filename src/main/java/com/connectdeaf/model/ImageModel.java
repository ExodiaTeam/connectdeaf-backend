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

    @NotBlank(message = "URL da imagem não pode estar em branco")
    private String imageUrl;

    @OneToOne(mappedBy = "imageModel", fetch = FetchType.LAZY)
    private CustomerModel customerModel;
}
