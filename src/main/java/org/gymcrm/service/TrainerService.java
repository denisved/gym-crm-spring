package org.gymcrm.service;

import lombok.extern.slf4j.Slf4j;
import org.gymcrm.model.Trainer;
import org.gymcrm.model.TrainingType;
import org.gymcrm.model.User;
import org.gymcrm.repository.TrainerRepository;
import org.gymcrm.repository.TrainingTypeRepository;
import org.gymcrm.repository.UserRepository;
import org.gymcrm.util.CredentialsGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hibernate.internal.util.StringHelper.isBlank;

@Slf4j
@Service
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final CredentialsGenerator credentialsGenerator;
    private final TrainingTypeRepository trainingTypeRepository;
    private final ValidationService validationService;

    @Autowired
    public TrainerService(TrainerRepository trainerRepository,
                          UserRepository userRepository,
                          CredentialsGenerator credentialsGenerator,
                          TrainingTypeRepository trainingTypeRepository,
                          ValidationService validationService) {
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.credentialsGenerator = credentialsGenerator;
        this.trainingTypeRepository = trainingTypeRepository;
        this.validationService = validationService;
    }

    @Transactional
    public Trainer createTrainer(String firstName, String lastName, String specialization) {
        validationService.validateName(firstName, "Ім'я тренера");
        validationService.validateName(lastName, "Прізвище тренера");

        if (isBlank(specialization)) {
            throw new IllegalArgumentException("Спеціалізація не може бути порожньою.");
        }

        List<String> existingUsernames = userRepository.findAll().stream().map(User::getUsername).toList();
        String username = credentialsGenerator.generateUsername(firstName, lastName, existingUsernames);
        String password = credentialsGenerator.generatePassword();

        TrainingType type = trainingTypeRepository.findByTrainingTypeNameIgnoreCase(specialization)
                .orElseThrow(() -> new IllegalArgumentException("Unknown training type: " + specialization));

        Trainer trainer = new Trainer();
        trainer.setFirstName(firstName.trim());
        trainer.setLastName(lastName.trim());
        trainer.setUsername(username);
        trainer.setPassword(password);
        trainer.setActive(true);
        trainer.setSpecialization(type);

        log.info("Creating trainer with username: {}", username);
        return trainerRepository.save(trainer);
    }

    @Transactional(readOnly = true)
    public Trainer getByUsername(String username) {
        return trainerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + username));
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        Trainer trainer = getByUsername(username);

        if (trainer.getPassword().equals(oldPassword)) {
            trainer.setPassword(newPassword);
            trainerRepository.save(trainer);
            log.info("Password changed successfully for trainee: {}", username);
        } else {
            log.warn("Password change failed for {}: incorrect old password", username);
            throw new IllegalArgumentException("Невірний старий пароль");
        }
    }

    @Transactional
    public Trainer updateTrainerSpecialization(String username, String newSpecializationName) {
        Trainer trainer = getByUsername(username);

        TrainingType type = trainingTypeRepository.findByTrainingTypeNameIgnoreCase(newSpecializationName)
                .orElseThrow(() -> new IllegalArgumentException("Unknown training type: " + newSpecializationName));

        trainer.setSpecialization(type);
        log.info("Trainer {} specialization updated to {}", username, newSpecializationName);

        return trainerRepository.save(trainer);
    }

    @Transactional
    public void toggleActivation(String username) {
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found with username: " + username));

        boolean newStatus = !trainer.isActive();
        trainer.setActive(newStatus);

        log.info("Trainer '{}' activation status toggled to: {}", username, newStatus);
        trainerRepository.save(trainer);
    }

    @Transactional(readOnly = true)
    public List<Trainer> getUnassignedTrainers(String traineeUsername) {
        return trainerRepository.getUnassignedTrainers(traineeUsername);
    }

    @Transactional(readOnly = true)
    public List<Trainer> findAll() {
        return trainerRepository.findAll();
    }
}