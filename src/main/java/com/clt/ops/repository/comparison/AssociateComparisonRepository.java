package com.clt.ops.repository.comparison;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.clt.ops.dto.AssociateComparisonDTO;
import com.clt.ops.query.entity.RetrivalEntity;

public interface AssociateComparisonRepository extends JpaRepository<RetrivalEntity, Long> {
    @Query(name = "AssociateComparison.getAssociateSummary", nativeQuery = true)
    List<AssociateComparisonDTO> getAssociateSummary(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("projectId") String projectId
    );
}
