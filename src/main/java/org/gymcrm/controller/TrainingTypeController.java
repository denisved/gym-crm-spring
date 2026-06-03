package org.gymcrm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.gymcrm.dto.TrainingTypeDto;
import org.gymcrm.repository.TrainingTypeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/training-types")
@RequiredArgsConstructor
@Tag(name = "Training Types", description = "Endpoints for training types reference")
public class TrainingTypeController {

    private final TrainingTypeRepository trainingTypeRepository;

    @GetMapping
    @Operation(summary = "Get Training types")
    public ResponseEntity<List<TrainingTypeDto>> getTrainingTypes() {
        List<TrainingTypeDto> types = trainingTypeRepository.findAll().stream()
                .map(type -> new TrainingTypeDto(type.getId(), type.getTrainingTypeName()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(types);
    }
}