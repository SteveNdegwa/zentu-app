package com.zentu.zentu_core.billing.service.account;

import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

public interface AccountService {
    ResponseEntity<?> topUp(String phoneNumber, String groupAlias, BigDecimal amount);
    ResponseEntity<?> withdraw(String phoneNumber, String groupAlias, BigDecimal amount);
}
