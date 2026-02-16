package com.fiserv.uba.account.controller;

import com.fiserv.uba.account.dto.AccountDTO;
import com.fiserv.uba.account.dto.ApiResponse;
import com.fiserv.uba.account.dto.CreateAccountRequest;
import com.fiserv.uba.account.dto.TransactionRequest;
import com.fiserv.uba.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    /**
     * POST /api/v1/accounts - Create a new account
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AccountDTO>> createAccount(@RequestBody CreateAccountRequest request) {
        log.info("Creating account for customer: {}", request.getCustomerId());
        AccountDTO accountDTO = accountService.createAccount(request);
        ApiResponse<AccountDTO> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Account created successfully",
                accountDTO
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/accounts/{accountNumber} - Get account by account number
     */
    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<AccountDTO>> getAccount(@PathVariable String accountNumber) {
        log.info("Fetching account: {}", accountNumber);
        AccountDTO accountDTO = accountService.getAccountByNumber(accountNumber);
        ApiResponse<AccountDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Account retrieved successfully",
                accountDTO
        );
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/customers/{customerId}/accounts - Get all accounts for a customer
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<AccountDTO>>> getCustomerAccounts(@PathVariable Long customerId) {
        log.info("Fetching accounts for customer: {}", customerId);
        List<AccountDTO> accounts = accountService.getCustomerAccounts(customerId);
        ApiResponse<List<AccountDTO>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Customer accounts retrieved successfully",
                accounts
        );
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/accounts/debit - Debit from an account
     */
    @PostMapping("/debit")
    public ResponseEntity<ApiResponse<AccountDTO>> debitAccount(@RequestBody TransactionRequest request) {
        log.info("Debiting account: {}", request.getAccountNumber());
        AccountDTO accountDTO = accountService.debitAccount(request);
        ApiResponse<AccountDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Amount debited successfully",
                accountDTO
        );
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/accounts/credit - Credit to an account
     */
    @PostMapping("/credit")
    public ResponseEntity<ApiResponse<AccountDTO>> creditAccount(@RequestBody TransactionRequest request) {
        log.info("Crediting account: {}", request.getAccountNumber());
        AccountDTO accountDTO = accountService.creditAccount(request);
        ApiResponse<AccountDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Amount credited successfully",
                accountDTO
        );
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/v1/accounts/{accountNumber}/freeze - Freeze an account
     */
    @PutMapping("/{accountNumber}/freeze")
    public ResponseEntity<ApiResponse<AccountDTO>> freezeAccount(@PathVariable String accountNumber) {
        log.info("Freezing account: {}", accountNumber);
        AccountDTO accountDTO = accountService.freezeAccount(accountNumber);
        ApiResponse<AccountDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Account frozen successfully",
                accountDTO
        );
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/v1/accounts/{accountNumber}/unfreeze - Unfreeze an account
     */
    @PutMapping("/{accountNumber}/unfreeze")
    public ResponseEntity<ApiResponse<AccountDTO>> unfreezeAccount(@PathVariable String accountNumber) {
        log.info("Unfreezing account: {}", accountNumber);
        AccountDTO accountDTO = accountService.unfreezeAccount(accountNumber);
        ApiResponse<AccountDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Account unfrozen successfully",
                accountDTO
        );
        return ResponseEntity.ok(response);
    }
}

