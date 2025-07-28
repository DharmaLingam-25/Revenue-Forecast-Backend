package com.clt.ops.repository.comparison;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.clt.ops.dto.ProjectTypeLevelSummaryDto;
import com.clt.ops.query.entity.RetrivalEntity;

public interface ProjectTypeLevelSummaryRepository extends JpaRepository<RetrivalEntity, Long> {

    @Query(name = "ProjectTypeLevelSummary.getSummaryByType", nativeQuery = true)
    List<ProjectTypeLevelSummaryDto> getSummaryByType(
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("accId") String accId
    );
}
