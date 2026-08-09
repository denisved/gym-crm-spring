Feature: Authentication and Registration

  Scenario: Successfully register a trainee
    Given a valid trainee registration request
    When I send a POST request for registration to "/api/v1/auth/trainee/register"
    Then the response status should be 200 OK

  Scenario: Fail to register a trainee with missing data
    Given an invalid trainee registration request
    When I send a POST request for registration to "/api/v1/auth/trainee/register"
    Then the response status should be 400 Bad Request

  Scenario: Successfully login a user
    Given a valid login request for user "john.doe"
    When I send a POST request to "/api/v1/auth/login" with username and password
    Then the response status should be 200 OK

  Scenario: Successfully change user password
    Given a valid password change request for user "john.doe"
    When I send a PUT request for password change to "/api/v1/auth/password"
    Then the response status should be 200 OK