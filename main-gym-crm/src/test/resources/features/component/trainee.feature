Feature: Trainee Profile Management

  Scenario: Successfully get trainee profile
    Given a valid authorization and mock data for trainee "john.doe"
    When I send a GET request to "/api/v1/trainees/john.doe"
    Then the response status should be 200 OK

  Scenario: Successfully update trainee profile
    Given a valid update request for trainee "john.doe"
    When I send a PUT request to "/api/v1/trainees/john.doe"
    Then the response status should be 200 OK

  Scenario: Successfully toggle trainee status
    Given a valid status change request for trainee "john.doe"
    When I send a PATCH request to "/api/v1/trainees/john.doe/status"
    Then the response status should be 200 OK

  Scenario: Successfully delete trainee profile
    Given an existing trainee with username "john.doe"
    When I send a DELETE request to "/api/v1/trainees/john.doe"
    Then the response status should be 200 OK

  Scenario: Successfully get unassigned trainers
    Given a valid request to fetch unassigned trainers for "john.doe"
    When I send a GET request to "/api/v1/trainees/john.doe/trainers/unassigned"
    Then the response status should be 200 OK

  Scenario: Successfully update trainee's trainers list
    Given a valid request to update trainers list for "john.doe"
    When I send a PUT request to "/api/v1/trainees/john.doe/trainers"
    Then the response status should be 200 OK