package com.fiserv.uba.transaction.repository;

import com.fiserv.uba.transaction.domain.TellerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TellerTransactionRepository extends JpaRepository<TellerTransaction, String> {}
