package com.zentu.zentu_core.group.client;

import com.zentu.zentu_core.base.dto.JsonResponse;
import com.zentu.zentu_core.base.config.IdentityServiceFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(
        name = "groupServiceClient",
        url = "${identity.api-url}",
        configuration = IdentityServiceFeignConfig.class
)
@RequestMapping("/groups")
public interface GroupServiceClient {
    @PostMapping("/create/")
    JsonResponse createGroup(@RequestBody Map<String, Object> data);

    @PostMapping("/update/")
    JsonResponse updateGroup(@RequestBody Map<String, Object> data);

    @PostMapping("/delete/")
    JsonResponse deleteGroup(@RequestBody Map<String, Object> data);

    @PostMapping("/get/")
    JsonResponse getGroup(@RequestBody Map<String, Object> data);

    @PostMapping("/filter/")
    JsonResponse filterGroups(@RequestBody Map<String, Object> data);

    @PostMapping("/join/")
    JsonResponse joinGroup(@RequestBody Map<String, Object> data);

    @PostMapping("/exit/")
    JsonResponse exitGroup(@RequestBody Map<String, Object> data);

    @PostMapping("/admins/add/")
    JsonResponse addUserToGroupAdmins(@RequestBody Map<String, Object> data);

    @PostMapping("/admins/remove/")
    JsonResponse removeUserFromGroupAdmins(@RequestBody Map<String, Object> data);

    @PostMapping("/roles/update/")
    JsonResponse updateGroupRole(@RequestBody Map<String, Object> data);

    @PostMapping("/members/get/")
    JsonResponse getGroupMembers(@RequestBody Map<String, Object> data);
}
