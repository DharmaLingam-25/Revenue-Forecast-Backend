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
@Table(name = "TBL_CLT_TIME_SHEET")
public class ClientEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name="ID")
	private int id;
	
	@Column(name ="CLIENT_ID",nullable = false)
	private String clientId;
		
    @Column(name ="EXTERNAL_ID",nullable = false)
	private String externalId;
    
    @Column(name ="DATE",nullable = false)
	private LocalDate date;
	
    @Column(name ="NAME",nullable = false)
	private String name;   
    
    @Column(name ="TIMESHEET_ID",nullable = false)
	private String timesheetId;
    
    @Column(name ="UNITS")
	private BigDecimal units;
    
    @Column(name ="ASSOSIATE_NAME",nullable = false)
   	private String assosiateName;
       
    @Column(name ="RT_RATE")
	private BigDecimal  rtRate;
    
    @Column(name ="SUBMITTON_DATE",nullable = false)
  	private LocalDate submitionDate;

    
    @Column(name ="END_DATE",nullable = false)
  	private LocalDate endDate;
    
    @Column(name ="EMAIL",nullable = false)
  	private String email;
      
    
    
    

}
