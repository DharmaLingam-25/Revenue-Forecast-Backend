package com.clt.ops.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clt.ops.entity.ForecastEntity;

public interface ForecastRepository extends JpaRepository<ForecastEntity, Long> {

	//Optional<ForecastEntity> findByAccountIdAndPlCategory(String accountId, String plCategory);	
	Optional<ForecastEntity> findByAccountIdAndPlCategoryAndPlHeader(String accountId, String plCategory, String plHeader);


}
