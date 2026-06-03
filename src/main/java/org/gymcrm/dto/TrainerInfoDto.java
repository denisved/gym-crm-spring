package org.gymcrm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrainerInfoDto {
    private String username;
    private String firstName;
    private String lastName;
    private String specialization;
}