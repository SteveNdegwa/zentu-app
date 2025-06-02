package com.zentu.zentu_core.user.service.util;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class UserServiceSync {

    private final String baseUrl = "http://localhost:8080/api/user/sync/";
    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> sync(String action, Map<String, Object> payload) {
        String url = baseUrl + action + "/";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            System.out.println("Sync failed: " + e.getMessage());
            return Map.of("status", "error", "message", e.getMessage());
        }
    }
}
