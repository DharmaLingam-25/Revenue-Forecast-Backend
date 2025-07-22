package com.clt.ops.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DateLevelComparisonDto {

	private String associateId;
    private String associateName;
    private String projectId;
    private String projectName;
    private String date;
    private Double companyHours;
    private Double clientHours;
    private Double varianceTimeUnits;
    private String comparisonResult;
}
