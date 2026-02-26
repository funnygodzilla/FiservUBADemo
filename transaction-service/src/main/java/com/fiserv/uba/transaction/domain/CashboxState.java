package com.fiserv.uba.transaction.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cashbox_state")
public class CashboxState {
    @Id
    private String drawerId;
    private String branchId;
    private BigDecimal openingFloat;
    private BigDecimal currentBalance;
    private String status;
    public String getDrawerId(){return drawerId;} public void setDrawerId(String d){drawerId=d;}
    public String getBranchId(){return branchId;} public void setBranchId(String b){branchId=b;}
    public BigDecimal getOpeningFloat(){return openingFloat;} public void setOpeningFloat(BigDecimal o){openingFloat=o;}
    public BigDecimal getCurrentBalance(){return currentBalance;} public void setCurrentBalance(BigDecimal c){currentBalance=c;}
    public String getStatus(){return status;} public void setStatus(String s){status=s;}
}
