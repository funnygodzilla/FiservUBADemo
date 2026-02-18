package com.fiserv.uba.user.service;

import com.fiserv.uba.user.domain.Drawer;
import com.fiserv.uba.user.dto.DrawerDTO;
import com.fiserv.uba.user.dto.UpdatedUserContextDTO;
import com.fiserv.uba.user.exception.UserServiceException;
import com.fiserv.uba.user.repository.DrawerRepository;
import com.fiserv.uba.user.repository.RoleRepository;
import com.fiserv.uba.user.repository.UserDrawerMappingRepository;
import com.fiserv.uba.user.repository.UserRepository;
import com.fiserv.uba.user.repository.UserRoleMappingRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class DrawerService {

    private final UserDrawerMappingRepository mappingRepository;
    private final UserRoleMappingRepository userRoleMappingRepository;
    private final DrawerRepository drawerRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public DrawerService(UserDrawerMappingRepository mappingRepository,
                         UserRoleMappingRepository userRoleMappingRepository,
                         DrawerRepository drawerRepository,
                         RoleRepository roleRepository,
                         UserRepository userRepository) {
        this.mappingRepository = mappingRepository;
        this.userRoleMappingRepository = userRoleMappingRepository;
        this.drawerRepository = drawerRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    public List<DrawerDTO> getDrawers(String sub) {
        List<String> drawerIds = mappingRepository.findByUserId(sub).stream().map(m -> m.getDrawerId()).toList();
        return drawerRepository.findByDrawerIdIn(drawerIds)
                .stream()
                .map(d -> new DrawerDTO(d.getDrawerId(), d.getBranchId(), d.getName()))
                .toList();
    }

    public UpdatedUserContextDTO selectDrawer(String sub, String drawerId) {
        if (!mappingRepository.existsByUserIdAndDrawerId(sub, drawerId)) {
            throw new UserServiceException(HttpStatus.BAD_REQUEST, "Drawer not assigned to user");
        }

        Drawer drawer = drawerRepository.findById(drawerId)
                .orElseThrow(() -> new UserServiceException(HttpStatus.NOT_FOUND, "Drawer not found"));

        String itUserId = userRepository.findById(sub)
                .map(u -> u.getItUserId())
                .orElseThrow(() -> new UserServiceException(HttpStatus.NOT_FOUND, "User not found"));

        List<String> roleIds = userRoleMappingRepository.findByUserId(sub).stream().map(r -> r.getRoleId()).toList();
        List<String> roles = roleRepository.findByRoleIdIn(roleIds)
                .stream()
                .map(role -> role.getRoleName())
                .toList();

        return new UpdatedUserContextDTO(sub, drawer.getBranchId(), drawerId, itUserId, roles);
    }
}
