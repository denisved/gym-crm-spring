package org.gymcrm.workload.service;

import org.gymcrm.workload.dto.ActionType;
import org.gymcrm.workload.dto.TrainerWorkloadRequest;
import org.gymcrm.workload.model.MonthWorkload;
import org.gymcrm.workload.model.TrainerWorkload;
import org.gymcrm.workload.model.YearWorkload;
import org.gymcrm.workload.repository.TrainerWorkloadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
        calendar.set(2023, Calendar.OCTOBER, 15); 
        testDate = calendar.getTime();
        expectedYear = 2023;
        expectedMonth = 10; 

        request = new TrainerWorkloadRequest();
        request.setUsername("j.doe");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setIsActive(true);
        request.setTrainingDate(testDate);
        request.setTrainingDuration(60);
    }

    @Test
    void processWorkload_WhenAddActionAndNoProfile_ShouldAddNewWorkload() {
        request.setActionType(ActionType.ADD);
        when(workloadRepository.findByUsername("j.doe")).thenReturn(Optional.empty());

        workloadService.processWorkload(request);

        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);
        verify(workloadRepository).save(captor.capture());

        TrainerWorkload saved = captor.getValue();
        assertEquals("j.doe", saved.getUsername());
        assertEquals("John", saved.getFirstName());
        assertEquals("Doe", saved.getLastName());
        assertTrue(saved.getIsActive());
        
        assertEquals(1, saved.getYears().size());
        YearWorkload yearWorkload = saved.getYears().get(0);
        assertEquals(expectedYear, yearWorkload.getYear());
        
        assertEquals(1, yearWorkload.getMonths().size());
        MonthWorkload monthWorkload = yearWorkload.getMonths().get(0);
        assertEquals(expectedMonth, monthWorkload.getMonth());
        assertEquals(60, monthWorkload.getTrainingSummaryDuration());
    }

    @Test
    void processWorkload_WhenAddActionAndExistingProfileNewYear_ShouldAddNewYear() {
        request.setActionType(ActionType.ADD);
        
        TrainerWorkload existingWorkload = new TrainerWorkload();
        existingWorkload.setUsername("j.doe");
        existingWorkload.setYears(new ArrayList<>());
        
        when(workloadRepository.findByUsername("j.doe")).thenReturn(Optional.of(existingWorkload));

        workloadService.processWorkload(request);

        verify(workloadRepository).save(any(TrainerWorkload.class));
        assertEquals(1, existingWorkload.getYears().size());
        assertEquals(expectedYear, existingWorkload.getYears().get(0).getYear());
        assertEquals(60, existingWorkload.getYears().get(0).getMonths().get(0).getTrainingSummaryDuration());
    }

    @Test
    void processWorkload_WhenAddActionAndExistingProfileNewMonth_ShouldAddNewMonth() {
        request.setActionType(ActionType.ADD);
        
        TrainerWorkload existingWorkload = new TrainerWorkload();
        existingWorkload.setUsername("j.doe");
        YearWorkload yearWorkload = new YearWorkload(expectedYear, new ArrayList<>());
        existingWorkload.getYears().add(yearWorkload);
        
        when(workloadRepository.findByUsername("j.doe")).thenReturn(Optional.of(existingWorkload));

        workloadService.processWorkload(request);

        verify(workloadRepository).save(any(TrainerWorkload.class));
        assertEquals(1, existingWorkload.getYears().get(0).getMonths().size());
        assertEquals(expectedMonth, existingWorkload.getYears().get(0).getMonths().get(0).getMonth());
        assertEquals(60, existingWorkload.getYears().get(0).getMonths().get(0).getTrainingSummaryDuration());
    }

    @Test
    void processWorkload_WhenAddActionAndExistingMonth_ShouldIncreaseDuration() {
        request.setActionType(ActionType.ADD);
        
        TrainerWorkload existingWorkload = new TrainerWorkload();
        existingWorkload.setUsername("j.doe");
        MonthWorkload monthWorkload = new MonthWorkload(expectedMonth, 120);
        List<MonthWorkload> months = new ArrayList<>();
        months.add(monthWorkload);
        YearWorkload yearWorkload = new YearWorkload(expectedYear, months);
        existingWorkload.getYears().add(yearWorkload);
        
        when(workloadRepository.findByUsername("j.doe")).thenReturn(Optional.of(existingWorkload));

        workloadService.processWorkload(request);

        verify(workloadRepository).save(any(TrainerWorkload.class));
        assertEquals(180, existingWorkload.getYears().get(0).getMonths().get(0).getTrainingSummaryDuration());
    }

    @Test
    void processWorkload_WhenDeleteAction_ShouldDecreaseDuration() {
        request.setActionType(ActionType.DELETE);
        
        TrainerWorkload existingWorkload = new TrainerWorkload();
        existingWorkload.setUsername("j.doe");
        MonthWorkload monthWorkload = new MonthWorkload(expectedMonth, 120);
        List<MonthWorkload> months = new ArrayList<>();
        months.add(monthWorkload);
        YearWorkload yearWorkload = new YearWorkload(expectedYear, months);
        existingWorkload.getYears().add(yearWorkload);
        
        when(workloadRepository.findByUsername("j.doe")).thenReturn(Optional.of(existingWorkload));

        workloadService.processWorkload(request);

        verify(workloadRepository).save(any(TrainerWorkload.class));
        assertEquals(60, existingWorkload.getYears().get(0).getMonths().get(0).getTrainingSummaryDuration());
    }

    @Test
    void processWorkload_WhenDeleteActionResultsInNegative_ShouldSetDurationToZero() {
        request.setActionType(ActionType.DELETE);
        request.setTrainingDuration(150); 
        
        TrainerWorkload existingWorkload = new TrainerWorkload();
        existingWorkload.setUsername("j.doe");
        MonthWorkload monthWorkload = new MonthWorkload(expectedMonth, 100);
        List<MonthWorkload> months = new ArrayList<>();
        months.add(monthWorkload);
        YearWorkload yearWorkload = new YearWorkload(expectedYear, months);
        existingWorkload.getYears().add(yearWorkload);
        
        when(workloadRepository.findByUsername("j.doe")).thenReturn(Optional.of(existingWorkload));

        workloadService.processWorkload(request);

        verify(workloadRepository).save(any(TrainerWorkload.class));
        assertEquals(0, existingWorkload.getYears().get(0).getMonths().get(0).getTrainingSummaryDuration());
    }

    @Test
    void processWorkload_WhenIsActiveChanges_ShouldUpdateIsActive() {
        request.setActionType(ActionType.ADD);
        request.setIsActive(false);
        
        TrainerWorkload existingWorkload = new TrainerWorkload();
        existingWorkload.setUsername("j.doe");
        existingWorkload.setIsActive(true);
        MonthWorkload monthWorkload = new MonthWorkload(expectedMonth, 100);
        List<MonthWorkload> months = new ArrayList<>();
        months.add(monthWorkload);
        YearWorkload yearWorkload = new YearWorkload(expectedYear, months);
        existingWorkload.getYears().add(yearWorkload);
        
        when(workloadRepository.findByUsername("j.doe")).thenReturn(Optional.of(existingWorkload));

        workloadService.processWorkload(request);

        verify(workloadRepository).save(any(TrainerWorkload.class));
        assertEquals(false, existingWorkload.getIsActive());
    }
}
