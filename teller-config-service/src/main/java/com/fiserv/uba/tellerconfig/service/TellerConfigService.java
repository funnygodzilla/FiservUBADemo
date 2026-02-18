package com.fiserv.uba.tellerconfig.service;

import com.fiserv.uba.tellerconfig.dto.BranchConfigDTO;
import com.fiserv.uba.tellerconfig.dto.RoleConfigDTO;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TellerConfigService {

    public RoleConfigDTO role(String roleId) {
        return new RoleConfigDTO(roleId, List.of("CASHBOX_VIEW", "DRAWER_SELECT"));
    }

    public BranchConfigDTO branch(String branchId) {
        return new BranchConfigDTO(branchId, "ACTIVE");
    }
}
