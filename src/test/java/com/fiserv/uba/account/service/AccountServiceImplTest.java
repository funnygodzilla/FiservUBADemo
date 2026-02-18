package com.fiserv.uba.account.service;

import com.fiserv.uba.account.domain.Account;
import com.fiserv.uba.account.dto.AccountDTO;
import com.fiserv.uba.account.dto.CreateAccountRequest;
import com.fiserv.uba.account.dto.TransactionRequest;
import com.fiserv.uba.account.mapper.AccountMapper;
import com.fiserv.uba.account.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account account;
    private AccountDTO accountDTO;
    private CreateAccountRequest createAccountRequest;
    private TransactionRequest transactionRequest;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .id(1L)
                .accountNumber("ACC1707210600ABCD1234")
                .customerId(1001L)
                .accountType("CHECKING")
                .balance(new BigDecimal("5000.00"))
                .currency("USD")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        accountDTO = AccountDTO.builder()
                .id(1L)
                .accountNumber("ACC1707210600ABCD1234")
                .customerId(1001L)
                .accountType("CHECKING")
                .balance(new BigDecimal("5000.00"))
                .currency("USD")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createAccountRequest = CreateAccountRequest.builder()
                .customerId(1001L)
                .accountType("CHECKING")
                .initialBalance(new BigDecimal("5000.00"))
                .currency("USD")
                .build();

        transactionRequest = TransactionRequest.builder()
                .accountNumber("ACC1707210600ABCD1234")
                .amount(new BigDecimal("500.00"))
                .description("Test transaction")
                .build();
    }

    @Test
    void testCreateAccount_Success() {
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountMapper.toDTO(account)).thenReturn(accountDTO);

        AccountDTO result = accountService.createAccount(createAccountRequest);

        assertNotNull(result);
        assertEquals(accountDTO.getAccountNumber(), result.getAccountNumber());
        assertEquals(accountDTO.getCustomerId(), result.getCustomerId());
        verify(accountRepository, times(1)).save(any(Account.class));
        verify(accountMapper, times(1)).toDTO(account);
    }

    @Test
    void testGetAccountByNumber_Success() {
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));
        when(accountMapper.toDTO(account)).thenReturn(accountDTO);

        AccountDTO result = accountService.getAccountByNumber("ACC1707210600ABCD1234");

        assertNotNull(result);
        assertEquals(accountDTO.getAccountNumber(), result.getAccountNumber());
        verify(accountRepository, times(1)).findByAccountNumber("ACC1707210600ABCD1234");
    }

    @Test
    void testGetAccountByNumber_NotFound() {
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            accountService.getAccountByNumber("INVALID_ACCOUNT");
        });
        verify(accountRepository, times(1)).findByAccountNumber("INVALID_ACCOUNT");
    }

    @Test
    void testGetCustomerAccounts_Success() {
        List<Account> accounts = Arrays.asList(account);
        List<AccountDTO> accountDTOs = Arrays.asList(accountDTO);

        when(accountRepository.findByCustomerId(anyLong())).thenReturn(accounts);
        when(accountMapper.toDTO(account)).thenReturn(accountDTO);

        List<AccountDTO> result = accountService.getCustomerAccounts(1001L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(accountDTO.getCustomerId(), result.get(0).getCustomerId());
        verify(accountRepository, times(1)).findByCustomerId(1001L);
    }

    @Test
    void testDebitAccount_Success() {
        Account debitedAccount = Account.builder()
                .id(1L)
                .accountNumber("ACC1707210600ABCD1234")
                .customerId(1001L)
                .accountType("CHECKING")
                .balance(new BigDecimal("4500.00"))
                .currency("USD")
                .status("ACTIVE")
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
        AccountDTO debitedDTO = AccountDTO.builder()
                .id(1L)
                .accountNumber("ACC1707210600ABCD1234")
                .customerId(1001L)
                .accountType("CHECKING")
                .balance(new BigDecimal("4500.00"))
                .currency("USD")
                .status("ACTIVE")
                .createdAt(accountDTO.getCreatedAt())
                .updatedAt(accountDTO.getUpdatedAt())
                .build();

        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(debitedAccount);
        when(accountMapper.toDTO(debitedAccount)).thenReturn(debitedDTO);

        AccountDTO result = accountService.debitAccount(transactionRequest);

        assertNotNull(result);
        assertEquals(new BigDecimal("4500.00"), result.getBalance());
        verify(accountRepository, times(1)).findByAccountNumber("ACC1707210600ABCD1234");
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testDebitAccount_InsufficientBalance() {
        TransactionRequest largeTransaction = TransactionRequest.builder()
                .accountNumber("ACC1707210600ABCD1234")
                .amount(new BigDecimal("10000.00"))
                .description("Large debit")
                .build();

        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));

        assertThrows(RuntimeException.class, () -> {
            accountService.debitAccount(largeTransaction);
        });
        verify(accountRepository, times(1)).findByAccountNumber("ACC1707210600ABCD1234");
    }

    @Test
    void testDebitAccount_FrozenAccount() {
        Account frozenAccount = Account.builder()
                .id(1L)
                .accountNumber("ACC1707210600ABCD1234")
                .customerId(1001L)
                .accountType("CHECKING")
                .balance(new BigDecimal("5000.00"))
                .currency("USD")
                .status("FROZEN")
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();

        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(frozenAccount));

        assertThrows(RuntimeException.class, () -> {
            accountService.debitAccount(transactionRequest);
        });
        verify(accountRepository, times(1)).findByAccountNumber("ACC1707210600ABCD1234");
    }

    @Test
    void testCreditAccount_Success() {
        Account creditedAccount = Account.builder()
                .id(1L)
                .accountNumber("ACC1707210600ABCD1234")
                .customerId(1001L)
                .accountType("CHECKING")
                .balance(new BigDecimal("5500.00"))
                .currency("USD")
                .status("ACTIVE")
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
        AccountDTO creditedDTO = AccountDTO.builder()
                .id(1L)
                .accountNumber("ACC1707210600ABCD1234")
                .customerId(1001L)
                .accountType("CHECKING")
                .balance(new BigDecimal("5500.00"))
                .currency("USD")
                .status("ACTIVE")
                .createdAt(accountDTO.getCreatedAt())
                .updatedAt(accountDTO.getUpdatedAt())
                .build();

        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(creditedAccount);
        when(accountMapper.toDTO(creditedAccount)).thenReturn(creditedDTO);

        AccountDTO result = accountService.creditAccount(transactionRequest);

        assertNotNull(result);
        assertEquals(new BigDecimal("5500.00"), result.getBalance());
        verify(accountRepository, times(1)).findByAccountNumber("ACC1707210600ABCD1234");
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testCreditAccount_FrozenAccount() {
        Account frozenAccount = Account.builder()
                .id(1L)
                .accountNumber("ACC1707210600ABCD1234")
                .customerId(1001L)
                .accountType("CHECKING")
                .balance(new BigDecimal("5000.00"))
                .currency("USD")
                .status("FROZEN")
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();

        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(frozenAccount));

        assertThrows(RuntimeException.class, () -> {
            accountService.creditAccount(transactionRequest);
        });
        verify(accountRepository, times(1)).findByAccountNumber("ACC1707210600ABCD1234");
    }

    @Test
    void testFreezeAccount_Success() {
        Account frozenAccount = Account.builder()
                .id(1L)
                .accountNumber("ACC1707210600ABCD1234")
                .customerId(1001L)
                .accountType("CHECKING")
                .balance(new BigDecimal("5000.00"))
                .currency("USD")
                .status("FROZEN")
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
        AccountDTO frozenDTO = AccountDTO.builder()
                .id(1L)
                .accountNumber("ACC1707210600ABCD1234")
                .customerId(1001L)
                .accountType("CHECKING")
                .balance(new BigDecimal("5000.00"))
                .currency("USD")
                .status("FROZEN")
                .createdAt(accountDTO.getCreatedAt())
                .updatedAt(accountDTO.getUpdatedAt())
                .build();

        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(frozenAccount);
        when(accountMapper.toDTO(frozenAccount)).thenReturn(frozenDTO);

        AccountDTO result = accountService.freezeAccount("ACC1707210600ABCD1234");

        assertNotNull(result);
        assertEquals("FROZEN", result.getStatus());
        verify(accountRepository, times(1)).findByAccountNumber("ACC1707210600ABCD1234");
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testUnfreezeAccount_Success() {
        Account unfrozenAccount = Account.builder()
                .id(1L)
                .accountNumber("ACC1707210600ABCD1234")
                .customerId(1001L)
                .accountType("CHECKING")
                .balance(new BigDecimal("5000.00"))
                .currency("USD")
                .status("ACTIVE")
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
        AccountDTO unfrozenDTO = AccountDTO.builder()
                .id(1L)
                .accountNumber("ACC1707210600ABCD1234")
                .customerId(1001L)
                .accountType("CHECKING")
                .balance(new BigDecimal("5000.00"))
                .currency("USD")
                .status("ACTIVE")
                .createdAt(accountDTO.getCreatedAt())
                .updatedAt(accountDTO.getUpdatedAt())
                .build();

        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(unfrozenAccount);
        when(accountMapper.toDTO(unfrozenAccount)).thenReturn(unfrozenDTO);

        AccountDTO result = accountService.unfreezeAccount("ACC1707210600ABCD1234");

        assertNotNull(result);
        assertEquals("ACTIVE", result.getStatus());
        verify(accountRepository, times(1)).findByAccountNumber("ACC1707210600ABCD1234");
        verify(accountRepository, times(1)).save(any(Account.class));
    }
}

