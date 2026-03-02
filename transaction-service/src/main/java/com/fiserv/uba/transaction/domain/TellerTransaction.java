package com.fiserv.uba.transaction.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "teller_transactions")
public class TellerTransaction {
    @Id
    private String txnRef;
    private String transactionType;
    private String branchId;
    private String drawerId;
    private String initiatedBy;
    private BigDecimal amount;
    private String status;
    private String reasonCode;
    private OffsetDateTime createdAt;
    @PrePersist void pre(){createdAt=OffsetDateTime.now();}
    public String getTxnRef(){return txnRef;} public void setTxnRef(String t){txnRef=t;}
    public String getTransactionType(){return transactionType;} public void setTransactionType(String t){transactionType=t;}
    public String getBranchId(){return branchId;} public void setBranchId(String b){branchId=b;}
    public String getDrawerId(){return drawerId;} public void setDrawerId(String d){drawerId=d;}
    public String getInitiatedBy(){return initiatedBy;} public void setInitiatedBy(String i){initiatedBy=i;}
    public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal a){amount=a;}
    public String getStatus(){return status;} public void setStatus(String s){status=s;}
    public String getReasonCode(){return reasonCode;} public void setReasonCode(String r){reasonCode=r;}
    public OffsetDateTime getCreatedAt(){return createdAt;}
}
