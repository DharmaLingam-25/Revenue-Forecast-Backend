package com.clt.ops.repository.comparison;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.clt.ops.dto.ProjectComparisonDto;
import com.clt.ops.query.entity.RetrivalEntity;

public interface ProjectComparisonRepository extends JpaRepository<RetrivalEntity, Long> {
	    @Query(name = "ProjectComparison.getProjectSummary", nativeQuery = true)
	    List<ProjectComparisonDto> getProjectSummary(
	        @Param("startDate") LocalDate startDate,
	        @Param("endDate") LocalDate endDate,
	        @Param("accId") String accId
	    );
	}


