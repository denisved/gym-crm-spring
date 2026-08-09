package org.gymcrm.bdd.component;

import io.cucumber.java.en.Given;
import org.gymcrm.dto.StatusChangeRequest;
import org.gymcrm.dto.UpdateTraineeRequest;
import org.gymcrm.facade.GymFacade;
import org.gymcrm.model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TraineeSteps {

    @Autowired
    private GymFacade gymFacade;

    @Given("a valid authorization and mock data for trainee {string}")
    public void a_valid_authorization_and_mock_data_for_trainee(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList())
        );

        Trainee mockTrainee = new Trainee();
        mockTrainee.setUsername(username);
        mockTrainee.setFirstName("John");
        mockTrainee.setLastName("Doe");
        mockTrainee.setTrainers(Collections.emptyList());
        when(gymFacade.getTrainee(username)).thenReturn(mockTrainee);
    }

    @Given("a valid update request for trainee {string}")
    public void a_valid_update_request_for_trainee(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList())
        );
        UpdateTraineeRequest req = new UpdateTraineeRequest();
        req.setUsername(username);
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setDateOfBirth(new Date());
        req.setAddress("New Address");
        req.setIsActive(true);
        CommonHttpSteps.sharedRequest = req;

        Trainee mockTrainee = new Trainee();
        mockTrainee.setUsername(username);
        mockTrainee.setTrainers(Collections.emptyList());
        when(gymFacade.getTrainee(username)).thenReturn(mockTrainee);
        when(gymFacade.updateTrainee(any())).thenReturn(mockTrainee);
    }

    @Given("a valid status change request for trainee {string}")
    public void a_valid_status_change_request_for_trainee(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList())
        );
        StatusChangeRequest req = new StatusChangeRequest();
        req.setIsActive(false);
        CommonHttpSteps.sharedRequest = req;

        Trainee mockTrainee = new Trainee();
        mockTrainee.setUsername(username);
        when(gymFacade.getTrainee(username)).thenReturn(mockTrainee);
    }

    @Given("an existing trainee with username {string}")
    public void an_existing_trainee_with_username(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList())
        );
    }

    @Given("a valid request to fetch unassigned trainers for {string}")
    public void a_valid_request_to_fetch_unassigned_trainers(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList())
        );
        when(gymFacade.getUnassignedTrainers(username)).thenReturn(Collections.emptyList());
    }

    @Given("a valid request to update trainers list for {string}")
    public void a_valid_request_to_update_trainers_list(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList())
        );
        when(gymFacade.updateTraineeTrainersList(any(), any())).thenReturn(Collections.emptyList());
        CommonHttpSteps.sharedRequest = null;
    }
}