package com.fiserv.uba.audit.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "compliance_alert")
public class ComplianceAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String eventType;
    private String branchId;
    private String drawerId;
    private String actor;
    private String severity;
    private String status;
    private String assignedTo;
    private OffsetDateTime slaDeadline;
    private String disposition;
    private String ofacDecision;
    private String overrideRationale;
    private String legalHoldState;
    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() {
        createdAt = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }
    public String getDrawerId() { return drawerId; }
    public void setDrawerId(String drawerId) { this.drawerId = drawerId; }
    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public OffsetDateTime getSlaDeadline() { return slaDeadline; }
    public void setSlaDeadline(OffsetDateTime slaDeadline) { this.slaDeadline = slaDeadline; }
    public String getDisposition() { return disposition; }
    public void setDisposition(String disposition) { this.disposition = disposition; }
    public String getOfacDecision() { return ofacDecision; }
    public void setOfacDecision(String ofacDecision) { this.ofacDecision = ofacDecision; }
    public String getOverrideRationale() { return overrideRationale; }
    public void setOverrideRationale(String overrideRationale) { this.overrideRationale = overrideRationale; }
    public String getLegalHoldState() { return legalHoldState; }
    public void setLegalHoldState(String legalHoldState) { this.legalHoldState = legalHoldState; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
