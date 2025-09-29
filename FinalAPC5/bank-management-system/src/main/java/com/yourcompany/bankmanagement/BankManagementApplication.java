package com.yourcompany.bankmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "com.yourcompany.bankmanagement.repository",
    "com.yourcompany.bankmanagement.service",
    "com.yourcompany.bankmanagement.presentation",
    "com.yourcompany.bankmanagement.security"
})
public class BankManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankManagementApplication.class, args);
    }
}