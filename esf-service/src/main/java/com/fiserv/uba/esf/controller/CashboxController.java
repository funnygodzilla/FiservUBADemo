package com.fiserv.uba.esf.controller;

import com.fiserv.uba.esf.dto.CashBoxDTO;
import com.fiserv.uba.esf.service.CashboxService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CashboxController {
    private final CashboxService cashboxService;
    public CashboxController(CashboxService cashboxService){this.cashboxService=cashboxService;}
    @GetMapping("/cashbox/details")
    public CashBoxDTO details(@RequestHeader("Authorization") String authorization){return cashboxService.getCashbox(authorization);}    
}
