package com.fiserv.uba.account.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service", url = "${customer.service.url:http://localhost:8080}", fallback = CustomerServiceClientFallback.class)
public interface CustomerServiceClient {

    @GetMapping("/api/v1/customers/{customerId}")
    CustomerResponse getCustomer(@PathVariable("customerId") Long customerId);
}

