package org.gymcrm.service;

import org.gymcrm.model.Trainee;
import org.gymcrm.model.Trainer;
import org.gymcrm.model.User;
import org.gymcrm.repository.TraineeRepository;
import org.gymcrm.repository.TrainerRepository;
import org.gymcrm.repository.UserRepository;
import org.gymcrm.util.CredentialsGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CredentialsGenerator credentialsGenerator;

    @InjectMocks
    private TraineeService traineeService;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setUsername("John.Doe");
        trainee.setPassword("password");
        trainee.setActive(true);
    }

    @Test
    void testCreateTrainee_Success() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        when(credentialsGenerator.generateUsername(anyString(), anyString(), anyList())).thenReturn("John.Doe");
        when(credentialsGenerator.generatePassword()).thenReturn("randomPass");
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(i -> i.getArguments()[0]);

        Trainee created = traineeService.createTrainee("John", "Doe", new Date(), "Address");

        assertNotNull(created);
        assertEquals("John", created.getFirstName());
        assertEquals("Doe", created.getLastName());
        assertEquals("John.Doe", created.getUsername());
        assertEquals("randomPass", created.getPassword());
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void testCreateTrainee_InvalidFirstName() {
        assertThrows(IllegalArgumentException.class, () -> 
            traineeService.createTrainee("Jo", "Doe", new Date(), "Address")
        );
    }

    @Test
    void testCreateTrainee_InvalidLastName() {
        assertThrows(IllegalArgumentException.class, () -> 
            traineeService.createTrainee("John", "Do", new Date(), "Address")
        );
    }

    @Test
    void testCreateTrainee_FutureBirthDate() {
        Date futureDate = new Date(System.currentTimeMillis() + 1000000);
        assertThrows(IllegalArgumentException.class, () -> 
            traineeService.createTrainee("John", "Doe", futureDate, "Address")
        );
    }

    @Test
    void testCreateTrainee_NullAddress() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        when(credentialsGenerator.generateUsername(anyString(), anyString(), anyList())).thenReturn("John.Doe");
        when(credentialsGenerator.generatePassword()).thenReturn("randomPass");
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(i -> i.getArguments()[0]);

        Trainee created = traineeService.createTrainee("John", "Doe", new Date(), null);

        assertNull(created.getAddress());
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void testAuthenticate_Success() {
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        assertTrue(traineeService.authenticate("John.Doe", "password"));
    }

    @Test
    void testAuthenticate_Failure() {
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        assertFalse(traineeService.authenticate("John.Doe", "wrongPassword"));
    }

    @Test
    void testAuthenticate_UserNotFound() {
        when(traineeRepository.findByUsername("Unknown")).thenReturn(Optional.empty());
        assertFalse(traineeService.authenticate("Unknown", "password"));
    }

    @Test
    void testGetByUsername_Success() {
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        Trainee found = traineeService.getByUsername("John.Doe");
        assertEquals(trainee, found);
    }

    @Test
    void testGetByUsername_NotFound() {
        when(traineeRepository.findByUsername("Unknown")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> traineeService.getByUsername("Unknown"));
    }

    @Test
    void testChangePassword_Success() {
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        traineeService.changePassword("John.Doe", "password", "newPassword");
        assertEquals("newPassword", trainee.getPassword());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void testChangePassword_AuthFailed() {
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        assertThrows(IllegalArgumentException.class, () -> 
            traineeService.changePassword("John.Doe", "wrongPassword", "newPassword")
        );
    }

    @Test
    void testUpdateTrainee() {
        when(traineeRepository.save(trainee)).thenReturn(trainee);
        Trainee updated = traineeService.updateTrainee(trainee);
        assertEquals(trainee, updated);
        verify(traineeRepository).save(trainee);
    }

    @Test
    void testToggleActivation() {
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        traineeService.toggleActivation("John.Doe", false);
        assertFalse(trainee.isActive());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void testDeleteByUsername() {
        traineeService.deleteByUsername("John.Doe");
        verify(traineeRepository).deleteByUsername("John.Doe");
    }

    @Test
    void testUpdateTrainersList_Success() {
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        Trainer trainer1 = new Trainer();
        trainer1.setUsername("Trainer1");
        when(trainerRepository.findByUsername("Trainer1")).thenReturn(Optional.of(trainer1));

        List<Trainer> trainers = traineeService.updateTrainersList("John.Doe", List.of("Trainer1"));

        assertEquals(1, trainers.size());
        assertTrue(trainers.contains(trainer1));
        verify(traineeRepository).save(trainee);
    }

    @Test
    void testUpdateTrainersList_EmptyList() {
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        
        List<Trainer> trainers = traineeService.updateTrainersList("John.Doe", List.of());

        assertTrue(trainers.isEmpty());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void testUpdateTrainersList_NullList() {
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        
        List<Trainer> trainers = traineeService.updateTrainersList("John.Doe", null);

        assertTrue(trainers.isEmpty());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void testFindAll() {
        List<Trainee> trainees = List.of(trainee);
        when(traineeRepository.findAll()).thenReturn(trainees);
        assertEquals(trainees, traineeService.findAll());
    }
}
