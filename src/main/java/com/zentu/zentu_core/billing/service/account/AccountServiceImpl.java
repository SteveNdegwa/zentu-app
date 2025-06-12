package com.zentu.zentu_core.billing.service.account;
import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.billing.entity.Account;
import com.zentu.zentu_core.billing.entity.BalanceLog;
import com.zentu.zentu_core.billing.entity.BalanceLogEntry;
import com.zentu.zentu_core.billing.entity.Transaction;
import com.zentu.zentu_core.billing.enums.EntryCategory;
import com.zentu.zentu_core.billing.enums.AccountFieldType;
import com.zentu.zentu_core.billing.enums.BalanceEntryType;
import com.zentu.zentu_core.billing.repository.AccountRepository;
import com.zentu.zentu_core.common.utils.ResponseProvider;
import com.zentu.zentu_core.common.db.GenericCrudService;
import com.zentu.zentu_core.group.entity.Group;
import com.zentu.zentu_core.group.repository.GroupRepository;
import com.zentu.zentu_core.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final GenericCrudService genericCrudService;
    private final AccountRepository accountService;
    private final GroupRepository groupService;

    @Override
    @Transactional
    public ResponseEntity<?> topUp(String receipt, String alias, String phoneNumber, BigDecimal amount) {
        try {
            User user = genericCrudService.findOneByField(User.class, "phoneNumber", phoneNumber);
            log.info("User found: {}", user);
            if (user == null) {
                throw new RuntimeException("User not found");
            }

            log.info("TopUp request received for phone number: {} with amount: {}", phoneNumber, amount);

            Group group = groupService.findByAlias(alias).orElseThrow(()-> new RuntimeException("Group not found"));
            if (group == null) {
                throw new RuntimeException("Group not found");
            }

            Account account = accountService.findByAccountGroup(group).orElseThrow(()-> new RuntimeException("Account not found"));
            if (account == null) {
                throw new RuntimeException("User account not found");
            }

            Transaction transaction = Transaction.createCreditTransaction(user, group, amount, receipt, account.getAvailable().add(amount)).save();
            log.info("TOP-UP Transaction updated with ID: {}", transaction.getId());

            account.addUnclearedAmount(amount);
            saveBalanceLog(receipt, transaction, amount, account.getCurrent().add(amount), AccountFieldType.UNCLEARED, BalanceEntryType.ACCOUNT_DEPOSIT, EntryCategory.CREDIT);

            account.addCurrentAmount(amount);
            saveBalanceLog(receipt, transaction, amount, account.getCurrent(), AccountFieldType.CURRENT, BalanceEntryType.ACCOUNT_DEPOSIT, EntryCategory.CREDIT);

            account.subtractUnclearedAmount(amount);
            saveBalanceLog(receipt, transaction, amount, account.getCurrent(), AccountFieldType.UNCLEARED, BalanceEntryType.APPROVE_ACCOUNT_DEPOSIT, EntryCategory.DEBIT);

            account.addAvailableAmount(amount);
            saveBalanceLog(receipt, transaction, amount, account.getCurrent(), AccountFieldType.AVAILABLE, BalanceEntryType.APPROVE_ACCOUNT_DEPOSIT, EntryCategory.CREDIT);
            log.info("Account created with ID: ");


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
    @Transactional
    public ResponseEntity<?> withdraw(String receipt,String phoneNumber, String alias, BigDecimal amount) {
        try {
            User user = genericCrudService.findOneByField(User.class, "phoneNumber", phoneNumber);
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            log.info("Withdraw request received for phone number: {} with amount: {}", phoneNumber, amount);

            Group group = genericCrudService.findOneByField(Group.class, "alias", alias);
            if (group == null) {
                throw new RuntimeException("Group not found");
            }

            Account account = genericCrudService.findOneByField(Account.class, "group", group);
            if (account == null) {
                throw new RuntimeException("User account not found");
            }

            if (account.getAvailable().compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient balance");
            }

            BigDecimal newAvailableBalance = account.getAvailable().subtract(amount);

//            String receipt = new TransactionRefGenerator().generate();
            Transaction transaction = Transaction.createDebitTransaction(user, group, amount, receipt, newAvailableBalance).save();
            log.info("WITHDRAW Transaction created with ID: {}", transaction.getId());

            account.subtractAvailableAmount(amount);
            saveBalanceLog(receipt, transaction, amount, newAvailableBalance, AccountFieldType.AVAILABLE, BalanceEntryType.ACCOUNT_WITHDRAW, EntryCategory.DEBIT);

            account.addReservedAmount(amount);
            saveBalanceLog(receipt, transaction, amount, newAvailableBalance, AccountFieldType.RESERVED, BalanceEntryType.ACCOUNT_WITHDRAW, EntryCategory.DEBIT);

            account.subtractReservedAmount(amount);
            saveBalanceLog(receipt, transaction, amount, newAvailableBalance, AccountFieldType.RESERVED, BalanceEntryType.APPROVE_ACCOUNT_WITHDRAW, EntryCategory.DEBIT);

            account.subtractCurrentAmount(amount);
            saveBalanceLog(receipt, transaction, amount, newAvailableBalance, AccountFieldType.CURRENT, BalanceEntryType.APPROVE_ACCOUNT_WITHDRAW, EntryCategory.DEBIT);
            log.info("Account updated with ID");

            Map<String, Object> data = new HashMap<>();
            data.put("code", "200.000.000");
            data.put("data", newAvailableBalance);
            return new ResponseProvider(data).success();

        } catch (Exception e) {
            log.error("Error in withdraw: {}", e.getMessage());
            return new ResponseProvider("500.000.001", "Failed to withdraw from Account").exception();
        }
    }

    private void saveBalanceLog(String receipt, Transaction transaction, BigDecimal amount, BigDecimal balance,
                                AccountFieldType accountFieldType, BalanceEntryType balanceEntryType, EntryCategory entryCategory) {
        BalanceLog balanceLog = new BalanceLog();
        balanceLog.setBalance(balance);
        balanceLog.setAmountTransacted(amount);
        balanceLog.setReceipt(receipt);
        balanceLog.setTransaction(transaction);
        balanceLog.setBalanceEntryType(balanceEntryType);
        balanceLog.setState(State.COMPLETED);
        balanceLog.save();
        log.info("BalanceLog updated with ID: {}", balanceLog.getId());

        BalanceLogEntry balanceLogEntry = new BalanceLogEntry();
        balanceLogEntry.setBalanceLog(balanceLog);
        balanceLogEntry.setAccountFieldType(accountFieldType);
        balanceLogEntry.setAmountTransacted(amount);
        balanceLogEntry.setStatus(State.COMPLETED);
        balanceLogEntry.setEntryCategory(entryCategory);
        balanceLogEntry.save();
        log.info("BalanceLogEntry updated with ID: {}", balanceLogEntry.getId());
    }
}
