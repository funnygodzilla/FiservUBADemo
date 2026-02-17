package com.fiserv.uba.account.service.impl;

import com.fiserv.uba.account.domain.Account;
import com.fiserv.uba.account.dto.AccountResponseDTO;
import com.fiserv.uba.account.dto.CreateAccountRequest;
import com.fiserv.uba.account.dto.TransactionRequest;
import com.fiserv.uba.account.exception.BusinessException;
import com.fiserv.uba.account.mapper.AccountMapper;
import com.fiserv.uba.account.repository.AccountRepository;
import com.fiserv.uba.account.service.AccountService;
import com.fiserv.uba.account.util.AccountUtils;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountResponseDTO createAccount(CreateAccountRequest request) {
        Account account = new Account();
        account.setAccountId("ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        account.setCustomerId(AccountUtils.normalizeAccountId(request.getCustomerId()));
        account.setAccountType(request.getAccountType() == null ? "SAVINGS" : request.getAccountType());
        account.setCurrency(request.getCurrency() == null ? "USD" : request.getCurrency());
        account.setStatus("ACTIVE");
        account.setBalance(BigDecimal.valueOf(request.getInitialBalance() == null ? 0.0 : request.getInitialBalance()));
        return AccountMapper.toResponse(accountRepository.save(account));
    }

    @Override
    public AccountResponseDTO getAccount(String accountId) {
        String normalizedId = AccountUtils.normalizeAccountId(accountId);
        if (normalizedId == null || normalizedId.isEmpty()) {
            throw new BusinessException("Account id must be provided");
        }
        Account account = accountRepository.findById(normalizedId)
                .orElseThrow(() -> new BusinessException("Account not found: " + normalizedId));
        return AccountMapper.toResponse(account);
    }

    @Override
    public List<AccountResponseDTO> getCustomerAccounts(String customerId) {
        return accountRepository.findByCustomerId(customerId).stream().map(AccountMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public AccountResponseDTO debitAccount(TransactionRequest request) {
        Account account = requireActiveAccount(request.getAccountId());
        BigDecimal amount = BigDecimal.valueOf(request.getAmount());
        if (account.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("Insufficient balance");
        }
        account.setBalance(account.getBalance().subtract(amount));
        return AccountMapper.toResponse(accountRepository.save(account));
    }

    @Override
    public AccountResponseDTO creditAccount(TransactionRequest request) {
        Account account = requireActiveAccount(request.getAccountId());
        account.setBalance(account.getBalance().add(BigDecimal.valueOf(request.getAmount())));
        return AccountMapper.toResponse(accountRepository.save(account));
    }

    @Override
    public AccountResponseDTO freezeAccount(String accountId) {
        Account account = requireAccount(accountId);
        account.setStatus("FROZEN");
        return AccountMapper.toResponse(accountRepository.save(account));
    }

    @Override
    public AccountResponseDTO unfreezeAccount(String accountId) {
        Account account = requireAccount(accountId);
        account.setStatus("ACTIVE");
        return AccountMapper.toResponse(accountRepository.save(account));
    }

    private Account requireActiveAccount(String accountId) {
        Account account = requireAccount(accountId);
        if ("FROZEN".equals(account.getStatus())) {
            throw new BusinessException("Cannot perform transaction on frozen account");
        }
        return account;
    }

    private Account requireAccount(String accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException("Account not found: " + accountId));
    }
}
