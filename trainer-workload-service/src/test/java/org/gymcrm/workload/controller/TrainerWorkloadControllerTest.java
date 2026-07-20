package org.gymcrm.workload.controller;

import org.gymcrm.workload.dto.ActionType;
import org.gymcrm.workload.dto.TrainerWorkloadRequest;
import org.gymcrm.workload.service.WorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadControllerTest {

    @Mock
    private WorkloadService workloadService;

    @InjectMocks
    private TrainerWorkloadController controller;

    private TrainerWorkloadRequest request;

    @BeforeEach
    void setUp() {
        request = new TrainerWorkloadRequest();
        request.setUsername("t.trainer");
        request.setFirstName("Tom");
        request.setLastName("Trainer");
        request.setIsActive(true);
        request.setTrainingDate(new Date());
        request.setTrainingDuration(60);
        request.setActionType(ActionType.ADD);
    }

    @Test
    void updateTrainerWorkload_ShouldCallServiceAndReturnOk() {
        ResponseEntity<Void> response = controller.updateTrainerWorkload(request);

        verify(workloadService).processWorkload(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
