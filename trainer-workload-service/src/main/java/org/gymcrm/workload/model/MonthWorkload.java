package org.gymcrm.workload.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthWorkload {
    private int month;
    private int trainingSummaryDuration;
}