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
	        WITH latest_rate AS (
    SELECT 
        cts.`EXTERNAL_ID`, 
        cts.`RT_RATE`
    FROM `tbl_clt_time_sheet` cts
    INNER JOIN (
        SELECT `EXTERNAL_ID`, MAX(`DATE`) AS `MaxDate`
        FROM `tbl_clt_time_sheet`
        GROUP BY `EXTERNAL_ID`
    ) latest_dates 
        ON cts.`EXTERNAL_ID` = latest_dates.`EXTERNAL_ID`
        AND cts.`DATE` = latest_dates.`MaxDate`
),

client_hours_summary AS (
    SELECT 
        a.`esa_project_id` AS `Project_ID`,
        SUM(cts.`UNITS`) AS `Total_Client_Hours`
    FROM `tbl_clt_time_sheet` cts
    INNER JOIN `associate_data` a
        ON cts.`EXTERNAL_ID` = a.`EXTERNAL_ID`
    WHERE cts.`DATE` BETWEEN :startDate AND :endDate
    GROUP BY a.`esa_project_id`
),

filtered_timesheet AS (
    SELECT *
    FROM `tbl_com_time_sheet`
    WHERE `CLIENT_BILLABLE` LIKE '%B%'
    AND `REPORTING_DATE` BETWEEN :startDate AND :endDate
)

SELECT
    comtd.`PROJECT_ID` AS `Project_ID`,
    comtd.`PROJECT_NAME` AS `Project_Name`,
    a.`ACC_ID` AS `Account_ID`,
    a.`ACC_NAME` AS `Account_Name`,
    COUNT(DISTINCT comtd.`ASSOCIATE_ID`) AS `Total_Associates_Count`,
    SUM(comtd.`TIME_QUANTITY`) AS `Total_Company_Hours`,
    COALESCE(clt.`Total_Client_Hours`, 0) AS `Total_Client_Hours`,
    ABS(SUM(comtd.`TIME_QUANTITY`) - COALESCE(clt.`Total_Client_Hours`, 0)) AS `Variance_Hours`,
    SUM(comtd.`TIME_QUANTITY` * COALESCE(lr.`RT_RATE`, 0)) AS `Revenue`
FROM
    filtered_timesheet comtd
LEFT JOIN `associate_data` a
    ON comtd.`ASSOCIATE_ID` = a.`CTS_ID`
    AND comtd.`PROJECT_ID` = a.`esa_project_id`
    AND a.`ACC_ID` = :accId
    AND a.`ACC_ID` IS NOT NULL AND a.`ACC_ID` != '' AND a.`ACC_ID` != 'NA'
    AND a.`ACC_NAME` IS NOT NULL AND a.`ACC_NAME` != '' AND a.`ACC_NAME` != 'NA'
    AND a.project_type = :projectType
LEFT JOIN latest_rate lr
    ON a.`EXTERNAL_ID` = lr.`EXTERNAL_ID`
LEFT JOIN client_hours_summary clt
    ON comtd.`PROJECT_ID` = clt.`Project_ID`
GROUP BY
    comtd.`PROJECT_ID`,
    comtd.`PROJECT_NAME`,
    a.`ACC_ID`,
    a.`ACC_NAME`,
    clt.`Total_Client_Hours`
ORDER BY
    comtd.`PROJECT_ID`,
    a.`ACC_ID`;

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
WITH latest_rate AS (
    SELECT
        cts.`EXTERNAL_ID`,
        cts.`RT_RATE`
    FROM `tbl_clt_time_sheet` cts
    INNER JOIN (
        SELECT `EXTERNAL_ID`, MAX(`DATE`) AS `MaxDate`
        FROM `tbl_clt_time_sheet`
        GROUP BY `EXTERNAL_ID`
    ) latest_dates
        ON cts.`EXTERNAL_ID` = latest_dates.`EXTERNAL_ID`
        AND cts.`DATE` = latest_dates.`MaxDate`
),

filtered_timesheet AS (
    SELECT
        *
    FROM `tbl_com_time_sheet`
    WHERE `CLIENT_BILLABLE` LIKE '%B%'
      AND `REPORTING_DATE` BETWEEN :startDate AND :endDate
),

project_level_accurate_revenue AS (
    SELECT
        comtd.`PROJECT_ID` AS `Project_ID`,
        a.`ACC_ID` AS `Account_ID`,
        a.`ACC_NAME` AS `Account_Name`,
        SUM(comtd.`TIME_QUANTITY` * COALESCE(lr.`RT_RATE`, 0)) AS `Revenue`
    FROM
        filtered_timesheet comtd
    INNER JOIN `associate_data` a
        ON comtd.`ASSOCIATE_ID` = a.`CTS_ID`
        AND comtd.`PROJECT_ID` = a.`esa_project_id`
        AND a.`ACC_ID` IS NOT NULL AND a.`ACC_ID` != '' AND a.`ACC_ID` != 'NA'
        AND a.`ACC_NAME` IS NOT NULL AND a.`ACC_NAME` != '' AND a.`ACC_NAME` != 'NA'
    LEFT JOIN latest_rate lr
        ON a.`EXTERNAL_ID` = lr.`EXTERNAL_ID`
    GROUP BY
        comtd.`PROJECT_ID`,
        a.`ACC_ID`,
        a.`ACC_NAME`
),


