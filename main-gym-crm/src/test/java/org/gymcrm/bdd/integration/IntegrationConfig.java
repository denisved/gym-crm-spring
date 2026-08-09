package org.gymcrm.bdd.integration;

import io.cucumber.spring.CucumberContextConfiguration;
import org.gymcrm.GymApplication;
import org.gymcrm.security.JwtUtils;
import org.gymcrm.security.LoginAttemptService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;

@CucumberContextConfiguration
@SpringBootTest(classes = GymApplication.class, properties = {
        "eureka.client.enabled=false"
})
@AutoConfigureMockMvc(addFilters = false)
public class IntegrationConfig {

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private LoginAttemptService loginAttemptService;
}