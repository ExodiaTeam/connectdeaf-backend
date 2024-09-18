package com.connectdeaf.controllers;

import com.connectdeaf.controllers.dtos.requests.ProfessionalRequestDTO;
import com.connectdeaf.controllers.dtos.response.ProfessionalResponseDTO;
import com.connectdeaf.controllers.dtos.response.ScheduleResponseDTO;
import com.connectdeaf.services.ProfessionalService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/professionals")
@CrossOrigin(origins = "http://localhost:5173") 
public class ProfessionalController {
    private final ProfessionalService professionalService;

    public ProfessionalController(ProfessionalService professionalService) {
        this.professionalService = professionalService;
    }

    @PostMapping
    public ResponseEntity<ProfessionalResponseDTO> createProfessional(
            @Valid @RequestBody ProfessionalRequestDTO professionalRequestDTO) {
        ProfessionalResponseDTO professionalResponseDTO = professionalService
                .createProfessional(professionalRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(professionalResponseDTO);
    }

    @GetMapping("/{professional_id}")
    public ResponseEntity<ProfessionalResponseDTO> getProfessional(@PathVariable UUID professional_id) {
        ProfessionalResponseDTO professionalResponseDTO = professionalService.findById(professional_id);
        return ResponseEntity.ok(professionalResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ProfessionalResponseDTO>> getAllProfessionals() {
        List<ProfessionalResponseDTO> professionalResponseDTOList = professionalService.findAll();
        return ResponseEntity.ok(professionalResponseDTOList);
    }

    @PutMapping("/{professional_id}")
    public ResponseEntity<ProfessionalResponseDTO> updateProfessional(
            @PathVariable UUID professional_id,
            @Valid @RequestBody ProfessionalRequestDTO professionalRequestDTO) {
        ProfessionalResponseDTO professionalResponseDTO = professionalService.updateProfessional(professional_id,
                professionalRequestDTO);
        return ResponseEntity.ok(professionalResponseDTO);
    }

    @DeleteMapping("/{professional_id}")
    public ResponseEntity<Void> deleteProfessional(@PathVariable UUID professional_id) {
        professionalService.deleteProfessional(professional_id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{professionalId}/{date}")
    public ResponseEntity<List<ScheduleResponseDTO>> getSchedulesByProfessional(
            @PathVariable UUID professionalId,
            @Valid @PathVariable LocalDate date) {
        List<ScheduleResponseDTO> schedules = professionalService.getSchedulesByProfessionalAndDate(professionalId,
                date);
        return ResponseEntity.ok(schedules);
    }
}
