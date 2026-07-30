Feature: Workload Processing Component Tests

  Scenario: Successfully process a workload addition message
    Given a valid workload message for trainer "jane.smith" to add 60 minutes
    When the workload message is received by the listener
    Then the trainer's workload document should be saved in MongoDB

  Scenario: Successfully process a workload deletion message
    Given a valid workload message for trainer "john.doe" to delete 30 minutes
    When the workload message is received by the listener
    Then the trainer's workload document should be saved in MongoDB