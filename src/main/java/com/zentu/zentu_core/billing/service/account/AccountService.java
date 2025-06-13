package com.zentu.zentu_core.billing.service.account;

import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

public interface AccountService {
    ResponseEntity<?> topUp(String receiptNumber, String alias,  BigDecimal amount, Boolean isGroup);
    ResponseEntity<?> withdraw(String receiptNumber, String alias, BigDecimal amount, Boolean isGroup);
    ResponseEntity<?> approveWithdrawal(String receiptNumber, String alias, BigDecimal amount, Boolean isGroup);
}
