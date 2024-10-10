package com.nerdysoft.feign;

import com.nerdysoft.dto.feign.UserActivityEvent;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(contextId = "auditFeignClient", value = "api-gateway")
public interface AuditFeignClient {
    @GetMapping("/audit-service/audit/user-activity/{userId}")
    ResponseEntity<List<UserActivityEvent>> getUserActivityLogsByUserId(@PathVariable UUID userId);
}
