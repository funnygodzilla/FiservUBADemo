package com.fiserv.uba.account.mapper;

import com.fiserv.uba.account.domain.Account;
import com.fiserv.uba.account.dto.AccountResponseDTO;
import java.math.BigDecimal;

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
                account.getBalance() == null ? BigDecimal.ZERO : account.getBalance()
        );
    }
}
