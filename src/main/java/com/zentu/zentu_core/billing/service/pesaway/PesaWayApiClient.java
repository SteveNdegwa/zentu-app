package com.zentu.zentu_core.billing.service.pesaway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.billing.entity.Account;
import com.zentu.zentu_core.billing.entity.PesawayTransactionLog;
import com.zentu.zentu_core.billing.entity.Transaction;
import com.zentu.zentu_core.billing.enums.AccountType;
import com.zentu.zentu_core.billing.enums.EntryCategory;
import com.zentu.zentu_core.billing.repository.AccountRepository;
import com.zentu.zentu_core.billing.repository.PesawayTransactionLogRepository;
import com.zentu.zentu_core.billing.repository.TransactionRepository;
import com.zentu.zentu_core.billing.service.account.AccountService;
import com.zentu.zentu_core.common.db.GenericCrudService;
import com.zentu.zentu_core.common.utils.ChargeCalculator;
import com.zentu.zentu_core.common.utils.TransactionRefGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class PesaWayApiClient {

    private final TransactionRepository transactionRepository;


    @Autowired
    private GenericCrudService genericCrudService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;


    @Autowired
    private PesawayTransactionLogRepository pesawayTransactionLogService;


    private final String clientId;
    private final String clientSecret;
    private final String baseUrl;
    private final String callbackUrl;
    private final String b2cResultsUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public PesaWayApiClient(
            TransactionRepository transactionRepository, @Value("${pesaway.client-id}") String clientId,
            @Value("${pesaway.client-secret}") String clientSecret,
            @Value("${pesaway.base-url}") String baseUrl
    ) {
        this.transactionRepository = transactionRepository;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.baseUrl = baseUrl;
        this.callbackUrl = "https://zentu.rentwaveafrica.co.ke/pesaway/c2b/callback";
        this.b2cResultsUrl = "https://zentu.rentwaveafrica.co.ke/pesaway/b2c/callback";
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

    public JsonNode sendB2CPayment(String externalReference, double amount, String phoneNumber,
                                   String channel, String alias, String reason, AccountType accountType) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        Map<String, Object> payload = Map.of(
                "ExternalReference", externalReference,
                "Amount", amount,
                "PhoneNumber", phoneNumber,
                "Channel", channel,
                "Reason", reason,
                "ResultsUrl", this.b2cResultsUrl
        );
        log.info("Transaction payload: {}", payload);
        Account account = accountRepository.findByAlias(alias)
                .orElseThrow(() -> new RuntimeException("Account not found for alias: " + alias));
        BigDecimal amountDecimal = BigDecimal.valueOf(amount);
        BigDecimal available = account.getAvailable();
        if (available.compareTo(amountDecimal) < 0) {
            throw new RuntimeException("Insufficient funds in account: " + alias);
        }
        JsonNode response = post("/api/v1/mobile-money/send-payment/", payload);
        var withdrawalResult = accountService.withdraw(
                externalReference,
                alias,
                amountDecimal,
                accountType,
                State.PROCESSING
        );
        log.info("Withdrawal result: {}", withdrawalResult);
        PesawayTransactionLog transactionLog = PesawayTransactionLog.builder()
                .originatorReference(externalReference)
                .alias(alias)
                .accountType(accountType)
                .phoneNumber(phoneNumber)
                .transactionType(EntryCategory.DEBIT)
                .state(State.COMPLETED)
                .build();
        genericCrudService.create(transactionLog);
        return response;
    }


    public JsonNode receiveC2BPayment(String externalReference, double amount, String phoneNumber, String channel, String alias,  String reason, AccountType accountType) {
        BigDecimal amountDecimal = BigDecimal.valueOf(amount);
//        BigDecimal charge = ChargeCalculator.calculateCharge(amountDecimal);
//        amount = amountDecimal.add(charge).doubleValue();
        Map<String, Object> payload = Map.of(
                "ExternalReference", externalReference,
                "Amount", amount,
                "PhoneNumber", phoneNumber,
                "Channel", channel,
                "Reason", reason,
                "ResultsUrl", this.callbackUrl
        );
        var processTopUp = accountService.topUp(
                externalReference,
                alias,
                amountDecimal,
                accountType,
                State.PROCESSING
        );
        log.info("Process Topup Logger : {}", processTopUp);
        PesawayTransactionLog transaction = PesawayTransactionLog.builder()
                .alias(alias)
                .originatorReference(externalReference)
                .accountType(accountType)
                .transactionType(EntryCategory.CREDIT)
                .phoneNumber(phoneNumber)
                .state(State.COMPLETED)
                .build();
        genericCrudService.create(transaction);
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

    public Map<String, Object> processPesawayB2Cresults(Map<String, Object> data) {
        log.info("Received Pesaway B2C Callback: {}", data);
        Integer resultCode = (Integer) data.get("ResultCode");
        BigDecimal amount = new BigDecimal((String) data.get("TransactionAmount"));
        log.info("Result Code: {}", resultCode);
        log.info("Big Decimal amount: {}", amount);
        String resultDesc = (String) data.getOrDefault("ResultDesc", "No description");
        String originatorReference = (String) data.get("OriginatorReference");
        String transactionId = (String) data.get("TransactionID");
        if (originatorReference == null || resultCode == null) {
            return response("200.200.001", "Missing callback data");
        }
        Optional<Transaction> optionalTransaction = this.transactionRepository.findByInternalReference(originatorReference);
        if (optionalTransaction.isEmpty()) {
            log.warn("Transaction not found for originatorReference: {}", originatorReference);
            return response("200.001", "Transaction not found");
        }
        try {
            String jsonResponse = objectMapper.writeValueAsString(data);
            log.info("Callback JSON: {}", jsonResponse);
        } catch (Exception e) {
            log.error("Failed to convert response to JSON: {}", e.getMessage());
        }
        if (resultCode == 0) {
            log.info("amount Amount: {}", amount);
            Transaction transaction = optionalTransaction.get();
            transaction.setReceiptNumber(transactionId);
            transaction.setStatus(State.COMPLETED);
            transaction.save();
            var approveWithdrawal = accountService.approveAccountWithdraw(
                    transactionId,
                    transaction,
                    transaction.getAlias(),
                    amount,
                    transaction.getAccountType(),
                    State.COMPLETED
            );
            log.info("Process Topup Logger : {}", approveWithdrawal);
//            } else {
//                Transaction transaction = optionalTransaction.get();
//                transaction.setStatus(State.FAILED);
//                transactionRepository.save(transaction);
//            }
            return response("200.200.000", "Transaction is being processed");
        } else {
            return response("200.200.001", resultDesc);
        }
    }



    public Map<String, Object> processPesawayCallback(Map<String, Object> data) {
        log.info("Received Pesaway Callback: {}", data);
        Integer resultCode = (Integer) data.get("ResultCode");
        String resultDesc = (String) data.getOrDefault("ResultDesc", "No description");
        String originatorReference = (String) data.get("OriginatorReference");
        String transactionReceipt = (String) data.get("TransactionReceipt");
        BigDecimal amount = new BigDecimal(data.get("TransactionAmount").toString());
        String phone = (String) data.get("ReceiverPartyPublicName");
        if (originatorReference == null || resultCode == null) {
            return response("200.001", "Missing callback data");
        }
        try {
            String jsonResponse = objectMapper.writeValueAsString(data);
            log.info("Callback JSON: {}", jsonResponse);
        } catch (Exception e) {
            log.error("Failed to convert response to JSON: {}", e.getMessage());
        }
        Optional<Transaction> optionalTransaction = this.transactionRepository.findByInternalReference(originatorReference);
        if (optionalTransaction.isEmpty()) {
            log.warn("Transaction not found for originatorReference: {}", originatorReference);
            return response("200.001", "Transaction not found");
        }
        if (resultCode == 0) {
            if (phone == null || amount == null) {
                log.warn("Missing phone or amount: phone={}, amount={}", phone, amount);
                return response("200.200.002", "Missing phone or amount");
            }
            String code = (String) data.get("code");
            if ("The service request is processed successfully.".equals(resultDesc)) {
                Transaction transaction = optionalTransaction.get();
                transaction.setReceiptNumber(transactionReceipt);
                transaction.setStatus(State.COMPLETED);
                transaction.save();
                var approveTopUp = accountService.approveAccountTopUp(
                        transactionReceipt,
                        transaction,
                        transaction.getAlias(),
                        amount,
                        transaction.getAccountType(),
                        State.COMPLETED
                );
                log.info("Process Topup Logger : {}", approveTopUp);
                transactionRepository.save(transaction);
            }  else {
                Transaction transaction = optionalTransaction.get();
                transaction.setStatus(State.FAILED);
                transactionRepository.save(transaction);
            }
            return response("200.001", "Transaction Successful");
        } else {
            Transaction transaction = optionalTransaction.get();
            transaction.setStatus(State.FAILED);
            transactionRepository.save(transaction);
            return response("200.001", resultDesc);
        }
    }


    private Map<String, Object> safeCastToMap(Object obj) {
        if (obj instanceof Map<?, ?> map) {
            Map<String, Object> result = new HashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() instanceof String) {
                    result.put((String) entry.getKey(), entry.getValue());
                }
            }
            return result;
        }
        return null;
    }

    private Map<String, Object> response(String code, Object data) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", code);
        resp.put("data", data);
        return resp;
    }

}
