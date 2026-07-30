package org.gymcrm.bdd.component;

import io.cucumber.java.en.Given;
import org.gymcrm.dto.PasswordChangeRequest;
import org.gymcrm.dto.TraineeRegistrationRequest;
import org.gymcrm.facade.GymFacade;
import org.gymcrm.model.Trainee;
import org.gymcrm.security.JwtUtils;
import org.gymcrm.security.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collections;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AuthSteps {

    @Autowired
    private GymFacade gymFacade;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Given("a valid trainee registration request")
    public void a_valid_trainee_registration_request() {
        TraineeRegistrationRequest req = new TraineeRegistrationRequest();
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setDateOfBirth(new Date());
        req.setAddress("123 Main St");
        CommonHttpSteps.sharedRequest = req;

        Trainee mockTrainee = new Trainee();
        mockTrainee.setUsername("john.doe");
        when(gymFacade.createTrainee(any(), any(), any(), any())).thenReturn(mockTrainee);
    }

    @Given("an invalid trainee registration request")
    public void an_invalid_trainee_registration_request() {
        TraineeRegistrationRequest req = new TraineeRegistrationRequest();
        req.setFirstName(""); 
        req.setLastName("Doe");
        CommonHttpSteps.sharedRequest = req;
    }

    @Given("a valid login request for user {string}")
    public void a_valid_login_request_for_user(String username) {
        when(loginAttemptService.isBlocked(username)).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenReturn(
                new UsernamePasswordAuthenticationToken(username, "password", Collections.emptyList())
        );
        when(jwtUtils.generateToken(username)).thenReturn("mocked-jwt-token");
    }

    @Given("a valid password change request for user {string}")
    public void a_valid_password_change_request(String username) {
        PasswordChangeRequest req = new PasswordChangeRequest();
        req.setUsername(username);
        req.setOldPassword("oldPass");
        req.setNewPassword("newPass");
        CommonHttpSteps.sharedRequest = req;
    }
}