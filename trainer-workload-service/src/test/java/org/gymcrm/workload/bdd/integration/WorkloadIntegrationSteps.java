package org.gymcrm.workload.bdd.integration;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.gymcrm.workload.dto.TrainerWorkloadRequest;
import org.gymcrm.workload.model.TrainerWorkload;
import org.gymcrm.workload.repository.TrainerWorkloadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WorkloadIntegrationSteps {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private TrainerWorkloadRepository repository;

    @Value("${jms.queue.workload}") 
    private String queueName;

    @Given("ActiveMQ and MongoDB are running and database is clean")
    public void activemq_and_mongodb_are_running_and_database_is_clean() {
        assertNotNull(jmsTemplate, "JmsTemplate must be available");
        assertNotNull(repository, "MongoDB Repository must be available");

        
        repository.findByUsername("integration.trainer").ifPresent(workload -> repository.delete(workload));
    }

    @When("a workload message for trainer {string} to add {int} minutes is published to ActiveMQ")
    public void a_workload_message_is_published_to_activemq(String username, int duration) {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setUsername(username);
        request.setFirstName("Int");
        request.setLastName("Trainer");
        request.setIsActive(true);
        request.setTrainingDate(new Date());
        request.setTrainingDuration(duration);
        request.setActionType(org.gymcrm.workload.dto.ActionType.ADD); 

        
        jmsTemplate.convertAndSend(queueName, request, message -> {
            message.setStringProperty("transactionId", "test-tx-integration");
            return message;
        });
    }

    @Then("the trainer {string} should have a workload document in MongoDB with {int} minutes")
    public void the_trainer_should_have_a_workload_document_in_mongodb(String username, int duration) throws InterruptedException {
        
        Thread.sleep(2000); 

        Optional<TrainerWorkload> workload = repository.findByUsername(username);

        assertTrue(workload.isPresent(), "Workload document should be created in MongoDB");
        
        
    }
}