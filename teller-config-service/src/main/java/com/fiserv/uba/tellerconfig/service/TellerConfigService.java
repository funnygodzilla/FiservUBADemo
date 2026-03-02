package com.fiserv.uba.tellerconfig.service;

import com.fiserv.uba.tellerconfig.dto.BranchConfigDTO;
import com.fiserv.uba.tellerconfig.dto.EntitlementDecisionDTO;
import com.fiserv.uba.tellerconfig.dto.EntitlementRequestDTO;
import com.fiserv.uba.tellerconfig.dto.RoleConfigDTO;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class TellerConfigService {

    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("10000.00");

    public RoleConfigDTO role(String roleId) {
        return new RoleConfigDTO(roleId, List.of("CASHBOX_VIEW", "DRAWER_SELECT"));
    }

    public BranchConfigDTO branch(String branchId) {
        return new BranchConfigDTO(branchId, "ACTIVE");
    }

    public EntitlementDecisionDTO check(EntitlementRequestDTO request) {
        Map<String, String> actorRoles = Map.of(
                "teller01", "TELLER",
                "supervisor01", "SUPERVISOR",
                "manager01", "MANAGER"
        );
        String role = actorRoles.getOrDefault(request.actorId().toLowerCase(Locale.ROOT), "TELLER");
        boolean approvalRequired = request.amount() != null && request.amount().compareTo(HIGH_VALUE_THRESHOLD) >= 0;
        if ("TELLER".equals(role) && approvalRequired) {
            return new EntitlementDecisionDTO(false, role,
                    "Transaction exceeds teller limit for operation " + request.operation(), true);
        }
        return new EntitlementDecisionDTO(true, role, "ALLOWED", approvalRequired);
    }
}
