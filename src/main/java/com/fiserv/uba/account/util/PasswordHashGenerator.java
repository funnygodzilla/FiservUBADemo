package com.fiserv.uba.account.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility to generate BCrypt password hashes
 * Run this to generate hashes for the migration file
 */
public class PasswordHashGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("Generating BCrypt hashes for default users:");
        System.out.println("===========================================");

        // Admin password
        String adminPassword = "Admin@123";
        String adminHash = encoder.encode(adminPassword);
        System.out.println("\nAdmin (Admin@123):");
        System.out.println(adminHash);

        // Manager password
        String managerPassword = "Manager@123";
        String managerHash = encoder.encode(managerPassword);
        System.out.println("\nManager (Manager@123):");
        System.out.println(managerHash);

        // User password
        String userPassword = "User@123";
        String userHash = encoder.encode(userPassword);
        System.out.println("\nUser (User@123):");
        System.out.println(userHash);

        // Customer password
        String customerPassword = "Customer@123";
        String customerHash = encoder.encode(customerPassword);
        System.out.println("\nCustomer (Customer@123):");
        System.out.println(customerHash);

        System.out.println("\n===========================================");
        System.out.println("Copy these hashes to V1.2__Create_Authentication_Schema.sql");
    }
}

