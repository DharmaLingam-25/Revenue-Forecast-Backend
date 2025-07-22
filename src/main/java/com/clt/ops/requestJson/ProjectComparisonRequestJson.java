package com.clt.ops.requestJson;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectComparisonRequestJson {

	private int month;
    private int year;
    private String accId;
}
