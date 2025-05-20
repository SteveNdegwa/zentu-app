package com.zentu.zentu_core.billing.service;
import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.billing.entity.Account;
import com.zentu.zentu_core.billing.entity.BalanceLog;
import com.zentu.zentu_core.billing.entity.BalanceLogEntry;
import com.zentu.zentu_core.billing.entity.Transaction;
import com.zentu.zentu_core.billing.enums.EntryCategory;
import com.zentu.zentu_core.billing.enums.AccountFieldType;
import com.zentu.zentu_core.billing.enums.BalanceEntryType;
import com.zentu.zentu_core.billing.repository.BalanceLogEntryRepository;
import com.zentu.zentu_core.billing.repository.BalanceLogRepository;
import com.zentu.zentu_core.billing.repository.TransactionRepository;
import com.zentu.zentu_core.billing.utils.ResponseProvider;
import com.zentu.zentu_core.billing.repository.AccountRepository;
import com.zentu.zentu_core.billing.utils.TransactionRefGenerator;
import com.zentu.zentu_core.group.entity.Group;
import com.zentu.zentu_core.group.repository.GroupRepository;
import com.zentu.zentu_core.user.entity.User;
import com.zentu.zentu_core.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.sql.Alias;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BalanceLogRepository balanceLogRepository;
    private final BalanceLogEntryRepository balanceLogentryRepository;
    
    
    @Override
    @Transactional
    public ResponseEntity<?> topUp(String alias, String phoneNumber, BigDecimal amount) {
        try {
            User user = userRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            log.error("TopUp request received for phone number: {} with amount: {}", phoneNumber, amount);
            Group group = groupRepository.findByAlias(alias)
                    .orElseThrow(() -> new RuntimeException("Group not found not found"));
            Account account = accountRepository.findByGroup(group)
                    .orElseThrow(() -> new RuntimeException("User account not found"));
            String receipt = new TransactionRefGenerator().generate();
            Transaction transaction = new Transaction();
            transaction.setUser(user);
            transaction.setAmount(amount);
            transaction.setTransactionType(EntryCategory.CREDIT);
            transaction.setInternalReference(receipt);
            transaction.setReceiptNumber(receipt);
            transaction.setStatus(State.COMPLETED);
            transaction.setBalance(account.getAvailable().add(amount));
            transactionRepository.save(transaction);
            
            BigDecimal newCurrentBalance = account.getCurrent().add(amount);
            String entryType = EntryCategory.CREDIT.name();
            String balEntryTypeName = BalanceEntryType.ACCOUNT_DEPOSIT.name();
            String accFieldType = AccountFieldType.UNCLEARED.name();
            account.setUncleared(account.getUncleared().add(amount));
            createBalanceLogs(receipt, transaction, amount, newCurrentBalance, accFieldType, balEntryTypeName, entryType);
            
            balEntryTypeName = BalanceEntryType.ACCOUNT_DEPOSIT.name();
            accFieldType = AccountFieldType.CURRENT.name();
            account.setCurrent(newCurrentBalance);
            createBalanceLogs(receipt, transaction, amount, newCurrentBalance, accFieldType, balEntryTypeName, entryType);
            
            entryType = EntryCategory.DEBIT.name();
            balEntryTypeName = BalanceEntryType.APPROVE_ACCOUNT_DEPOSIT.name();
            accFieldType = AccountFieldType.UNCLEARED.name();
            account.setUncleared(account.getUncleared().subtract(amount));
            createBalanceLogs(receipt, transaction, amount, newCurrentBalance,accFieldType, balEntryTypeName, entryType);
            
            entryType =  EntryCategory.CREDIT.name();
            balEntryTypeName = BalanceEntryType.APPROVE_ACCOUNT_DEPOSIT.name();
            accFieldType = AccountFieldType.AVAILABLE.name();
            account.setAvailable(amount);
            createBalanceLogs(receipt, transaction, amount, newCurrentBalance, accFieldType, balEntryTypeName, entryType);
            accountRepository.save(account);
            Map<String, Object> data = new HashMap<>();
            data.put("code", "200.000.000");
            data.put("data", account.getAvailable());
            return new ResponseProvider(data).success();
        } catch (Exception e) {
            log.error("Error in topUp: {}", e.getMessage());
            return new ResponseProvider("500.000.001", "Failed to topup Account").exception();
        }
    }
    @Override
    public ResponseEntity<?> withdraw(String phoneNumber,String alias, BigDecimal amount) {
        try {
            User user = userRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            log.error("TopUp request received for phone number: {} with amount: {}", phoneNumber, amount);
            Group group = groupRepository.findByAlias(alias)
                    .orElseThrow(() -> new RuntimeException("Group not found not found"));
            Account account = accountRepository.findByGroup(group)
                    .orElseThrow(() -> new RuntimeException("User account not found"));
            if (account.getAvailable().compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient balance");
            }
            BigDecimal newAvailableBalance = account.getAvailable().subtract(amount);
            String receipt = new TransactionRefGenerator().generate();
            Transaction transaction = new Transaction();
            transaction.setUser(user);
            transaction.setAmount(amount);
            transaction.setBalance(newAvailableBalance);
            transaction.setTransactionType(EntryCategory.DEBIT);
            transaction.setReceiptNumber(receipt);
            transaction.setStatus(State.COMPLETED);
            transactionRepository.save(transaction);
            String entryTypeName = EntryCategory.DEBIT.name();
            String balEntryTypeName = BalanceEntryType.ACCOUNT_WITHDRAW.name();
            String accFieldType = AccountFieldType.AVAILABLE.name();
            account.setAvailable(newAvailableBalance);
            createBalanceLogs(receipt, transaction, amount, newAvailableBalance, accFieldType, balEntryTypeName, entryTypeName);
            
            entryTypeName = EntryCategory.DEBIT.name();
            balEntryTypeName = BalanceEntryType.ACCOUNT_WITHDRAW.name();
            accFieldType = AccountFieldType.RESERVED.name();
            createBalanceLogs(receipt, transaction, amount, newAvailableBalance, accFieldType, balEntryTypeName, entryTypeName);
            account.setReserved(amount);
            
            entryTypeName = EntryCategory.DEBIT.name();
            balEntryTypeName = BalanceEntryType.APPROVE_ACCOUNT_WITHDRAW.name();
            accFieldType = AccountFieldType.RESERVED.name();
            createBalanceLogs(receipt, transaction, amount, newAvailableBalance, accFieldType, balEntryTypeName, entryTypeName);
            account.setReserved(account.getReserved().subtract(amount));
            
            entryTypeName = EntryCategory.DEBIT.name();
            balEntryTypeName = BalanceEntryType.APPROVE_ACCOUNT_WITHDRAW.name();
            accFieldType = AccountFieldType.CURRENT.name();
            createBalanceLogs(receipt, transaction, amount, newAvailableBalance, accFieldType, balEntryTypeName, entryTypeName);
            account.setCurrent(account.getCurrent().subtract(amount));
            accountRepository.save(account);
            Map<String, Object> data = new HashMap<>();
            data.put("code", "200.000.000");
            data.put("data", newAvailableBalance);
            return new ResponseProvider(data).success();
        } catch (Exception e) {
            log.error("Error in deduct: {}", e.getMessage());
            return new ResponseProvider("500.000.001", "Failed to deduct from Account").exception();
        }
    }
    
    public void createBalanceLogs(String receipt, Transaction transaction, BigDecimal amount, BigDecimal balance, String accFieldType, String balEntryTypeName, String entryTypeName) {
        BalanceLog balanceLog = new BalanceLog();
        balanceLog.setBalance(balance);
        balanceLog.setAmountTransacted(amount);
        balanceLog.setReceipt(receipt);
        balanceLog.setTransaction(transaction);
        balanceLog.setBalanceEntryType(BalanceEntryType.valueOf(balEntryTypeName));
        balanceLog.setState(State.COMPLETED);
        balanceLogRepository.save(balanceLog);
        BalanceLogEntry balanceLogEntry = new BalanceLogEntry();
        balanceLogEntry.setBalanceLog(balanceLog);
        balanceLogEntry.setAccountFieldType(AccountFieldType.valueOf(accFieldType));
        balanceLogEntry.setAmountTransacted(amount);
        balanceLogEntry.setStatus(State.COMPLETED);
        balanceLogEntry.setEntryCategory(EntryCategory.valueOf(entryTypeName));
        balanceLogentryRepository.save(balanceLogEntry);
    }
}
