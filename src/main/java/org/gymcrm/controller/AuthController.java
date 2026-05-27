package org.gymcrm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gymcrm.dto.PasswordChangeRequest;
import org.gymcrm.dto.RegistrationResponse;
import org.gymcrm.dto.TraineeRegistrationRequest;
import org.gymcrm.dto.TrainerRegistrationRequest;
import org.gymcrm.facade.GymFacade;
import org.gymcrm.model.Trainee;
import org.gymcrm.model.Trainer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for registration, login, and password changes")
public class AuthController {

    private final GymFacade gymFacade;

    @PostMapping("/trainee/register")
    @Operation(summary = "Register a new Trainee")
    public ResponseEntity<RegistrationResponse> registerTrainee(@RequestBody @Valid TraineeRegistrationRequest request) {
        Trainee trainee = gymFacade.createTrainee(
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getAddress()
        );
        return new ResponseEntity<>(new RegistrationResponse(trainee.getUsername(), trainee.getPassword()), HttpStatus.OK);
    }

    @PostMapping("/trainer/register")
    @Operation(summary = "Register a new Trainer")
    public ResponseEntity<RegistrationResponse> registerTrainer(@RequestBody @Valid TrainerRegistrationRequest request) {
        Trainer trainer = gymFacade.createTrainer(
                request.getFirstName(),
                request.getLastName(),
                request.getSpecialization()
        );
        return new ResponseEntity<>(new RegistrationResponse(trainer.getUsername(), trainer.getPassword()), HttpStatus.OK);
    }

    @GetMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<Void> login(@RequestParam String username, @RequestParam String password) {
        boolean isAuthenticated = gymFacade.authenticate(username, password);
        if (isAuthenticated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/password")
    @Operation(summary = "Change user password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid PasswordChangeRequest request) {
        if (!gymFacade.authenticate(request.getUsername(), request.getOldPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            gymFacade.changeTraineePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
        } catch (IllegalArgumentException e) {
            try {
                gymFacade.changeTrainerPassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
            } catch (IllegalArgumentException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }

        return ResponseEntity.ok().build();
    }
}