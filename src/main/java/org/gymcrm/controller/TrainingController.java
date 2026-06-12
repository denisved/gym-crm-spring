package org.gymcrm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gymcrm.dto.AddTrainingRequest;
import org.gymcrm.dto.TrainingResponse;
import org.gymcrm.facade.GymFacade;
import org.gymcrm.model.Training;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/trainings")
@RequiredArgsConstructor
@Tag(name = "Trainings", description = "Endpoints for trainings management")
public class TrainingController {

    private final GymFacade gymFacade;

    @PostMapping
    @Operation(summary = "Add Training")
    public ResponseEntity<Void> addTraining(@RequestBody @Valid AddTrainingRequest request) {
        gymFacade.createTraining(
                request.getTraineeUsername(),
                request.getTrainerUsername(),
                request.getTrainingName(),
                request.getTrainingDate(),
                request.getTrainingDuration()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/trainee/{username}")
    @PreAuthorize("#username == authentication.name")
    @Operation(summary = "Get Trainee Trainings List")
    public ResponseEntity<List<TrainingResponse>> getTraineeTrainings(
            @PathVariable String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date periodFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date periodTo,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) String trainingType) {

        List<Training> trainings = gymFacade.getTraineeTrainings(username, periodFrom, periodTo, trainerName, trainingType);

        List<TrainingResponse> response = trainings.stream()
                .map(t -> new TrainingResponse(
                        t.getTrainingName(),
                        t.getTrainingDate(),
                        t.getTrainingType().getTrainingTypeName(),
                        t.getTrainingDuration(),
                        t.getTrainer().getUsername(),
                        null
                )).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/trainer/{username}")
    @PreAuthorize("#username == authentication.name")
    @Operation(summary = "Get Trainer Trainings List")
    public ResponseEntity<List<TrainingResponse>> getTrainerTrainings(
            @PathVariable String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date periodFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date periodTo,
            @RequestParam(required = false) String traineeName) {

        List<Training> trainings = gymFacade.getTrainerTrainings(username, periodFrom, periodTo, traineeName);

        List<TrainingResponse> response = trainings.stream()
                .map(t -> new TrainingResponse(
                        t.getTrainingName(),
                        t.getTrainingDate(),
                        t.getTrainingType().getTrainingTypeName(),
                        t.getTrainingDuration(),
                        null,
                        t.getTrainee().getUsername()
                )).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}