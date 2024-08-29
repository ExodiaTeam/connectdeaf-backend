package com.connectdeaf.services;

import com.connectdeaf.domain.service.ServiceEntity;
import com.connectdeaf.domain.professional.Professional;
import com.connectdeaf.repositories.ProfessionalRepository;
import com.connectdeaf.repositories.ServiceRepository;
import com.connectdeaf.controllers.dtos.requests.ServiceRequestDTO;
import com.connectdeaf.controllers.dtos.response.ServiceResponseDTO;
import com.connectdeaf.exceptions.ProfessionalNotFoundException;
import com.connectdeaf.exceptions.ServiceNotFoundException;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ProfessionalRepository professionalRepository;

    public ServiceService(ServiceRepository serviceRepository, ProfessionalRepository professionalRepository) {
        this.serviceRepository = serviceRepository;
        this.professionalRepository = professionalRepository;
    }

    @Transactional
    public ServiceResponseDTO createService(UUID professionalId, ServiceRequestDTO serviceRequestDTO) {

        Professional professional = professionalRepository.findById(professionalId)
            .orElseThrow(() -> new ProfessionalNotFoundException());

        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setName(serviceRequestDTO.name());
        serviceEntity.setDescription(serviceRequestDTO.description());
        serviceEntity.setValue(serviceRequestDTO.value());
        serviceEntity.setProfessional(professional);

        ServiceEntity savedService = serviceRepository.save(serviceEntity);

        return new ServiceResponseDTO(
            savedService.getId(),
            savedService.getName(),
            savedService.getDescription(),
            savedService.getValue(),
            savedService.getProfessional()
        );
    }

    public ServiceResponseDTO findServiceById(UUID serviceId) {
        ServiceEntity serviceEntity = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ServiceNotFoundException(serviceId));

        return new ServiceResponseDTO(
                serviceEntity.getId(),
                serviceEntity.getName(),
                serviceEntity.getDescription(),
                serviceEntity.getValue(),
                serviceEntity.getProfessional()
        );
    }

    public List<ServiceResponseDTO> findAllServices() {
        return serviceRepository.findAll().stream()
                .map(service -> new ServiceResponseDTO(
                        service.getId(),
                        service.getName(),
                        service.getDescription(),
                        service.getValue(),
                        service.getProfessional()
                ))
                .collect(Collectors.toList());
    }

    public void deleteService(UUID serviceId) {
        ServiceEntity serviceEntity = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ServiceNotFoundException(serviceId));
        serviceRepository.delete(serviceEntity);
    }
}

