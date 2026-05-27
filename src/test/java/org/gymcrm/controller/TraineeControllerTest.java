package org.gymcrm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gymcrm.dto.UpdateTraineeRequest;
import org.gymcrm.facade.GymFacade;
import org.gymcrm.model.Trainee;
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
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GymFacade gymFacade;

    @InjectMocks
    private TraineeController traineeController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(traineeController).build();
    }

    @Test
    void getTraineeProfile_Success() throws Exception {
        Trainee trainee = createSampleTrainee("john.doe");

        when(gymFacade.getTrainee("john.doe")).thenReturn(trainee);

        mockMvc.perform(get("/api/v1/trainees/john.doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"));
    }

    @Test
    void updateTraineeProfile_Success() throws Exception {
        Trainee trainee = createSampleTrainee("john.doe");
        trainee.setActive(true);

        UpdateTraineeRequest request = new UpdateTraineeRequest();
        request.setUsername("john.doe");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDateOfBirth(new Date());
        request.setAddress("New Address");
        request.setIsActive(false);

        when(gymFacade.getTrainee("john.doe")).thenReturn(trainee);
        when(gymFacade.updateTrainee(any(Trainee.class))).thenReturn(trainee);

        mockMvc.perform(put("/api/v1/trainees/john.doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(gymFacade).toggleTraineeActivation("john.doe");
    }

    @Test
    void deleteTraineeProfile_Success() throws Exception {
        doNothing().when(gymFacade).deleteTrainee("john.doe");

        mockMvc.perform(delete("/api/v1/trainees/john.doe"))
                .andExpect(status().isOk());
    }

    @Test
    void getUnassignedTrainers_Success() throws Exception {
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer.one");
        trainer.setActive(true);
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Yoga");
        trainer.setSpecialization(type);

        when(gymFacade.getUnassignedTrainers("john.doe")).thenReturn(Collections.singletonList(trainer));

        mockMvc.perform(get("/api/v1/trainees/john.doe/trainers/unassigned"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("trainer.one"));
    }

    @Test
    void updateTraineeTrainers_Success() throws Exception {
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer.one");
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Yoga");
        trainer.setSpecialization(type);

        when(gymFacade.updateTraineeTrainersList(eq("john.doe"), any())).thenReturn(Collections.singletonList(trainer));

        mockMvc.perform(put("/api/v1/trainees/john.doe/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Collections.singletonList("trainer.one"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("trainer.one"));
    }

    private Trainee createSampleTrainee(String username) {
        Trainee trainee = new Trainee();
        trainee.setUsername(username);
        trainee.setFirstName("First");
        trainee.setLastName("Last");
        trainee.setTrainers(Collections.emptyList());
        return trainee;
    }
}
