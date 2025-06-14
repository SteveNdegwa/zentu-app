package com.zentu.zentu_core.billing.service.account;

import com.zentu.zentu_core.billing.enums.AccountType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

public interface AccountService {
    ResponseEntity<?> topUp(String receiptNumber, String alias,  BigDecimal amount, AccountType accountType);
    ResponseEntity<?> withdraw(String receiptNumber, String alias, BigDecimal amount, AccountType accountType);
    ResponseEntity<?> approveWithdrawal(String receiptNumber, String alias, BigDecimal amount, AccountType accountType);
}
