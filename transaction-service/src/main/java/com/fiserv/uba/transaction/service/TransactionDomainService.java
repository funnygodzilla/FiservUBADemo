package com.fiserv.uba.transaction.service;

import com.fiserv.uba.transaction.client.AuditClient;
import com.fiserv.uba.transaction.client.ComplianceDecisionClient;
import com.fiserv.uba.transaction.domain.CashboxState;
import com.fiserv.uba.transaction.domain.IdempotencyRecord;
import com.fiserv.uba.transaction.domain.TellerTransaction;
import com.fiserv.uba.transaction.dto.*;
import com.fiserv.uba.transaction.exception.TransactionException;
import com.fiserv.uba.transaction.repository.CashboxStateRepository;
import com.fiserv.uba.transaction.repository.IdempotencyRecordRepository;
import com.fiserv.uba.transaction.repository.TellerTransactionRepository;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class TransactionDomainService {

    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("10000.00");
    private static final BigDecimal VARIANCE_TOLERANCE = new BigDecimal("50.00");

    private final TellerTransactionRepository txRepository;
    private final CashboxStateRepository cashboxRepository;
    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final ComplianceDecisionClient complianceClient;
    private final AuditClient auditClient;

    public TransactionDomainService(TellerTransactionRepository txRepository,
                                    CashboxStateRepository cashboxRepository,
                                    IdempotencyRecordRepository idempotencyRecordRepository,
                                    ComplianceDecisionClient complianceClient,
                                    AuditClient auditClient) {
        this.txRepository = txRepository;
        this.cashboxRepository = cashboxRepository;
        this.idempotencyRecordRepository = idempotencyRecordRepository;
        this.complianceClient = complianceClient;
        this.auditClient = auditClient;
    }

    public TransactionResponse cashIn(TransactionRequest request, String correlationId, String idempotencyKey) {
        return withIdempotency(idempotencyKey, "CASH_IN", () -> post(request, "CASH_IN", correlationId, true));
    }

    public TransactionResponse cashOut(TransactionRequest request, String correlationId, String idempotencyKey) {
        return withIdempotency(idempotencyKey, "CASH_OUT", () -> {
            if (complianceClient.isBlockedByOfac(request.initiatedBy(), request.amount())) {
                throw new TransactionException(HttpStatus.UNPROCESSABLE_ENTITY, "OFAC hard stop");
            }
            return post(request, "CASH_OUT", correlationId, false);
        });
    }

    public TransactionResponse transfer(TransactionRequest request, String correlationId, String idempotencyKey) {
        return withIdempotency(idempotencyKey, "TRANSFER", () -> post(request, "TRANSFER", correlationId, false));
    }

    public TransactionResponse openCashbox(CashboxLifecycleRequest request, String correlationId) {
        assertAllowed(request.initiatedBy(), "OPEN_CASHBOX", request.branchId(), request.amount());
        CashboxState state = cashboxRepository.findById(request.drawerId()).orElseGet(CashboxState::new);
        state.setDrawerId(request.drawerId());
        state.setBranchId(request.branchId());
        state.setOpeningFloat(request.amount());
        state.setCurrentBalance(request.amount());
        state.setStatus("OPEN");
        cashboxRepository.save(state);
        auditClient.publish("CASHBOX_OPEN", request.initiatedBy(), request.branchId(), request.drawerId(), correlationId, null, request.amount().toPlainString());
        return new TransactionResponse("CASHBOX-OPEN-" + request.drawerId(), "OPEN", state.getCurrentBalance());
    }

    public TransactionResponse closeCashbox(CashboxLifecycleRequest request, String correlationId) {
        assertAllowed(request.initiatedBy(), "CLOSE_CASHBOX", request.branchId(), request.amount());
        CashboxState state = cashboxRepository.findById(request.drawerId())
                .orElseThrow(() -> new TransactionException(HttpStatus.NOT_FOUND, "Cashbox not found"));
        state.setStatus("CLOSED");
        cashboxRepository.save(state);
        auditClient.publish("CASHBOX_CLOSE", request.initiatedBy(), request.branchId(), request.drawerId(), correlationId, state.getCurrentBalance().toPlainString(), request.amount().toPlainString());
        return new TransactionResponse("CASHBOX-CLOSE-" + request.drawerId(), "CLOSED", state.getCurrentBalance());
    }

    public TransactionResponse adjustment(AdjustmentRequest request, String correlationId) {
        return withIdempotency(request.idempotencyKey(), "ADJUSTMENT", () -> {
            assertAllowed(request.initiatedBy(), "ADJUSTMENT", request.branchId(), request.amount());
            CashboxState state = cashboxRepository.findById(request.drawerId())
                    .orElseThrow(() -> new TransactionException(HttpStatus.NOT_FOUND, "Cashbox not found"));
            BigDecimal before = state.getCurrentBalance();
            boolean increase = "VAULT_IN".equalsIgnoreCase(request.adjustmentType()) || "CORRECTION_CREDIT".equalsIgnoreCase(request.adjustmentType());
            BigDecimal after = increase ? before.add(request.amount()) : before.subtract(request.amount());
            state.setCurrentBalance(after);
            cashboxRepository.save(state);

            TellerTransaction tx = buildTx(new TransactionRequest(request.branchId(), request.drawerId(), request.initiatedBy(), request.amount(), request.reasonCode()),
                    "ADJUSTMENT_" + request.adjustmentType().toUpperCase(Locale.ROOT),
                    requiresDualApproval(request.amount()) ? "PENDING_APPROVAL" : "POSTED");
            txRepository.save(tx);
            auditClient.publish("ADJUSTMENT", request.initiatedBy(), request.branchId(), request.drawerId(), correlationId, before.toPlainString(), after.toPlainString());
            return new TransactionResponse(tx.getTxnRef(), tx.getStatus(), after);
        });
    }

    public TransactionResponse approve(String txnRef, ApprovalRequest request, String correlationId) {
        TellerTransaction tx = txRepository.findById(txnRef)
                .orElseThrow(() -> new TransactionException(HttpStatus.NOT_FOUND, "Transaction not found"));
        if (tx.getInitiatedBy().equalsIgnoreCase(request.approverId())) {
            throw new TransactionException(HttpStatus.UNPROCESSABLE_ENTITY, "Maker-checker violation: approver cannot be initiator");
        }
        if (!"SUPERVISOR".equalsIgnoreCase(request.approverRole()) && requiresDualApproval(tx.getAmount())) {
            throw new TransactionException(HttpStatus.FORBIDDEN, "Supervisor approval required for high-value transaction");
        }
        tx.setStatus("APPROVED_POSTED");
        txRepository.save(tx);
        auditClient.publish("TX_APPROVE", request.approverId(), tx.getBranchId(), tx.getDrawerId(), correlationId, "PENDING_APPROVAL", "APPROVED_POSTED");
        CashboxState state = cashboxRepository.findById(tx.getDrawerId()).orElseThrow();
        return new TransactionResponse(tx.getTxnRef(), tx.getStatus(), state.getCurrentBalance());
    }

    public TransactionResponse reverse(String txnRef, String reasonCode, String correlationId) {
        TellerTransaction tx = txRepository.findById(txnRef)
                .orElseThrow(() -> new TransactionException(HttpStatus.NOT_FOUND, "Transaction not found"));
        tx.setStatus("REVERSED");
        tx.setReasonCode(reasonCode);
        txRepository.save(tx);
        auditClient.publish("TX_REVERSE", tx.getInitiatedBy(), tx.getBranchId(), tx.getDrawerId(), correlationId, "POSTED", "REVERSED");
        CashboxState state = cashboxRepository.findById(tx.getDrawerId()).orElseThrow();
        return new TransactionResponse(tx.getTxnRef(), tx.getStatus(), state.getCurrentBalance());
    }

    public ReconcileResponse reconcile(ReconcileRequest request, String correlationId) {
        CashboxState state = cashboxRepository.findById(request.drawerId())
                .orElseThrow(() -> new TransactionException(HttpStatus.NOT_FOUND, "Cashbox not found"));
        BigDecimal variance = request.countedAmount().subtract(state.getCurrentBalance());
        String action = variance.abs().compareTo(VARIANCE_TOLERANCE) > 0 ? "CASHBOX_RECONCILE_VARIANCE_HIGH" : "CASHBOX_RECONCILE";
        auditClient.publish(action, "SYSTEM", request.branchId(), request.drawerId(), correlationId, state.getCurrentBalance().toPlainString(), request.countedAmount().toPlainString());
        return new ReconcileResponse(request.drawerId(), state.getCurrentBalance(), request.countedAmount(), variance);
    }

    public TransactionResponse approveVariance(VarianceApprovalRequest request, String correlationId) {
        if (request.initiatedBy().equalsIgnoreCase(request.approverId())) {
            throw new TransactionException(HttpStatus.UNPROCESSABLE_ENTITY, "Maker-checker violation for variance approval");
        }
        if (!"SUPERVISOR".equalsIgnoreCase(request.approverRole())) {
            throw new TransactionException(HttpStatus.FORBIDDEN, "Supervisor role required for variance approval");
        }
        CashboxState state = cashboxRepository.findById(request.drawerId())
                .orElseThrow(() -> new TransactionException(HttpStatus.NOT_FOUND, "Cashbox not found"));
        BigDecimal before = state.getCurrentBalance();
        state.setCurrentBalance(request.countedAmount());
        cashboxRepository.save(state);

        TellerTransaction tx = buildTx(new TransactionRequest(request.branchId(), request.drawerId(), request.initiatedBy(), before.subtract(request.countedAmount()).abs(), request.reasonCode()),
                "VARIANCE_ADJUSTMENT",
                "APPROVED_POSTED");
        txRepository.save(tx);
        auditClient.publish("VARIANCE_APPROVED", request.approverId(), request.branchId(), request.drawerId(), correlationId, before.toPlainString(), request.countedAmount().toPlainString());
        return new TransactionResponse(tx.getTxnRef(), tx.getStatus(), state.getCurrentBalance());
    }

    public IdempotencyReplayResponse replay(String idempotencyKey) {
        IdempotencyRecord record = idempotencyRecordRepository.findById(idempotencyKey)
                .orElseThrow(() -> new TransactionException(HttpStatus.NOT_FOUND, "Idempotency key not found"));
        return new IdempotencyReplayResponse(record.getIdempotencyKey(), record.getOperation(), record.getTransactionRef(), record.getStatus(), record.getResponsePayload());
    }

    private TransactionResponse post(TransactionRequest request, String type, String correlationId, boolean increase) {
        assertAllowed(request.initiatedBy(), type, request.branchId(), request.amount());
        CashboxState state = cashboxRepository.findById(request.drawerId())
                .orElseThrow(() -> new TransactionException(HttpStatus.NOT_FOUND, "Cashbox not found"));
        if (complianceClient.isAmlThresholdBreached(request.amount())) {
            TellerTransaction pending = buildTx(request, type, "PENDING_APPROVAL");
            txRepository.save(pending);
            auditClient.publish("TX_AML_PENDING", request.initiatedBy(), request.branchId(), request.drawerId(), correlationId, null, pending.getStatus());
            return new TransactionResponse(pending.getTxnRef(), pending.getStatus(), state.getCurrentBalance());
        }

        BigDecimal before = state.getCurrentBalance();
        BigDecimal after = increase ? before.add(request.amount()) : before.subtract(request.amount());
        state.setCurrentBalance(after);
        cashboxRepository.save(state);

        TellerTransaction tx = buildTx(request, type, requiresDualApproval(request.amount()) ? "PENDING_APPROVAL" : "POSTED");
        txRepository.save(tx);
        auditClient.publish("TX_POSTED_" + type, request.initiatedBy(), request.branchId(), request.drawerId(), correlationId, before.toPlainString(), after.toPlainString());
        return new TransactionResponse(tx.getTxnRef(), tx.getStatus(), state.getCurrentBalance());
    }

    private void assertAllowed(String actorId, String operation, String branchId, BigDecimal amount) {
        Map<String, String> roleMatrix = Map.of(
                "teller01", "TELLER",
                "supervisor01", "SUPERVISOR",
                "manager01", "MANAGER"
        );
        String role = roleMatrix.getOrDefault(actorId.toLowerCase(Locale.ROOT), "TELLER");
        if ("TELLER".equals(role) && amount.compareTo(HIGH_VALUE_THRESHOLD) > 0) {
            throw new TransactionException(HttpStatus.FORBIDDEN,
                    "Entitlement check failed for operation " + operation + " at branch " + branchId);
        }
    }

    private boolean requiresDualApproval(BigDecimal amount) {
        return amount.compareTo(HIGH_VALUE_THRESHOLD) >= 0;
    }

    private TransactionResponse withIdempotency(String idempotencyKey, String operation, TxAction action) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new TransactionException(HttpStatus.BAD_REQUEST, "Idempotency key is required");
        }
        IdempotencyRecord existing = idempotencyRecordRepository.findById(idempotencyKey).orElse(null);
        if (existing != null) {
            BigDecimal balance = parseBalance(existing.getResponsePayload());
            return new TransactionResponse(existing.getTransactionRef(), existing.getStatus(), balance);
        }

        TransactionResponse response = action.execute();
        IdempotencyRecord record = new IdempotencyRecord();
        record.setIdempotencyKey(idempotencyKey);
        record.setOperation(operation);
        record.setTransactionRef(response.txnRef());
        record.setStatus(response.status());
        record.setResponsePayload(response.status() + ":" + response.postBalance());
        idempotencyRecordRepository.save(record);
        return response;
    }

    private BigDecimal parseBalance(String payload) {
        if (payload == null || !payload.contains(":")) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(payload.substring(payload.indexOf(':') + 1));
    }

    private TellerTransaction buildTx(TransactionRequest request, String type, String status) {
        TellerTransaction tx = new TellerTransaction();
        tx.setTxnRef("TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        tx.setTransactionType(type);
        tx.setBranchId(request.branchId());
        tx.setDrawerId(request.drawerId());
        tx.setInitiatedBy(request.initiatedBy());
        tx.setAmount(request.amount());
        tx.setStatus(status);
        tx.setReasonCode(request.reasonCode());
        return tx;
    }

    @FunctionalInterface
    private interface TxAction {
        TransactionResponse execute();
    }
}
