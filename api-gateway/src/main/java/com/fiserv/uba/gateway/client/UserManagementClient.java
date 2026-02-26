package com.fiserv.uba.gateway.client;

import com.fiserv.uba.gateway.dto.DrawerDTO;
import com.fiserv.uba.gateway.dto.UpdatedUserContextDTO;
import java.util.List;
import reactor.core.publisher.Mono;

public interface UserManagementClient {
    Mono<List<DrawerDTO>> getDrawers(String sub, String correlationId);
    Mono<UpdatedUserContextDTO> selectDrawer(String sub, String drawerId, String correlationId);
}
