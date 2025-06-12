package com.zentu.zentu_core.user.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@Slf4j
public class UserServiceSync {
    @Value("${identity.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public UserServiceSync(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    public Map<String, Object> sync(String action, Map<String, Object> payload) {
        String url = baseUrl + action + "/";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String jsonPayload = objectMapper.writeValueAsString(payload);
            HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);
            log.info("Syncing with URL: {}, Payload: {}", url, jsonPayload);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            log.info("Sync response: {}", response.getBody());
            return response.getBody();
        } catch (Exception e) {
            log.error("Sync failed: {}", e.getMessage());
            try {
                String errorMessage = e.getMessage();
                String jsonPart = errorMessage.substring(errorMessage.indexOf("{"));
                Map<String, Object> errorMap = objectMapper.readValue(jsonPart, Map.class);
                String msg = String.valueOf(errorMap.getOrDefault("message", "Unexpected error occurred."));
                return Map.of("code", "500.000", "message", msg);
            } catch (Exception ex) {
                log.warn("Error parsing JSON from exception: {}", ex.getMessage());
                return Map.of("code", "500.000", "message", "Unexpected error occurred.");
            }
        }
    }
}
