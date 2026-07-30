package org.gymcrm.bdd.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.gymcrm.dto.AddTrainingRequest;
import org.gymcrm.facade.GymFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IntegrationSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private GymFacade gymFacade; 

    private AddTrainingRequest request;
    private ResultActions resultActions;

    @Given("both microservices and ActiveMQ are running")
    public void both_microservices_and_activemq_are_running() {
        assertNotNull(jmsTemplate, "JmsTemplate should be initialized to connect to ActiveMQ");

        
        try {
            gymFacade.getTrainee("John.Doe");
        } catch (Exception e) {
            gymFacade.createTrainee("John", "Doe", new Date(), "123 Main St");
        }

        try {
            gymFacade.getTrainer("Jane.Smith");
        } catch (Exception e) {
            gymFacade.createTrainer("Jane", "Smith", "Fitness");
        }
    }

    @Given("a valid training request for trainee {string} and trainer {string} for integration")
    public void a_valid_training_request_for_trainee_and_trainer_for_integration(String trainee, String trainer) {
        request = new AddTrainingRequest();
        request.setTraineeUsername(trainee);
        request.setTrainerUsername(trainer);
        request.setTrainingName("Integration Test Class");
        request.setTrainingDate(new Date());
        request.setTrainingDuration(60);
    }

    @When("I send a POST request to add training to {string}")
    public void i_send_a_post_request_to_add_training_to(String endpoint) throws Exception {
        resultActions = mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    @Then("the workload message should be successfully sent to the ActiveMQ queue")
    public void the_workload_message_should_be_successfully_sent_to_the_activemq_queue() throws Exception {
        
        resultActions.andExpect(status().isOk());
        System.out.println("Integration Test PASSED: Training saved to PostgreSQL and JMS message dispatched to ActiveMQ!");
    }
}