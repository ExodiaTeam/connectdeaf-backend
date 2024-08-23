package com.connectdeaf.service;

import com.connectdeaf.controllers.dtos.requests.ProfessionalRequestDTO;
import com.connectdeaf.controllers.dtos.requests.UserRequestDTO;
import com.connectdeaf.controllers.dtos.response.ProfessionalResponseDTO;
import com.connectdeaf.controllers.dtos.response.UserResponseDTO;
import com.connectdeaf.domain.professional.Professional;
import com.connectdeaf.domain.user.User;
import com.connectdeaf.exceptions.UserNotFoundException;
import com.connectdeaf.repository.ProfessionalRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class ProfessionalService {
    private final ProfessionalRepository professionalRepository;
    private final UserService userService;

    public ProfessionalService(ProfessionalRepository professionalRepository, UserService userService) {
        this.professionalRepository = professionalRepository;
        this.userService = userService;
    }


    @Transactional
    public ProfessionalResponseDTO createProfessional(ProfessionalRequestDTO professionalRequestDTO) {
        // userService.findByEmail(professionalRequestDTO.email());

        UserRequestDTO userRequestDTO = new UserRequestDTO(
                professionalRequestDTO.name(),
                professionalRequestDTO.email(),
                professionalRequestDTO.password(),
                professionalRequestDTO.phoneNumber(),
                professionalRequestDTO.addresses()
        );

        UserResponseDTO userResponseDTO = userService.createUser(userRequestDTO);

        User user = userService.findById(userResponseDTO.id());

        Professional newProfessional = new Professional();
        newProfessional.setUser(user);
        newProfessional.setQualification(professionalRequestDTO.qualification());
        newProfessional.setAreaOfExpertise(professionalRequestDTO.areaOfExpertise());

        Professional savedProfessional = professionalRepository.save(newProfessional);

        return new ProfessionalResponseDTO(
                savedProfessional.getId(),
                userResponseDTO.name(),
                userResponseDTO.email(),
                userResponseDTO.phoneNumber(),
                savedProfessional.getQualification(),
                savedProfessional.getAreaOfExpertise()
        );
    }

    public ProfessionalResponseDTO findById(UUID professionalId) {
        Professional professional = professionalRepository.findById(professionalId)
                .orElseThrow(() -> new UserNotFoundException(professionalId));

        User user = professional.getUser();

        return new ProfessionalResponseDTO(
                professional.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                professional.getQualification(),
                professional.getAreaOfExpertise()
        );
    }

    @Transactional
    public ProfessionalResponseDTO updateProfessional(UUID professionalId, @Valid ProfessionalRequestDTO professionalRequestDTO) {
        Professional professional = professionalRepository.findById(professionalId)
                .orElseThrow(() -> new UserNotFoundException(professionalId));

        User user = professional.getUser();

        UserRequestDTO userRequestDTO = new UserRequestDTO(
                professionalRequestDTO.name(),
                professionalRequestDTO.email(),
                user.getPassword(),
                professionalRequestDTO.phoneNumber(),
                professionalRequestDTO.addresses()
        );

        userService.updateUser(user.getId(), userRequestDTO);

        if (professionalRequestDTO.qualification() != null) {
            professional.setQualification(professionalRequestDTO.qualification());
        }
        if (professionalRequestDTO.areaOfExpertise() != null) {
            professional.setAreaOfExpertise(professionalRequestDTO.areaOfExpertise());
        }

        Professional updatedProfessional = professionalRepository.save(professional);

        return new ProfessionalResponseDTO(
                updatedProfessional.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                updatedProfessional.getQualification(),
                updatedProfessional.getAreaOfExpertise()
        );
    }

    private ProfessionalResponseDTO createProfessionalResponseDTO(Professional professional) {
        User user = professional.getUser();
        return new ProfessionalResponseDTO(
                professional.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                professional.getQualification(),
                professional.getAreaOfExpertise()
        );
    }

    public List<ProfessionalResponseDTO> findAll() {
        return professionalRepository.findAll()
                .stream()
                .map(this::createProfessionalResponseDTO)
                .toList();
    }

    public void deleteProfessional(UUID professionalId) {
        Professional professional = professionalRepository.findById(professionalId)
                .orElseThrow(() -> new UserNotFoundException(professionalId));

        professionalRepository.delete(professional);
    }
}