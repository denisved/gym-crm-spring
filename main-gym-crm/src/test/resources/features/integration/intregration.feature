Feature: Microservices Integration Tests

  Scenario: Successfully send workload message to ActiveMQ when training is added
    Given both microservices and ActiveMQ are running
    And a valid training request for trainee "John.Doe" and trainer "Jane.Smith" for integration
    When I send a POST request to add training to "/api/v1/trainings"
    Then the workload message should be successfully sent to the ActiveMQ queue