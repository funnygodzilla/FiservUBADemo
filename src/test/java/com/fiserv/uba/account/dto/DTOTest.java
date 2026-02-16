package com.fiserv.uba.account.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DTOTest {

    @Test
    void testAccountDTOCreation() {
        AccountDTO accountDTO = AccountDTO.builder()
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

        assertNotNull(accountDTO);
        assertEquals(1L, accountDTO.getId());
        assertEquals("ACC1707210600ABCD1234", accountDTO.getAccountNumber());
        assertEquals(1001L, accountDTO.getCustomerId());
    }

    @Test
    void testCreateAccountRequestCreation() {
        CreateAccountRequest request = CreateAccountRequest.builder()
                .customerId(1001L)
                .accountType("CHECKING")
                .initialBalance(new BigDecimal("5000.00"))
                .currency("USD")
                .build();

        assertNotNull(request);
        assertEquals(1001L, request.getCustomerId());
        assertEquals("CHECKING", request.getAccountType());
        assertEquals(new BigDecimal("5000.00"), request.getInitialBalance());
        assertEquals("USD", request.getCurrency());
    }

    @Test
    void testCreateAccountRequestWithNullBalance() {
        CreateAccountRequest request = CreateAccountRequest.builder()
                .customerId(1001L)
                .accountType("CHECKING")
                .currency("USD")
                .build();

        assertNotNull(request);
        assertNull(request.getInitialBalance());
    }

    @Test
    void testTransactionRequestCreation() {
        TransactionRequest request = TransactionRequest.builder()
                .accountNumber("ACC1707210600ABCD1234")
                .amount(new BigDecimal("500.00"))
                .description("Test debit")
                .build();

        assertNotNull(request);
        assertEquals("ACC1707210600ABCD1234", request.getAccountNumber());
        assertEquals(new BigDecimal("500.00"), request.getAmount());
        assertEquals("Test debit", request.getDescription());
    }

    @Test
    void testApiResponseCreation() {
        AccountDTO accountDTO = AccountDTO.builder()
                .accountNumber("ACC1707210600ABCD1234")
                .customerId(1001L)
                .build();

        ApiResponse<AccountDTO> response = new ApiResponse<>(200, "Success", accountDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Success", response.getMessage());
        assertEquals(accountDTO, response.getData());
    }

    @Test
    void testApiResponseWithNullData() {
        ApiResponse<String> response = new ApiResponse<>(404, "Not Found", null);

        assertNotNull(response);
        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testDTOSettersAndGetters() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(1L);
        accountDTO.setAccountNumber("ACC1707210600ABCD1234");
        accountDTO.setCustomerId(1001L);

        assertEquals(1L, accountDTO.getId());
        assertEquals("ACC1707210600ABCD1234", accountDTO.getAccountNumber());
        assertEquals(1001L, accountDTO.getCustomerId());
    }

    @Test
    void testCreateAccountRequestSettersAndGetters() {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setCustomerId(1001L);
        request.setAccountType("CHECKING");
        request.setInitialBalance(new BigDecimal("5000.00"));
        request.setCurrency("USD");

        assertEquals(1001L, request.getCustomerId());
        assertEquals("CHECKING", request.getAccountType());
        assertEquals(new BigDecimal("5000.00"), request.getInitialBalance());
        assertEquals("USD", request.getCurrency());
    }

    @Test
    void testTransactionRequestSettersAndGetters() {
        TransactionRequest request = new TransactionRequest();
        request.setAccountNumber("ACC1707210600ABCD1234");
        request.setAmount(new BigDecimal("500.00"));
        request.setDescription("Test");

        assertEquals("ACC1707210600ABCD1234", request.getAccountNumber());
        assertEquals(new BigDecimal("500.00"), request.getAmount());
        assertEquals("Test", request.getDescription());
    }
}

