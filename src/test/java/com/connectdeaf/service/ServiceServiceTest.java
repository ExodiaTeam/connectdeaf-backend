package com.connectdeaf.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.connectdeaf.controllers.dtos.requests.ServiceRequestDTO;
import com.connectdeaf.controllers.dtos.response.ServiceResponseDTO;
import com.connectdeaf.domain.professional.Professional;
import com.connectdeaf.domain.service.ServiceEntity;
import com.connectdeaf.domain.user.User;
import com.connectdeaf.exceptions.ProfessionalNotFoundException;
import com.connectdeaf.exceptions.ServiceNotFoundException;
import com.connectdeaf.repositories.ProfessionalRepository;
import com.connectdeaf.repositories.ServiceRepository;
import com.connectdeaf.services.ServiceService;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class ServiceServiceIntegrationTest {

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @BeforeEach
    void setUp() {
        serviceRepository.deleteAll();
        professionalRepository.deleteAll();
    }

    @Test
    void testCreateService() {
        final Professional professional = new Professional(
                UUID.randomUUID(),
                "Software Engineer",
                "Web Development",
                new User(UUID.randomUUID(), "John Doe", "john.doe@example.com", "password123", "+1234567890", null)
        );
        professionalRepository.save(professional);

        final ServiceRequestDTO serviceRequestDTO = new ServiceRequestDTO(
                "Web Development Service",
                "Creating a full-stack web application",
                1500.00
        );

        ServiceResponseDTO createdService = serviceService.createService(professional.getId(), serviceRequestDTO);

        assertThat(createdService).isNotNull();
        assertThat(createdService.name()).isEqualTo("Web Development Service");
        assertThat(createdService.description()).isEqualTo("Creating a full-stack web application");
        assertThat(createdService.value()).isEqualTo(1500.00);
        assertThat(createdService.professional()).isEqualTo(professional);

        assertThat(serviceRepository.findById(createdService.id())).isPresent();
    }

    @Test
    void testCreateService_ProfessionalNotFound() {
        final UUID nonExistentProfessionalId = UUID.randomUUID();

        final ServiceRequestDTO serviceRequestDTO = new ServiceRequestDTO(
                "Web Development Service",
                "Creating a full-stack web application",
                1500.00
        );

        assertThrows(ProfessionalNotFoundException.class, () -> serviceService.createService(nonExistentProfessionalId, serviceRequestDTO));
    }

    @Test
    void testFindServiceById() {
        final Professional professional = new Professional(
                UUID.randomUUID(),
                "Software Engineer",
                "Web Development",
                new User(UUID.randomUUID(), "Alice Johnson", "alice.johnson@example.com", "passwordAlice", "+1122334455", null)
        );
        professionalRepository.save(professional);

        final ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setName("Consultancy Service");
        serviceEntity.setDescription("Consulting on software architecture");
        serviceEntity.setValue(2000.00);
        serviceEntity.setProfessional(professional);
        serviceRepository.save(serviceEntity);

        ServiceResponseDTO foundService = serviceService.findServiceById(serviceEntity.getId());

        assertThat(foundService).isNotNull();
        assertThat(foundService.name()).isEqualTo("Consultancy Service");
        assertThat(foundService.description()).isEqualTo("Consulting on software architecture");
        assertThat(foundService.value()).isEqualTo(2000.00);
        assertThat(foundService.professional()).isEqualTo(professional);
    }

    @Test
    void testFindServiceById_ServiceNotFound() {
        final UUID nonExistentServiceId = UUID.randomUUID();

        assertThrows(ServiceNotFoundException.class, () -> serviceService.findServiceById(nonExistentServiceId));
    }

    @Test
    void testFindAllServices() {
        final Professional professional1 = new Professional(
                UUID.randomUUID(),
                "Software Engineer",
                "Web Development",
                new User(UUID.randomUUID(), "John Doe", "john.doe@example.com", "password123", "+1234567890", null)
        );
        professionalRepository.save(professional1);

        final Professional professional2 = new Professional(
                UUID.randomUUID(),
                "Consultant",
                "Business Strategy",
                new User(UUID.randomUUID(), "Alice Johnson", "alice.johnson@example.com", "passwordAlice", "+1122334455", null)
        );
        professionalRepository.save(professional2);

        final ServiceEntity serviceEntity1 = new ServiceEntity();
        serviceEntity1.setName("Web Development Service");
        serviceEntity1.setDescription("Creating a full-stack web application");
        serviceEntity1.setValue(1500.00);
        serviceEntity1.setProfessional(professional1);

        final ServiceEntity serviceEntity2 = new ServiceEntity();
        serviceEntity2.setName("Consultancy Service");
        serviceEntity2.setDescription("Consulting on business strategy");
        serviceEntity2.setValue(2500.00);
        serviceEntity2.setProfessional(professional2);

        serviceRepository.saveAll(List.of(serviceEntity1, serviceEntity2));

        List<ServiceResponseDTO> services = serviceService.findAllServices();

        assertThat(services).hasSize(2);
        assertThat(services.get(0).name()).isEqualTo("Web Development Service");
        assertThat(services.get(1).name()).isEqualTo("Consultancy Service");
    }

    @Test
    void testDeleteService() {
        final Professional professional = new Professional(
                UUID.randomUUID(),
                "Software Engineer",
                "Web Development",
                new User(UUID.randomUUID(), "Bob Brown", "bob.brown@example.com", "passwordBob", "+9988776655", null)
        );
        professionalRepository.save(professional);

        final ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setName("Maintenance Service");
        serviceEntity.setDescription("Maintaining software systems");
        serviceEntity.setValue(1000.00);
        serviceEntity.setProfessional(professional);
        serviceRepository.save(serviceEntity);

        serviceService.deleteService(serviceEntity.getId());

        assertThat(serviceRepository.findById(serviceEntity.getId())).isNotPresent();
    }
}

