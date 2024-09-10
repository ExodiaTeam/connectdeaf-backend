package com.connectdeaf.services;

import com.connectdeaf.controllers.dtos.requests.ProfessionalRequestDTO;
import com.connectdeaf.controllers.dtos.requests.UserRequestDTO;
import com.connectdeaf.controllers.dtos.response.ProfessionalResponseDTO;
import com.connectdeaf.controllers.dtos.response.ScheduleResponseDTO;
import com.connectdeaf.controllers.dtos.response.UserResponseDTO;
import com.connectdeaf.domain.professional.Professional;
import com.connectdeaf.domain.schedule.Schedule;
import com.connectdeaf.domain.user.User;
import com.connectdeaf.exceptions.ProfessionalNotFoundException;
import com.connectdeaf.repositories.ProfessionalRepository;
import com.connectdeaf.repositories.ScheduleRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ProfessionalService {
    private final ProfessionalRepository professionalRepository;
    private final UserService userService;
    private final ScheduleRepository scheduleRepository;

    public ProfessionalService(ProfessionalRepository professionalRepository, UserService userService, ScheduleRepository scheduleRepository) {
        this.professionalRepository = professionalRepository;
        this.userService = userService;
        this.scheduleRepository = scheduleRepository;
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
    
        UserResponseDTO userResponseDTO = userService.createUser(userRequestDTO, true);
        User user = userService.findById(userResponseDTO.id());
    
        Professional newProfessional = new Professional(
                null,
                professionalRequestDTO.qualification(),
                professionalRequestDTO.areaOfExpertise(),
                user,
                null,
                professionalRequestDTO.workStartTime(),
                professionalRequestDTO.workEndTime(),
                professionalRequestDTO.breakDuration()
        );
    
        Professional savedProfessional = professionalRepository.save(newProfessional);
    
        // Gerar e salvar horários na tabela SCHEDULE
        generateAndSaveSchedule(savedProfessional);
    
        return createProfessionalResponseDTO(savedProfessional);
    }

    public ProfessionalResponseDTO findById(UUID professionalId) {
        Professional professional = professionalRepository.findById(professionalId)
                .orElseThrow(() -> new ProfessionalNotFoundException());

        return createProfessionalResponseDTO(professional);
    }

    @Transactional
    public ProfessionalResponseDTO updateProfessional(UUID professionalId, @Valid ProfessionalRequestDTO professionalRequestDTO) {
        Professional professional = professionalRepository.findById(professionalId)
                .orElseThrow(() -> new ProfessionalNotFoundException());

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

        return createProfessionalResponseDTO(updatedProfessional);
    }

    private ProfessionalResponseDTO createProfessionalResponseDTO(Professional professional) {
        User user = professional.getUser();
        return new ProfessionalResponseDTO(
                professional.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                professional.getQualification(),
                professional.getAreaOfExpertise(),
                professional.getWorkStartTime(),
                professional.getWorkEndTime(),
                professional.getBreakDuration()
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
                .orElseThrow(() -> new ProfessionalNotFoundException());

        professionalRepository.delete(professional);
    }

    private void generateAndSaveSchedule(Professional professional) {
        LocalTime workStartTime = professional.getWorkStartTime();
        LocalTime workEndTime = professional.getWorkEndTime();
        Duration breakDuration = professional.getBreakDuration();

        LocalTime currentTime = workStartTime;

        while (currentTime.isBefore(workEndTime)) {
            // Crie um horário disponível
            LocalTime nextTime = currentTime.plusMinutes(breakDuration.toMinutes());

            // Verifique se o próximo horário não ultrapassa o final do expediente
            if (nextTime.isAfter(workEndTime)) {
                break;
            }

            // Salvar horário disponível
            Schedule newSchedule = new Schedule(
                null,
                professional,
                LocalDate.now(),
                currentTime,
                nextTime,
                true
            );

            scheduleRepository.save(newSchedule);

            // Atualize o currentTime
            currentTime = nextTime;
        }
    }

    // Método para listar os horários de um profissional
    public List<ScheduleResponseDTO> getSchedulesByProfessional(UUID professionalId) {
        List<Schedule> schedules = scheduleRepository.findByProfessionalId(professionalId);
        return schedules.stream()
                .map(this::createScheduleResponseDTO)
                .collect(Collectors.toList());
    }

    // Método auxiliar para converter Schedule em DTO
    private ScheduleResponseDTO createScheduleResponseDTO(Schedule schedule) {
        return new ScheduleResponseDTO(
                schedule.getId(),
                schedule.getProfessional().getId(),
                schedule.getDate(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getIsAvailable()
        );
    }
}