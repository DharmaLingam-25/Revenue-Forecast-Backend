package com.clt.ops.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clt.ops.entity.AssociateEntity;


public interface AssociateRepository extends JpaRepository<AssociateEntity, String> {
    Optional<AssociateEntity> findByCtsId(String ctsId);
}
