package com.fiserv.uba.account;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test to verify password hashes in migration file match documented passwords
 */
public class PasswordHashTest {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    public void verifyMigrationPasswordHashes() {
        System.out.println("\n===========================================");
        System.out.println("Verifying password hashes from V1.2 migration:");
        System.out.println("===========================================\n");

        // Hashes from V1.2__Create_Authentication_Schema.sql
        String adminHash = "$2a$10$mQpM99Vvwx5sdQ0TBeSdguLVjQrJ2YON/DUqK.M4XOANTJULe5ErO";
        String managerHash = "$2a$10$3gRVrHj/s0ZwSNNTufTZ5.T5uk3Ug0PcTzcIv7DlOHfLh0sGLGSWO";
        String userHash = "$2a$10$.GSG3vuZavBXPS5JiodkVeejkPE24VbDx3c7cb/pD/kanMYhJZLhu";
        String customerHash = "$2a$10$nVM5aF3UWsus6lgDJh.9Gu4AukT0jlA/7TURljJHRlhoaqcDDNIEO";

        // Verify each password matches its hash
        boolean adminMatch = encoder.matches("Admin@123", adminHash);
        boolean managerMatch = encoder.matches("Manager@123", managerHash);
        boolean userMatch = encoder.matches("User@123", userHash);
        boolean customerMatch = encoder.matches("Customer@123", customerHash);

        System.out.println("admin / Admin@123: " + (adminMatch ? "✓ VALID" : "✗ INVALID"));
        assertTrue(adminMatch, "Admin password should match Admin@123");

        System.out.println("manager1 / Manager@123: " + (managerMatch ? "✓ VALID" : "✗ INVALID"));
        assertTrue(managerMatch, "Manager password should match Manager@123");

        System.out.println("user1 / User@123: " + (userMatch ? "✓ VALID" : "✗ INVALID"));
        assertTrue(userMatch, "User password should match User@123");

        System.out.println("customer1 / Customer@123: " + (customerMatch ? "✓ VALID" : "✗ INVALID"));
        assertTrue(customerMatch, "Customer password should match Customer@123");

        System.out.println("\n✓ All password hashes verified successfully!");
        System.out.println("Bootstrap logins will work as documented.");
        System.out.println("===========================================\n");
    }
}

