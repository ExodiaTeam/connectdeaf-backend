package com.connectdeaf.services;

import com.connectdeaf.domain.service.ServiceEntity;
import com.connectdeaf.domain.professional.Professional;
import com.connectdeaf.repositories.ProfessionalRepository;
import com.connectdeaf.repositories.ServiceRepository;
import com.connectdeaf.controllers.dtos.requests.ServiceRequestDTO;
import com.connectdeaf.controllers.dtos.response.ServiceResponseDTO;
import com.connectdeaf.controllers.dtos.response.ProfessionalResponseDTO;
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
            mapToProfessionalResponseDTO(savedService.getProfessional())
        );
    }

    public ServiceResponseDTO findServiceById(UUID serviceId) {
        ServiceEntity serviceEntity = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ServiceNotFoundException());

        return new ServiceResponseDTO(
                serviceEntity.getId(),
                serviceEntity.getName(),
                serviceEntity.getDescription(),
                serviceEntity.getValue(),
                mapToProfessionalResponseDTO(serviceEntity.getProfessional())
        );
    }

    public List<ServiceResponseDTO> findAllServices() {
        return serviceRepository.findAll().stream()
                .map(service -> new ServiceResponseDTO(
                        service.getId(),
                        service.getName(),
                        service.getDescription(),
                        service.getValue(),
                        mapToProfessionalResponseDTO(service.getProfessional())
                ))
                .collect(Collectors.toList());
    }

    public void deleteService(UUID serviceId) {
        ServiceEntity serviceEntity = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ServiceNotFoundException());
        serviceRepository.delete(serviceEntity);
    }

    private ProfessionalResponseDTO mapToProfessionalResponseDTO(Professional professional) {
        return new ProfessionalResponseDTO(
            professional.getId(),
            professional.getUser().getName(),
            professional.getUser().getEmail(),
            professional.getUser().getPhoneNumber(),
            professional.getQualification(),
            professional.getAreaOfExpertise(),
            professional.getWorkStartTime(),
            professional.getWorkEndTime(),
            professional.getBreakDuration()
        );
    }
}
