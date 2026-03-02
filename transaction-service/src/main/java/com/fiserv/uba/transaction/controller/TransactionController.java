package com.fiserv.uba.transaction.controller;

import com.fiserv.uba.transaction.dto.*;
import com.fiserv.uba.transaction.service.TransactionDomainService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class TransactionController {

    private final TransactionDomainService service;

    public TransactionController(TransactionDomainService service) {
        this.service = service;
    }

    @PostMapping("/transactions/cash-in")
    public TransactionResponse cashIn(@RequestBody TransactionRequest request,
                                      @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
                                      @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return service.cashIn(request, correlationId == null ? idempotencyKey : correlationId, idempotencyKey);
    }

    @PostMapping("/transactions/cash-out")
    public TransactionResponse cashOut(@RequestBody TransactionRequest request,
                                       @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
                                       @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return service.cashOut(request, correlationId == null ? idempotencyKey : correlationId, idempotencyKey);
    }

    @PostMapping("/transactions/transfer")
    public TransactionResponse transfer(@RequestBody TransactionRequest request,
                                        @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
                                        @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return service.transfer(request, correlationId == null ? idempotencyKey : correlationId, idempotencyKey);
    }

    @PostMapping("/transactions/{txnRef}/approve")
    public TransactionResponse approve(@PathVariable String txnRef,
                                       @RequestBody ApprovalRequest request,
                                       @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        return service.approve(txnRef, request, correlationId);
    }

    @PostMapping("/transactions/{txnRef}/reverse")
    public TransactionResponse reverse(@PathVariable String txnRef,
                                       @RequestParam String reasonCode,
                                       @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        return service.reverse(txnRef, reasonCode, correlationId);
    }

    @PostMapping("/cashbox/open")
    public TransactionResponse openCashbox(@RequestBody CashboxLifecycleRequest request,
                                           @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        return service.openCashbox(request, correlationId == null ? request.idempotencyKey() : correlationId);
    }

    @PostMapping("/cashbox/close")
    public TransactionResponse closeCashbox(@RequestBody CashboxLifecycleRequest request,
                                            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        return service.closeCashbox(request, correlationId == null ? request.idempotencyKey() : correlationId);
    }

    @PostMapping("/cashbox/adjustments")
    public TransactionResponse adjustment(@RequestBody AdjustmentRequest request,
                                          @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        return service.adjustment(request, correlationId == null ? request.idempotencyKey() : correlationId);
    }

    @PostMapping("/cashbox/reconcile")
    public ReconcileResponse reconcile(@RequestBody ReconcileRequest request,
                                       @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        return service.reconcile(request, correlationId);
    }

    @PostMapping("/cashbox/variance/approve")
    public TransactionResponse approveVariance(@RequestBody VarianceApprovalRequest request,
                                               @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        return service.approveVariance(request, correlationId);
    }

    @GetMapping("/idempotency/{idempotencyKey}")
    public IdempotencyReplayResponse replay(@PathVariable String idempotencyKey) {
        return service.replay(idempotencyKey);
    }
}
