package org.gymcrm.service;

import lombok.extern.slf4j.Slf4j;
import org.gymcrm.dao.TraineeDao;
import org.gymcrm.dao.TrainerDao;
import org.gymcrm.model.Trainee;
import org.gymcrm.model.Trainer;
import org.gymcrm.util.CredentialsGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class TrainerService {

    private TrainerDao trainerDao;
    private TraineeDao traineeDao;
    private CredentialsGenerator credentialsGenerator;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setCredentialsGenerator(CredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

    public Trainer createTrainer(String firstName, String lastName, String specialization) {
        Trainer trainer = new Trainer();
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setSpecialization(specialization);
        trainer.setActive(true);

        List<String> existingUsernames = Stream.concat(
                trainerDao.findAll().stream().map(Trainer::getUsername),
                traineeDao.findAll().stream().map(Trainee::getUsername)
        ).toList();

        trainer.setUsername(credentialsGenerator.generateUsername(firstName, lastName, existingUsernames));
        trainer.setPassword(credentialsGenerator.generatePassword());

        trainerDao.save(trainer);
        log.info("Trainer created: ID [{}], Username [{}], Specialization [{}]",
                trainer.getId(), trainer.getUsername(), trainer.getSpecialization());
        return trainer;
    }

    public Trainer updateTrainer(Trainer trainer) {
        trainerDao.save(trainer);
        log.info("Trainer updated: ID [{}], Username [{}]", trainer.getId(), trainer.getUsername());
        return trainer;
    }

    public Trainer getTrainer(Long id) {
        Trainer trainer = trainerDao.findById(id);
        if (trainer != null) {
            log.debug("Fetched Trainer with ID: [{}]", id);
        } else {
            log.warn("Trainer with ID: [{}] not found", id);
        }
        return trainer;
    }

    public List<Trainer> getAllTrainers() {
        return trainerDao.findAll();
    }
}