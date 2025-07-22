package com.clt.ops.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor

public class AccountComparisonDto {
    private String accountId;
    private String accountName;
    private Long totalProjects;
    private Double totalRevenue;
    private String accountComment;
    private Double forecastNetRevenue;

}
