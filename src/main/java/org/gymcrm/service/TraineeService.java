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

    @Autowired
    public TraineeService(TraineeRepository traineeRepository, TrainerRepository trainerRepository,
                          UserRepository userRepository, CredentialsGenerator credentialsGenerator) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.credentialsGenerator = credentialsGenerator;
    }

    private void validateName(String name, String fieldName) {
        if (name == null || name.trim().length() < 3) {
            throw new IllegalArgumentException(fieldName + " має містити мінімум 3 символи.");
        }
    }

    @Transactional
    public Trainee createTrainee(String firstName, String lastName, Date dateOfBirth, String address) {
        validateName(firstName, "Ім'я учня");
        validateName(lastName, "Прізвище учня");

        if (dateOfBirth != null && dateOfBirth.after(new Date())) {
            throw new IllegalArgumentException("Помилка: Дата народження не може бути в майбутньому часі.");
        }

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
    public boolean authenticate(String username, String password) {
        return traineeRepository.findByUsername(username)
                .map(trainee -> trainee.getPassword().equals(password))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public Trainee getByUsername(String username) {
        return traineeRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found: " + username));
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        if (authenticate(username, oldPassword)) {
            Trainee trainee = getByUsername(username);
            trainee.setPassword(newPassword);
            traineeRepository.save(trainee);
            log.info("Password changed successfully for trainee: {}", username);
        } else {
            log.warn("Password change failed for {}: incorrect old password", username);
            throw new IllegalArgumentException("Authentication failed");
        }
    }

    @Transactional
    public Trainee updateTrainee(Trainee trainee) {
        log.info("Updating trainee profile: {}", trainee.getUsername());
        return traineeRepository.save(trainee);
    }

    @Transactional
    public void toggleActivation(String username, boolean isActive) {
        Trainee trainee = getByUsername(username);
        trainee.setActive(isActive);
        traineeRepository.save(trainee);
        log.info("Trainee {} activation status changed to: {}", username, isActive);
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