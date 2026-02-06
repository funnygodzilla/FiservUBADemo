package com.fiserv.uba.account.service;

import com.fiserv.uba.account.domain.Account;
import com.fiserv.uba.account.dto.AccountDTO;
import com.fiserv.uba.account.dto.CreateAccountRequest;
import com.fiserv.uba.account.dto.TransactionRequest;
import com.fiserv.uba.account.mapper.AccountMapper;
import com.fiserv.uba.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    /**
     * Create a new account
     */
    public AccountDTO createAccount(CreateAccountRequest request) {
        log.info("Creating account for customer: {}", request.getCustomerId());

        String accountNumber = generateAccountNumber();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .customerId(request.getCustomerId())
                .accountType(request.getAccountType())
                .balance(request.getInitialBalance() != null ? request.getInitialBalance() : BigDecimal.ZERO)
                .currency(request.getCurrency() != null ? request.getCurrency() : "USD")
                .status("ACTIVE")
                .build();

        Account savedAccount = accountRepository.save(account);
        log.info("Account created successfully with number: {}", accountNumber);
        return accountMapper.toDTO(savedAccount);
    }

    /**
     * Get account by account number
     */
    public AccountDTO getAccountByNumber(String accountNumber) {
        log.info("Fetching account: {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
        return accountMapper.toDTO(account);
    }

    /**
     * Get all accounts for a customer
     */
    public List<AccountDTO> getCustomerAccounts(Long customerId) {
        log.info("Fetching accounts for customer: {}", customerId);
        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        return accounts.stream()
                .map(accountMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Debit an account
     */
    public AccountDTO debitAccount(TransactionRequest request) {
        log.info("Debiting account: {} with amount: {}", request.getAccountNumber(), request.getAmount());

        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found: " + request.getAccountNumber()));

        if ("FROZEN".equals(account.getStatus())) {
            throw new RuntimeException("Cannot perform transaction on frozen account");
        }

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        Account updatedAccount = accountRepository.save(account);
        log.info("Account debited successfully");
        return accountMapper.toDTO(updatedAccount);
    }

    /**
     * Credit an account
     */
    public AccountDTO creditAccount(TransactionRequest request) {
        log.info("Crediting account: {} with amount: {}", request.getAccountNumber(), request.getAmount());

        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found: " + request.getAccountNumber()));

        if ("FROZEN".equals(account.getStatus())) {
            throw new RuntimeException("Cannot perform transaction on frozen account");
        }

        account.setBalance(account.getBalance().add(request.getAmount()));
        Account updatedAccount = accountRepository.save(account);
        log.info("Account credited successfully");
        return accountMapper.toDTO(updatedAccount);
    }

    /**
     * Freeze an account
     */
    public AccountDTO freezeAccount(String accountNumber) {
        log.info("Freezing account: {}", accountNumber);

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        account.setStatus("FROZEN");
        Account updatedAccount = accountRepository.save(account);
        log.info("Account frozen successfully");
        return accountMapper.toDTO(updatedAccount);
    }

    /**
     * Unfreeze an account
     */
    public AccountDTO unfreezeAccount(String accountNumber) {
        log.info("Unfreezing account: {}", accountNumber);

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        account.setStatus("ACTIVE");
        Account updatedAccount = accountRepository.save(account);
        log.info("Account unfrozen successfully");
        return accountMapper.toDTO(updatedAccount);
    }

    /**
     * Generate unique account number
     */
    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

