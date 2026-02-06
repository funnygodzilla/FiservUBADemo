package com.fiserv.uba.account.mapper;

import com.fiserv.uba.account.domain.Account;
import com.fiserv.uba.account.dto.AccountResponseDTO;
import java.math.BigDecimal;

public final class AccountMapper {

    private AccountMapper() {
    }

    public static AccountResponseDTO toResponse(Account account) {
        if (account == null) {
            return null;
        }
        double balance = account.getBalance() == null ? 0.0 : account.getBalance().doubleValue();
        return new AccountResponseDTO(
                account.getAccountId(),
                account.getStatus(),
                account.getCurrency(),
                balance
        );
    }

    public static Account toEntity(AccountResponseDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Account(
                dto.getAccountId(),
                dto.getStatus(),
                dto.getCurrency(),
                BigDecimal.valueOf(dto.getBalance())
        );
    }
}
