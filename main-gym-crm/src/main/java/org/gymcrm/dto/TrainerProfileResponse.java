package org.gymcrm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class TrainerProfileResponse {
    private String username;
    private String firstName;
    private String lastName;
    private String specialization;
    private boolean isActive;
    private List<TraineeInfoDto> trainees;
}