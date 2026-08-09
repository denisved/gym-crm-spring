Feature: Training Types Reference

  Scenario: Successfully get training types
    Given a valid authorization and mock data for trainee "any.user"
    When I send a GET request to "/api/v1/training-types"
    Then the response status should be 200 OK