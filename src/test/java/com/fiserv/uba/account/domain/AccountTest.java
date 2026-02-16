package com.fiserv.uba.account.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account account;

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
    }

    @Test
    void testAccountCreation() {
        assertNotNull(account);
        assertEquals(1L, account.getId());
        assertEquals("ACC1707210600ABCD1234", account.getAccountNumber());
        assertEquals(1001L, account.getCustomerId());
        assertEquals("CHECKING", account.getAccountType());
        assertEquals(new BigDecimal("5000.00"), account.getBalance());
        assertEquals("USD", account.getCurrency());
        assertEquals("ACTIVE", account.getStatus());
    }

    @Test
    void testAccountBuilder() {
        Account builtAccount = Account.builder()
                .accountNumber("ACC_TEST_001")
                .customerId(2001L)
                .accountType("SAVINGS")
                .balance(new BigDecimal("10000.00"))
                .currency("EUR")
                .status("ACTIVE")
                .build();

        assertNotNull(builtAccount);
        assertEquals("ACC_TEST_001", builtAccount.getAccountNumber());
        assertEquals(2001L, builtAccount.getCustomerId());
        assertEquals("SAVINGS", builtAccount.getAccountType());
    }

    @Test
    void testAccountSettersAndGetters() {
        account.setBalance(new BigDecimal("6000.00"));
        account.setStatus("FROZEN");

        assertEquals(new BigDecimal("6000.00"), account.getBalance());
        assertEquals("FROZEN", account.getStatus());
    }

    @Test
    void testAccountEquality() {
        Account account2 = Account.builder()
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

        assertEquals(account, account2);
    }

    @Test
    void testAccountHashCode() {
        Account account2 = Account.builder()
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

        assertEquals(account.hashCode(), account2.hashCode());
    }

    @Test
    void testAccountWithNullValues() {
        Account nullAccount = Account.builder().build();

        assertNotNull(nullAccount);
        assertNull(nullAccount.getId());
        assertNull(nullAccount.getAccountNumber());
    }

    @Test
    void testAccountToBuilder() {
        Account newAccount = Account.builder()
                .accountNumber("ACC1707210600ABCD1234")
                .customerId(1001L)
                .accountType("CHECKING")
                .balance(new BigDecimal("7000.00"))
                .currency("USD")
                .status("ACTIVE")
                .build();

        assertEquals("ACC1707210600ABCD1234", newAccount.getAccountNumber());
        assertEquals(new BigDecimal("7000.00"), newAccount.getBalance());
        assertEquals(1001L, newAccount.getCustomerId());
    }
}

