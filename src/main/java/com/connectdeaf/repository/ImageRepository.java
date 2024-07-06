package com.connectdeaf.repository;

import com.connectdeaf.model.ImageModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageRepository extends JpaRepository<ImageModel, UUID> {
}
