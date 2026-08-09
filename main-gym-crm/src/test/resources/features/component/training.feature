Feature: Training Management

  Scenario: Successfully add a new training
    Given a valid training request for trainee "john.doe" and trainer "jane.smith"
    When I send a POST request to "/api/v1/trainings"
    Then the response status should be 200 OK

  Scenario: Fail to add training with missing data
    Given an invalid training request with missing trainee username
    When I send a POST request to "/api/v1/trainings"
    Then the response status should be 400 Bad Request

  Scenario: Successfully delete a training
    Given an existing training with ID 1
    When I send a DELETE request to "/api/v1/trainings/1"
    Then the response status should be 200 OK

  Scenario: Successfully get trainee trainings
    Given a valid request to fetch trainings for trainee "john.doe"
    When I send a GET request to "/api/v1/trainings/trainee/john.doe"
    Then the response status should be 200 OK