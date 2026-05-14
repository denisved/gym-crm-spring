package org.gymcrm.service;

import org.gymcrm.model.Trainee;
import org.gymcrm.model.Trainer;
import org.gymcrm.model.Training;
import org.gymcrm.model.TrainingType;
import org.gymcrm.repository.TraineeRepository;
import org.gymcrm.repository.TrainerRepository;
import org.gymcrm.repository.TrainingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private TrainingService trainingService;

    private Trainee trainee;
    private Trainer trainer;
    private Training training;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setUsername("John.Doe");

        trainer = new Trainer();
        trainer.setUsername("Jane.Smith");
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Yoga");
        trainer.setSpecialization(type);

        training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName("Morning Yoga");
        training.setTrainingDate(new Date());
        training.setTrainingDuration(60);
    }

    @Test
    void testCreateTraining_Success() {
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("Jane.Smith")).thenReturn(Optional.of(trainer));
        when(trainingRepository.save(any(Training.class))).thenAnswer(i -> i.getArguments()[0]);

        Training created = trainingService.createTraining("John.Doe", "Jane.Smith", "Morning Yoga", new Date(), 60);

        assertNotNull(created);
        assertEquals("Morning Yoga", created.getTrainingName());
        assertEquals(trainee, created.getTrainee());
        assertEquals(trainer, created.getTrainer());
        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void testCreateTraining_InvalidName() {
        assertThrows(IllegalArgumentException.class, () -> 
            trainingService.createTraining("John.Doe", "Jane.Smith", "Yo", new Date(), 60)
        );
    }

    @Test
    void testCreateTraining_InvalidDuration() {
        assertThrows(IllegalArgumentException.class, () -> 
            trainingService.createTraining("John.Doe", "Jane.Smith", "Morning Yoga", new Date(), 0)
        );
    }

    @Test
    void testGetTraineeTrainings() {
        List<Training> trainings = List.of(training);
        when(trainingRepository.getTraineeTrainingsByCriteria(anyString(), any(Date.class), any(Date.class), anyString(), anyString()))
                .thenReturn(trainings);

        List<Training> result = trainingService.getTraineeTrainings("John.Doe", null, null, null, null);

        assertEquals(trainings, result);
    }

    @Test
    void testGetTrainerTrainings() {
        List<Training> trainings = List.of(training);
        when(trainingRepository.getTrainerTrainingsByCriteria(anyString(), any(Date.class), any(Date.class), anyString()))
                .thenReturn(trainings);

        List<Training> result = trainingService.getTrainerTrainings("Jane.Smith", null, null, null);

        assertEquals(trainings, result);
    }

    @Test
    void testFindAll() {
        List<Training> trainings = List.of(training);
        when(trainingRepository.findAll()).thenReturn(trainings);
        assertEquals(trainings, trainingService.findAll());
    }

    @Test
    void testGetTraineeTrainings_WithFilters() {
        List<Training> trainings = List.of(training);
        Date from = new Date();
        Date to = new Date();
        when(trainingRepository.getTraineeTrainingsByCriteria("John.Doe", from, to, "Jane.Smith", "Yoga"))
                .thenReturn(trainings);

        List<Training> result = trainingService.getTraineeTrainings("John.Doe", from, to, "Jane.Smith", "Yoga");

        assertEquals(trainings, result);
    }

    @Test
    void testGetTrainerTrainings_WithFilters() {
        List<Training> trainings = List.of(training);
        Date from = new Date();
        Date to = new Date();
        when(trainingRepository.getTrainerTrainingsByCriteria("Jane.Smith", from, to, "John.Doe"))
                .thenReturn(trainings);

        List<Training> result = trainingService.getTrainerTrainings("Jane.Smith", from, to, "John.Doe");

        assertEquals(trainings, result);
    }
}
