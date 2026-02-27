package com.fiserv.uba.account.dto;

public class CreateAccountRequest {
    private String customerId;
    private String accountType;
    private Double initialBalance;
    private String currency;

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public Double getInitialBalance() { return initialBalance; }
    public void setInitialBalance(Double initialBalance) { this.initialBalance = initialBalance; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
