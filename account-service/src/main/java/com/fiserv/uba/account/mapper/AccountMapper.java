package com.fiserv.uba.account.mapper;

import com.fiserv.uba.account.domain.Account;
import com.fiserv.uba.account.dto.AccountResponseDTO;

public final class AccountMapper {

    private AccountMapper() {}

    public static AccountResponseDTO toResponse(Account account) {
        if (account == null) {
            return null;
        }
        return new AccountResponseDTO(
                account.getAccountId(),
                account.getStatus(),
                account.getCurrency(),
                account.getBalance() == null ? 0.0 : account.getBalance().doubleValue()
        );
    }
}
