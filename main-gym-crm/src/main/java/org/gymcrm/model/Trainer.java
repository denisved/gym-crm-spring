package org.gymcrm.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"trainees", "trainings"})
@EqualsAndHashCode(callSuper = true, exclude = {"trainees", "trainings"})
@Entity
@Table(name = "trainers")
@PrimaryKeyJoinColumn(name = "user_id")
public class Trainer extends User {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "specialization_id", nullable = false)
    private TrainingType specialization;

    @ManyToMany(mappedBy = "trainers")
    private List<Trainee> trainees;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Training> trainings;
}