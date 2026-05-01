package org.gymcrm.service;

import lombok.extern.slf4j.Slf4j;
import org.gymcrm.dao.TraineeDao;
import org.gymcrm.dao.TrainerDao;
import org.gymcrm.model.Trainee;
import org.gymcrm.model.Trainer;
import org.gymcrm.util.CredentialsGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class TraineeService {

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private CredentialsGenerator credentialsGenerator;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setCredentialsGenerator(CredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

    public Trainee createTrainee(String firstName, String lastName, Date dateOfBirth, String address) {
        Trainee trainee = new Trainee();
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        trainee.setActive(true);

        List<String> existingUsernames = Stream.concat(
                trainerDao.findAll().stream().map(Trainer::getUsername),
                traineeDao.findAll().stream().map(Trainee::getUsername)
        ).toList();

        trainee.setUsername(credentialsGenerator.generateUsername(firstName, lastName, existingUsernames));
        trainee.setPassword(credentialsGenerator.generatePassword());

        traineeDao.save(trainee);
        log.info("Trainee created: ID [{}], Username [{}], Address [{}]",
                trainee.getId(), trainee.getUsername(), trainee.getAddress());
        return trainee;
    }

    public Trainee updateTrainee(Trainee trainee) {
        traineeDao.save(trainee);
        log.info("Trainee updated: ID [{}], Username [{}]", trainee.getId(), trainee.getUsername());
        return trainee;
    }

    public void deleteTrainee(Long id) {
        Optional.ofNullable(traineeDao.findById(id)).ifPresentOrElse(
                trainee -> {
                    traineeDao.delete(id);
                    log.info("Trainee deleted: ID [{}], Username [{}]", id, trainee.getUsername());
                },
                () -> log.warn("Attempted to delete Trainee with ID: [{}], but it was not found", id)
        );
    }

    public Trainee getTrainee(Long id) {
        Optional<Trainee> traineeOpt = Optional.ofNullable(traineeDao.findById(id));

        traineeOpt.ifPresentOrElse(
                t -> log.debug("Fetched Trainee with ID: [{}]", id),
                () -> log.warn("Trainee with ID: [{}] not found", id)
        );

        return traineeOpt.orElse(null);
    }

    public List<Trainee> getAllTrainees() {
        return traineeDao.findAll();
    }
}