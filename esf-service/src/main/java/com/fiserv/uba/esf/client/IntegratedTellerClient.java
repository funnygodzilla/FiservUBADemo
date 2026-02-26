package com.fiserv.uba.esf.client;

import com.fiserv.uba.esf.dto.CashBoxDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "integrated-teller", url = "${integrated.teller.url:http://localhost:8085}")
public interface IntegratedTellerClient {
    @GetMapping("/cashbox/details")
    CashBoxDTO getCashBoxDetails(@RequestHeader("Authorization") String authorization);
}
