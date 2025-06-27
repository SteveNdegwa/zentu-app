package com.zentu.zentu_core.billing.service.account;

import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.billing.entity.Transaction;
import com.zentu.zentu_core.billing.enums.AccountType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

public interface AccountService {
    ResponseEntity<?> topUp(String receiptNumber, String alias, BigDecimal amount, AccountType accountType, State status);
    ResponseEntity<?> approveAccountTopUp(String receiptNumber, Transaction transaction, String alias, BigDecimal amount, AccountType accountType, State status);
    ResponseEntity<?> withdraw(String receiptNumber, String alias, BigDecimal amount, AccountType accountType);
    ResponseEntity<?> accountTransfer(String receiptNumber, String alias, BigDecimal amount, AccountType accountType);
}
