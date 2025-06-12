package com.zentu.zentu_core.billing.controller.pesaway;

import com.fasterxml.jackson.databind.JsonNode;
import com.zentu.zentu_core.billing.dto.PesawayTopupRequest;
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

    @PostMapping("/group-topup")
    public ResponseEntity<JsonNode> topupAccount(@Valid @RequestBody PesawayTopupRequest request) {
        double amount = request.getAmount();
        String receipt = new TransactionRefGenerator().generate();
        String phoneNumber = request.getPhoneNumber();
        String channel = request.getChannel();
        String reason = request.getReason();
        String groupAlias = request.getGroupAlias();
        String userId = request.getUserId();
        String resultsUrl = "https://wolf-im-grande-kidney.trycloudflare.com/pesaway/callback";
        return ResponseEntity.ok(pesaWayApiClient.receiveC2BPayment(receipt, amount, phoneNumber, channel, groupAlias, userId, reason, resultsUrl));
    }

    @PostMapping("/callback")
    public ResponseEntity<?> handleCallback(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(pesaWayApiClient.processPesawayCallback(payload));
    }

}
