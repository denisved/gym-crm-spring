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

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private CredentialsGenerator credentialsGenerator;

    @InjectMocks
    private TraineeService traineeService;

    @BeforeEach
    void setUp() {
        traineeService.setTraineeDao(traineeDao);
        traineeService.setTrainerDao(trainerDao);
        traineeService.setCredentialsGenerator(credentialsGenerator);
    }

    @Test
    void testCreateTrainee() {
        String firstName = "John";
        String lastName = "Doe";
        Date dateOfBirth = new Date();
        String address = "123 Street";

        when(trainerDao.findAll()).thenReturn(List.of());
        when(traineeDao.findAll()).thenReturn(List.of());
        when(credentialsGenerator.generateUsername(eq(firstName), eq(lastName), anyList())).thenReturn("John.Doe");
        when(credentialsGenerator.generatePassword()).thenReturn("pass123");

        Trainee created = traineeService.createTrainee(firstName, lastName, dateOfBirth, address);

        assertNotNull(created);
        assertEquals(firstName, created.getFirstName());
        assertEquals(lastName, created.getLastName());
        assertEquals(dateOfBirth, created.getDateOfBirth());
        assertEquals(address, created.getAddress());
        assertTrue(created.isActive());
        assertEquals("John.Doe", created.getUsername());
        assertEquals("pass123", created.getPassword());

        verify(traineeDao).save(any(Trainee.class));
    }

    @Test
    void testUpdateTrainee() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);

        Trainee updated = traineeService.updateTrainee(trainee);

        assertEquals(trainee, updated);
        verify(traineeDao).save(trainee);
    }

    @Test
    void testDeleteTrainee_Existing() {
        Trainee trainee = new Trainee();
        when(traineeDao.findById(1L)).thenReturn(trainee);

        traineeService.deleteTrainee(1L);

        verify(traineeDao).delete(1L);
    }

    @Test
    void testDeleteTrainee_NotExisting() {
        when(traineeDao.findById(1L)).thenReturn(null);

        traineeService.deleteTrainee(1L);

        verify(traineeDao, never()).delete(1L);
    }

    @Test
    void testGetTrainee_Existing() {
        Trainee trainee = new Trainee();
        when(traineeDao.findById(1L)).thenReturn(trainee);

        Trainee result = traineeService.getTrainee(1L);

        assertEquals(trainee, result);
    }

    @Test
    void testGetTrainee_NotExisting() {
        when(traineeDao.findById(1L)).thenReturn(null);

        Trainee result = traineeService.getTrainee(1L);

        assertNull(result);
    }

    @Test
    void testGetAllTrainees() {
        Trainee trainee = new Trainee();
        when(traineeDao.findAll()).thenReturn(List.of(trainee));

        List<Trainee> result = traineeService.getAllTrainees();

        assertEquals(1, result.size());
        assertEquals(trainee, result.get(0));
    }
}