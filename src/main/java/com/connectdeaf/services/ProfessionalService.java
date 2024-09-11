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
import com.connectdeaf.controllers.dtos.response.AddressResponseDTO;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProfessionalService {
    private final ProfessionalRepository professionalRepository;
    private final UserService userService;
    private final ScheduleRepository scheduleRepository;

    public ProfessionalService(ProfessionalRepository professionalRepository, UserService userService,
            ScheduleRepository scheduleRepository) {
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
                professionalRequestDTO.addresses());

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
                professionalRequestDTO.breakDuration());

        Professional savedProfessional = professionalRepository.save(newProfessional);

        return createProfessionalResponseDTO(savedProfessional);
    }

    public ProfessionalResponseDTO findById(UUID professionalId) {
        Professional professional = professionalRepository.findById(professionalId)
                .orElseThrow(() -> new ProfessionalNotFoundException());

        return createProfessionalResponseDTO(professional);
    }

    @Transactional
    public ProfessionalResponseDTO updateProfessional(UUID professionalId,
            @Valid ProfessionalRequestDTO professionalRequestDTO) {
        Professional professional = professionalRepository.findById(professionalId)
                .orElseThrow(() -> new ProfessionalNotFoundException());

        User user = professional.getUser();

        UserRequestDTO userRequestDTO = new UserRequestDTO(
                professionalRequestDTO.name(),
                professionalRequestDTO.email(),
                user.getPassword(),
                professionalRequestDTO.phoneNumber(),
                professionalRequestDTO.addresses());

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
                professional.getBreakDuration(),
                professional.getUser().getAddresses().stream().map(
                        address -> new AddressResponseDTO(
                                address.getStreet(),
                                address.getCity(),
                                address.getState(),
                                address.getCep()))
                        .collect(Collectors.toList()));
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

    // Método para listar os horários de um profissional
    public List<ScheduleResponseDTO> getSchedulesByProfessionalAndDate(UUID professionalId, LocalDate date) {
        List<Schedule> schedulesNotAvaibled = scheduleRepository.findByProfessionalIdAndDate(professionalId, date);
        List<Schedule> schedulesAvaibled = generateSchedulesAvaibled(professionalId, date, schedulesNotAvaibled);
        return schedulesAvaibled.stream()
                .map(this::createScheduleResponseDTO)
                .collect(Collectors.toList());
    }

    private List<Schedule> generateSchedulesAvaibled(UUID professionalId, LocalDate date,
            List<Schedule> schedulesNotAvaibled) {
        Professional professional = professionalRepository.findById(professionalId)
                .orElseThrow(() -> new ProfessionalNotFoundException());

        LocalTime workStartTime = professional.getWorkStartTime();
        LocalTime workEndTime = professional.getWorkEndTime();
        Duration sessionDuration = professional.getBreakDuration(); // Duração da sessão de trabalho

        List<Schedule> schedulesAvaibled = new ArrayList<>();
        LocalTime currentTime = workStartTime;

        // Itera sobre o tempo de trabalho até o final do expediente
        while (!currentTime.plus(sessionDuration).isAfter(workEndTime)) {
            final LocalTime startTime = currentTime;
            final LocalTime endTime = currentTime.plus(sessionDuration);

            // Verifica se o horário está disponível
            boolean isAvailable = schedulesNotAvaibled.stream()
                    .noneMatch(schedule -> schedule.getStartTime().equals(startTime));

            if (isAvailable) {
                schedulesAvaibled.add(new Schedule(null, professional, date, startTime, endTime));
            }

            // Avança para o próximo slot
            currentTime = endTime;
        }

        return schedulesAvaibled;
    }

    // Método auxiliar para converter Schedule em DTO
    private ScheduleResponseDTO createScheduleResponseDTO(Schedule schedule) {
        return new ScheduleResponseDTO(
                schedule.getId(),
                schedule.getProfessional().getId(),
                schedule.getDate(),
                schedule.getStartTime(),
                schedule.getEndTime());
    }
}