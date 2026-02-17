package com.fiserv.uba.account.dto;

import java.math.BigDecimal;

public class AccountResponseDTO {

    private String accountId;
    private String status;
    private String currency;
    private BigDecimal balance;

    public AccountResponseDTO() {
    }

    public AccountResponseDTO(String accountId, String status, String currency, BigDecimal balance) {
        this.accountId = accountId;
        this.status = status;
        this.currency = currency;
        this.balance = balance;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
