package com.fiserv.uba.user.controller;

import com.fiserv.uba.user.dto.DrawerDTO;
import com.fiserv.uba.user.dto.UpdatedUserContextDTO;
import com.fiserv.uba.user.service.DrawerService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class DrawerController {

    private final DrawerService drawerService;

    public DrawerController(DrawerService drawerService) {
        this.drawerService = drawerService;
    }

    @GetMapping("/{sub}/drawers")
    public List<DrawerDTO> getDrawers(@PathVariable String sub) {
        return drawerService.getDrawers(sub);
    }

    @PostMapping("/{sub}/drawer/select/{drawerId}")
    public UpdatedUserContextDTO selectDrawer(@PathVariable String sub, @PathVariable String drawerId) {
        return drawerService.selectDrawer(sub, drawerId);
    }
}
