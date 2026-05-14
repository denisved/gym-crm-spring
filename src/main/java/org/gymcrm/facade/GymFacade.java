package org.gymcrm.facade;

import org.gymcrm.model.Trainee;
import org.gymcrm.model.Trainer;
import org.gymcrm.model.Training;
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

    public boolean authenticateTrainee(String username, String password) {
        return traineeService.authenticate(username, password);
    }

    public boolean authenticateTrainer(String username, String password) {
        return trainerService.authenticate(username, password);
    }

    public Trainee getTrainee(String username) {
        return traineeService.getByUsername(username);
    }

    public Trainer getTrainer(String username) {
        return trainerService.getByUsername(username);
    }

    public void changeTraineePassword(String username, String oldPassword, String newPassword) {
        traineeService.changePassword(username, oldPassword, newPassword);
    }

    public void changeTrainerPassword(String username, String oldPassword, String newPassword) {
        trainerService.changePassword(username, oldPassword, newPassword);
    }

    public Trainee updateTrainee(Trainee trainee) {
        return traineeService.updateTrainee(trainee);
    }

    public Trainer updateTrainerSpecialization(String username, String specializationName) {
        return trainerService.updateTrainerSpecialization(username, specializationName);
    }

    public void toggleTraineeActivation(String username, boolean isActive) {
        traineeService.toggleActivation(username, isActive);
    }

    public void toggleTrainerActivation(String username, boolean isActive) {
        trainerService.toggleActivation(username, isActive);
    }

    public void deleteTrainee(String username) {
        traineeService.deleteByUsername(username);
    }

    public List<Training> getTraineeTrainings(String username, Date from, Date to, String trainerName, String type) {
        return trainingService.getTraineeTrainings(username, from, to, trainerName, type);
    }

    public List<Training> getTrainerTrainings(String username, Date from, Date to, String traineeName) {
        return trainingService.getTrainerTrainings(username, from, to, traineeName);
    }

    public Training createTraining(String traineeUsername, String trainerUsername, String name, Date date, Number duration) {
        return trainingService.createTraining(traineeUsername, trainerUsername, name, date, duration);
    }

    public List<Trainer> getUnassignedTrainers(String traineeUsername) {
        return trainerService.getUnassignedTrainers(traineeUsername);
    }

    public List<Trainer> updateTraineeTrainersList(String traineeUsername, List<String> trainerUsernames) {
        return traineeService.updateTrainersList(traineeUsername, trainerUsernames);
    }

    public List<Trainer> getAllTrainers() {
        return trainerService.findAll();
    }

    public List<Trainee> getAllTrainees() {
        return traineeService.findAll();
    }

    public List<Training> getAllTrainings() {
        return trainingService.findAll();
    }
}