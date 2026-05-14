package org.gymcrm.service;

import org.gymcrm.model.Trainer;
import org.gymcrm.model.TrainingType;
import org.gymcrm.model.User;
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
        trainer.setPassword("password");
        trainer.setActive(true);
        trainer.setSpecialization(trainingType);
    }

    @Test
    void testCreateTrainer_Success() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        when(credentialsGenerator.generateUsername(anyString(), anyString(), anyList())).thenReturn("Jane.Smith");
        when(credentialsGenerator.generatePassword()).thenReturn("randomPass");
        when(trainingTypeRepository.findByTrainingTypeNameIgnoreCase("Yoga")).thenReturn(Optional.of(trainingType));
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(i -> i.getArguments()[0]);

        Trainer created = trainerService.createTrainer("Jane", "Smith", "Yoga");

        assertNotNull(created);
        assertEquals("Jane", created.getFirstName());
        assertEquals("Yoga", created.getSpecialization().getTrainingTypeName());
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void testCreateTrainer_InvalidSpecialization() {
        when(trainingTypeRepository.findByTrainingTypeNameIgnoreCase("Unknown")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> 
            trainerService.createTrainer("Jane", "Smith", "Unknown")
        );
    }

    @Test
    void testAuthenticate_Success() {
        when(trainerRepository.findByUsername("Jane.Smith")).thenReturn(Optional.of(trainer));
        assertTrue(trainerService.authenticate("Jane.Smith", "password"));
    }

    @Test
    void testGetByUsername_Success() {
        when(trainerRepository.findByUsername("Jane.Smith")).thenReturn(Optional.of(trainer));
        Trainer found = trainerService.getByUsername("Jane.Smith");
        assertEquals(trainer, found);
    }

    @Test
    void testChangePassword_Success() {
        when(trainerRepository.findByUsername("Jane.Smith")).thenReturn(Optional.of(trainer));
        trainerService.changePassword("Jane.Smith", "password", "newPass");
        assertEquals("newPass", trainer.getPassword());
        verify(trainerRepository).save(trainer);
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
        trainerService.toggleActivation("Jane.Smith", false);
        assertFalse(trainer.isActive());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void testGetUnassignedTrainers() {
        List<Trainer> unassigned = List.of(trainer);
        when(trainerRepository.getUnassignedTrainers("traineeUser")).thenReturn(unassigned);
        assertEquals(unassigned, trainerService.getUnassignedTrainers("traineeUser"));
    }

    @Test
    void testFindAll() {
        List<Trainer> trainers = List.of(trainer);
        when(trainerRepository.findAll()).thenReturn(trainers);
        assertEquals(trainers, trainerService.findAll());
    }
}
