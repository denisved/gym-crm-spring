package org.gymcrm.bdd.component;

import io.cucumber.java.en.Given;
import org.gymcrm.dto.AddTrainingRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Date;

public class TrainingSteps {

    @Given("a valid training request for trainee {string} and trainer {string}")
    public void a_valid_training_request_for_trainee_and_trainer(String trainee, String trainer) {
        AddTrainingRequest request = new AddTrainingRequest();
        request.setTraineeUsername(trainee);
        request.setTrainerUsername(trainer);
        request.setTrainingName("Yoga Class");
        request.setTrainingDate(new Date());
        request.setTrainingDuration(60);
        CommonHttpSteps.sharedRequest = request;
    }

    @Given("an invalid training request with missing trainee username")
    public void an_invalid_training_request_with_missing_trainee_username() {
        AddTrainingRequest request = new AddTrainingRequest();
        request.setTrainerUsername("jane.smith");
        request.setTrainingName("Yoga Class");
        request.setTrainingDate(new Date());
        request.setTrainingDuration(60);
        CommonHttpSteps.sharedRequest = request;
    }

    @Given("an existing training with ID {int}")
    public void an_existing_training_with_id(Integer id) {
        
    }

    @Given("a valid request to fetch trainings for trainee {string}")
    public void a_valid_request_to_fetch_trainings_for_trainee(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList())
        );
    }
}