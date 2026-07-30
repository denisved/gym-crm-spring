package org.gymcrm.workload.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearWorkload {
    private int year;
    private List<MonthWorkload> months = new ArrayList<>();
}