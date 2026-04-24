package org.gymcrm.service;

import org.gymcrm.dao.TrainingDao;
import org.gymcrm.model.Training;
import org.gymcrm.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingDao trainingDao;

    @InjectMocks
    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        trainingService.setTrainingDao(trainingDao);
    }

    @Test
    void testCreateTraining() {
        Long traineeId = 1L;
        Long trainerId = 2L;
        String name = "Morning Session";
        TrainingType type = TrainingType.YOGA;
        Date date = new Date();
        Number duration = 60;

        Training created = trainingService.createTraining(traineeId, trainerId, name, type, date, duration);

        assertNotNull(created);
        assertEquals(traineeId, created.getTraineeId());
        assertEquals(trainerId, created.getTrainerId());
        assertEquals(name, created.getTrainingName());
        assertEquals(type, created.getTrainingType());
        assertEquals(date, created.getTrainingDate());
        assertEquals(duration, created.getTrainingDuration());

        verify(trainingDao).save(any(Training.class));
    }

    @Test
    void testGetTraining_Existing() {
        Training training = new Training();
        when(trainingDao.findById(3L)).thenReturn(training);

        Training result = trainingService.getTraining(3L);

        assertEquals(training, result);
    }

    @Test
    void testGetTraining_NotExisting() {
        when(trainingDao.findById(3L)).thenReturn(null);

        Training result = trainingService.getTraining(3L);

        assertNull(result);
    }

    @Test
    void testGetAllTrainings() {
        Training training = new Training();
        when(trainingDao.findAll()).thenReturn(List.of(training));

        List<Training> result = trainingService.getAllTrainings();

        assertEquals(1, result.size());
        assertEquals(training, result.get(0));
    }
}