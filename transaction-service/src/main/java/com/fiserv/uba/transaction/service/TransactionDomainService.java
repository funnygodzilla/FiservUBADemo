package com.fiserv.uba.transaction.service;

import com.fiserv.uba.transaction.client.AuditClient;
import com.fiserv.uba.transaction.client.ComplianceDecisionClient;
import com.fiserv.uba.transaction.domain.CashboxState;
import com.fiserv.uba.transaction.domain.TellerTransaction;
import com.fiserv.uba.transaction.dto.*;
import com.fiserv.uba.transaction.exception.TransactionException;
import com.fiserv.uba.transaction.repository.CashboxStateRepository;
import com.fiserv.uba.transaction.repository.TellerTransactionRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class TransactionDomainService {

    private final TellerTransactionRepository txRepository;
    private final CashboxStateRepository cashboxRepository;
    private final ComplianceDecisionClient complianceClient;
    private final AuditClient auditClient;

    public TransactionDomainService(TellerTransactionRepository txRepository,
                                    CashboxStateRepository cashboxRepository,
                                    ComplianceDecisionClient complianceClient,
                                    AuditClient auditClient) {
        this.txRepository = txRepository;
        this.cashboxRepository = cashboxRepository;
        this.complianceClient = complianceClient;
        this.auditClient = auditClient;
    }

    public TransactionResponse cashIn(TransactionRequest request, String correlationId) {
        return post(request, "CASH_IN", correlationId, true);
    }

    public TransactionResponse cashOut(TransactionRequest request, String correlationId) {
        if (complianceClient.isBlockedByOfac(request.initiatedBy(), request.amount())) {
            throw new TransactionException(HttpStatus.UNPROCESSABLE_ENTITY, "OFAC hard stop");
        }
        return post(request, "CASH_OUT", correlationId, false);
    }

    public TransactionResponse transfer(TransactionRequest request, String correlationId) {
        return post(request, "TRANSFER", correlationId, false);
    }

    public TransactionResponse approve(String txnRef, ApprovalRequest request, String correlationId) {
        TellerTransaction tx = txRepository.findById(txnRef)
                .orElseThrow(() -> new TransactionException(HttpStatus.NOT_FOUND, "Transaction not found"));
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
        auditClient.publish("CASHBOX_RECONCILE", "SYSTEM", request.branchId(), request.drawerId(), correlationId, state.getCurrentBalance().toPlainString(), request.countedAmount().toPlainString());
        return new ReconcileResponse(request.drawerId(), state.getCurrentBalance(), request.countedAmount(), variance);
    }

    private TransactionResponse post(TransactionRequest request, String type, String correlationId, boolean increase) {
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

        TellerTransaction tx = buildTx(request, type, "POSTED");
        txRepository.save(tx);
        auditClient.publish("TX_POSTED_" + type, request.initiatedBy(), request.branchId(), request.drawerId(), correlationId, before.toPlainString(), after.toPlainString());
        return new TransactionResponse(tx.getTxnRef(), tx.getStatus(), state.getCurrentBalance());
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
}
