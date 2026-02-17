package com.fiserv.uba.account.util;

public final class AccountUtils {

    private AccountUtils() {
    }

    public static String normalizeAccountId(String accountId) {
        return accountId == null ? null : accountId.trim();
    }
}
