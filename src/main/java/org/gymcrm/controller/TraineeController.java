package org.gymcrm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gymcrm.dto.StatusChangeRequest;
import org.gymcrm.dto.TraineeProfileResponse;
import org.gymcrm.dto.TrainerInfoDto;
import org.gymcrm.dto.UpdateTraineeRequest;
import org.gymcrm.facade.GymFacade;
import org.gymcrm.model.Trainee;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/trainees")
@RequiredArgsConstructor
@Tag(name = "Trainee Profile", description = "Endpoints for managing trainee profiles")
public class TraineeController {

    private final GymFacade gymFacade;

    @GetMapping("/{username}")
    @Operation(summary = "Get Trainee Profile")
    public ResponseEntity<TraineeProfileResponse> getTraineeProfile(@PathVariable("username") String username) {
        Trainee trainee = gymFacade.getTrainee(username);
        return ResponseEntity.ok(mapToTraineeProfileResponse(trainee));
    }

    @PutMapping("/{username}")
    @Operation(summary = "Update Trainee Profile")
    public ResponseEntity<TraineeProfileResponse> updateTraineeProfile(
            @PathVariable("username") String username,
            @RequestBody @Valid UpdateTraineeRequest request) {

        Trainee trainee = gymFacade.getTrainee(username);

        trainee.setDateOfBirth(request.getDateOfBirth());
        trainee.setAddress(request.getAddress());

        if (trainee.isActive() != request.getIsActive()) {
            gymFacade.toggleTraineeActivation(username);
            trainee.setActive(request.getIsActive());
        }

        Trainee updatedTrainee = gymFacade.updateTrainee(trainee);
        return ResponseEntity.ok(mapToTraineeProfileResponse(updatedTrainee));
    }

    @PatchMapping("/{username}/status")
    @Operation(summary = "Activate/De-Activate Trainee")
    public ResponseEntity<Void> toggleTraineeStatus(
            @PathVariable("username") String username,
            @RequestBody @Valid StatusChangeRequest request) {

        Trainee trainee = gymFacade.getTrainee(username);
        if (trainee.isActive() != request.getIsActive()) {
            gymFacade.toggleTraineeActivation(username);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Delete Trainee Profile")
    public ResponseEntity<Void> deleteTraineeProfile(@PathVariable("username") String username) {
        gymFacade.deleteTrainee(username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/trainers/unassigned")
    @Operation(summary = "Get not assigned on trainee active trainers")
    public ResponseEntity<List<TrainerInfoDto>> getUnassignedTrainers(@PathVariable("username") String username) {
        List<TrainerInfoDto> unassignedTrainers = gymFacade.getUnassignedTrainers(username).stream()
                .filter(t -> t.isActive())
                .map(t -> new TrainerInfoDto(t.getUsername(), t.getFirstName(), t.getLastName(), t.getSpecialization().getTrainingTypeName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(unassignedTrainers);
    }

    @PutMapping("/{username}/trainers")
    @Operation(summary = "Update Trainee's Trainer List")
    public ResponseEntity<List<TrainerInfoDto>> updateTraineeTrainers(
            @PathVariable("username") String username,
            @RequestBody List<String> trainerUsernames) {

        List<TrainerInfoDto> updatedTrainers = gymFacade.updateTraineeTrainersList(username, trainerUsernames).stream()
                .map(t -> new TrainerInfoDto(t.getUsername(), t.getFirstName(), t.getLastName(), t.getSpecialization().getTrainingTypeName()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(updatedTrainers);
    }

    private TraineeProfileResponse mapToTraineeProfileResponse(Trainee trainee) {
        List<TrainerInfoDto> trainersList = trainee.getTrainers().stream()
                .map(t -> new TrainerInfoDto(t.getUsername(), t.getFirstName(), t.getLastName(), t.getSpecialization().getTrainingTypeName()))
                .collect(Collectors.toList());

        return new TraineeProfileResponse(
                trainee.getUsername(),
                trainee.getFirstName(),
                trainee.getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.isActive(),
                trainersList
        );
    }
}