monthly_account_forecast AS (
    SELECT
        f.`acc_id`,
        f.`year`,
        COALESCE(SUM(
            CASE LOWER(:monthNameParam) 
                WHEN 'january' THEN f.`january`
                WHEN 'february' THEN f.`february`
                WHEN 'march' THEN f.`march`
                WHEN 'april' THEN f.`april`
                WHEN 'may' THEN f.`may`
                WHEN 'june' THEN f.`june`
                WHEN 'july' THEN f.`july`
                WHEN 'august' THEN f.`august`
                WHEN 'september' THEN f.`september`
                WHEN 'october' THEN f.`october`
                WHEN 'november' THEN f.`november`
                WHEN 'december' THEN f.`december`
                ELSE 0
            END
        ), 0) AS `forecast_value`
    FROM `revenue_forecast` f
    WHERE
        f.`plheader` = 'Net Revenue'
        AND f.`YEAR` = :year
    GROUP BY f.`acc_id`, f.`year`
)

SELECT
    plar.`Account_ID`,
    plar.`Account_Name`,
    COUNT(DISTINCT plar.`Project_ID`) AS `Total_Projects`,
    SUM(plar.`Revenue`) AS `Total_Revenue`,
    ac.`comment` AS `Account_Comment`,
    maf.`forecast_value` AS `Forecast_Net_Revenue`, 
    (maf.`forecast_value` - SUM(plar.`Revenue`)) AS `Difference` 

FROM
    project_level_accurate_revenue plar
LEFT JOIN `account_comments` ac
    ON plar.`Account_ID` = ac.`acc_id`
    AND ac.`month` = :month
    AND ac.`year` = :year
LEFT JOIN monthly_account_forecast maf 
    ON plar.`Account_ID` = maf.`acc_id`
    AND maf.`year` = :year

GROUP BY
    plar.`Account_ID`,
    plar.`Account_Name`,
    ac.`comment`,
    maf.`forecast_value` 
ORDER BY
    plar.`Account_ID`;

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
	        WITH latest_rate AS (
    SELECT
        cts.`EXTERNAL_ID`,
        cts.`RT_RATE`
    FROM `tbl_clt_time_sheet` cts
    INNER JOIN (
        SELECT `EXTERNAL_ID`, MAX(`DATE`) AS `MaxDate`
        FROM `tbl_clt_time_sheet`
        GROUP BY `EXTERNAL_ID`
    ) latest_dates
        ON cts.`EXTERNAL_ID` = latest_dates.`EXTERNAL_ID`
        AND cts.`DATE` = latest_dates.`MaxDate`
),

client_hours_summary AS (
    SELECT
        a.`esa_project_id` AS `Project_ID`,
        SUM(cts.`UNITS`) AS `Total_Client_Hours`
    FROM `tbl_clt_time_sheet` cts
    INNER JOIN `associate_data` a
        ON cts.`EXTERNAL_ID` = a.`EXTERNAL_ID`
    WHERE cts.`DATE` BETWEEN :startDate AND :endDate
    GROUP BY a.`esa_project_id`
),

filtered_timesheet AS (
    SELECT
        *
    FROM `tbl_com_time_sheet`
    WHERE `CLIENT_BILLABLE` LIKE '%B%'
      AND `REPORTING_DATE` BETWEEN :startDate AND :endDate
)

SELECT
    a.`PROJECT_TYPE`,
    COUNT(DISTINCT comtd.`PROJECT_ID`) AS `Total_Projects`,
    SUM(comtd.`TIME_QUANTITY` * COALESCE(lr.`RT_RATE`, 0)) AS `Total_Revenue_By_Project_Type`
FROM filtered_timesheet comtd
INNER JOIN `associate_data` a
    ON comtd.`ASSOCIATE_ID` = a.`CTS_ID`
    AND comtd.`PROJECT_ID` = a.`esa_project_id`
LEFT JOIN latest_rate lr
    ON a.`EXTERNAL_ID` = lr.`EXTERNAL_ID`
WHERE
    a.`ACC_ID` = :accId
    AND a.`PROJECT_TYPE` IS NOT NULL
    And a.`project_type` not like '%BFD%'
GROUP BY a.`PROJECT_TYPE`
ORDER BY a.`PROJECT_TYPE`

	    """,
	    resultSetMapping = "ProjectTypeLevelSummaryMapping"
	)

	@SqlResultSetMapping(
	    name = "ProjectTypeLevelSummaryMapping",
	    classes = @ConstructorResult(
	        targetClass = ProjectTypeLevelSummaryDto.class,
	        columns = {
	            @ColumnResult(name = "PROJECT_TYPE", type = String.class),
	            @ColumnResult(name = "Total_Projects", type = Long.class),
	            @ColumnResult(name = "Total_Revenue_By_Project_Type", type = Double.class)
	            
	        }
	    )
	)
@Entity
public class RetrivalEntity {
	@Id
	private Long id;
}
