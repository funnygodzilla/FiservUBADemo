package com.fiserv.uba.it.controller;

import com.fiserv.uba.it.dto.CashBoxDTO;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CashboxController {
    @GetMapping("/cashbox/details")
    public CashBoxDTO details() {
        return new CashBoxDTO("D-100", "BR-01", BigDecimal.valueOf(15000));
    }
}
