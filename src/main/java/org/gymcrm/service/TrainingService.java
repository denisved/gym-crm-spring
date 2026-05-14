package org.gymcrm.service;

import lombok.extern.slf4j.Slf4j;
import org.gymcrm.model.Trainee;
import org.gymcrm.model.Trainer;
import org.gymcrm.model.Training;
import org.gymcrm.repository.TraineeRepository;
import org.gymcrm.repository.TrainerRepository;
import org.gymcrm.repository.TrainingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    @Autowired
    public TrainingService(TrainingRepository trainingRepository, TraineeRepository traineeRepository, TrainerRepository trainerRepository) {
        this.trainingRepository = trainingRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }

    @Transactional
    public Training createTraining(String traineeUsername, String trainerUsername, String trainingName, Date trainingDate, Number trainingDuration) {
        if (trainingName == null || trainingName.trim().length() < 3) {
            throw new IllegalArgumentException("Назва тренування має містити мінімум 3 символи.");
        }
        if (trainingDate == null) {
            throw new IllegalArgumentException("Дата тренування є обов'язковою.");
        }
        if (trainingDuration == null || trainingDuration.intValue() <= 0) {
            throw new IllegalArgumentException("Тривалість тренування має бути більшою за 0 хвилин.");
        }

        log.info("Creating training: {} for Trainee: {} and Trainer: {}", trainingName, traineeUsername, trainerUsername);

        Trainee trainee = traineeRepository.findByUsername(traineeUsername)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found with username: " + traineeUsername));

        Trainer trainer = trainerRepository.findByUsername(trainerUsername)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found with username: " + trainerUsername));

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName(trainingName.trim());
        training.setTrainingDate(trainingDate);
        training.setTrainingDuration(trainingDuration);
        training.setTrainingType(trainer.getSpecialization());

        return trainingRepository.save(training);
    }

    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainings(String traineeUsername, Date fromDate, Date toDate, String trainerUsername, String trainingTypeName) {
        log.debug("Fetching trainings for trainee: {} with filters", traineeUsername);

        Date safeFrom = fromDate != null ? fromDate : java.sql.Date.valueOf("1900-01-01");
        Date safeTo = toDate != null ? toDate : java.sql.Date.valueOf("3000-01-01");

        String safeTrainer = (trainerUsername != null) ? trainerUsername.trim() : "";
        String safeType = (trainingTypeName != null) ? trainingTypeName.trim() : "";
        String cleanTrainee = traineeUsername.trim();

        return trainingRepository.getTraineeTrainingsByCriteria(cleanTrainee, safeFrom, safeTo, safeTrainer, safeType);
    }

    @Transactional(readOnly = true)
    public List<Training> getTrainerTrainings(String trainerUsername, Date fromDate, Date toDate, String traineeUsername) {
        log.debug("Fetching trainings for trainer: {} with criteria", trainerUsername);

        Date safeFromDate = fromDate != null ? fromDate : java.sql.Date.valueOf("1900-01-01");
        Date safeToDate = toDate != null ? toDate : java.sql.Date.valueOf("3000-01-01");

        String safeTraineeUsername = (traineeUsername != null) ? traineeUsername.trim() : "";
        String cleanTrainerUser = trainerUsername.trim();

        return trainingRepository.getTrainerTrainingsByCriteria(cleanTrainerUser, safeFromDate, safeToDate, safeTraineeUsername);
    }

    @Transactional(readOnly = true)
    public List<Training> findAll() {
        return trainingRepository.findAll();
    }
}