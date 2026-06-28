package org.gymcrm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gymcrm.dto.StatusChangeRequest;
import org.gymcrm.dto.UpdateTrainerRequest;
import org.gymcrm.facade.GymFacade;
import org.gymcrm.model.Trainer;
import org.gymcrm.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GymFacade gymFacade;

    @InjectMocks
    private TrainerController trainerController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(trainerController).build();
    }

    @Test
    void getTrainerProfile_Success() throws Exception {
        Trainer trainer = createSampleTrainer("jane.smith");

        when(gymFacade.getTrainer("jane.smith")).thenReturn(trainer);

        mockMvc.perform(get("/api/v1/trainers/jane.smith"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jane.smith"));
    }

    @Test
    void updateTrainerProfile_Success() throws Exception {
        Trainer trainer = createSampleTrainer("jane.smith");
        trainer.setActive(true);

        UpdateTrainerRequest request = new UpdateTrainerRequest();
        request.setUsername("jane.smith");
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setSpecialization("Yoga");
        request.setIsActive(false);

        when(gymFacade.getTrainer("jane.smith")).thenReturn(trainer);

        mockMvc.perform(put("/api/v1/trainers/jane.smith")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(gymFacade).toggleTrainerActivation("jane.smith");
    }

    @Test
    void updateTrainerProfile_SpecializationChange_Success() throws Exception {
        Trainer trainer = createSampleTrainer("jane.smith");
        trainer.setActive(true);

        UpdateTrainerRequest request = new UpdateTrainerRequest();
        request.setUsername("jane.smith");
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setSpecialization("Pilates");
        request.setIsActive(true);

        when(gymFacade.getTrainer("jane.smith")).thenReturn(trainer);
        when(gymFacade.updateTrainerSpecialization("jane.smith", "Pilates")).thenReturn(trainer);

        mockMvc.perform(put("/api/v1/trainers/jane.smith")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(gymFacade).updateTrainerSpecialization("jane.smith", "Pilates");
    }

    @Test
    void toggleTrainerStatus_Success() throws Exception {
        Trainer trainer = createSampleTrainer("jane.smith");
        trainer.setActive(true);

        StatusChangeRequest request = new StatusChangeRequest();
        request.setIsActive(false);

        when(gymFacade.getTrainer("jane.smith")).thenReturn(trainer);

        mockMvc.perform(patch("/api/v1/trainers/jane.smith/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(gymFacade).toggleTrainerActivation("jane.smith");
    }

    private Trainer createSampleTrainer(String username) {
        Trainer trainer = new Trainer();
        trainer.setUsername(username);
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Yoga");
        trainer.setSpecialization(type);
        trainer.setTrainees(Collections.emptyList());
        return trainer;
    }
}
