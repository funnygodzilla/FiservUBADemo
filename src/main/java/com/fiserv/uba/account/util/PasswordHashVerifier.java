package com.fiserv.uba.account.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility to verify BCrypt password hashes match the documented passwords
 */
public class PasswordHashVerifier {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("Verifying BCrypt hashes for default users:");
        System.out.println("===========================================\n");

        // Test hashes from migration file
        String adminHash = "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";
        String managerHash = "$2a$10$EblZqNptyYvcKm4ejf.qFOXZBSbQ7Qjz3k7hWgPZjdBjkEg3L7M4K";
        String userHash = "$2a$10$cI42ypzW4gv7PcmYq5p8VuADd.GNnwAVN5mL71sQp6GkPn7L.08Ia";
        String customerHash = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.";

        // Test passwords
        boolean adminMatch = encoder.matches("Admin@123", adminHash);
        boolean managerMatch = encoder.matches("Manager@123", managerHash);
        boolean userMatch = encoder.matches("User@123", userHash);
        boolean customerMatch = encoder.matches("Customer@123", customerHash);

        System.out.println("Admin (Admin@123): " + (adminMatch ? "✓ VALID" : "✗ INVALID"));
        System.out.println("Manager (Manager@123): " + (managerMatch ? "✓ VALID" : "✗ INVALID"));
        System.out.println("User (User@123): " + (userMatch ? "✓ VALID" : "✗ INVALID"));
        System.out.println("Customer (Customer@123): " + (customerMatch ? "✓ VALID" : "✗ INVALID"));

        System.out.println("\n===========================================");

        if (adminMatch && managerMatch && userMatch && customerMatch) {
            System.out.println("✓ All password hashes are correct!");
            System.exit(0);
        } else {
            System.out.println("✗ Some password hashes are incorrect!");
            System.exit(1);
        }
    }
}

