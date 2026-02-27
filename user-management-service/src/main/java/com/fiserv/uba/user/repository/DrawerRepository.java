package com.fiserv.uba.user.repository;

import com.fiserv.uba.user.domain.Drawer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrawerRepository extends JpaRepository<Drawer, String> {
    List<Drawer> findByDrawerIdIn(List<String> drawerIds);
}
