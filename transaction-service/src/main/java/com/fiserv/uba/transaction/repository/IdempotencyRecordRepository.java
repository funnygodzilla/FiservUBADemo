package com.fiserv.uba.transaction.repository;

import com.fiserv.uba.transaction.domain.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, String> {
}
