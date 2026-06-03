package org.gymcrm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gymcrm.dto.AddTrainingRequest;
import org.gymcrm.facade.GymFacade;
import org.gymcrm.model.Trainee;
import org.gymcrm.model.Trainer;
import org.gymcrm.model.Training;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GymFacade gymFacade;

    @InjectMocks
    private TrainingController trainingController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(trainingController).build();
    }

    @Test
    void addTraining_Success() throws Exception {
        AddTrainingRequest request = new AddTrainingRequest();
        request.setTraineeUsername("trainee");
        request.setTrainerUsername("trainer");
        request.setTrainingName("Yoga");
        request.setTrainingDate(new Date());
        request.setTrainingDuration(60);

        when(gymFacade.createTraining(anyString(), anyString(), anyString(), any(), any())).thenReturn(new Training());

        mockMvc.perform(post("/api/v1/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getTraineeTrainings_Success() throws Exception {
        Training training = createSampleTraining();
        when(gymFacade.getTraineeTrainings(eq("trainee"), any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(training));

        mockMvc.perform(get("/api/v1/trainings/trainee/trainee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Yoga"));
    }

    @Test
    void getTrainerTrainings_Success() throws Exception {
        Training training = createSampleTraining();
        when(gymFacade.getTrainerTrainings(eq("trainer"), any(), any(), any()))
                .thenReturn(Collections.singletonList(training));

        mockMvc.perform(get("/api/v1/trainings/trainer/trainer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Yoga"));
    }

    private Training createSampleTraining() {
        Training training = new Training();
        training.setTrainingName("Yoga");
        training.setTrainingDate(new Date());
        training.setTrainingDuration(60);
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Yoga");
        training.setTrainingType(type);
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer");
        training.setTrainer(trainer);
        Trainee trainee = new Trainee();
        trainee.setUsername("trainee");
        training.setTrainee(trainee);
        return training;
    }
}
