package com.clt.ops.query.entity;

import com.clt.ops.dto.AccountComparisonDto;
import com.clt.ops.dto.AssociateComparisonDTO;
import com.clt.ops.dto.DateLevelComparisonDto;
import com.clt.ops.dto.ProjectComparisonDto;
import com.clt.ops.dto.ProjectTypeLevelSummaryDto;

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.SqlResultSetMapping;

@NamedNativeQuery(
	    name = "ProjectComparison.getProjectSummary",
	    query = """
	        SELECT
	            comtd.`PROJECT_ID` AS `Project_ID`,
	            comtd.`PROJECT_NAME` AS `Project_Name`,
	            a.`ACC_ID` AS `Account_ID`,
	            a.`ACC_NAME` AS `Account_Name`,
	            COUNT(DISTINCT comtd.`ASSOCIATE_ID`) AS `Total_Associates_Count`,
	            SUM(comtd.`TIME_QUANTITY`) AS `Total_Company_Hours`,
	            COALESCE(clt_summary.`Total_Client_Hours`, 0) AS `Total_Client_Hours`,
	            ABS(SUM(comtd.`TIME_QUANTITY`) - COALESCE(clt_summary.`Total_Client_Hours`, 0)) AS `Variance_Hours`,
	            SUM(comtd.`TIME_QUANTITY` * COALESCE(ctd.`RT_RATE`, 0)) AS `Revenue`
	        FROM
	            `tbl_com_time_sheet` comtd
	        LEFT JOIN `associate_data` a
	            ON comtd.`ASSOCIATE_ID` = a.`CTS_ID`
	            AND comtd.`PROJECT_ID` = a.`esa_project_id`
	            AND a.`ACC_ID` IS NOT NULL AND a.`ACC_ID` != '' AND a.`ACC_ID` != 'NA'
	            AND a.`ACC_NAME` IS NOT NULL AND a.`ACC_NAME` != '' AND a.`ACC_NAME` != 'NA'
	        LEFT JOIN `tbl_clt_time_sheet` ctd
	            ON a.`EXTERNAL_ID` = ctd.`EXTERNAL_ID`
	            AND comtd.`REPORTING_DATE` = ctd.`DATE`
	        LEFT JOIN (
	            SELECT
	                a.`esa_project_id` AS `Project_ID`,
	                SUM(cts.`UNITS`) AS `Total_Client_Hours`
	            FROM
	                `tbl_clt_time_sheet` cts
	            INNER JOIN `associate_data` a
	                ON cts.`EXTERNAL_ID` = a.`EXTERNAL_ID`
	            WHERE
	                cts.`DATE` BETWEEN :startDate AND :endDate
	            GROUP BY
	                a.`esa_project_id`
	        ) AS clt_summary
	            ON clt_summary.`Project_ID` = comtd.`PROJECT_ID`
	        WHERE
	            comtd.`CLIENT_BILLABLE` IS NOT NULL
	            AND comtd.`CLIENT_BILLABLE` LIKE '%B%'
	            AND comtd.`REPORTING_DATE` BETWEEN :startDate AND :endDate
	            AND a.`ACC_ID` = :accId
	            AND a.project_type =:projectType
	        GROUP BY
	            comtd.`PROJECT_ID`,
	            comtd.`PROJECT_NAME`,
	            a.`ACC_ID`,
	            a.`ACC_NAME`,
	            clt_summary.`Total_Client_Hours`
	        ORDER BY
	            comtd.`PROJECT_ID`,
	            a.`ACC_ID`
	        """,
	    resultSetMapping = "ProjectSummaryMapping"
	)

	@SqlResultSetMapping(
	    name = "ProjectSummaryMapping",
	    classes = @ConstructorResult(
	        targetClass = ProjectComparisonDto.class,
	        columns = {
	            @ColumnResult(name = "Project_ID", type = String.class),
	            @ColumnResult(name = "Project_Name", type = String.class),
	            @ColumnResult(name = "Account_ID", type = String.class),
	            @ColumnResult(name = "Account_Name", type = String.class),
	            @ColumnResult(name = "Total_Associates_Count", type = Integer.class),
	            @ColumnResult(name = "Total_Company_Hours", type = Double.class),
	            @ColumnResult(name = "Total_Client_Hours", type = Double.class),
	            @ColumnResult(name = "Variance_Hours", type = Double.class),
	            @ColumnResult(name = "Revenue", type = Double.class)
	        }
	    )
	)


