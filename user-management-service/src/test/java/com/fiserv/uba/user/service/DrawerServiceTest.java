package com.fiserv.uba.user.service;

import com.fiserv.uba.user.domain.Drawer;
import com.fiserv.uba.user.domain.Role;
import com.fiserv.uba.user.domain.User;
import com.fiserv.uba.user.domain.UserDrawerMapping;
import com.fiserv.uba.user.domain.UserRoleMapping;
import com.fiserv.uba.user.exception.UserServiceException;
import com.fiserv.uba.user.repository.DrawerRepository;
import com.fiserv.uba.user.repository.RoleRepository;
import com.fiserv.uba.user.repository.UserDrawerMappingRepository;
import com.fiserv.uba.user.repository.UserRepository;
import com.fiserv.uba.user.repository.UserRoleMappingRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DrawerServiceTest {

    private UserDrawerMappingRepository mappingRepository;
    private UserRoleMappingRepository userRoleMappingRepository;
    private DrawerRepository drawerRepository;
    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private DrawerService drawerService;

    @BeforeEach
    void setup() {
        mappingRepository = Mockito.mock(UserDrawerMappingRepository.class);
        userRoleMappingRepository = Mockito.mock(UserRoleMappingRepository.class);
        drawerRepository = Mockito.mock(DrawerRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        drawerService = new DrawerService(mappingRepository, userRoleMappingRepository, drawerRepository, roleRepository, userRepository);
    }

    @Test
    void selectDrawerResolvesContext() {
        Mockito.when(mappingRepository.existsByUserIdAndDrawerId("u1", "d1")).thenReturn(true);
        Drawer drawer = new Drawer(); drawer.setDrawerId("d1"); drawer.setBranchId("b1"); drawer.setName("main");
        Mockito.when(drawerRepository.findById("d1")).thenReturn(Optional.of(drawer));
        User user = new User(); user.setUserId("u1"); user.setItUserId("it1"); user.setBranchId("b1");
        Mockito.when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        UserRoleMapping m = new UserRoleMapping(); m.setUserId("u1"); m.setRoleId("r1");
        Mockito.when(userRoleMappingRepository.findByUserId("u1")).thenReturn(List.of(m));
        Role role = new Role(); role.setRoleId("r1"); role.setRoleName("ROLE_TELLER");
        Mockito.when(roleRepository.findByRoleIdIn(List.of("r1"))).thenReturn(List.of(role));

        var ctx = drawerService.selectDrawer("u1", "d1");
        Assertions.assertEquals("u1", ctx.sub());
        Assertions.assertEquals("d1", ctx.drawerId());
        Assertions.assertEquals("it1", ctx.itUserId());
        Assertions.assertEquals(List.of("ROLE_TELLER"), ctx.roles());
    }

    @Test
    void invalidDrawerAssignmentThrows4xxError() {
        Mockito.when(mappingRepository.existsByUserIdAndDrawerId("u1", "d9")).thenReturn(false);
        Assertions.assertThrows(UserServiceException.class, () -> drawerService.selectDrawer("u1", "d9"));
    }

    @Test
    void getDrawersReturnsMappedDrawers() {
        UserDrawerMapping m = new UserDrawerMapping(); m.setUserId("u1"); m.setDrawerId("d1");
        Mockito.when(mappingRepository.findByUserId("u1")).thenReturn(List.of(m));
        Drawer drawer = new Drawer(); drawer.setDrawerId("d1"); drawer.setBranchId("b1"); drawer.setName("Main");
        Mockito.when(drawerRepository.findByDrawerIdIn(List.of("d1"))).thenReturn(List.of(drawer));

        var drawers = drawerService.getDrawers("u1");
        Assertions.assertEquals(1, drawers.size());
        Assertions.assertEquals("d1", drawers.get(0).drawerId());
    }
}
