package com.clt.ops.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectTypeLevelSummaryDto {
    private String projectType;
    private Long totalProjectsInType;
    private Double totalRevenueByType;
   
}
