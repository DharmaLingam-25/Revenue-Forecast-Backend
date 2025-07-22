package com.clt.ops.requestJson;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DateLevelComparisonRequestJson{
    private String projectId;
    private String associateId;
    private int month;
    private int year;

    // Constructors, Getters, Setters (or use Lombok)
}

