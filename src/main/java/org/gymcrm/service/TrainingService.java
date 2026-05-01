package org.gymcrm.service;

import lombok.extern.slf4j.Slf4j;
import org.gymcrm.dao.TrainingDao;
import org.gymcrm.model.Training;
import org.gymcrm.model.TrainingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class TrainingService {

    private TrainingDao trainingDao;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    public Training createTraining(Long traineeId, Long trainerId, String trainingName, TrainingType trainingType, Date trainingDate, Number trainingDuration) {
        Training training = Training.builder()
                .traineeId(traineeId)
                .trainerId(trainerId)
                .trainingName(trainingName)
                .trainingType(trainingType)
                .trainingDate(trainingDate)
                .trainingDuration(trainingDuration)
                .build();

        trainingDao.save(training);
        log.info("Training created: ID [{}], Name [{}], Type [{}] for TraineeID [{}] and TrainerID [{}]",
                training.getId(), training.getTrainingName(), training.getTrainingType(), traineeId, trainerId);
        return training;
    }

    public Training getTraining(Long id) {
        Training training = trainingDao.findById(id);
        if (training != null) {
            log.debug("Fetched Training with ID: [{}]", id);
        } else {
            log.warn("Training with ID: [{}] not found", id);
        }
        return training;
    }

    public List<Training> getAllTrainings() {
        return trainingDao.findAll();
    }
}