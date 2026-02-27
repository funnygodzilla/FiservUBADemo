package com.fiserv.uba.tellerconfig.controller;

import com.fiserv.uba.tellerconfig.dto.BranchConfigDTO;
import com.fiserv.uba.tellerconfig.dto.RoleConfigDTO;
import com.fiserv.uba.tellerconfig.service.TellerConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
public class TellerConfigController {

    private final TellerConfigService tellerConfigService;

    public TellerConfigController(TellerConfigService tellerConfigService) {
        this.tellerConfigService = tellerConfigService;
    }

    @GetMapping("/roles/{roleId}")
    public RoleConfigDTO role(@PathVariable String roleId) {
        return tellerConfigService.role(roleId);
    }

    @GetMapping("/branches/{branchId}")
    public BranchConfigDTO branch(@PathVariable String branchId) {
        return tellerConfigService.branch(branchId);
    }
}
