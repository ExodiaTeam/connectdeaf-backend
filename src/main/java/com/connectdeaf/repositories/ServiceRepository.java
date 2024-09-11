package com.connectdeaf.repositories;

import com.connectdeaf.domain.service.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, UUID>, JpaSpecificationExecutor<ServiceEntity> {
    
    @NonNull
    Optional<ServiceEntity> findById(@NonNull UUID id);
    
    Optional<ServiceEntity> findByName(String name);
}
