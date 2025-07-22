package com.clt.ops.repository.comparison;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clt.ops.dto.DateLevelComparisonDto;
import com.clt.ops.query.entity.RetrivalEntity;

@Repository
public interface DateLevelComparisonRepository extends JpaRepository<RetrivalEntity, Long> {
    
    @Query(nativeQuery = true, name = "DateComparison.getDateLevelComparison")
    List<DateLevelComparisonDto> getDateLevelComparison(
        @Param("projectId") String projectId,
        @Param("associateId") String associateId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    
//    @Query(nativeQuery = true, name = "DateComparison.getDateLevelComparison")
//    List<DateLevelComparisonDto> getRetriveData(
//        @Param("projectId") String projectId,
//        @Param("associateId") String associateId,
//        @Param("startDate") String startDate,
//        @Param("endDate") String endDate,
//        @Param("accId") String accId
//    );

}
