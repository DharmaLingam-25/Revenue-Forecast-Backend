package com.clt.ops.repository;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.clt.ops.entity.CompanyEntity;


@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Integer> {

	Optional<CompanyEntity> findByAssociateIdAndReportingDate(String associateId, LocalDate reportingDate);

}
