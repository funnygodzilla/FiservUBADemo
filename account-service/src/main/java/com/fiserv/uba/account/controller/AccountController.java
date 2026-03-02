package com.fiserv.uba.account.controller;

import com.fiserv.uba.account.dto.AccountResponseDTO;
import com.fiserv.uba.account.dto.ApiResponse;
import com.fiserv.uba.account.dto.CreateAccountRequest;
import com.fiserv.uba.account.dto.TransactionRequest;
import com.fiserv.uba.account.service.AccountService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponseDTO>> createAccount(@RequestBody CreateAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), "Account created successfully", accountService.createAccount(request)));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<ApiResponse<AccountResponseDTO>> getAccount(@PathVariable String accountId) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Account retrieved successfully", accountService.getAccount(accountId)));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<AccountResponseDTO>>> getCustomerAccounts(@PathVariable String customerId) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Customer accounts retrieved successfully", accountService.getCustomerAccounts(customerId)));
    }

    @PostMapping("/debit")
    public ResponseEntity<ApiResponse<AccountResponseDTO>> debitAccount(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Amount debited successfully", accountService.debitAccount(request)));
    }

    @PostMapping("/credit")
    public ResponseEntity<ApiResponse<AccountResponseDTO>> creditAccount(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Amount credited successfully", accountService.creditAccount(request)));
    }

    @PutMapping("/{accountId}/freeze")
    public ResponseEntity<ApiResponse<AccountResponseDTO>> freezeAccount(@PathVariable String accountId) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Account frozen successfully", accountService.freezeAccount(accountId)));
    }

    @PutMapping("/{accountId}/unfreeze")
    public ResponseEntity<ApiResponse<AccountResponseDTO>> unfreezeAccount(@PathVariable String accountId) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Account unfrozen successfully", accountService.unfreezeAccount(accountId)));
    }
}