@NamedNativeQuery(name = "AccountRevenueSummary.getAccountComparison", query = """
SELECT
    ProjectRevenueSummary.`Account_ID`,
    ProjectRevenueSummary.`Account_Name`,
    COUNT(DISTINCT ProjectRevenueSummary.`Project_ID`) AS `Total_Projects`,
    SUM(ProjectRevenueSummary.`Revenue`) AS `Total_Revenue`,
    ac.`comment` AS `Account_Comment`,
    
    -- Forecasted revenue for the selected month
    COALESCE(SUM(
        CASE :monthNameParam
            WHEN 'january' THEN rf.`january`
            WHEN 'february' THEN rf.`february`
            WHEN 'march' THEN rf.`march`
            WHEN 'april' THEN rf.`april`
            WHEN 'may' THEN rf.`may`
            WHEN 'june' THEN rf.`june`
            WHEN 'july' THEN rf.`july`
            WHEN 'august' THEN rf.`august`
            WHEN 'september' THEN rf.`september`
            WHEN 'october' THEN rf.`october`
            WHEN 'november' THEN rf.`november`
            WHEN 'december' THEN rf.`december`
            ELSE 0
        END
    ), 0) AS `Forecast_Net_Revenue`,
    
    -- Difference between forecasted and actual revenue
    (
        COALESCE(SUM(
            CASE :monthNameParam
                WHEN 'january' THEN rf.`january`
                WHEN 'february' THEN rf.`february`
                WHEN 'march' THEN rf.`march`
                WHEN 'april' THEN rf.`april`
                WHEN 'may' THEN rf.`may`
                WHEN 'june' THEN rf.`june`
                WHEN 'july' THEN rf.`july`
                WHEN 'august' THEN rf.`august`
                WHEN 'september' THEN rf.`september`
                WHEN 'october' THEN rf.`october`
                WHEN 'november' THEN rf.`november`
                WHEN 'december' THEN rf.`december`
                ELSE 0
            END
        ), 0) - SUM(ProjectRevenueSummary.`Revenue`)
    ) AS `Difference`
    
FROM (
    SELECT
        comtd.`PROJECT_ID` AS `Project_ID`,
        comtd.`PROJECT_NAME` AS `Project_Name`,
        a.`ACC_ID` AS `Account_ID`,
        a.`ACC_NAME` AS `Account_Name`,
        SUM(comtd.`TIME_QUANTITY` * COALESCE(ctd.`RT_RATE`, 0)) AS `Revenue`
    FROM
        `tbl_com_time_sheet` comtd
    INNER JOIN `associate_data` a
        ON comtd.`ASSOCIATE_ID` = a.`CTS_ID`
        AND comtd.`PROJECT_ID` = a.`esa_project_id`
        AND a.`ACC_ID` IS NOT NULL AND a.`ACC_ID` != '' AND a.`ACC_ID` != 'NA'
        AND a.`ACC_NAME` IS NOT NULL AND a.`ACC_NAME` != '' AND a.`ACC_NAME` != 'NA'
    LEFT JOIN `tbl_clt_time_sheet` ctd
        ON a.`EXTERNAL_ID` = ctd.`EXTERNAL_ID`
        AND comtd.`REPORTING_DATE` = ctd.`DATE`
    WHERE
        comtd.`CLIENT_BILLABLE` IS NOT NULL
        AND comtd.`CLIENT_BILLABLE` LIKE '%B%'
        AND comtd.`REPORTING_DATE` BETWEEN :startDate AND :endDate
    GROUP BY
        comtd.`PROJECT_ID`,
        comtd.`PROJECT_NAME`,
        a.`ACC_ID`,
        a.`ACC_NAME`
) AS ProjectRevenueSummary

LEFT JOIN `account_comments` ac
    ON ProjectRevenueSummary.`Account_ID` = ac.`acc_id`
    AND ac.`month` = :month
    AND ac.`year` = :year

LEFT JOIN `revenue_forecast` rf
    ON ProjectRevenueSummary.`Account_ID` = rf.`ACC_ID`
    AND rf.`PLHeader` = 'Net Revenue'
    AND rf.`year` = :year

GROUP BY
    ProjectRevenueSummary.`Account_ID`,
    ProjectRevenueSummary.`Account_Name`,
    ac.`comment`

ORDER BY
    ProjectRevenueSummary.`Account_ID`;


""", resultSetMapping = "AccountComparisonMapping")
@SqlResultSetMapping(name = "AccountComparisonMapping", classes = @ConstructorResult(targetClass = AccountComparisonDto.class, columns = {
@ColumnResult(name = "Account_ID", type = String.class),
@ColumnResult(name = "Account_Name", type = String.class),
@ColumnResult(name = "Total_Projects", type = Long.class),
@ColumnResult(name = "Total_Revenue", type = Double.class),
@ColumnResult(name = "Account_Comment", type = String.class),
@ColumnResult(name = "Forecast_Net_Revenue", type = Double.class),
@ColumnResult(name = "Difference", type = Double.class) 
}))	


