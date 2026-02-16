package com.fiserv.uba.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiserv.uba.account.dto.AccountDTO;
import com.fiserv.uba.account.dto.ApiResponse;
import com.fiserv.uba.account.dto.CreateAccountRequest;
import com.fiserv.uba.account.dto.TransactionRequest;
import com.fiserv.uba.account.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    private AccountDTO accountDTO;
    private CreateAccountRequest createAccountRequest;
    private TransactionRequest transactionRequest;

    @BeforeEach
    void setUp() {
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
    void testCreateAccount() throws Exception {
        when(accountService.createAccount(any(CreateAccountRequest.class))).thenReturn(accountDTO);

        mockMvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAccountRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Account created successfully"))
                .andExpect(jsonPath("$.data.accountNumber").value("ACC1707210600ABCD1234"));
    }

    @Test
    void testGetAccount() throws Exception {
        when(accountService.getAccountByNumber(anyString())).thenReturn(accountDTO);

        mockMvc.perform(get("/api/v1/accounts/{accountNumber}", "ACC1707210600ABCD1234")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Account retrieved successfully"))
                .andExpect(jsonPath("$.data.accountNumber").value("ACC1707210600ABCD1234"));
    }

    @Test
    void testGetCustomerAccounts() throws Exception {
        List<AccountDTO> accounts = Arrays.asList(accountDTO);
        when(accountService.getCustomerAccounts(anyLong())).thenReturn(accounts);

        mockMvc.perform(get("/api/v1/accounts/customer/{customerId}", 1001L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Customer accounts retrieved successfully"))
                .andExpect(jsonPath("$.data[0].customerId").value(1001));
    }

    @Test
    void testDebitAccount() throws Exception {
        AccountDTO debitedAccount = AccountDTO.builder()
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
        when(accountService.debitAccount(any(TransactionRequest.class))).thenReturn(debitedAccount);

        mockMvc.perform(post("/api/v1/accounts/debit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Amount debited successfully"))
                .andExpect(jsonPath("$.data.balance").value(4500.00));
    }

    @Test
    void testCreditAccount() throws Exception {
        AccountDTO creditedAccount = AccountDTO.builder()
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
        when(accountService.creditAccount(any(TransactionRequest.class))).thenReturn(creditedAccount);

        mockMvc.perform(post("/api/v1/accounts/credit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Amount credited successfully"))
                .andExpect(jsonPath("$.data.balance").value(5500.00));
    }

    @Test
    void testFreezeAccount() throws Exception {
        AccountDTO frozenAccount = AccountDTO.builder()
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
        when(accountService.freezeAccount(anyString())).thenReturn(frozenAccount);

        mockMvc.perform(put("/api/v1/accounts/{accountNumber}/freeze", "ACC1707210600ABCD1234")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Account frozen successfully"))
                .andExpect(jsonPath("$.data.status").value("FROZEN"));
    }

    @Test
    void testUnfreezeAccount() throws Exception {
        AccountDTO unfrozenAccount = AccountDTO.builder()
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
        when(accountService.unfreezeAccount(anyString())).thenReturn(unfrozenAccount);

        mockMvc.perform(put("/api/v1/accounts/{accountNumber}/unfreeze", "ACC1707210600ABCD1234")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Account unfrozen successfully"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }
}

