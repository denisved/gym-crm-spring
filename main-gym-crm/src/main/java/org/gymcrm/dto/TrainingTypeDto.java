package org.gymcrm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrainingTypeDto {
    private Long trainingTypeId;
    private String trainingType;
}