@NamedNativeQuery(
	    name = "AssociateComparison.getAssociateSummary",
	    query = """
	        WITH associate_rates AS (
	            SELECT a.`CTS_ID` AS `Associate_ID`,
	                   MAX(ctd.`RT_RATE`) AS `Flat_RT_Rate`
	            FROM `associate_data` a
	            LEFT JOIN `tbl_clt_time_sheet` ctd
	                ON a.`EXTERNAL_ID` = ctd.`EXTERNAL_ID`
	            WHERE ctd.`DATE` BETWEEN :startDate AND :endDate
	            GROUP BY a.`CTS_ID`
	        )
	        SELECT
	            comtd.`ASSOCIATE_ID`,
	            comtd.`ASSOCIATE_NAME`,
	            comtd.`PROJECT_ID`,
	            comtd.`PROJECT_NAME`,
	            a.ESA_ID,
	            SUM(comtd.`TIME_QUANTITY`) AS `Total_Company_Hours_Monthly`,
	            SUM(CASE WHEN ctd.`EXTERNAL_ID` IS NULL THEN 0 ELSE ctd.`UNITS` END) AS `Total_Client_Hours_Monthly`,
	            abs(SUM(comtd.`TIME_QUANTITY`) - SUM(CASE WHEN ctd.`EXTERNAL_ID` IS NULL THEN 0 ELSE ctd.`UNITS` END)) AS `Variance_Hours_Monthly`,
	            ROUND(SUM(comtd.`TIME_QUANTITY`) * COALESCE(ar.`Flat_RT_Rate`, 0), 2) AS `Actual_Revenue`,
	            COALESCE(ar.`Flat_RT_Rate`, 0) AS `Associate_RT_Rate`
	        FROM `tbl_com_time_sheet` comtd
	        INNER JOIN `associate_data` a
	            ON comtd.`ASSOCIATE_ID` = a.`CTS_ID` AND comtd.`PROJECT_ID` = a.`esa_project_id`
	        LEFT JOIN `tbl_clt_time_sheet` ctd
	            ON a.`EXTERNAL_ID` = ctd.`EXTERNAL_ID` AND comtd.`REPORTING_DATE` = ctd.`DATE`
	        LEFT JOIN associate_rates ar
	            ON ar.`Associate_ID` = comtd.`ASSOCIATE_ID`
	        WHERE comtd.`CLIENT_BILLABLE` LIKE '%B%'
	          AND comtd.`REPORTING_DATE` BETWEEN :startDate AND :endDate
	          AND comtd.`PROJECT_ID` = :projectId
	        GROUP BY
	            comtd.`ASSOCIATE_ID`, comtd.`ASSOCIATE_NAME`, comtd.`PROJECT_ID`,
	            comtd.`PROJECT_NAME`, ar.`Flat_RT_Rate`
	        ORDER BY comtd.`ASSOCIATE_ID`
	    """,
	    resultSetMapping = "AssociateComparisonMapping"
	)
	@SqlResultSetMapping(
	    name = "AssociateComparisonMapping",
	    classes = @ConstructorResult(
	        targetClass = AssociateComparisonDTO.class,
	        columns = {
	            @ColumnResult(name = "ASSOCIATE_ID", type = String.class),
	            @ColumnResult(name = "ASSOCIATE_NAME", type = String.class),
	            @ColumnResult(name = "PROJECT_ID", type = String.class),
	            @ColumnResult(name = "PROJECT_NAME", type = String.class),
	            @ColumnResult(name = "ESA_ID", type = String.class),
	            @ColumnResult(name = "Total_Company_Hours_Monthly", type = Double.class),
	            @ColumnResult(name = "Total_Client_Hours_Monthly", type = Double.class),
	            @ColumnResult(name = "Variance_Hours_Monthly", type = Double.class),
	            @ColumnResult(name = "Actual_Revenue", type = Double.class),
	            @ColumnResult(name = "Associate_RT_Rate", type = Double.class)
	        }
	    )
	)



