package com.zentu.zentu_core.billing.enums;

public enum EntryCategory {
    CREDIT("CR"),
    DEBIT("DR");

    private final String code;

    EntryCategory(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
