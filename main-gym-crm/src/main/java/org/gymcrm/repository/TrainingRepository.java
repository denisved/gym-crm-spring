package org.gymcrm.repository;

import org.gymcrm.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
    @Query("SELECT t FROM Training t " +
            "JOIN FETCH t.trainee tr " +
            "JOIN FETCH t.trainer tn " +
            "JOIN FETCH t.trainingType tt " +
            "WHERE tr.username = :traineeUsername " +
            "AND t.trainingDate >= :fromDate " +
            "AND t.trainingDate <= :toDate " +
            "AND (:trainerUsername = '' OR tn.username = :trainerUsername) " +
            "AND (:trainingTypeName = '' OR tt.trainingTypeName = :trainingTypeName)")
    List<Training> getTraineeTrainingsByCriteria(
            @Param("traineeUsername") String traineeUsername,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("trainerUsername") String trainerUsername,
            @Param("trainingTypeName") String trainingTypeName);

    @Query("SELECT t FROM Training t " +
            "JOIN FETCH t.trainer tn " +
            "JOIN FETCH t.trainee tr " +
            "WHERE tn.username = :trainerUsername " +
            "AND t.trainingDate >= :fromDate " +
            "AND t.trainingDate <= :toDate " +
            "AND (:traineeUsername = '' OR tr.username = :traineeUsername)")
    List<Training> getTrainerTrainingsByCriteria(
            @Param("trainerUsername") String trainerUsername,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("traineeUsername") String traineeUsername);
}