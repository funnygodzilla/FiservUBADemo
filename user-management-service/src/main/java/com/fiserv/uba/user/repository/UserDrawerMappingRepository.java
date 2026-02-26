package com.fiserv.uba.user.repository;

import com.fiserv.uba.user.domain.UserDrawerMapping;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDrawerMappingRepository extends JpaRepository<UserDrawerMapping, Long> {
    List<UserDrawerMapping> findByUserId(String userId);
    boolean existsByUserIdAndDrawerId(String userId, String drawerId);
}
