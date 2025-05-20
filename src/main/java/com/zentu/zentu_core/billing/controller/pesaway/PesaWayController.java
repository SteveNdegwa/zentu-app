package com.zentu.zentu_core.billing.controller.pesaway;

import com.fasterxml.jackson.databind.JsonNode;
import com.zentu.zentu_core.billing.service.pesaway.PesaWayApiClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
