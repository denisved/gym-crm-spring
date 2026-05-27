package org.gymcrm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gymcrm.dto.PasswordChangeRequest;
import org.gymcrm.dto.TraineeRegistrationRequest;
import org.gymcrm.dto.TrainerRegistrationRequest;
import org.gymcrm.facade.GymFacade;
import org.gymcrm.model.Trainee;
import org.gymcrm.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GymFacade gymFacade;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void registerTrainee_Success() throws Exception {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.doe");
        trainee.setPassword("pass123");

        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDateOfBirth(new Date());
        request.setAddress("Street 1");

        when(gymFacade.createTrainee(anyString(), anyString(), any(), anyString())).thenReturn(trainee);

        mockMvc.perform(post("/api/v1/auth/trainee/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.password").value("pass123"));
    }

    @Test
    void registerTrainer_Success() throws Exception {
        Trainer trainer = new Trainer();
        trainer.setUsername("jane.smith");
        trainer.setPassword("pass456");

        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setSpecialization("Yoga");

        when(gymFacade.createTrainer(anyString(), anyString(), anyString())).thenReturn(trainer);

        mockMvc.perform(post("/api/v1/auth/trainer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jane.smith"))
                .andExpect(jsonPath("$.password").value("pass456"));
    }

    @Test
    void login_Success() throws Exception {
        when(gymFacade.authenticate("user", "pass")).thenReturn(true);

        mockMvc.perform(get("/api/v1/auth/login")
                        .param("username", "user")
                        .param("password", "pass"))
                .andExpect(status().isOk());
    }

    @Test
    void login_Failure() throws Exception {
        when(gymFacade.authenticate("user", "pass")).thenReturn(false);

        mockMvc.perform(get("/api/v1/auth/login")
                        .param("username", "user")
                        .param("password", "pass"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changePassword_Success() throws Exception {
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setUsername("user");
        request.setOldPassword("old");
        request.setNewPassword("new");

        when(gymFacade.authenticate("user", "old")).thenReturn(true);
        doNothing().when(gymFacade).changeTraineePassword("user", "old", "new");

        mockMvc.perform(put("/api/v1/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void changePassword_Trainer_Success() throws Exception {
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setUsername("user");
        request.setOldPassword("old");
        request.setNewPassword("new");

        when(gymFacade.authenticate("user", "old")).thenReturn(true);
        doThrow(new IllegalArgumentException()).when(gymFacade).changeTraineePassword("user", "old", "new");
        doNothing().when(gymFacade).changeTrainerPassword("user", "old", "new");

        mockMvc.perform(put("/api/v1/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void changePassword_Unauthorized() throws Exception {
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setUsername("user");
        request.setOldPassword("wrong");
        request.setNewPassword("new");

        when(gymFacade.authenticate("user", "wrong")).thenReturn(false);

        mockMvc.perform(put("/api/v1/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changePassword_NotFound() throws Exception {
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setUsername("user");
        request.setOldPassword("old");
        request.setNewPassword("new");

        when(gymFacade.authenticate("user", "old")).thenReturn(true);
        doThrow(new IllegalArgumentException()).when(gymFacade).changeTraineePassword("user", "old", "new");
        doThrow(new IllegalArgumentException()).when(gymFacade).changeTrainerPassword("user", "old", "new");

        mockMvc.perform(put("/api/v1/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
