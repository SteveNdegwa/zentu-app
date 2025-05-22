package com.zentu.zentu_core.billing.service.account;

import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

public interface AccountService {
    ResponseEntity<?> topUp(String receiptNumber, String groupAlias, String phoneNumber, BigDecimal amount);
    ResponseEntity<?> withdraw(String receiptNumber, String groupAlias, String phoneNumber, BigDecimal amount);
}
