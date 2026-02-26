package com.fiserv.uba.user.repository;

import com.fiserv.uba.user.domain.Role;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
    List<Role> findByRoleIdIn(List<String> roleIds);
}
