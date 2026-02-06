package com.fiserv.uba.account.service.impl;

import com.fiserv.uba.account.domain.Account;
import com.fiserv.uba.account.dto.AccountResponseDTO;
import com.fiserv.uba.account.exception.BusinessException;
import com.fiserv.uba.account.mapper.AccountMapper;
import com.fiserv.uba.account.repository.AccountRepository;
import com.fiserv.uba.account.service.AccountService;
import com.fiserv.uba.account.util.AccountUtils;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountResponseDTO getAccount(String accountId) {
        String normalizedId = AccountUtils.normalizeAccountId(accountId);
        if (normalizedId == null || normalizedId.isEmpty()) {
            throw new BusinessException("Account id must be provided");
        }
        Optional<Account> account = accountRepository.findById(normalizedId);
        return AccountMapper.toResponse(account.orElseThrow(
                () -> new BusinessException("Account not found: " + normalizedId)
        ));
    }
}
