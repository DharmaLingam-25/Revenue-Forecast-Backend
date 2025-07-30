package com.clt.ops.repository.comparison;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.clt.ops.dto.AccountComparisonDto;
import com.clt.ops.query.entity.RetrivalEntity;

public interface AccountComparisonRepository extends JpaRepository<RetrivalEntity, Long> {
	@Query(nativeQuery = true, name = "AccountRevenueSummary.getAccountComparison")
	List<AccountComparisonDto> getAccountComparison(
	    @Param("startDate") LocalDate startDate,
	    @Param("endDate") LocalDate endDate,
	    @Param("month") int month,
	    @Param("year") int year,
	    @Param("monthNameParam") String monthNameParam,
	    @Param("sbu") String sbu
	);

}



