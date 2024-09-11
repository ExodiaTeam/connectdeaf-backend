// package com.connectdeaf.services;

// import com.connectdeaf.controllers.dtos.requests.ServiceRequestDTO;
// import com.connectdeaf.controllers.dtos.response.ProfessionalResponseDTO;
// import com.connectdeaf.controllers.dtos.response.ServiceResponseDTO;
// import com.connectdeaf.domain.professional.Professional;
// import com.connectdeaf.domain.service.ServiceEntity;
// import com.connectdeaf.domain.user.User;
// import com.connectdeaf.exceptions.ProfessionalNotFoundException;
// import com.connectdeaf.exceptions.ServiceNotFoundException;
// import com.connectdeaf.repositories.ProfessionalRepository;
// import com.connectdeaf.repositories.ServiceRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.util.Collections;
// import java.util.List;
// import java.util.Optional;
// import java.util.UUID;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.assertj.core.api.Assertions.assertThatThrownBy;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// @ExtendWith(MockitoExtension.class)
// class ServiceServiceTest {

//     @Mock
//     private ServiceRepository mockServiceRepository;
//     @Mock
//     private ProfessionalRepository mockProfessionalRepository;

//     private ServiceService serviceServiceUnderTest;

//     @BeforeEach
//     void setUp() {
//         serviceServiceUnderTest = new ServiceService(mockServiceRepository, mockProfessionalRepository);
//     }

//     @Test
//     void testCreateService() {
//         // Setup
//         final ServiceRequestDTO serviceRequestDTO = new ServiceRequestDTO("Plumbing Service", "High-quality plumbing service", 150.0);
//         final ServiceResponseDTO expectedResult = new ServiceResponseDTO(
//                 UUID.fromString("8432ef02-f003-4efa-8f18-c30fe09001e6"), // ID real esperado
//                 "Plumbing Service",
//                 "High-quality plumbing service",
//                 150.0,
//                 new ProfessionalResponseDTO(
//                         UUID.fromString("d532f5a4-c099-43b7-9fd1-11c19669bacf"), // ID real do profissional
//                         "John Doe",
//                         "john.doe@example.com",
//                         "+1234567890",
//                         "Certified Plumber",
//                         "Residential and Commercial Plumbing"
//                 )
//         );

//         // Configure ProfessionalRepository.findById(...).
//         final Professional professional = new Professional();
//         professional.setId(UUID.fromString("d532f5a4-c099-43b7-9fd1-11c19669bacf")); // ID real do profissional
//         professional.setQualification("Certified Plumber");
//         professional.setAreaOfExpertise("Residential and Commercial Plumbing");
//         final User user = new User();
//         user.setName("John Doe");
//         user.setEmail("john.doe@example.com");
//         user.setPhoneNumber("+1234567890");
//         professional.setUser(user);
//         final Optional<Professional> optionalProfessional = Optional.of(professional);
//         when(mockProfessionalRepository.findById(UUID.fromString("d532f5a4-c099-43b7-9fd1-11c19669bacf")))
//                 .thenReturn(optionalProfessional);

//         // Configure ServiceRepository.save(...).
//         final ServiceEntity serviceEntity = new ServiceEntity();
//         serviceEntity.setId(UUID.fromString("8432ef02-f003-4efa-8f18-c30fe09001e6")); // ID real
//         final Professional serviceProfessional = new Professional();
//         serviceProfessional.setId(UUID.fromString("d532f5a4-c099-43b7-9fd1-11c19669bacf")); // ID real do profissional
//         serviceProfessional.setQualification("Certified Plumber");
//         serviceProfessional.setAreaOfExpertise("Residential and Commercial Plumbing");
//         final User serviceUser = new User();
//         serviceUser.setName("John Doe");
//         serviceUser.setEmail("john.doe@example.com");
//         serviceUser.setPhoneNumber("+1234567890");
//         serviceProfessional.setUser(serviceUser);
//         serviceEntity.setProfessional(serviceProfessional);
//         serviceEntity.setValue(150.0);
//         serviceEntity.setName("Plumbing Service");
//         serviceEntity.setDescription("High-quality plumbing service");
//         when(mockServiceRepository.save(any(ServiceEntity.class))).thenReturn(serviceEntity);

//         // Run the test
//         final ServiceResponseDTO result = serviceServiceUnderTest.createService(
//                 UUID.fromString("d532f5a4-c099-43b7-9fd1-11c19669bacf"), // ID real do profissional
//                 serviceRequestDTO
//         );

//         // Verify the results
//         assertThat(result).isEqualTo(expectedResult);
//     }


