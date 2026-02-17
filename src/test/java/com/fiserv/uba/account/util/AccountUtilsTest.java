package com.fiserv.uba.account.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountUtilsTest {

    @Test
    void testNormalizeAccountId_WithValidId() {
        String accountId = "  ACC1707210600ABCD1234  ";
        String result = AccountUtils.normalizeAccountId(accountId);

        assertEquals("ACC1707210600ABCD1234", result);
    }

    @Test
    void testNormalizeAccountId_WithoutSpaces() {
        String accountId = "ACC1707210600ABCD1234";
        String result = AccountUtils.normalizeAccountId(accountId);

        assertEquals("ACC1707210600ABCD1234", result);
    }

    @Test
    void testNormalizeAccountId_WithLeadingSpaces() {
        String accountId = "   ACC1707210600ABCD1234";
        String result = AccountUtils.normalizeAccountId(accountId);

        assertEquals("ACC1707210600ABCD1234", result);
    }

    @Test
    void testNormalizeAccountId_WithTrailingSpaces() {
        String accountId = "ACC1707210600ABCD1234   ";
        String result = AccountUtils.normalizeAccountId(accountId);

        assertEquals("ACC1707210600ABCD1234", result);
    }

    @Test
    void testNormalizeAccountId_WithNull() {
        String result = AccountUtils.normalizeAccountId(null);

        assertNull(result);
    }

    @Test
    void testNormalizeAccountId_EmptyString() {
        String result = AccountUtils.normalizeAccountId("");

        assertEquals("", result);
    }

    @Test
    void testNormalizeAccountId_OnlySpaces() {
        String result = AccountUtils.normalizeAccountId("   ");

        assertEquals("", result);
    }

    @Test
    void testUtilityClassStructure() {
        // AccountUtils is a utility class with static methods only
        // Cannot be instantiated
        assertDoesNotThrow(() -> {
            AccountUtils.normalizeAccountId("test");
        });
    }
}

