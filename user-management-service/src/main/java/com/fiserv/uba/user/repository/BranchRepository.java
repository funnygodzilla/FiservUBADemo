package com.fiserv.uba.user.repository;

import com.fiserv.uba.user.domain.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, String> {}
