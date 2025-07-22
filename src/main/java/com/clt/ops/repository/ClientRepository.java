package com.clt.ops.repository;

import com.clt.ops.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<ClientEntity, Integer> {

	Optional<ClientEntity> findByExternalIdAndDate(String externalId, LocalDate date);

}