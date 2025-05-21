package com.zentu.zentu_core.billing.service.pesaway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class PesaWayApiClient {

    private final String clientId;
    private final String clientSecret;
    private final String baseUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public PesaWayApiClient(
            @Value("${pesaway.client-id}") String clientId,
            @Value("${pesaway.client-secret}") String clientSecret,
            @Value("${pesaway.base-url}") String baseUrl
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    private String authenticate() {
        String url = baseUrl + "/api/v1/token/";
        Map<String, String> payload = new HashMap<>();
        payload.put("consumer_key", clientId);
        payload.put("consumer_secret", clientSecret);
        payload.put("grant_type", "client_credentials");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("data").get("token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse access token", e);
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String accessToken = authenticate();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private JsonNode post(String endpoint, Map<String, Object> payload) {
        String url = baseUrl + endpoint;
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, getHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        try {
            return objectMapper.readTree(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("POST request failed", e);
        }
    }

    private JsonNode get(String endpoint) {
        String url = baseUrl + endpoint;
        HttpEntity<Void> request = new HttpEntity<>(getHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        try {
            return objectMapper.readTree(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("GET request failed", e);
        }
    }

    public JsonNode getAccountBalance() {
        return get("/api/v1/account-balance/");
    }

    public JsonNode sendMobileMoney(double amount, String currency, String recipientNumber, String reference) {
        Map<String, Object> payload = Map.of(
                "amount", amount,
                "currency", currency,
                "recipient_number", recipientNumber,
                "reference", reference
        );
        return post("/api/v1/mobile-money/send-payment/", payload);
    }

    public JsonNode sendB2BPayment(String externalReference, double amount, String accountNumber, String channel, String reason, String resultsUrl) {
        Map<String, Object> payload = Map.of(
                "ExternalReference", externalReference,
                "Amount", amount,
                "AccountNumber", accountNumber,
                "Channel", channel,
                "Reason", reason,
                "ResultsUrl", resultsUrl
        );
        return post("/api/v1/mobile-money/send-payment/", payload);
    }

    public JsonNode sendB2CPayment(String externalReference, double amount, String phoneNumber, String channel, String reason, String resultsUrl) {
        Map<String, Object> payload = Map.of(
                "ExternalReference", externalReference,
                "Amount", amount,
                "PhoneNumber", phoneNumber,
                "Channel", channel,
                "Reason", reason,
                "ResultsUrl", resultsUrl
        );
        return post("/api/v1/mobile-money/send-payment/", payload);
    }

    public JsonNode receiveC2BPayment(String externalReference, double amount, String phoneNumber, String channel, String reason, String resultsUrl) {
        Map<String, Object> payload = Map.of(
                "ExternalReference", externalReference,
                "Amount", amount,
                "PhoneNumber", phoneNumber,
                "Channel", channel,
                "Reason", reason,
                "ResultsUrl", resultsUrl
        );
        return post("/api/v1/mobile-money/receive-payment/", payload);
    }

    public JsonNode authorizeTransaction(String transactionId, String otp) {
        Map<String, Object> payload = Map.of(
                "TransactionID", transactionId,
                "OTP", otp
        );
        return post("/api/v1/mobile-money/authorize-transaction/", payload);
    }

    public JsonNode sendBankPayment(String externalReference, double amount, String accountNumber, String channel, String bankCode, String currency, String reason, String resultsUrl) {
        Map<String, Object> payload = Map.of(
                "ExternalReference", externalReference,
                "Amount", amount,
                "AccountNumber", accountNumber,
                "Channel", channel,
                "BankCode", bankCode,
                "Currency", currency,
                "Reason", reason,
                "ResultsUrl", resultsUrl
        );
        return post("/api/v1/bank/send-payment/", payload);
    }

    public JsonNode queryBankTransaction(String transactionReference) {
        Map<String, Object> payload = Map.of(
                "TransactionReference", transactionReference
        );
        return post("/api/v1/bank/transaction-query/", payload);
    }

    public JsonNode queryMobileMoneyTransaction(String transactionReference) {
        Map<String, Object> payload = Map.of(
                "TransactionReference", transactionReference
        );
        return post("/api/v1/mobile-money/transaction-query/", payload);
    }

    public JsonNode pullMobileMoneyTransactions(LocalDateTime startDate, LocalDateTime endDate, String transType, int offsetValue) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> payload = Map.of(
                "StartDate", formatter.format(startDate),
                "EndDate", formatter.format(endDate),
                "TransType", transType,
                "OffsetValue", offsetValue
        );
        return post("/api/v1/mobile-money/pull-transactions/", payload);
    }

    public JsonNode sendAirtime(String externalReference, double amount, String phoneNumber, String reason, String resultsUrl) {
        Map<String, Object> payload = Map.of(
                "ExternalReference", externalReference,
                "Amount", amount,
                "PhoneNumber", phoneNumber,
                "Reason", reason,
                "ResultsUrl", resultsUrl
        );
        return post("/api/v1/airtime/send-airtime/", payload);
    }
}
