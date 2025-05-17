package com.zentu.zentu_core.billing.service;

import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

public interface AccountService {
    ResponseEntity<?> topUp(String groupAlias, BigDecimal amount);
    ResponseEntity<?> withdraw(String groupAlias, BigDecimal amount);
}
