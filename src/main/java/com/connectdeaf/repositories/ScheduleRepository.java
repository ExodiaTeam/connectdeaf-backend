package com.connectdeaf.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.connectdeaf.domain.schedule.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {

    List<Schedule> findByProfessionalIdAndDate(UUID professionalId, LocalDate date);
}
