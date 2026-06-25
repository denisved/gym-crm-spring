package org.gymcrm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gymcrm.dto.StatusChangeRequest;
import org.gymcrm.dto.TraineeInfoDto;
import org.gymcrm.dto.TrainerProfileResponse;
import org.gymcrm.dto.UpdateTrainerRequest;
import org.gymcrm.facade.GymFacade;
import org.gymcrm.model.Trainer;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
@Tag(name = "Trainer Profile", description = "Endpoints for managing trainer profiles")
public class TrainerController {

    private final GymFacade gymFacade;

    @GetMapping("/{username}")
    @PreAuthorize("#username == authentication.name")
    @Operation(summary = "Get Trainer Profile")
    public ResponseEntity<TrainerProfileResponse> getTrainerProfile(@PathVariable("username") String username) {
        Trainer trainer = gymFacade.getTrainer(username);
        return ResponseEntity.ok(mapToTrainerProfileResponse(trainer));
    }

    @PutMapping("/{username}")
    @PreAuthorize("#username == authentication.name")
    @Operation(summary = "Update Trainer Profile")
    public ResponseEntity<TrainerProfileResponse> updateTrainerProfile(
            @PathVariable("username") String username,
            @RequestBody @Valid UpdateTrainerRequest request) {

        Trainer trainer = gymFacade.getTrainer(username);

        if (trainer.isActive() != request.getIsActive()) {
            gymFacade.toggleTrainerActivation(username);
            trainer.setActive(request.getIsActive());
        }

        if (!trainer.getSpecialization().getTrainingTypeName().equals(request.getSpecialization())) {
            trainer = gymFacade.updateTrainerSpecialization(username, request.getSpecialization());
        }

        return ResponseEntity.ok(mapToTrainerProfileResponse(trainer));
    }

    @PatchMapping("/{username}/status")
    @PreAuthorize("#username == authentication.name")
    @Operation(summary = "Activate/De-Activate Trainer")
    public ResponseEntity<Void> toggleTrainerStatus(
            @PathVariable("username") String username,
            @RequestBody @Valid StatusChangeRequest request) {

        Trainer trainer = gymFacade.getTrainer(username);
        if (trainer.isActive() != request.getIsActive()) {
            gymFacade.toggleTrainerActivation(username);
        }
        return ResponseEntity.ok().build();
    }

    private TrainerProfileResponse mapToTrainerProfileResponse(Trainer trainer) {
        List<TraineeInfoDto> traineesList = trainer.getTrainees().stream()
                .map(t -> new TraineeInfoDto(t.getUsername(), t.getFirstName(), t.getLastName()))
                .collect(Collectors.toList());

        return new TrainerProfileResponse(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getSpecialization().getTrainingTypeName(),
                trainer.isActive(),
                traineesList
        );
    }
}