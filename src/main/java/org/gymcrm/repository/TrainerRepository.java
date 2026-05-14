package org.gymcrm.repository;

import org.gymcrm.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByUsername(String username);

    @Query("SELECT t FROM Trainer t WHERE t.id NOT IN " +
            "(SELECT tr.trainer.id FROM Training tr WHERE tr.trainee.username = :traineeUsername)")
    List<Trainer> getUnassignedTrainers(@Param("traineeUsername") String traineeUsername);
}