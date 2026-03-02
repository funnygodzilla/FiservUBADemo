package com.fiserv.uba.audit.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "audit_events")
public class AuditEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String actor;
    private String branchId;
    private String drawerId;
    private String correlationId;
    private String action;
    @Column(length = 4096)
    private String beforeState;
    @Column(length = 4096)
    private String afterState;
    private OffsetDateTime createdAt;
    @PrePersist void pre(){createdAt = OffsetDateTime.now();}
    public Long getId(){return id;} public String getActor(){return actor;} public void setActor(String a){actor=a;}
    public String getBranchId(){return branchId;} public void setBranchId(String b){branchId=b;}
    public String getDrawerId(){return drawerId;} public void setDrawerId(String d){drawerId=d;}
    public String getCorrelationId(){return correlationId;} public void setCorrelationId(String c){correlationId=c;}
    public String getAction(){return action;} public void setAction(String a){action=a;}
    public String getBeforeState(){return beforeState;} public void setBeforeState(String b){beforeState=b;}
    public String getAfterState(){return afterState;} public void setAfterState(String a){afterState=a;}
    public OffsetDateTime getCreatedAt(){return createdAt;}
}
