package org.gymcrm.bdd.component;

import io.cucumber.java.en.Given;
import org.gymcrm.dto.StatusChangeRequest;
import org.gymcrm.dto.UpdateTrainerRequest;
import org.gymcrm.facade.GymFacade;
import org.gymcrm.model.Trainer;
import org.gymcrm.model.TrainingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.mockito.Mockito.when;

public class TrainerSteps {

    @Autowired
    private GymFacade gymFacade;

    @Given("a valid authorization and mock data for trainer {string}")
    public void a_valid_authorization_and_mock_data_for_trainer(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList())
        );

        Trainer mockTrainer = new Trainer();
        mockTrainer.setUsername(username);
        mockTrainer.setFirstName("Jane");
        mockTrainer.setLastName("Smith");
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Fitness");
        mockTrainer.setSpecialization(type);
        mockTrainer.setTrainees(Collections.emptyList());
        when(gymFacade.getTrainer(username)).thenReturn(mockTrainer);
    }

    @Given("a valid update request for trainer {string}")
    public void a_valid_update_request_for_trainer(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList())
        );
        UpdateTrainerRequest req = new UpdateTrainerRequest();
        req.setUsername(username);
        req.setFirstName("Jane");
        req.setLastName("Smith");
        req.setSpecialization("Yoga");
        req.setIsActive(true);
        CommonHttpSteps.sharedRequest = req;

        Trainer mockTrainer = new Trainer();
        mockTrainer.setUsername(username);
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Yoga");
        mockTrainer.setSpecialization(type);
        mockTrainer.setTrainees(Collections.emptyList());
        when(gymFacade.getTrainer(username)).thenReturn(mockTrainer);
    }

    @Given("a valid status change request for trainer {string}")
    public void a_valid_status_change_request_for_trainer(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList())
        );
        StatusChangeRequest req = new StatusChangeRequest();
        req.setIsActive(false);
        CommonHttpSteps.sharedRequest = req;

        Trainer mockTrainer = new Trainer();
        mockTrainer.setUsername(username);
        when(gymFacade.getTrainer(username)).thenReturn(mockTrainer);
    }
}