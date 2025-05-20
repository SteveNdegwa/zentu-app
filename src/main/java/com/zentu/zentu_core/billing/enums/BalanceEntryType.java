package com.zentu.zentu_core.billing.enums;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum BalanceEntryType {

    ACCOUNT_DEPOSIT("ACCOUNT DEPOSIT", EntryCategory.CREDIT),
    APPROVE_ACCOUNT_DEPOSIT("APPROVE ACCOUNT DEPOSIT", EntryCategory.CREDIT),
    ACCOUNT_WITHDRAW("ACCOUNT WITHDRAW", EntryCategory.DEBIT),
    APPROVE_ACCOUNT_WITHDRAW("APPROVE ACCOUNT WITHDRAW", EntryCategory.DEBIT),
    FUNDS_TRANSFER("FUNDS TRANSFER", EntryCategory.DEBIT),
    APPROVE_FUNDS_TRANSFER("APPROVE FUNDS TRANSFER", EntryCategory.DEBIT),
    TRANSACTION_CHARGES("TRANSACTION CHARGES", EntryCategory.DEBIT),
    APPROVE_TRANSACTION_CHARGES("APPROVE TRANSACTION CHARGES", EntryCategory.DEBIT),
    TRANSACTION_REVERSAL("TRANSACTION REVERSAL", EntryCategory.CREDIT),
    APPROVE_TRANSACTION_REVERSAL("APPROVE TRANSACTION REVERSAL", EntryCategory.CREDIT);

    private final String displayName;
    private final EntryCategory category;

    BalanceEntryType(String displayName, EntryCategory category) {
        this.displayName = displayName;
        this.category = category;
    }
	
	public static BalanceEntryType fromString(String value) {
        for (BalanceEntryType type : BalanceEntryType.values()) {
            if (type.name().equalsIgnoreCase(value) || type.displayName.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown BalanceEntryType: " + value);
    }
}

