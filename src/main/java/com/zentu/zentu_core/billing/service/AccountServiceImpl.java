package com.zentu.zentu_core.billing.service;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
@Service
public class AccountServiceImpl implements AccountService {

    @Override
    public ResponseEntity<?> topUp(String groupAlias, BigDecimal amount) {
        return ResponseEntity.ok("Top-up successful");
    }

    @Override
    public ResponseEntity<?> withdraw(String groupAlias, BigDecimal amount) {
        return ResponseEntity.ok("Withdraw successful");
    }
}
