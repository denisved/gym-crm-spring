package org.gymcrm.bdd.component;

import io.cucumber.java.en.Given;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

public class TrainingTypeSteps {

    @Given("a valid authorization to fetch training types")
    public void a_valid_authorization_to_fetch_training_types() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user", null, Collections.emptyList())
        );
    }
}