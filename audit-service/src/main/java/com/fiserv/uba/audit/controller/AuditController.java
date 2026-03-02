package com.fiserv.uba.audit.controller;

import com.fiserv.uba.audit.domain.AuditEvent;
import com.fiserv.uba.audit.dto.AuditEventRequest;
import com.fiserv.uba.audit.service.AuditEventService;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audit-events")
public class AuditController {
    private final AuditEventService service;
    public AuditController(AuditEventService service){this.service=service;}
    @PostMapping
    public AuditEvent create(@RequestBody AuditEventRequest request){return service.create(request);}    
    @GetMapping
    public List<AuditEvent> search(@RequestParam OffsetDateTime from, @RequestParam OffsetDateTime to){return service.search(from,to);}    
}
