package com.zentu.zentu_core.community.client;

import com.zentu.zentu_core.base.dto.JsonResponse;
import com.zentu.zentu_core.base.config.IdentityServiceFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(
        name = "communityServiceClient",
        url = "${identity.api-url}",
        configuration = IdentityServiceFeignConfig.class,
        path = "/communities"
)
public interface CommunityServiceClient {

    @PostMapping("/create/")
    JsonResponse createCommunity(@RequestBody Map<String, Object> data);

    @PostMapping("/update/")
    JsonResponse updateCommunity(@RequestBody Map<String, Object> data);

    @PostMapping("/delete/")
    JsonResponse deleteCommunity(@RequestBody Map<String, Object> data);

    @PostMapping("/get/")
    JsonResponse getCommunity(@RequestBody Map<String, Object> data);

    @PostMapping("/filter/")
    JsonResponse filterCommunities(@RequestBody Map<String, Object> data);

    @PostMapping("/join/")
    JsonResponse joinCommunity(@RequestBody Map<String, Object> data);

    @PostMapping("/exit/")
    JsonResponse exitCommunity(@RequestBody Map<String, Object> data);

    @PostMapping("/admins/add/")
    JsonResponse addUserToCommunityAdmins(@RequestBody Map<String, Object> data);

    @PostMapping("/admins/remove/")
    JsonResponse removeUserFromCommunityAdmins(@RequestBody Map<String, Object> data);

    @PostMapping("/roles/update/")
    JsonResponse updateCommunityRole(@RequestBody Map<String, Object> data);

    @PostMapping("/members/get/")
    JsonResponse getCommunityMembers(@RequestBody Map<String, Object> data);
}
