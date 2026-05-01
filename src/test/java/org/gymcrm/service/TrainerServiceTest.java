package org.gymcrm.service;

import org.gymcrm.dao.TraineeDao;
import org.gymcrm.dao.TrainerDao;
import org.gymcrm.model.Trainee;
import org.gymcrm.model.Trainer;
import org.gymcrm.util.CredentialsGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private CredentialsGenerator credentialsGenerator;

    @InjectMocks
    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        trainerService.setTrainerDao(trainerDao);
        trainerService.setTraineeDao(traineeDao);
        trainerService.setCredentialsGenerator(credentialsGenerator);
    }

    @Test
    void testCreateTrainer() {
        String firstName = "Jane";
        String lastName = "Smith";
        String specialization = "Yoga";

        when(trainerDao.findAll()).thenReturn(List.of());
        when(traineeDao.findAll()).thenReturn(List.of());
        when(credentialsGenerator.generateUsername(eq(firstName), eq(lastName), anyList())).thenReturn("Jane.Smith");
        when(credentialsGenerator.generatePassword()).thenReturn("pass456");

        Trainer created = trainerService.createTrainer(firstName, lastName, specialization);

        assertNotNull(created);
        assertEquals(firstName, created.getFirstName());
        assertEquals(lastName, created.getLastName());
        assertEquals(specialization, created.getSpecialization());
        assertTrue(created.isActive());
        assertEquals("Jane.Smith", created.getUsername());
        assertEquals("pass456", created.getPassword());

        verify(trainerDao).save(any(Trainer.class));
    }

    @Test
    void testUpdateTrainer() {
        Trainer trainer = new Trainer();
        trainer.setId(2L);

        Trainer updated = trainerService.updateTrainer(trainer);

        assertEquals(trainer, updated);
        verify(trainerDao).save(trainer);
    }

    @Test
    void testGetTrainer_Existing() {
        Trainer trainer = new Trainer();
        when(trainerDao.findById(2L)).thenReturn(trainer);

        Trainer result = trainerService.getTrainer(2L);

        assertEquals(trainer, result);
    }

    @Test
    void testGetTrainer_NotExisting() {
        when(trainerDao.findById(2L)).thenReturn(null);

        Trainer result = trainerService.getTrainer(2L);

        assertNull(result);
    }

    @Test
    void testGetAllTrainers() {
        Trainer trainer = new Trainer();
        when(trainerDao.findAll()).thenReturn(List.of(trainer));

        List<Trainer> result = trainerService.getAllTrainers();

        assertEquals(1, result.size());
        assertEquals(trainer, result.get(0));
    }
}