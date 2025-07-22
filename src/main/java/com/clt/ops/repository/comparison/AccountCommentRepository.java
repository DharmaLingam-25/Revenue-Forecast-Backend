package com.clt.ops.repository.comparison;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clt.ops.entity.AccountCommentEntity;

public interface AccountCommentRepository extends JpaRepository<AccountCommentEntity, Long> {
	Optional<AccountCommentEntity> findByAccIdAndMonthAndYear(String accId, int month, int year);


}
