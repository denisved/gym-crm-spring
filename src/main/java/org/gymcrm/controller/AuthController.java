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
import org.gymcrm.security.JwtUtils;
import org.gymcrm.security.LoginAttemptService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for registration, login, and password changes")
public class AuthController {

    private final GymFacade gymFacade;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    private final LoginAttemptService loginAttemptService;

    @PostMapping("/trainee/register")
    public ResponseEntity<RegistrationResponse> registerTrainee(@RequestBody @Valid TraineeRegistrationRequest request) {
        Trainee trainee = gymFacade.createTrainee(
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getAddress()
        );
        return new ResponseEntity<>(new RegistrationResponse(trainee.getUsername(), trainee.getPlainPassword()), HttpStatus.OK);
    }

    @PostMapping("/trainer/register")
    public ResponseEntity<RegistrationResponse> registerTrainer(@RequestBody @Valid TrainerRegistrationRequest request) {
        Trainer trainer = gymFacade.createTrainer(
                request.getFirstName(),
                request.getLastName(),
                request.getSpecialization()
        );
        return new ResponseEntity<>(new RegistrationResponse(trainer.getUsername(), trainer.getPlainPassword()), HttpStatus.OK);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user and get JWT token")
    public ResponseEntity<Map<String, String>> login(@RequestParam String username, @RequestParam String password) {
        if (loginAttemptService.isBlocked(username)) {
            throw new LockedException("Account is locked for 5 minutes due to too many failed login attempts.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            loginAttemptService.loginSucceeded(username);

            String token = jwtUtils.generateToken(authentication.getName());

            return ResponseEntity.ok(Map.of("token", token));

        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(username);
            throw e;
        }
    }

    @PutMapping("/password")
    @Operation(summary = "Change user password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid PasswordChangeRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getOldPassword())
        );

        gymFacade.changeUserPassword(request.getUsername(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}