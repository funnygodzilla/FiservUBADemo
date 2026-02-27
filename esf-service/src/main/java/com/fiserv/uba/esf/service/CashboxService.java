package com.fiserv.uba.esf.service;

import com.fiserv.uba.esf.client.IntegratedTellerClient;
import com.fiserv.uba.esf.dto.CashBoxDTO;
import org.springframework.stereotype.Service;

@Service
public class CashboxService {
    private final IntegratedTellerClient integratedTellerClient;
    public CashboxService(IntegratedTellerClient integratedTellerClient){this.integratedTellerClient=integratedTellerClient;}
    public CashBoxDTO getCashbox(String auth){return integratedTellerClient.getCashBoxDetails(auth);} 
}
