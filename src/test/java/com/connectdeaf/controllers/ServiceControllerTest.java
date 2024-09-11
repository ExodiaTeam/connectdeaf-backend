// package com.connectdeaf.controllers;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.endsWith;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// import com.connectdeaf.controllers.dtos.requests.ServiceRequestDTO;
// import com.connectdeaf.controllers.dtos.response.ServiceResponseDTO;
// import com.connectdeaf.controllers.dtos.response.ProfessionalResponseDTO;
// import com.connectdeaf.services.ServiceService;
// import com.fasterxml.jackson.databind.ObjectMapper;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;

// import java.util.Collections;
// import java.util.List;
// import java.util.UUID;

// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest(ServiceController.class)
// class ServiceControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private ObjectMapper objectMapper;

//     @MockBean
//     private ServiceService mockServiceService;

//     @Test
//     void testCreateService() throws Exception {
//         final UUID professionalId = UUID.fromString("86f03304-71a6-4596-b223-48667365c34f");

//         final ServiceRequestDTO serviceRequestDTO = new ServiceRequestDTO(
//                 "Web Development",
//                 "Developing modern web applications",
//                 500.00
//         );

//         final ProfessionalResponseDTO professionalResponseDTO = new ProfessionalResponseDTO(
//                 professionalId,
//                 "John Doe",
//                 "john.doe@example.com",
//                 "+1234567890",
//                 "Software Engineer",
//                 "Web Development"
//         );

//         final ServiceResponseDTO serviceResponseDTO = new ServiceResponseDTO(
//                 UUID.fromString("9e28442e-0258-485b-aeee-3a056f4e2357"),
//                 "Web Development",
//                 "Developing modern web applications",
//                 500.00,
//                 professionalResponseDTO
//         );

//         when(mockServiceService.createService(eq(professionalId), any(ServiceRequestDTO.class)))
//                 .thenReturn(serviceResponseDTO);

//         mockMvc.perform(post("/api/services")
//                         .param("professionalId", professionalId.toString())
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(serviceRequestDTO)))
//                 .andExpect(status().isCreated())
//                 .andExpect(content().json(objectMapper.writeValueAsString(serviceResponseDTO)))
//                 .andExpect(header().string("Location", endsWith("/api/services/" + serviceResponseDTO.id())));

//         verify(mockServiceService).createService(eq(professionalId), any(ServiceRequestDTO.class));
//     }

//     @Test
//     void testGetService() throws Exception {
//         final UUID serviceId = UUID.fromString("86f03304-71a6-4596-b223-48667365c34f");

//         final UUID professionalId = UUID.fromString("9e28442e-0258-485b-aeee-3a056f4e2357");

//         final ProfessionalResponseDTO professionalResponseDTO = new ProfessionalResponseDTO(
//                 professionalId,
//                 "John Doe",
//                 "john.doe@example.com",
//                 "+1234567890",
//                 "Software Engineer",
//                 "Web Development"
//         );

//         final ServiceResponseDTO serviceResponseDTO = new ServiceResponseDTO(
//                 serviceId, 
//                 "Consulting", 
//                 "Business consulting services", 
//                 1000.00, 
//                 professionalResponseDTO
//         );

//         when(mockServiceService.findServiceById(eq(serviceId))).thenReturn(serviceResponseDTO);

//         mockMvc.perform(get("/api/services/{service_id}", serviceId)
//                         .accept(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(content().json(objectMapper.writeValueAsString(serviceResponseDTO)));

//         verify(mockServiceService).findServiceById(eq(serviceId));
//     }

//     @Test
//     void testGetAllServices() throws Exception {
//         final ProfessionalResponseDTO professional1 = new ProfessionalResponseDTO(
//                 UUID.fromString("86f03304-71a6-4596-b223-48667365c34f"),
//                 "John Doe",
//                 "john.doe@example.com",
//                 "+1234567890",
//                 "Software Engineer",
//                 "Web Development"
//         );

//         final ProfessionalResponseDTO professional2 = new ProfessionalResponseDTO(
//                 UUID.fromString("9e28442e-0258-485b-aeee-3a056f4e2357"),
//                 "Alice Johnson",
//                 "alice.johnson@example.com",
//                 "+1122334455",
//                 "Business Consultant",
//                 "Business Strategy"
//         );

//         final List<ServiceResponseDTO> serviceResponseDTOList = List.of(
//                 new ServiceResponseDTO(UUID.fromString("91318e88-378a-4c56-bf95-2bcd8628c946"),
//                         "Graphic Design", "Creating visual content", 300.00, professional1),
//                 new ServiceResponseDTO(UUID.fromString("e181d3ae-7526-4fa8-bdd9-1f760f0f4f39"),
//                         "SEO Optimization", "Improving website ranking", 200.00, professional2)
//         );

//         when(mockServiceService.findAllServices()).thenReturn(serviceResponseDTOList);

//         mockMvc.perform(get("/api/services")
//                         .accept(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(content().json(objectMapper.writeValueAsString(serviceResponseDTOList)));

//         verify(mockServiceService).findAllServices();
//     }

//     @Test
//     void testGetAllServices_ServiceServiceReturnsNoItems() throws Exception {
//         when(mockServiceService.findAllServices()).thenReturn(Collections.emptyList());

//         mockMvc.perform(get("/api/services")
//                         .accept(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(content().json("[]"));

//         verify(mockServiceService).findAllServices();
//     }

//     @Test
//     void testDeleteService() throws Exception {
//         final UUID serviceId = UUID.fromString("e5614a68-8937-4321-b25b-a694d4303385");

//         mockMvc.perform(delete("/api/services/{service_id}", serviceId)
//                         .accept(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isNoContent());

//         verify(mockServiceService).deleteService(eq(serviceId));
//     }
// }