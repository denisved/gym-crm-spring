package org.gymcrm.workload.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gymcrm.workload.dto.ActionType;
import org.gymcrm.workload.dto.TrainerWorkloadRequest;
import org.gymcrm.workload.model.MonthWorkload;
import org.gymcrm.workload.model.TrainerWorkload;
import org.gymcrm.workload.model.YearWorkload;
import org.gymcrm.workload.repository.TrainerWorkloadRepository;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkloadService {

    private final TrainerWorkloadRepository workloadRepository;

    public void processWorkload(TrainerWorkloadRequest request) {
        log.info("Processing workload for trainer: {}, Action: {}", request.getUsername(), request.getActionType());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(request.getTrainingDate());
        int yearValue = calendar.get(Calendar.YEAR);
        int monthValue = calendar.get(Calendar.MONTH) + 1;

        TrainerWorkload workload = workloadRepository.findByUsername(request.getUsername())
                .orElseGet(() -> {
                    log.info("Trainer profile not found. Creating new for username: {}", request.getUsername());
                    TrainerWorkload newWorkload = new TrainerWorkload();
                    newWorkload.setUsername(request.getUsername());
                    newWorkload.setFirstName(request.getFirstName());
                    newWorkload.setLastName(request.getLastName());
                    return newWorkload;
                });

        workload.setIsActive(request.getIsActive());

        YearWorkload yearWorkload = workload.getYears().stream()
                .filter(y -> y.getYear() == yearValue)
                .findFirst()
                .orElseGet(() -> {
                    YearWorkload newYear = new YearWorkload(yearValue, new java.util.ArrayList<>());
                    workload.getYears().add(newYear);
                    return newYear;
                });

        MonthWorkload monthWorkload = yearWorkload.getMonths().stream()
                .filter(m -> m.getMonth() == monthValue)
                .findFirst()
                .orElseGet(() -> {
                    MonthWorkload newMonth = new MonthWorkload(monthValue, 0);
                    yearWorkload.getMonths().add(newMonth);
                    return newMonth;
                });

        int durationChange = request.getActionType() == ActionType.ADD
                ? request.getTrainingDuration()
                : -request.getTrainingDuration();

        int newDuration = Math.max(0, monthWorkload.getTrainingSummaryDuration() + durationChange);
        monthWorkload.setTrainingSummaryDuration(newDuration);

        workloadRepository.save(workload);

        log.info("Workload updated successfully for {}. Year: {}, Month: {}, New duration: {} minutes",
                workload.getUsername(), yearValue, monthValue, newDuration);
    }
}