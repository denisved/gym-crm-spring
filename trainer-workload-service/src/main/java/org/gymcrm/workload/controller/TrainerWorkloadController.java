package org.gymcrm.workload.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gymcrm.workload.dto.TrainerWorkloadRequest;
import org.gymcrm.workload.service.WorkloadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/trainer-workload")
@RequiredArgsConstructor
public class TrainerWorkloadController {

    private final WorkloadService workloadService;

    @PostMapping
    public ResponseEntity<Void> updateTrainerWorkload(@Valid @RequestBody TrainerWorkloadRequest request) {
        log.info("Received workload request for trainer: {} with action: {}",
                request.getUsername(), request.getActionType());

        workloadService.processWorkload(request);

        return ResponseEntity.ok().build();
    }
}