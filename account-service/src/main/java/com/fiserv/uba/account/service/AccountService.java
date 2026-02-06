package com.fiserv.uba.account.service;

import com.fiserv.uba.account.dto.AccountResponseDTO;

public interface AccountService {
    AccountResponseDTO getAccount(String accountId);
}
