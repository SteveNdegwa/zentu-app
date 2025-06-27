package com.zentu.zentu_core.billing.controller.pesaway;

import com.fasterxml.jackson.databind.JsonNode;
import com.zentu.zentu_core.auth.annotation.ProtectedEndpoint;
import com.zentu.zentu_core.billing.dto.FundsTransfer;
import com.zentu.zentu_core.billing.dto.PesawayTopupRequest;
import com.zentu.zentu_core.billing.enums.AccountType;
import com.zentu.zentu_core.billing.service.account.AccountService;
import com.zentu.zentu_core.billing.service.pesaway.PesaWayApiClient;
import com.zentu.zentu_core.common.utils.TransactionRefGenerator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/pesaway")
public class PesaWayController {

    private final PesaWayApiClient pesaWayApiClient;
    private final AccountService accountService;

    public PesaWayController(PesaWayApiClient pesaWayApiClient, AccountService accountService) {
        this.pesaWayApiClient = pesaWayApiClient;
	    this.accountService = accountService;
    }

    @GetMapping("/balance")
    @ProtectedEndpoint
    public ResponseEntity<JsonNode> getBalance() {
        return ResponseEntity.ok(pesaWayApiClient.getAccountBalance());
    }

    
    @PostMapping("/community-topup")
    @ProtectedEndpoint
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
    @ProtectedEndpoint
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
    @ProtectedEndpoint
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
    
    @PostMapping("/transfer-funds")
    @ProtectedEndpoint
    public ResponseEntity<JsonNode> transferFunds(@Valid @RequestBody FundsTransfer request) {
        BigDecimal amount = request.getAmount();
        String receipt = new TransactionRefGenerator().generate();
        String phoneNumber = request.getPhoneNumber();
        String destination = request.getDestination();
        String alias = request.getWalletAlias();
        AccountType accountType = AccountType.USER;
        if (destination.equalsIgnoreCase("MPESA") || destination.equalsIgnoreCase("M-PESA")) {
            return ResponseEntity.ok(pesaWayApiClient.sendB2CPayment(receipt, amount.doubleValue(), phoneNumber, "M-PESA", alias, "Transfer to M-PESA", accountType));
        } else {
            ResponseEntity<?> response = accountService.accountTransfer(receipt, alias, amount, accountType);
            if (response.getBody() instanceof JsonNode) {
                return ResponseEntity
                        .status(response.getStatusCode())
                        .body((JsonNode) response.getBody());
            } else {
                throw new IllegalStateException("accountTransfer response is not a JsonNode");
            }
        }
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
