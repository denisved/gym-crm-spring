package org.gymcrm.service;

import lombok.extern.slf4j.Slf4j;
import org.gymcrm.model.Trainee;
import org.gymcrm.model.Trainer;
import org.gymcrm.model.User;
import org.gymcrm.repository.TraineeRepository;
import org.gymcrm.repository.TrainerRepository;
import org.gymcrm.repository.UserRepository;
import org.gymcrm.util.CredentialsGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class TraineeService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final CredentialsGenerator credentialsGenerator;
    private final ValidationService validationService;

    @Autowired
    public TraineeService(TraineeRepository traineeRepository, TrainerRepository trainerRepository,
                          UserRepository userRepository, CredentialsGenerator credentialsGenerator,
                          ValidationService validationService) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.credentialsGenerator = credentialsGenerator;
        this.validationService = validationService;
    }

    @Transactional
    public Trainee createTrainee(String firstName, String lastName, Date dateOfBirth, String address) {
        validationService.validateName(firstName, "Ім'я учня");
        validationService.validateName(lastName, "Прізвище учня");
        validationService.validateDateOfBirth(dateOfBirth);

        List<String> existingUsernames = userRepository.findAll().stream().map(User::getUsername).toList();
        String username = credentialsGenerator.generateUsername(firstName, lastName, existingUsernames);
        String password = credentialsGenerator.generatePassword();

        Trainee trainee = new Trainee();
        trainee.setFirstName(firstName.trim());
        trainee.setLastName(lastName.trim());
        trainee.setUsername(username);
        trainee.setPassword(password);
        trainee.setActive(true);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address != null ? address.trim() : null);

        log.info("Creating trainee with username: {}", username);
        return traineeRepository.save(trainee);
    }

    @Transactional(readOnly = true)
    public Trainee getByUsername(String username) {
        return traineeRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found: " + username));
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        Trainee trainee = getByUsername(username);

        if (trainee.getPassword().equals(oldPassword)) {
            trainee.setPassword(newPassword);
            traineeRepository.save(trainee);
            log.info("Password changed successfully for trainee: {}", username);
        } else {
            log.warn("Password change failed for {}: incorrect old password", username);
            throw new IllegalArgumentException("Невірний старий пароль");
        }
    }

    @Transactional
    public Trainee updateTrainee(Trainee trainee) {
        log.info("Updating trainee profile: {}", trainee.getUsername());
        return traineeRepository.save(trainee);
    }

    @Transactional
    public void toggleActivation(String username) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found with username: " + username));

        boolean newStatus = !trainee.isActive();
        trainee.setActive(newStatus);

        log.info("Trainee '{}' activation status toggled to: {}", username, newStatus);
        traineeRepository.save(trainee);
    }

    @Transactional
    public void deleteByUsername(String username) {
        traineeRepository.deleteByUsername(username);
        log.info("Trainee deleted: {}", username);
    }

    @Transactional
    public List<Trainer> updateTrainersList(String traineeUsername, List<String> trainerUsernames) {
        Trainee trainee = getByUsername(traineeUsername);

        trainee.getTrainers().clear();

        if (trainerUsernames != null && !trainerUsernames.isEmpty()) {
            for (String tName : trainerUsernames) {
                trainerRepository.findByUsername(tName.trim()).ifPresent(trainer -> {
                    trainee.getTrainers().add(trainer);
                });
            }
        }

        traineeRepository.save(trainee);
        log.info("Trainers list updated for trainee: {}", traineeUsername);
        return trainee.getTrainers();
    }

    @Transactional(readOnly = true)
    public List<Trainee> findAll() {
        return traineeRepository.findAll();
    }
}