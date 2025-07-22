package com.clt.ops.entity;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "TBL_COM_TIME_SHEET")
public class CompanyEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID")
	private int id;	
	
    @Column(name = "ASSOCIATE_ID",nullable = false)
    private String associateId;
	
    @Column(name = "REPORTING_DATE", nullable = false)
    private LocalDate reportingDate;
 
    @Column(name = "ASSOCIATE_NAME", nullable = false)
    private String associateName;
 
    @Column(name = "TIMESHEET_ID",nullable = false)
    private String timesheetId;
 
    @Column(name = "PROJECT_ID", nullable = false)
    private String projectId;
 
    @Column(name = "PROJECT_NAME",nullable = false)
    private String projectName;
 
    @Column(name = "PROJECT_POLICY_HOURS",nullable = false)
    private String projectPolicyHours;
 
    @Column(name = "TIME_QUANTITY", nullable = false)
    private BigDecimal timeQuantity;
 
    @Column(name = "SUBMISSION_DATE", nullable = false)
    private LocalDate submissionDate;
    
    @Column(name = "ONSITE_OFFSHORE")
    private String onsiteOffshore;
 
    @Column(name = "CLIENT_BILLABLE")
    private String clientBillable;
	
}


