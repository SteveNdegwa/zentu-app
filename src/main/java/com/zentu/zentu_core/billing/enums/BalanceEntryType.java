package com.zentu.zentu_core.billing.enums;

public enum BalanceEntryType {

    TOPUP_WALLET("Topup Wallet", EntryCategory.CREDIT),
    WITHDRAW_WALLET("Withdraw Wallet", EntryCategory.DEBIT),
    FUNDS_TRANSFER("Funds Transfer", EntryCategory.DEBIT),
    TRANSACTION_CHARGES("Transaction Charges", EntryCategory.DEBIT),
    TRANSACTION_REVERSAL("Transaction Reversal", EntryCategory.CREDIT);

    private final String displayName;
    private final EntryCategory category;

    BalanceEntryType(String displayName, EntryCategory category) {
        this.displayName = displayName;
        this.category = category;
    }

    public String getDisplayName() {
        return displayName;
    }

    public EntryCategory getCategory() {
        return category;
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

