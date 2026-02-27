package com.fiserv.uba.account.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiserv.uba.account.domain.Account;
import com.fiserv.uba.account.dto.CreateAccountRequest;
import com.fiserv.uba.account.dto.TransactionRequest;
import com.fiserv.uba.account.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

//    @Test
//    void testCreateAndRetrieveAccount() throws Exception {
//        CreateAccountRequest request = CreateAccountRequest.builder()
//                .customerId(1001L)
//                .accountType("CHECKING")
//                .initialBalance(new BigDecimal("5000.00"))
//                .currency("USD")
//                .build();
//
//        mockMvc.perform(post("/api/v1/accounts")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.status").value(201));
//    }

    /*@Test
    void testCreateAndDebitAccount() throws Exception {
        // Create account
        CreateAccountRequest createRequest = CreateAccountRequest.builder()
                .customerId(1001L)
                .accountType("CHECKING")
                .initialBalance(new BigDecimal("5000.00"))
                .currency("USD")
                .build();

        // Manually save account for test
        Account account = Account.builder()
                .accountNumber("ACC1707210600ABCD1234")
                .customerId(1001L)
                .accountType("CHECKING")
                .balance(new BigDecimal("5000.00"))
                .currency("USD")
                .status("ACTIVE")
                .build();
        accountRepository.save(account);

        // Debit the account
        TransactionRequest debitRequest = TransactionRequest.builder()
                .accountNumber("ACC1707210600ABCD1234")
                .amount(new BigDecimal("500.00"))
                .description("Test debit")
                .build();

        mockMvc.perform(post("/api/v1/accounts/debit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(debitRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.balance").value(4500.00));
    }*/

//    @Test
//    void testCreateAndFreezeAccount() throws Exception {
//        // Create account
//        Account account = Account.builder()
//                .accountNumber("ACC1707210600ABCD1234")
//                .customerId(1001L)
//                .accountType("CHECKING")
//                .balance(new BigDecimal("5000.00"))
//                .currency("USD")
//                .status("ACTIVE")
//                .build();
//        accountRepository.save(account);
//
//        // Freeze the account
//        mockMvc.perform(put("/api/v1/accounts/{accountNumber}/freeze", "ACC1707210600ABCD1234")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.status").value("FROZEN"));
//
//        // Try to debit frozen account - should fail
//        TransactionRequest debitRequest = TransactionRequest.builder()
//                .accountNumber("ACC1707210600ABCD1234")
//                .amount(new BigDecimal("500.00"))
//                .description("Test debit")
//                .build();
//
//        mockMvc.perform(post("/api/v1/accounts/debit")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(debitRequest)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void testGetCustomerAccounts() throws Exception {
//        // Create multiple accounts
//        Account account1 = Account.builder()
//                .accountNumber("ACC1")
//                .customerId(1001L)
//                .accountType("CHECKING")
//                .balance(new BigDecimal("5000.00"))
//                .currency("USD")
//                .status("ACTIVE")
//                .build();
//
//        Account account2 = Account.builder()
//                .accountNumber("ACC2")
//                .customerId(1001L)
//                .accountType("SAVINGS")
//                .balance(new BigDecimal("10000.00"))
//                .currency("USD")
//                .status("ACTIVE")
//                .build();
//
//        accountRepository.save(account1);
//        accountRepository.save(account2);
//
//        // Retrieve customer accounts
//        mockMvc.perform(get("/api/v1/accounts/customer/{customerId}", 1001L)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(200))
//                .andExpect(jsonPath("$.data[0].customerId").value(1001));
//    }

    /*@Test
    void testTransactionSequence() throws Exception {
        // Create account
        Account account = Account.builder()
                .accountNumber("ACC_TEST_001")
                .customerId(1001L)
                .accountType("CHECKING")
                .balance(new BigDecimal("5000.00"))
                .currency("USD")
                .status("ACTIVE")
                .build();
        accountRepository.save(account);

        // Credit account
        TransactionRequest creditRequest = TransactionRequest.builder()
                .accountNumber("ACC_TEST_001")
                .amount(new BigDecimal("1000.00"))
                .description("Credit")
                .build();

        mockMvc.perform(post("/api/v1/accounts/credit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creditRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.balance").value(6000.00));

        // Debit account
        TransactionRequest debitRequest = TransactionRequest.builder()
                .accountNumber("ACC_TEST_001")
                .amount(new BigDecimal("500.00"))
                .description("Debit")
                .build();

        mockMvc.perform(post("/api/v1/accounts/debit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(debitRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.balance").value(5500.00));
    }*/
}

