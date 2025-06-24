package com.zentu.zentu_core.billing.controller.pesaway;

import com.fasterxml.jackson.databind.JsonNode;
import com.zentu.zentu_core.billing.dto.PesawayTopupRequest;
import com.zentu.zentu_core.billing.enums.AccountType;
import com.zentu.zentu_core.billing.service.pesaway.PesaWayApiClient;
import com.zentu.zentu_core.common.utils.TransactionRefGenerator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pesaway")
public class PesaWayController {

    private final PesaWayApiClient pesaWayApiClient;

    public PesaWayController(PesaWayApiClient pesaWayApiClient) {
        this.pesaWayApiClient = pesaWayApiClient;
    }

    @GetMapping("/balance")
    public ResponseEntity<JsonNode> getBalance() {
        return ResponseEntity.ok(pesaWayApiClient.getAccountBalance());
    }

    @PostMapping("/community-topup")
    public ResponseEntity<JsonNode> topupCommunityAccount(@Valid @RequestBody PesawayTopupRequest request) {
        double amount = request.getAmount();
        String receipt = new TransactionRefGenerator().generate();
        String phoneNumber = request.getPhoneNumber();
        String channel = request.getChannel();
        String reason = request.getReason();
        String alias = request.getAlias();
        AccountType accountType = AccountType.COMMUNITY;
        return ResponseEntity.ok(pesaWayApiClient.receiveC2BPayment(receipt, amount, phoneNumber, channel, alias, reason, accountType));
    }

    @PostMapping("/user-topup")
    public ResponseEntity<JsonNode> topupUserAccount(@Valid @RequestBody PesawayTopupRequest request) {
        double amount = request.getAmount();
        String receipt = new TransactionRefGenerator().generate();
        String phoneNumber = request.getPhoneNumber();
        String channel = request.getChannel();
        String reason = request.getReason();
        String alias = request.getAlias();
        AccountType accountType = AccountType.USER;
        return ResponseEntity.ok(pesaWayApiClient.receiveC2BPayment(receipt, amount, phoneNumber, channel, alias, reason, accountType));
    }


    @PostMapping("/user-withdraw")
    public ResponseEntity<JsonNode> withdrawUserAccount(@Valid @RequestBody PesawayTopupRequest request) {
        double amount = request.getAmount();
        String receipt = new TransactionRefGenerator().generate();
        String phoneNumber = request.getPhoneNumber();
        String channel = request.getChannel();
        String reason = request.getReason();
        String alias = request.getAlias();
        AccountType accountType = AccountType.USER;
        return ResponseEntity.ok(pesaWayApiClient.sendB2CPayment(receipt, amount, phoneNumber, channel, alias, reason, accountType));
    }

    @PostMapping("/c2b/callback")
    public ResponseEntity<?> handleCallback(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(pesaWayApiClient.processPesawayCallback(payload));
    }

    @PostMapping("/b2c/callback")
    public ResponseEntity<?> handleB2CResults(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(pesaWayApiClient.processPesawayB2Cresults(payload));
    }

}
