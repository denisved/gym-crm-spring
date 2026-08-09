package org.gymcrm.workload.bdd.component;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.gymcrm.workload.dto.TrainerWorkloadRequest;
import org.gymcrm.workload.listener.WorkloadMessageListener;
import org.gymcrm.workload.model.TrainerWorkload;
import org.gymcrm.workload.repository.TrainerWorkloadRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WorkloadSteps {

    @Autowired
    private WorkloadMessageListener workloadMessageListener;
    
    @Autowired
    private TrainerWorkloadRepository trainerWorkloadRepository;

    private TrainerWorkloadRequest request;

    @Given("a valid workload message for trainer {string} to add {int} minutes")
    public void a_valid_workload_message_for_trainer_to_add_minutes(String username, Integer duration) {
        request = new TrainerWorkloadRequest();
        request.setUsername(username);
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setIsActive(true);
        request.setTrainingDate(new Date());
        request.setTrainingDuration(duration);

        
        
        request.setActionType(org.gymcrm.workload.dto.ActionType.ADD);

        
        when(trainerWorkloadRepository.findByUsername(username)).thenReturn(Optional.empty());
    }

    @Given("a valid workload message for trainer {string} to delete {int} minutes")
    public void a_valid_workload_message_for_trainer_to_delete_minutes(String username, Integer duration) {
        request = new TrainerWorkloadRequest();
        request.setUsername(username);
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setIsActive(true);
        request.setTrainingDate(new Date());
        request.setTrainingDuration(duration);
        request.setActionType(org.gymcrm.workload.dto.ActionType.DELETE);

        
        TrainerWorkload existingWorkload = new TrainerWorkload();
        existingWorkload.setUsername(username);
        when(trainerWorkloadRepository.findByUsername(username)).thenReturn(Optional.of(existingWorkload));
    }

    @When("the workload message is received by the listener")
    public void the_workload_message_is_received_by_the_listener() {
        org.mockito.Mockito.reset(trainerWorkloadRepository);

        workloadMessageListener.receiveWorkloadMessage(request, "test-tx-12345");
    }

    @Then("the trainer's workload document should be saved in MongoDB")
    public void the_trainer_s_workload_document_should_be_saved_in_mongodb() {
        
        verify(trainerWorkloadRepository, times(1)).save(any(TrainerWorkload.class));
    }
}