package com.fiserv.uba.account.mapper;

import com.fiserv.uba.account.domain.Account;
import com.fiserv.uba.account.dto.AccountDTO;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountDTO toDTO(Account account) {
        if (account == null) {
            return null;
        }
        return AccountDTO.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .customerId(account.getCustomerId())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .status(account.getStatus())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    public Account toEntity(AccountDTO dto) {
        if (dto == null) {
            return null;
        }
        return Account.builder()
                .id(dto.getId())
                .accountNumber(dto.getAccountNumber())
                .customerId(dto.getCustomerId())
                .accountType(dto.getAccountType())
                .balance(dto.getBalance())
                .currency(dto.getCurrency())
                .status(dto.getStatus())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }
}

