package org.gymcrm.facade;

import org.gymcrm.model.Trainee;
import org.gymcrm.model.Trainer;
import org.gymcrm.model.Training;
import org.gymcrm.model.TrainingType;
import org.gymcrm.service.TraineeService;
import org.gymcrm.service.TrainerService;
import org.gymcrm.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class GymFacade {
    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingService trainingService;

    @Autowired
    public GymFacade(TrainerService trainerService, TraineeService traineeService, TrainingService trainingService) {
        this.trainerService = trainerService;
        this.traineeService = traineeService;
        this.trainingService = trainingService;
    }

    public Trainer createTrainer(String firstName, String lastName, String specialization) {
        return trainerService.createTrainer(firstName, lastName, specialization);
    }

    public Trainee createTrainee(String firstName, String lastName, Date dateOfBirth, String address) {
        return traineeService.createTrainee(firstName, lastName, dateOfBirth, address);
    }

    public Trainer getTrainer(Long id) {
        return trainerService.getTrainer(id);
    }

    public Trainer updateTrainer(Trainer trainer) {
        return trainerService.updateTrainer(trainer);
    }

    public void deleteTrainee(Long id) {
        traineeService.deleteTrainee(id);
    }

    public Training createTraining(Long traineeId, Long trainerId, String trainingName, TrainingType type, Date date, Number duration) {
        return trainingService.createTraining(traineeId, trainerId, trainingName, type, date, duration);
    }

    public List<Trainer> getAllTrainers() {
        return trainerService.getAllTrainers();
    }

    public List<Trainee> getAllTrainees() {
        return traineeService.getAllTrainees();
    }

    public List<Training> getAllTrainings() {
        return trainingService.getAllTrainings();
    }
}