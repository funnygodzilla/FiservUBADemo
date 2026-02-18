package com.fiserv.uba.account.dto;

public class AccountRequestDTO {

    private String customerId;

    public AccountRequestDTO() {
    }

    public AccountRequestDTO(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
