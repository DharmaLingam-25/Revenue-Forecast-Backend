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

    private String projectId;     
    private String associateId;   
    private String accId;        
    private String projectType;
}
