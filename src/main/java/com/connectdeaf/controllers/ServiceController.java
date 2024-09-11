package com.connectdeaf.controllers;

import java.util.List;
import java.util.UUID;
import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.connectdeaf.controllers.dtos.requests.ServiceRequestDTO;
import com.connectdeaf.controllers.dtos.response.ServiceResponseDTO;
import com.connectdeaf.domain.service.ServiceEntity;
import com.connectdeaf.services.ServiceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @PostMapping
    public ResponseEntity<ServiceResponseDTO> createService(
            @RequestParam UUID professionalId,
            @RequestBody @Valid ServiceRequestDTO serviceRequestDTO) {
        ServiceResponseDTO createdService = serviceService.createService(professionalId, serviceRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{service_id}")
                .buildAndExpand(createdService.id())
                .toUri();
        return ResponseEntity.created(location).body(createdService);
    }

    @GetMapping("/{service_id}")
    public ResponseEntity<ServiceResponseDTO> getService(@PathVariable("service_id") UUID serviceId) {
        ServiceResponseDTO service = serviceService.findServiceById(serviceId);
        return ResponseEntity.ok(service);
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponseDTO>> getAllServices() {
        List<ServiceResponseDTO> services = serviceService.findAllServices();
        return ResponseEntity.ok(services);
    }

    @DeleteMapping("/{service_id}")
    public ResponseEntity<Void> deleteService(@PathVariable("service_id") UUID serviceId) {
        serviceService.deleteService(serviceId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ServiceEntity>> findServices(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state) {
        List<ServiceEntity> services = serviceService.findServices(name, city, state);
        return ResponseEntity.ok(services);
    }
}
