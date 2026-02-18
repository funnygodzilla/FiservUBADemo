package com.fiserv.uba.user.repository;

import com.fiserv.uba.user.domain.UserRoleMapping;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleMappingRepository extends JpaRepository<UserRoleMapping, Long> {
    List<UserRoleMapping> findByUserId(String userId);
}
