package org.gymcrm.bdd.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommonHttpSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    
    public static Object sharedRequest;
    public static ResultActions sharedResultActions;

    @When("I send a POST request to {string}")
    public void i_send_a_post_request_to(String endpoint) throws Exception {
        sharedResultActions = mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sharedRequest)));
    }

    @When("I send a POST request for registration to {string}")
    public void i_send_a_post_request_for_registration_to(String endpoint) throws Exception {
        sharedResultActions = mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sharedRequest)));
    }

    @When("I send a POST request to {string} with username and password")
    public void i_send_a_post_request_to_login(String endpoint) throws Exception {
        sharedResultActions = mockMvc.perform(post(endpoint)
                .param("username", "john.doe")
                .param("password", "password123"));
    }

    @When("I send a PUT request for password change to {string}")
    public void i_send_a_put_request_for_password_change(String endpoint) throws Exception {
        sharedResultActions = mockMvc.perform(put(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sharedRequest)));
    }

    @When("I send a PUT request to {string}")
    public void i_send_a_put_request_to(String endpoint) throws Exception {
        Object content = (sharedRequest != null) ? sharedRequest : List.of("jane.smith");
        sharedResultActions = mockMvc.perform(put(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content)));
    }

    @When("I send a PATCH request to {string}")
    public void i_send_a_patch_request_to(String endpoint) throws Exception {
        sharedResultActions = mockMvc.perform(patch(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sharedRequest)));
    }

    @When("I send a DELETE request to {string}")
    public void i_send_a_delete_request_to(String endpoint) throws Exception {
        sharedResultActions = mockMvc.perform(delete(endpoint));
    }

    @When("I send a GET request to {string}")
    public void i_send_a_get_request_to(String endpoint) throws Exception {
        sharedResultActions = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("the response status should be {int} OK")
    public void the_response_status_should_be_ok(int statusCode) throws Exception {
        sharedResultActions.andExpect(status().is(statusCode));
    }

    @Then("the response status should be {int} Bad Request")
    public void the_response_status_should_be_bad_request(int statusCode) throws Exception {
        sharedResultActions.andExpect(status().is(statusCode));
    }
}