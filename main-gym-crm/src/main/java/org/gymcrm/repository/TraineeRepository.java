package org.gymcrm.repository;

import org.gymcrm.model.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    Optional<Trainee> findByUsername(String username);
    void deleteByUsername(String username);
}