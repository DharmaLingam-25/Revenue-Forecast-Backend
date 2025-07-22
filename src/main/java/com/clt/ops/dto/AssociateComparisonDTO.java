package com.clt.ops.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssociateComparisonDTO {
    private String associateId;
    private String associateName; 
    private String projectId;
    private String projectName;
    private String esaID;
    private double totalCompanyHoursMonthly;
    private double totalClientHoursMonthly;
    private double varianceHoursMonthly;
    private double actualRevenue;
    private double associateRtRate;

}

