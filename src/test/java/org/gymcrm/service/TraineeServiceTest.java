package org.gymcrm.service;

import org.gymcrm.model.Trainee;
import org.gymcrm.model.Trainer;
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
import org.springframework.security.crypto.password.PasswordEncoder;

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
    @Mock
    private ValidationService validationService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TraineeService traineeService;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setUsername("John.Doe");
        trainee.setPassword("hashedPassword");
        trainee.setActive(true);

        lenient().doNothing().when(validationService).validateName(anyString(), anyString());
        lenient().doNothing().when(validationService).validateDateOfBirth(any(Date.class));
    }

    @Test
    void testCreateTrainee_Success() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        when(credentialsGenerator.generateUsername(anyString(), anyString(), anyList())).thenReturn("John.Doe");
        when(credentialsGenerator.generatePassword()).thenReturn("randomPass");
        when(passwordEncoder.encode("randomPass")).thenReturn("hashedPass");
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(i -> i.getArguments()[0]);

        Trainee created = traineeService.createTrainee("John", "Doe", new Date(), "Address");

        assertNotNull(created);
        assertEquals("John", created.getFirstName());
        assertEquals("Doe", created.getLastName());
        assertEquals("John.Doe", created.getUsername());
        assertEquals("hashedPass", created.getPassword());
        assertEquals("randomPass", created.getPlainPassword());
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void testChangePassword_Success() {
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(passwordEncoder.matches("oldPass", "hashedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("newHashedPass");

        traineeService.changePassword("John.Doe", "oldPass", "newPass");

        assertEquals("newHashedPass", trainee.getPassword());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void testChangePassword_AuthFailed() {
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(passwordEncoder.matches("wrongPass", "hashedPassword")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> 
            traineeService.changePassword("John.Doe", "wrongPass", "newPass")
        );
    }

    @Test
    void testGetByUsername_Success() {
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        Trainee found = traineeService.getByUsername("John.Doe");
        assertEquals(trainee, found);
    }

    @Test
    void testToggleActivation() {
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        traineeService.toggleActivation("John.Doe");
        assertFalse(trainee.isActive());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void testDeleteByUsername() {
        traineeService.deleteByUsername("John.Doe");
        verify(traineeRepository).deleteByUsername("John.Doe");
    }

    @Test
    void testFindAll() {
        List<Trainee> trainees = List.of(trainee);
        when(traineeRepository.findAll()).thenReturn(trainees);
        assertEquals(trainees, traineeService.findAll());
    }

    @Test
    void testUpdateTrainersList() {
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer1");
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        List<Trainer> result = traineeService.updateTrainersList("John.Doe", List.of("trainer1"));
        assertEquals(1, result.size());
        assertTrue(result.contains(trainer));
    }
}
