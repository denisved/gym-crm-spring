package org.gymcrm.workload.repository;

import org.gymcrm.workload.model.TrainerWorkload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerWorkloadRepository extends JpaRepository<TrainerWorkload, Long> {
    Optional<TrainerWorkload> findByUsernameAndYearAndMonth(String username, Integer year, Integer month);
}