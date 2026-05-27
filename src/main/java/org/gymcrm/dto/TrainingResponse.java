package org.gymcrm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Date;

@Data
@AllArgsConstructor
public class TrainingResponse {
    private String trainingName;
    private Date trainingDate;
    private String trainingType;
    private Number trainingDuration;
    private String trainerName;
    private String traineeName;
}