package org.gymcrm.workload.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkloadRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Active status is required")
    private Boolean isActive;

    @NotNull(message = "Training date is required")
    private Date trainingDate;

    @NotNull(message = "Training duration is required")
    private Integer trainingDuration;

    @NotNull(message = "Action type is required")
    private ActionType actionType;
}