@NamedNativeQuery(
    name = "DateComparison.getDateLevelComparison",
    query = """
SELECT
    comtd.ASSOCIATE_ID AS Associate_ID,
    comtd.ASSOCIATE_NAME AS Associate_Name,
    comtd.PROJECT_ID AS Project_ID,
    comtd.PROJECT_NAME AS Project_Name,
    comtd.REPORTING_DATE AS Date,
    comtd.TIME_QUANTITY AS Company_Hours,
    CASE WHEN ctd.EXTERNAL_ID IS NULL THEN 0 ELSE ctd.UNITS END AS Client_Hours,
    ABS(comtd.TIME_QUANTITY - (CASE WHEN ctd.EXTERNAL_ID IS NULL THEN 0 ELSE ctd.UNITS END)) AS Variance_Time_Units,
    CASE
        WHEN ctd.EXTERNAL_ID IS NOT NULL AND ctd.DATE = comtd.REPORTING_DATE AND ctd.UNITS = comtd.TIME_QUANTITY THEN 'MATCH'
        WHEN ctd.EXTERNAL_ID IS NOT NULL AND (ctd.DATE != comtd.REPORTING_DATE OR ctd.UNITS != comtd.TIME_QUANTITY) THEN
            CASE
                WHEN comtd.TIME_QUANTITY < (CASE WHEN ctd.EXTERNAL_ID IS NULL THEN 0 ELSE ctd.UNITS END) THEN 'Short Billed in ESA'
                WHEN comtd.TIME_QUANTITY > (CASE WHEN ctd.EXTERNAL_ID IS NULL THEN 0 ELSE ctd.UNITS END) THEN 'Excess Billed in ESA'
                ELSE 'MISMATCH - Date/Units Difference' -- Fallback if hours are somehow equal but other conditions don't match
            END
        WHEN ctd.EXTERNAL_ID IS NULL THEN 'Client Timesheet Missing'
        ELSE 'Unexpected State'
    END AS Comparison_Result
FROM tbl_com_time_sheet comtd
INNER JOIN associate_data a ON comtd.ASSOCIATE_ID = a.CTS_ID AND comtd.PROJECT_ID = a.esa_project_id
LEFT JOIN tbl_clt_time_sheet ctd ON a.EXTERNAL_ID = ctd.EXTERNAL_ID AND comtd.REPORTING_DATE = ctd.DATE
WHERE comtd.CLIENT_BILLABLE IS NOT NULL
  AND comtd.CLIENT_BILLABLE LIKE '%B%'
  AND comtd.PROJECT_ID = :projectId
  AND comtd.ASSOCIATE_ID = :associateId
  AND comtd.REPORTING_DATE BETWEEN :startDate AND :endDate
ORDER BY comtd.REPORTING_DATE
        """,
    resultSetMapping = "DateLevelMapping"
)
@SqlResultSetMapping(
    name = "DateLevelMapping",
    classes = @ConstructorResult(
        targetClass = DateLevelComparisonDto.class,
        columns = {
            @ColumnResult(name = "Associate_ID", type = String.class),
            @ColumnResult(name = "Associate_Name", type = String.class),
            @ColumnResult(name = "Project_ID", type = String.class),
            @ColumnResult(name = "Project_Name", type = String.class),
            @ColumnResult(name = "Date", type = String.class),
            @ColumnResult(name = "Company_Hours", type = Double.class),
            @ColumnResult(name = "Client_Hours", type = Double.class),
            @ColumnResult(name = "Variance_Time_Units", type = Double.class),
            @ColumnResult(name = "Comparison_Result", type = String.class)
        }
    )
)


