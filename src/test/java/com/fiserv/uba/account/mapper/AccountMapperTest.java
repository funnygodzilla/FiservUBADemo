package com.fiserv.uba.account.mapper;

import com.fiserv.uba.account.domain.Account;
import com.fiserv.uba.account.dto.AccountDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AccountMapperTest {

    private AccountMapper accountMapper;
    private Account account;
    private AccountDTO accountDTO;

    @BeforeEach
    void setUp() {
        accountMapper = new AccountMapper();

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
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    @Test
    void testToDTO_Success() {
        AccountDTO result = accountMapper.toDTO(account);

        assertNotNull(result);
        assertEquals(account.getId(), result.getId());
        assertEquals(account.getAccountNumber(), result.getAccountNumber());
        assertEquals(account.getCustomerId(), result.getCustomerId());
        assertEquals(account.getAccountType(), result.getAccountType());
        assertEquals(account.getBalance(), result.getBalance());
        assertEquals(account.getCurrency(), result.getCurrency());
        assertEquals(account.getStatus(), result.getStatus());
    }

    @Test
    void testToDTO_NullAccount() {
        AccountDTO result = accountMapper.toDTO(null);

        assertNull(result);
    }

    @Test
    void testToEntity_Success() {
        Account result = accountMapper.toEntity(accountDTO);

        assertNotNull(result);
        assertEquals(accountDTO.getId(), result.getId());
        assertEquals(accountDTO.getAccountNumber(), result.getAccountNumber());
        assertEquals(accountDTO.getCustomerId(), result.getCustomerId());
        assertEquals(accountDTO.getAccountType(), result.getAccountType());
        assertEquals(accountDTO.getBalance(), result.getBalance());
        assertEquals(accountDTO.getCurrency(), result.getCurrency());
        assertEquals(accountDTO.getStatus(), result.getStatus());
    }

    @Test
    void testToEntity_NullDTO() {
        Account result = accountMapper.toEntity(null);

        assertNull(result);
    }

    @Test
    void testBidirectionalMapping() {
        AccountDTO dto = accountMapper.toDTO(account);
        Account mappedAccount = accountMapper.toEntity(dto);
        AccountDTO mappedDTO = accountMapper.toDTO(mappedAccount);

        assertEquals(dto.getAccountNumber(), mappedDTO.getAccountNumber());
        assertEquals(dto.getCustomerId(), mappedDTO.getCustomerId());
        assertEquals(dto.getBalance(), mappedDTO.getBalance());
    }
}