//     @Test
//     void testCreateService_ProfessionalRepositoryReturnsAbsent() {
//         // Setup
//         final ServiceRequestDTO serviceRequestDTO = new ServiceRequestDTO("Plumbing Service", "Top-notch plumbing service", 150.0);
//         // Simular que o profissional não é encontrado
//         when(mockProfessionalRepository.findById(UUID.fromString("08019119-90bb-455d-ad7c-239d7baaf10c")))
//                 .thenReturn(Optional.empty());

//         // Run the test
//         assertThatThrownBy(
//                 () -> serviceServiceUnderTest.createService(UUID.fromString("08019119-90bb-455d-ad7c-239d7baaf10c"), serviceRequestDTO))
//                 .isInstanceOf(ProfessionalNotFoundException.class);
//     }


//     @Test
//     void testFindServiceById() {
//         // Setup
//         final ServiceResponseDTO expectedResult = new ServiceResponseDTO(
//                 UUID.fromString("6de17c7b-5b29-4bd6-9466-cb1f6fd6bba3"), // ID real esperado
//                 "Plumbing Service",
//                 "Top-notch plumbing service",
//                 150.0,
//                 new ProfessionalResponseDTO(
//                         UUID.fromString("b610ac7f-8450-4b89-91c6-c759245fd1ef"), // ID real do profissional
//                         "Alice Johnson",
//                         "alice.johnson@example.com",
//                         "+1987654321",
//                         "Master Plumber",
//                         "Residential and Commercial Plumbing"
//                 )
//         );

//         // Configure ServiceRepository.findById(...).
//         final ServiceEntity serviceEntity = new ServiceEntity();
//         serviceEntity.setId(UUID.fromString("6de17c7b-5b29-4bd6-9466-cb1f6fd6bba3")); // ID real

//         final Professional professional = new Professional();
//         professional.setId(UUID.fromString("b610ac7f-8450-4b89-91c6-c759245fd1ef")); // ID real do profissional
//         professional.setQualification("Master Plumber");
//         professional.setAreaOfExpertise("Residential and Commercial Plumbing");

//         final User user = new User();
//         user.setName("Alice Johnson");
//         user.setEmail("alice.johnson@example.com");
//         user.setPhoneNumber("+1987654321");
//         professional.setUser(user);
//         serviceEntity.setProfessional(professional);
//         serviceEntity.setValue(150.0);
//         serviceEntity.setName("Plumbing Service");
//         serviceEntity.setDescription("Top-notch plumbing service");

//         final Optional<ServiceEntity> serviceEntityOptional = Optional.of(serviceEntity);
//         when(mockServiceRepository.findById(UUID.fromString("6de17c7b-5b29-4bd6-9466-cb1f6fd6bba3")))
//                 .thenReturn(serviceEntityOptional);

//         // Run the test
//         final ServiceResponseDTO result = serviceServiceUnderTest.findServiceById(
//                 UUID.fromString("6de17c7b-5b29-4bd6-9466-cb1f6fd6bba3")); // ID real

//         // Verify the results
//         assertThat(result).isEqualTo(expectedResult);
//     }


//     @Test
//     void testFindServiceById_ServiceRepositoryReturnsAbsent() {
//         // Configuração do mock
//         when(mockServiceRepository.findById(UUID.fromString("d10e9f12-86be-4d43-a1a5-d88b8d1e6797")))
//                 .thenReturn(Optional.empty());

//         // Execução e verificação
//         assertThatThrownBy(() -> serviceServiceUnderTest.findServiceById(
//                 UUID.fromString("d10e9f12-86be-4d43-a1a5-d88b8d1e6797")))
//                 .isInstanceOf(ServiceNotFoundException.class);
//     }



//     @Test
//     void testFindAllServices() {
//         // Setup
//         final List<ServiceResponseDTO> expectedResult = List.of(
//                 new ServiceResponseDTO(
//                         UUID.fromString("a7fef3b0-5b1e-4d2b-89c4-b6cf0a1d7c45"), // ID real esperado
//                         "Home Cleaning",
//                         "Professional home cleaning service",
//                         100.0,
//                         new ProfessionalResponseDTO(
//                                 UUID.fromString("b7d6f9b1-2d83-4a39-9b84-8b9b65b689c1"), // ID real do profissional
//                                 "Emma Brown",
//                                 "emma.brown@example.com",
//                                 "+12015551234",
//                                 "Cleaning Specialist",
//                                 "Residential Cleaning"
//                         )
//                 ),
//                 new ServiceResponseDTO(
//                         UUID.fromString("e9c9e61e-6a74-4bb9-8e8a-c1f3e9a4e0f1"), // ID real esperado
//                         "Gardening",
//                         "Expert gardening and landscaping services",
//                         150.0,
//                         new ProfessionalResponseDTO(
//                                 UUID.fromString("c4e9d759-6d91-44f5-bd44-3f4906b4c6c0"), // ID real do profissional
//                                 "John Smith",
//                                 "john.smith@example.com",
//                                 "+19876543210",
//                                 "Landscaper",
//                                 "Gardening and Landscaping"
//                         )
//                 )
//         );

