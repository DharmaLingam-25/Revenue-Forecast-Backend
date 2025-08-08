package com.clt.ops.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor

public class SbuRevenueForecastDto {

	  	private String sbuName;
	    private Double totalSbuRevenue;
	    private Double totalSbuForecast;
	    private Double totalAccount;
}
