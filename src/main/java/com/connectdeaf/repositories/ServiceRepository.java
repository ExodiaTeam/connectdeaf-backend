package com.connectdeaf.repositories;

import com.connectdeaf.domain.service.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, UUID> {
    
    Optional<ServiceEntity> findById(UUID id);
    
    Optional<ServiceEntity> findByName(String name);
}
