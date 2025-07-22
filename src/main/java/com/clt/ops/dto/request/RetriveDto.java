package com.clt.ops.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetriveDto {

    private int month;
    private int year;
    private String monthNameParam;

    private String projectId;     // Optional: for Associate/Project/Date-level comparisons
    private String associateId;   // Optional: for Date-level comparisons
    private String accId;         // Optional: for Project-level comparison
}
