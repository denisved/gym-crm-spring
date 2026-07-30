Feature: Trainer Profile Management

  Scenario: Successfully get trainer profile
    Given a valid authorization and mock data for trainer "jane.smith"
    When I send a GET request to "/api/v1/trainers/jane.smith"
    Then the response status should be 200 OK

  Scenario: Successfully update trainer profile
    Given a valid update request for trainer "jane.smith"
    When I send a PUT request to "/api/v1/trainers/jane.smith"
    Then the response status should be 200 OK

  Scenario: Successfully toggle trainer status
    Given a valid status change request for trainer "jane.smith"
    When I send a PATCH request to "/api/v1/trainers/jane.smith/status"
    Then the response status should be 200 OK