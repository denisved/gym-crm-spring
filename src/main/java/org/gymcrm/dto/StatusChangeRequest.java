package org.gymcrm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusChangeRequest {
    @NotNull(message = "Is Active flag is required")
    private Boolean isActive;
}