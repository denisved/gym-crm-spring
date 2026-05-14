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

@Slf4j
@Service
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final CredentialsGenerator credentialsGenerator;
    private final TrainingTypeRepository trainingTypeRepository;

    @Autowired
    public TrainerService(TrainerRepository trainerRepository,
                          UserRepository userRepository,
                          CredentialsGenerator credentialsGenerator,
                          TrainingTypeRepository trainingTypeRepository) {
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.credentialsGenerator = credentialsGenerator;
        this.trainingTypeRepository = trainingTypeRepository;
    }

    private void validateName(String name, String fieldName) {
        if (name == null || name.trim().length() < 3) {
            throw new IllegalArgumentException(fieldName + " має містити мінімум 3 символи і не складатися лише з пробілів.");
        }
    }

    @Transactional
    public Trainer createTrainer(String firstName, String lastName, String specialization) {
        validateName(firstName, "Ім'я тренера");
        validateName(lastName, "Прізвище тренера");

        if (specialization == null || specialization.trim().isEmpty()) {
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
    public boolean authenticate(String username, String password) {
        return trainerRepository.findByUsername(username)
                .map(trainer -> trainer.getPassword().equals(password))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public Trainer getByUsername(String username) {
        return trainerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + username));
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        if (authenticate(username, oldPassword)) {
            Trainer trainer = getByUsername(username);
            trainer.setPassword(newPassword);
            trainerRepository.save(trainer);
            log.info("Password changed successfully for trainer: {}", username);
        } else {
            log.warn("Password change failed for {}: incorrect old password", username);
            throw new IllegalArgumentException("Authentication failed");
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
    public void toggleActivation(String username, boolean isActive) {
        Trainer trainer = getByUsername(username);
        trainer.setActive(isActive);
        trainerRepository.save(trainer);
        log.info("Trainer {} activation status changed to: {}", username, isActive);
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