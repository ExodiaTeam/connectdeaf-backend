package com.connectdeaf.repository;

import com.connectdeaf.model.CustomerModel;
import com.connectdeaf.model.ImageModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<ImageModel, UUID> {
    Optional<ImageModel> findByCustomerModel(CustomerModel customer);
}