//         // Configure ServiceRepository.findAll(...).
//         final ServiceEntity serviceEntity1 = new ServiceEntity();
//         serviceEntity1.setId(UUID.fromString("a7fef3b0-5b1e-4d2b-89c4-b6cf0a1d7c45"));
//         serviceEntity1.setName("Home Cleaning");
//         serviceEntity1.setDescription("Professional home cleaning service");
//         serviceEntity1.setValue(100.0);

//         final Professional professional1 = new Professional();
//         professional1.setId(UUID.fromString("b7d6f9b1-2d83-4a39-9b84-8b9b65b689c1"));
//         professional1.setQualification("Cleaning Specialist");
//         professional1.setAreaOfExpertise("Residential Cleaning");

//         final User user1 = new User();
//         user1.setName("Emma Brown");
//         user1.setEmail("emma.brown@example.com");
//         user1.setPhoneNumber("+12015551234");
//         professional1.setUser(user1);
//         serviceEntity1.setProfessional(professional1);

//         final ServiceEntity serviceEntity2 = new ServiceEntity();
//         serviceEntity2.setId(UUID.fromString("e9c9e61e-6a74-4bb9-8e8a-c1f3e9a4e0f1"));
//         serviceEntity2.setName("Gardening");
//         serviceEntity2.setDescription("Expert gardening and landscaping services");
//         serviceEntity2.setValue(150.0);

//         final Professional professional2 = new Professional();
//         professional2.setId(UUID.fromString("c4e9d759-6d91-44f5-bd44-3f4906b4c6c0"));
//         professional2.setQualification("Landscaper");
//         professional2.setAreaOfExpertise("Gardening and Landscaping");

//         final User user2 = new User();
//         user2.setName("John Smith");
//         user2.setEmail("john.smith@example.com");
//         user2.setPhoneNumber("+19876543210");
//         professional2.setUser(user2);
//         serviceEntity2.setProfessional(professional2);

//         final List<ServiceEntity> serviceEntities = List.of(serviceEntity1, serviceEntity2);
//         when(mockServiceRepository.findAll()).thenReturn(serviceEntities);

//         // Run the test
//         final List<ServiceResponseDTO> result = serviceServiceUnderTest.findAllServices();

//         // Verify the results
//         assertThat(result).isEqualTo(expectedResult);
//     }


//     @Test
//     void testFindAllServices_ServiceRepositoryReturnsNoItems() {
//         // Setup
//         when(mockServiceRepository.findAll()).thenReturn(Collections.emptyList());

//         // Run the test
//         final List<ServiceResponseDTO> result = serviceServiceUnderTest.findAllServices();

//         // Verify the results
//         assertThat(result).isEqualTo(Collections.emptyList());
//     }


//     @Test
//     void testDeleteService() {
//         // Setup
//         // Configure ServiceRepository.findById(...).
//         final ServiceEntity serviceEntity1 = new ServiceEntity();
//         serviceEntity1.setId(UUID.fromString("6de17c7b-5b29-4bd6-9466-cb1f6fd6bba3"));
//         final Professional professional = new Professional();
//         professional.setId(UUID.fromString("b610ac7f-8450-4b89-91c6-c759245fd1ef"));
//         professional.setQualification("qualification");
//         professional.setAreaOfExpertise("areaOfExpertise");
//         final User user = new User();
//         user.setName("name");
//         user.setEmail("email");
//         user.setPhoneNumber("phoneNumber");
//         professional.setUser(user);
//         serviceEntity1.setProfessional(professional);
//         serviceEntity1.setValue(0.0);
//         serviceEntity1.setName("name");
//         serviceEntity1.setDescription("description");
//         final Optional<ServiceEntity> serviceEntity = Optional.of(serviceEntity1);
//         when(mockServiceRepository.findById(UUID.fromString("34341e37-47b1-4f0e-bffe-7158164578b6")))
//                 .thenReturn(serviceEntity);

//         // Run the test
//         serviceServiceUnderTest.deleteService(UUID.fromString("34341e37-47b1-4f0e-bffe-7158164578b6"));

//         // Verify the results
//         verify(mockServiceRepository).delete(any(ServiceEntity.class));
//     }

//     @Test
//     void testDeleteService_ServiceRepositoryFindByIdReturnsAbsent() {
//         // Setup
//         when(mockServiceRepository.findById(UUID.fromString("34341e37-47b1-4f0e-bffe-7158164578b6")))
//                 .thenReturn(Optional.empty());

//         // Run the test
//         assertThatThrownBy(() -> serviceServiceUnderTest.deleteService(
//                 UUID.fromString("34341e37-47b1-4f0e-bffe-7158164578b6"))).isInstanceOf(ServiceNotFoundException.class);
//     }
// }
