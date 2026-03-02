package com.fiserv.uba.user.repository;

import com.fiserv.uba.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {}
