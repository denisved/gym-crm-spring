Feature: Workload Microservice Integration

  Scenario: Successfully receive message from ActiveMQ and save to MongoDB
    Given ActiveMQ and MongoDB are running and database is clean
    When a workload message for trainer "integration.trainer" to add 90 minutes is published to ActiveMQ
    Then the trainer "integration.trainer" should have a workload document in MongoDB with 90 minutes