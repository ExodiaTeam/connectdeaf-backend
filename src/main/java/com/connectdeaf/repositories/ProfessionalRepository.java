package com.connectdeaf.repositories;

import com.connectdeaf.domain.professional.Professional;
import com.connectdeaf.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfessionalRepository extends JpaRepository<Professional, UUID> {
    Optional<Professional> findByUser(User user);
}
