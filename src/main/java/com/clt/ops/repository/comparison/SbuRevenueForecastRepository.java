package com.clt.ops.repository.comparison;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import com.clt.ops.dto.SbuRevenueForecastDto;
import com.clt.ops.query.entity.RetrivalEntity;

public interface SbuRevenueForecastRepository extends JpaRepository<RetrivalEntity, Long>  {

	@Query(name = "SbuRevenueForecast.getRevenueVsForecast", nativeQuery = true)
	List<SbuRevenueForecastDto> getSbuSummary(
			@Param("startDate") LocalDate startDate,
		    @Param("endDate") LocalDate endDate,
		    @Param("year") int year,
		    @Param("monthNameParam") String monthNameParam
	        );
}
