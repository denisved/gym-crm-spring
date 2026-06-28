package org.gymcrm.workload.service;

import org.gymcrm.workload.dto.ActionType;
import org.gymcrm.workload.dto.TrainerWorkloadRequest;
import org.gymcrm.workload.model.TrainerWorkload;
import org.gymcrm.workload.repository.TrainerWorkloadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkloadServiceTest {

    @Mock
    private TrainerWorkloadRepository workloadRepository;

    @InjectMocks
    private WorkloadService workloadService;

    private TrainerWorkloadRequest request;
    private Date testDate;
    private int expectedYear;
    private int expectedMonth;

    @BeforeEach
    void setUp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2023, Calendar.OCTOBER, 15); // October is month 9 in Calendar
        testDate = calendar.getTime();
        expectedYear = 2023;
        expectedMonth = 10; // Calendar.MONTH + 1

        request = new TrainerWorkloadRequest();
        request.setUsername("j.doe");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setIsActive(true);
        request.setTrainingDate(testDate);
        request.setTrainingDuration(60);
    }

    @Test
    void processWorkload_WhenAddAction_ShouldAddNewWorkload() {
        // Arrange
        request.setActionType(ActionType.ADD);
        when(workloadRepository.findByUsernameAndYearAndMonth("j.doe", expectedYear, expectedMonth))
                .thenReturn(Optional.empty());

        // Act
        workloadService.processWorkload(request);

        // Assert
        verify(workloadRepository).save(argThat(workload -> 
            workload.getUsername().equals("j.doe") &&
            workload.getFirstName().equals("John") &&
            workload.getLastName().equals("Doe") &&
            workload.getIsActive().equals(true) &&
            workload.getYear() == expectedYear &&
            workload.getMonth() == expectedMonth &&
            workload.getTrainingSummaryDuration() == 60
        ));
    }

    @Test
    void processWorkload_WhenAddActionAndExistingWorkload_ShouldIncreaseDuration() {
        // Arrange
        request.setActionType(ActionType.ADD);
        
        TrainerWorkload existingWorkload = new TrainerWorkload();
        existingWorkload.setUsername("j.doe");
        existingWorkload.setTrainingSummaryDuration(120);
        
        when(workloadRepository.findByUsernameAndYearAndMonth("j.doe", expectedYear, expectedMonth))
                .thenReturn(Optional.of(existingWorkload));

        // Act
        workloadService.processWorkload(request);

        // Assert
        verify(workloadRepository).save(argThat(workload -> 
            workload.getTrainingSummaryDuration() == 180 &&
            workload.getIsActive().equals(true)
        ));
    }

    @Test
    void processWorkload_WhenDeleteAction_ShouldDecreaseDuration() {
        // Arrange
        request.setActionType(ActionType.DELETE);
        
        TrainerWorkload existingWorkload = new TrainerWorkload();
        existingWorkload.setUsername("j.doe");
        existingWorkload.setTrainingSummaryDuration(120);
        
        when(workloadRepository.findByUsernameAndYearAndMonth("j.doe", expectedYear, expectedMonth))
                .thenReturn(Optional.of(existingWorkload));

        // Act
        workloadService.processWorkload(request);

        // Assert
        verify(workloadRepository).save(argThat(workload -> 
            workload.getTrainingSummaryDuration() == 60
        ));
    }

    @Test
    void processWorkload_WhenDeleteActionResultsInNegative_ShouldSetDurationToZero() {
        // Arrange
        request.setActionType(ActionType.DELETE);
        request.setTrainingDuration(150); // Greater than existing
        
        TrainerWorkload existingWorkload = new TrainerWorkload();
        existingWorkload.setUsername("j.doe");
        existingWorkload.setTrainingSummaryDuration(100);
        
        when(workloadRepository.findByUsernameAndYearAndMonth("j.doe", expectedYear, expectedMonth))
                .thenReturn(Optional.of(existingWorkload));

        // Act
        workloadService.processWorkload(request);

        // Assert
        verify(workloadRepository).save(argThat(workload -> 
            workload.getTrainingSummaryDuration() == 0
        ));
    }

    @Test
    void processWorkload_WhenIsActiveChanges_ShouldUpdateIsActive() {
        // Arrange
        request.setActionType(ActionType.ADD);
        request.setIsActive(false);
        
        TrainerWorkload existingWorkload = new TrainerWorkload();
        existingWorkload.setUsername("j.doe");
        existingWorkload.setIsActive(true);
        existingWorkload.setTrainingSummaryDuration(100);
        
        when(workloadRepository.findByUsernameAndYearAndMonth("j.doe", expectedYear, expectedMonth))
                .thenReturn(Optional.of(existingWorkload));

        // Act
        workloadService.processWorkload(request);

        // Assert
        verify(workloadRepository).save(argThat(workload -> 
            workload.getIsActive().equals(false)
        ));
    }
}
