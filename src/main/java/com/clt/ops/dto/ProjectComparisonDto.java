package com.clt.ops.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectComparisonDto {

	private String projectId;
    private String projectName;
    private String accountId;
    private String accountName;
    private int totalAssociatesCount;
    private double totalCompanyHours;
    private double totalClientHours;
    private double varianceHours;
    private double revenue;
}
