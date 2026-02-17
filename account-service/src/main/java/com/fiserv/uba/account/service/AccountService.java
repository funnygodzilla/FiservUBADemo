package com.fiserv.uba.account.service;

import com.fiserv.uba.account.dto.AccountResponseDTO;
import com.fiserv.uba.account.dto.CreateAccountRequest;
import com.fiserv.uba.account.dto.TransactionRequest;
import java.util.List;

public interface AccountService {
    AccountResponseDTO createAccount(CreateAccountRequest request);
    AccountResponseDTO getAccount(String accountId);
    List<AccountResponseDTO> getCustomerAccounts(String customerId);
    AccountResponseDTO debitAccount(TransactionRequest request);
    AccountResponseDTO creditAccount(TransactionRequest request);
    AccountResponseDTO freezeAccount(String accountId);
    AccountResponseDTO unfreezeAccount(String accountId);
}