@NamedNativeQuery(
	    name = "ProjectTypeLevelSummary.getSummaryByType",
	    query = """
	        WITH associate_rates AS (
	            SELECT a.`CTS_ID` AS `Associate_ID`,
	                   MAX(COALESCE(ctd.`RT_RATE`, 0)) AS `Flat_RT_Rate`
	            FROM `associate_data` a
	            LEFT JOIN `tbl_clt_time_sheet` ctd
	                ON a.`EXTERNAL_ID` = ctd.`EXTERNAL_ID`
	            WHERE ctd.`DATE` BETWEEN :startDate AND :endDate
	            GROUP BY a.`CTS_ID`
	        ),
	        ProjectLevelSummary AS (
	            SELECT
	                a.`PROJECT_TYPE` AS `Project_Type`,
	                comtd.`PROJECT_ID` AS `Project_ID`,
	                SUM(comtd.`TIME_QUANTITY` * COALESCE(ar.`Flat_RT_Rate`, 0)) AS `Project_Actual_Revenue_Billable`,
	                ABS(SUM(comtd.`TIME_QUANTITY`) - SUM(CASE WHEN ctd.`EXTERNAL_ID` IS NULL THEN 0 ELSE ctd.`UNITS` END)) AS `Project_Billable_Hour_Variance`
	            FROM `tbl_com_time_sheet` comtd
	            INNER JOIN `associate_data` a
	                ON comtd.`ASSOCIATE_ID` = a.`CTS_ID`
	                AND comtd.`PROJECT_ID` = a.`esa_project_id`
	            LEFT JOIN `tbl_clt_time_sheet` ctd
	                ON a.`EXTERNAL_ID` = ctd.`EXTERNAL_ID`
	                AND comtd.`REPORTING_DATE` = ctd.`DATE`
	            LEFT JOIN associate_rates ar
	                ON ar.`Associate_ID` = comtd.`ASSOCIATE_ID`
	            WHERE comtd.`CLIENT_BILLABLE` LIKE '%B%'
	              AND comtd.`REPORTING_DATE` BETWEEN :startDate AND :endDate
	              AND a.`ACC_ID` = :accId
	            GROUP BY a.`PROJECT_TYPE`, comtd.`PROJECT_ID`
	        )
	        SELECT
	            pls.`Project_Type`,
	            COUNT(DISTINCT pls.`Project_ID`) AS `Total_Projects_In_Type`,
	            SUM(pls.`Project_Actual_Revenue_Billable`) AS `Total_Revenue_By_Type`,
	            ROUND(SUM(pls.`Project_Billable_Hour_Variance` * (
	                SELECT AVG(COALESCE(ar_inner.`Flat_RT_Rate`, 0))
	                FROM associate_rates ar_inner
	                INNER JOIN associate_data a_inner ON ar_inner.`Associate_ID` = a_inner.`CTS_ID`
	                WHERE a_inner.`PROJECT_TYPE` = pls.`Project_Type`
	                  AND a_inner.`ACC_ID` = :accId
	            )), 2) AS `Total_Revenue_Variance_Estimate_By_Type`
	        FROM ProjectLevelSummary pls
	        GROUP BY pls.`Project_Type`
	        ORDER BY pls.`Project_Type`
	    """,
	    resultSetMapping = "ProjectTypeLevelSummaryMapping"
	)

	@SqlResultSetMapping(
	    name = "ProjectTypeLevelSummaryMapping",
	    classes = @ConstructorResult(
	        targetClass = ProjectTypeLevelSummaryDto.class,
	        columns = {
	            @ColumnResult(name = "Project_Type", type = String.class),
	            @ColumnResult(name = "Total_Projects_In_Type", type = Long.class),
	            @ColumnResult(name = "Total_Revenue_By_Type", type = Double.class),
	            @ColumnResult(name = "Total_Revenue_Variance_Estimate_By_Type", type = Double.class)
	        }
	    )
	)
@Entity
public class RetrivalEntity {
	@Id
	private Long id;
}
