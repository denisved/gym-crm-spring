package org.gymcrm.workload.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trainer_workloads")
@Data
@NoArgsConstructor
public class TrainerWorkload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false, name = "workload_year")
    private Integer year;

    @Column(nullable = false, name = "workload_month")
    private Integer month;

    @Column(nullable = false)
    private Integer trainingSummaryDuration;
}