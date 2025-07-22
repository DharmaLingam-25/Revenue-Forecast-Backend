package com.clt.ops.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "RevenueForecast")

@Getter
public class ForecastEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name="ID")
	private Long id;
	
	@Column(name = "ACC_ID")
    private String accountId;

    @Column(name = "AccountName", length = 255)
    private String accountName;

    @Column(name = "PLCategory", length = 50, nullable = false)
    private String plCategory;

    @Column(name = "PLHeader", length = 255)
    private String plHeader;

    @Column(name = "YTD")
    private BigDecimal ytd;

    @Column(name = "FY")
    private BigDecimal fy;

    @Column(name = "Q1")
    private BigDecimal q1;

    @Column(name = "Q2")
    private BigDecimal q2;

    @Column(name = "Q3")
    private BigDecimal q3;

    @Column(name = "Q4")
    private BigDecimal q4;

    @Column(name = "year")
    private Integer year;

    @Column(name = "January")
    private BigDecimal jan;

    @Column(name = "February")
    private BigDecimal feb;

    @Column(name = "March")
    private BigDecimal mar;

    @Column(name = "April")
    private BigDecimal apr;

    @Column(name = "May")
    private BigDecimal may;

    @Column(name = "June")
    private BigDecimal jun;

    @Column(name = "July")
    private BigDecimal jul;

    @Column(name = "August")
    private BigDecimal aug;

    @Column(name = "September")
    private BigDecimal sep;

    @Column(name = "October")
    private BigDecimal oct;

    @Column(name = "November")
    private BigDecimal nov;

    @Column(name = "December")
    private BigDecimal dece;

       
    
    private BigDecimal multiplyByThousand(BigDecimal value) {
        return value != null ? value.multiply(BigDecimal.valueOf(1000)).setScale(0, RoundingMode.HALF_UP) : null;
    }


    
    public void setId(Long id) {
		this.id = id;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public void setPlCategory(String plCategory) {
		this.plCategory = plCategory;
	}

	public void setPlHeader(String plHeader) {
		this.plHeader = plHeader;
	}

	public void setYtd(BigDecimal ytd) {
		this.ytd = multiplyByThousand(ytd);
	}

	public void setFy(BigDecimal fy) {
		this.fy = multiplyByThousand(fy);
	}

	public void setQ1(BigDecimal q1) {
		this.q1 = multiplyByThousand(q1);
	}

	public void setQ2(BigDecimal q2) {
		this.q2 = multiplyByThousand(q2);
	}

	public void setQ3(BigDecimal q3) {
		this.q3 = multiplyByThousand(q3);
	}

	public void setQ4(BigDecimal q4) {
		this.q4 = multiplyByThousand(q4);
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public void setJan(BigDecimal jan) {
		this.jan = multiplyByThousand(jan);
	}

	public void setFeb(BigDecimal feb) {
		this.feb = multiplyByThousand(feb);
	}

	public void setMar(BigDecimal mar) {
		this.mar = multiplyByThousand(mar);
	}

	public void setApr(BigDecimal apr) {
		this.apr = multiplyByThousand(apr);
	}

	public void setMay(BigDecimal may) {
		this.may = multiplyByThousand(may);
	}

	public void setJun(BigDecimal jun) {
		this.jun = multiplyByThousand(jun);
	}

	public void setJul(BigDecimal jul) {
		this.jul = multiplyByThousand(jul);
	}

	public void setAug(BigDecimal aug) {
		this.aug = multiplyByThousand(aug);
	}

	public void setSep(BigDecimal sep) {
		this.sep = multiplyByThousand(sep);
	}

	public void setOct(BigDecimal oct) {
		this.oct = multiplyByThousand(oct);
	}

	public void setNov(BigDecimal nov) {
		this.nov = multiplyByThousand(nov);
	}

	public void setDece(BigDecimal dece) {
		this.dece = multiplyByThousand(dece);
	}

}

