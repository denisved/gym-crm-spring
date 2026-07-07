package org.gymcrm.service;

import org.gymcrm.model.Trainer;
import org.gymcrm.model.TrainingType;
import org.gymcrm.repository.TrainerRepository;
import org.gymcrm.repository.TrainingTypeRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CredentialsGenerator credentialsGenerator;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private ValidationService validationService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TrainerService trainerService;

    private Trainer trainer;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainingType = new TrainingType();
        trainingType.setTrainingTypeName("Yoga");

        trainer = new Trainer();
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");
        trainer.setUsername("Jane.Smith");
        trainer.setPassword("hashedPassword");
        trainer.setActive(true);
        trainer.setSpecialization(trainingType);

        lenient().doNothing().when(validationService).validateName(anyString(), anyString());
    }

    @Test
    void testCreateTrainer_Success() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        when(credentialsGenerator.generateUsername(anyString(), anyString(), anyList())).thenReturn("Jane.Smith");
        when(credentialsGenerator.generatePassword()).thenReturn("randomPass");
        when(passwordEncoder.encode("randomPass")).thenReturn("hashedPass");
        when(trainingTypeRepository.findByTrainingTypeNameIgnoreCase("Yoga")).thenReturn(Optional.of(trainingType));
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(i -> i.getArguments()[0]);

        Trainer created = trainerService.createTrainer("Jane", "Smith", "Yoga");

        assertNotNull(created);
        assertEquals("Jane", created.getFirstName());
        assertEquals("hashedPass", created.getPassword());
        assertEquals("randomPass", created.getPlainPassword());
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void testChangePassword_Success() {
        when(trainerRepository.findByUsername("Jane.Smith")).thenReturn(Optional.of(trainer));
        when(passwordEncoder.matches("oldPass", "hashedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("newHashedPass");

        trainerService.changePassword("Jane.Smith", "oldPass", "newPass");

        assertEquals("newHashedPass", trainer.getPassword());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void testChangePassword_AuthFailed() {
        when(trainerRepository.findByUsername("Jane.Smith")).thenReturn(Optional.of(trainer));
        when(passwordEncoder.matches("wrongPass", "hashedPassword")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> 
            trainerService.changePassword("Jane.Smith", "wrongPass", "newPass")
        );
    }

    @Test
    void testUpdateTrainerSpecialization_Success() {
        TrainingType newType = new TrainingType();
        newType.setTrainingTypeName("Pilates");
        when(trainerRepository.findByUsername("Jane.Smith")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeNameIgnoreCase("Pilates")).thenReturn(Optional.of(newType));
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(i -> i.getArguments()[0]);

        Trainer updated = trainerService.updateTrainerSpecialization("Jane.Smith", "Pilates");

        assertEquals("Pilates", updated.getSpecialization().getTrainingTypeName());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void testToggleActivation() {
        when(trainerRepository.findByUsername("Jane.Smith")).thenReturn(Optional.of(trainer));
        trainerService.toggleActivation("Jane.Smith");
        assertFalse(trainer.isActive());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void testFindAll() {
        List<Trainer> trainers = List.of(trainer);
        when(trainerRepository.findAll()).thenReturn(trainers);
        assertEquals(trainers, trainerService.findAll());
    }
}
