package com.zentu.zentu_core.contribution.client;

import com.zentu.zentu_core.base.config.WassengerApiFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@FeignClient(
        name = "wassengerApiClient",
        url = "${wassenger.api-url}",
        path = "/",
        configuration = WassengerApiFeignConfig.class
)
public interface WassengerApiClient {

    @PostMapping("/numbers/exists")
    void checkNumberExists(@RequestBody Map<String, String> payload);

    @PostMapping("/devices/${wassenger.device-id}/groups")
    Map<String, Object> createGroup(@RequestBody Map<String, Object> payload);

    @GetMapping("/devices/${wassenger.device-id}/groups/{groupId}")
    Map<String, Object> getGroup(@PathVariable("groupId") String groupId);

    @PostMapping("/devices/${wassenger.device-id}/groups/{groupId}/participants")
    void addParticipantsToGroup(
            @PathVariable("groupId") String groupId,
            @RequestBody Map<String, Object> payload
    );

    @DeleteMapping("/devices/${wassenger.device-id}/groups/{groupId}/participants")
    void removeParticipantsFromGroup(
            @PathVariable("groupId") String groupId,
            @RequestBody Map<String, Object> payload
    );

    @PostMapping("/messages")
    void sendMessage(@RequestBody Map<String, Object> payload);

}
