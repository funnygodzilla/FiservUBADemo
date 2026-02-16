package com.fiserv.uba.account.client;

import org.springframework.stereotype.Component;

@Component
public class CustomerServiceClientFallback implements CustomerServiceClient {
    @Override
    public CustomerResponse getCustomer(Long customerId) {
        return CustomerResponse.builder()
                .id(customerId)
                .name("Unknown")
                .email("unknown@example.com")
                .build();
    }
}

