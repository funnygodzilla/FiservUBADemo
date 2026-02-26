package com.fiserv.uba.audit.service;

import com.fiserv.uba.audit.domain.AuditEvent;
import com.fiserv.uba.audit.dto.AuditEventRequest;
import com.fiserv.uba.audit.repository.AuditEventRepository;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AuditEventService {
    private final AuditEventRepository repository;
    public AuditEventService(AuditEventRepository repository){this.repository=repository;}
    public AuditEvent create(AuditEventRequest request){
        AuditEvent e = new AuditEvent();
        e.setActor(request.actor()); e.setBranchId(request.branchId()); e.setDrawerId(request.drawerId());
        e.setCorrelationId(request.correlationId()); e.setAction(request.action()); e.setBeforeState(request.beforeState()); e.setAfterState(request.afterState());
        return repository.save(e);
    }
    public List<AuditEvent> search(OffsetDateTime from, OffsetDateTime to){return repository.findByCreatedAtBetween(from,to);}    
}
