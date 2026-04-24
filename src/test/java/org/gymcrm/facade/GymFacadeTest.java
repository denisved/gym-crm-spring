package org.gymcrm.facade;

import org.gymcrm.model.Trainee;
import org.gymcrm.model.Trainer;
import org.gymcrm.model.Training;
import org.gymcrm.model.TrainingType;
import org.gymcrm.service.TraineeService;
import org.gymcrm.service.TrainerService;
import org.gymcrm.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock
    private TrainerService trainerService;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private GymFacade gymFacade;

    @Test
    void testCreateTrainer() {
        Trainer trainer = new Trainer();
        when(trainerService.createTrainer("John", "Doe", "Yoga")).thenReturn(trainer);

        Trainer result = gymFacade.createTrainer("John", "Doe", "Yoga");

        assertEquals(trainer, result);
        verify(trainerService).createTrainer("John", "Doe", "Yoga");
    }

    @Test
    void testCreateTrainee() {
        Trainee trainee = new Trainee();
        Date date = new Date();
        when(traineeService.createTrainee("Jane", "Smith", date, "123 Ave")).thenReturn(trainee);

        Trainee result = gymFacade.createTrainee("Jane", "Smith", date, "123 Ave");

        assertEquals(trainee, result);
        verify(traineeService).createTrainee("Jane", "Smith", date, "123 Ave");
    }

    @Test
    void testGetTrainer() {
        Trainer trainer = new Trainer();
        when(trainerService.getTrainer(1L)).thenReturn(trainer);

        Trainer result = gymFacade.getTrainer(1L);

        assertEquals(trainer, result);
        verify(trainerService).getTrainer(1L);
    }

    @Test
    void testUpdateTrainer() {
        Trainer trainer = new Trainer();
        when(trainerService.updateTrainer(trainer)).thenReturn(trainer);

        Trainer result = gymFacade.updateTrainer(trainer);

        assertEquals(trainer, result);
        verify(trainerService).updateTrainer(trainer);
    }

    @Test
    void testDeleteTrainee() {
        gymFacade.deleteTrainee(2L);
        verify(traineeService).deleteTrainee(2L);
    }

    @Test
    void testCreateTraining() {
        Training training = new Training();
        TrainingType type = TrainingType.FITNESS;
        Date date = new Date();
        when(trainingService.createTraining(1L, 2L, "Morning", type, date, 60)).thenReturn(training);

        Training result = gymFacade.createTraining(1L, 2L, "Morning", type, date, 60);

        assertEquals(training, result);
        verify(trainingService).createTraining(1L, 2L, "Morning", type, date, 60);
    }

    @Test
    void testGetAllTrainers() {
        List<Trainer> trainers = List.of(new Trainer());
        when(trainerService.getAllTrainers()).thenReturn(trainers);

        List<Trainer> result = gymFacade.getAllTrainers();

        assertEquals(trainers, result);
        verify(trainerService).getAllTrainers();
    }

    @Test
    void testGetAllTrainees() {
        List<Trainee> trainees = List.of(new Trainee());
        when(traineeService.getAllTrainees()).thenReturn(trainees);

        List<Trainee> result = gymFacade.getAllTrainees();

        assertEquals(trainees, result);
        verify(traineeService).getAllTrainees();
    }

    @Test
    void testGetAllTrainings() {
        List<Training> trainings = List.of(new Training());
        when(trainingService.getAllTrainings()).thenReturn(trainings);

        List<Training> result = gymFacade.getAllTrainings();

        assertEquals(trainings, result);
        verify(trainingService).getAllTrainings();
    }
}