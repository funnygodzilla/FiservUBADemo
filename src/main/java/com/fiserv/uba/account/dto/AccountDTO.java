package com.fiserv.uba.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO {
    private Long id;
    private String accountNumber;
    private Long customerId;
    private String accountType;
    private BigDecimal balance;
    private String currency;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

