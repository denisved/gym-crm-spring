package org.gymcrm.facade;

import org.gymcrm.model.Trainee;
import org.gymcrm.model.Trainer;
import org.gymcrm.model.Training;
import org.gymcrm.service.TraineeService;
import org.gymcrm.service.TrainerService;
import org.gymcrm.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private GymFacade gymFacade;

    @Test
    void testCreateTrainer() {
        Trainer trainer = new Trainer();
        when(trainerService.createTrainer("Jane", "Smith", "Yoga")).thenReturn(trainer);
        Trainer result = gymFacade.createTrainer("Jane", "Smith", "Yoga");
        assertEquals(trainer, result);
        verify(trainerService).createTrainer("Jane", "Smith", "Yoga");
    }

    @Test
    void testCreateTrainee() {
        Trainee trainee = new Trainee();
        Date dob = new Date();
        when(traineeService.createTrainee("John", "Doe", dob, "Addr")).thenReturn(trainee);
        Trainee result = gymFacade.createTrainee("John", "Doe", dob, "Addr");
        assertEquals(trainee, result);
        verify(traineeService).createTrainee("John", "Doe", dob, "Addr");
    }

    @Test
    void testAuthenticateTrainee() {
        when(traineeService.authenticate("user", "pass")).thenReturn(true);
        assertTrue(gymFacade.authenticateTrainee("user", "pass"));
    }

    @Test
    void testAuthenticateTrainer() {
        when(trainerService.authenticate("user", "pass")).thenReturn(true);
        assertTrue(gymFacade.authenticateTrainer("user", "pass"));
    }

    @Test
    void testGetTrainee() {
        Trainee trainee = new Trainee();
        when(traineeService.getByUsername("user")).thenReturn(trainee);
        assertEquals(trainee, gymFacade.getTrainee("user"));
    }

    @Test
    void testGetTrainer() {
        Trainer trainer = new Trainer();
        when(trainerService.getByUsername("user")).thenReturn(trainer);
        assertEquals(trainer, gymFacade.getTrainer("user"));
    }

    @Test
    void testChangeTraineePassword() {
        gymFacade.changeTraineePassword("user", "old", "new");
        verify(traineeService).changePassword("user", "old", "new");
    }

    @Test
    void testChangeTrainerPassword() {
        gymFacade.changeTrainerPassword("user", "old", "new");
        verify(trainerService).changePassword("user", "old", "new");
    }

    @Test
    void testUpdateTrainee() {
        Trainee trainee = new Trainee();
        when(traineeService.updateTrainee(trainee)).thenReturn(trainee);
        assertEquals(trainee, gymFacade.updateTrainee(trainee));
    }

    @Test
    void testUpdateTrainerSpecialization() {
        Trainer trainer = new Trainer();
        when(trainerService.updateTrainerSpecialization("user", "Yoga")).thenReturn(trainer);
        assertEquals(trainer, gymFacade.updateTrainerSpecialization("user", "Yoga"));
    }

    @Test
    void testToggleTraineeActivation() {
        gymFacade.toggleTraineeActivation("user", true);
        verify(traineeService).toggleActivation("user", true);
    }

    @Test
    void testToggleTrainerActivation() {
        gymFacade.toggleTrainerActivation("user", true);
        verify(trainerService).toggleActivation("user", true);
    }

    @Test
    void testDeleteTrainee() {
        gymFacade.deleteTrainee("user");
        verify(traineeService).deleteByUsername("user");
    }

    @Test
    void testGetTraineeTrainings() {
        List<Training> trainings = List.of(new Training());
        Date from = new Date();
        Date to = new Date();
        when(trainingService.getTraineeTrainings("user", from, to, "trainer", "type")).thenReturn(trainings);
        assertEquals(trainings, gymFacade.getTraineeTrainings("user", from, to, "trainer", "type"));
    }

    @Test
    void testGetTrainerTrainings() {
        List<Training> trainings = List.of(new Training());
        Date from = new Date();
        Date to = new Date();
        when(trainingService.getTrainerTrainings("user", from, to, "trainee")).thenReturn(trainings);
        assertEquals(trainings, gymFacade.getTrainerTrainings("user", from, to, "trainee"));
    }

    @Test
    void testCreateTraining() {
        Training training = new Training();
        Date date = new Date();
        when(trainingService.createTraining("trainee", "trainer", "name", date, 60)).thenReturn(training);
        assertEquals(training, gymFacade.createTraining("trainee", "trainer", "name", date, 60));
    }

    @Test
    void testGetUnassignedTrainers() {
        List<Trainer> trainers = List.of(new Trainer());
        when(trainerService.getUnassignedTrainers("user")).thenReturn(trainers);
        assertEquals(trainers, gymFacade.getUnassignedTrainers("user"));
    }

    @Test
    void testUpdateTraineeTrainersList() {
        List<Trainer> trainers = List.of(new Trainer());
        when(traineeService.updateTrainersList("user", List.of("t1"))).thenReturn(trainers);
        assertEquals(trainers, gymFacade.updateTraineeTrainersList("user", List.of("t1")));
    }

    @Test
    void testGetAllTrainers() {
        List<Trainer> trainers = List.of(new Trainer());
        when(trainerService.findAll()).thenReturn(trainers);
        assertEquals(trainers, gymFacade.getAllTrainers());
    }

    @Test
    void testGetAllTrainees() {
        List<Trainee> trainees = List.of(new Trainee());
        when(traineeService.findAll()).thenReturn(trainees);
        assertEquals(trainees, gymFacade.getAllTrainees());
    }

    @Test
    void testGetAllTrainings() {
        List<Training> trainings = List.of(new Training());
        when(trainingService.findAll()).thenReturn(trainings);
        assertEquals(trainings, gymFacade.getAllTrainings());
    }
}
