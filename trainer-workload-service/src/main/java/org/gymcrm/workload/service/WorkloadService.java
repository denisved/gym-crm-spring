package org.gymcrm.workload.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gymcrm.workload.dto.ActionType;
import org.gymcrm.workload.dto.TrainerWorkloadRequest;
import org.gymcrm.workload.model.TrainerWorkload;
import org.gymcrm.workload.repository.TrainerWorkloadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkloadService {

    private final TrainerWorkloadRepository workloadRepository;

    @Transactional
    public void processWorkload(TrainerWorkloadRequest request) {
        log.info("Processing workload for trainer: {}, Action: {}", request.getUsername(), request.getActionType());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(request.getTrainingDate());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        TrainerWorkload workload = workloadRepository.findByUsernameAndYearAndMonth(request.getUsername(), year, month)
                .orElseGet(() -> {
                    TrainerWorkload newWorkload = new TrainerWorkload();
                    newWorkload.setUsername(request.getUsername());
                    newWorkload.setFirstName(request.getFirstName());
                    newWorkload.setLastName(request.getLastName());
                    newWorkload.setIsActive(request.getIsActive());
                    newWorkload.setYear(year);
                    newWorkload.setMonth(month);
                    newWorkload.setTrainingSummaryDuration(0);
                    return newWorkload;
                });

        workload.setIsActive(request.getIsActive());

        if (request.getActionType() == ActionType.ADD) {
            workload.setTrainingSummaryDuration(workload.getTrainingSummaryDuration() + request.getTrainingDuration());
        } else if (request.getActionType() == ActionType.DELETE) {
            int newDuration = workload.getTrainingSummaryDuration() - request.getTrainingDuration();
            workload.setTrainingSummaryDuration(Math.max(newDuration, 0));
        }

        workloadRepository.save(workload);
        log.info("Workload updated successfully. New duration: {} minutes", workload.getTrainingSummaryDuration());
    }
}