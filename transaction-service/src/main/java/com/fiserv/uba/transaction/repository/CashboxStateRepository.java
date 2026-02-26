package com.fiserv.uba.transaction.repository;

import com.fiserv.uba.transaction.domain.CashboxState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashboxStateRepository extends JpaRepository<CashboxState, String> {}
