package com.zentu.zentu_core.billing.enums;

import lombok.Getter;

@Getter
public enum EntryCategory {
    CREDIT("CR"),
    DEBIT("DR");

    private final String code;

    EntryCategory(String code) {
        this.code = code;
    }
	
}
