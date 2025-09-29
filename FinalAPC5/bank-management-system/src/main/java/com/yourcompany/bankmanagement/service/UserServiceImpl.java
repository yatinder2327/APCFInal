package com.yourcompany.bankmanagement.service;

import com.yourcompany.bankmanagement.data.User;
import com.yourcompany.bankmanagement.data.Account;
import com.yourcompany.bankmanagement.repository.UserRepository;
import com.yourcompany.bankmanagement.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserServiceInterface {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public User createUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("USER");
        }
        // Generate unique userId
        user.setUserId(generateUniqueUserId());
        
        // Generate bank person ID if role is BANK_PERSON
        if ("BANK_PERSON".equalsIgnoreCase(user.getRole())) {
            user.setBankPersonId(generateUniqueBankPersonId());
        }
        
        User saved = userRepository.save(user);
        // create account with unique 11-digit account number
        Account account = new Account();
        account.setUser(saved);
        account.setBalance(java.math.BigDecimal.ZERO);
        account.setAccountNumber(generateUniqueAccountNumber());
        accountRepository.save(account);
        return saved;
    }

    @Override
    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        
        // If user is a bank person but doesn't have a bank person ID, generate one
        if (user != null && "BANK_PERSON".equalsIgnoreCase(user.getRole()) && 
            (user.getBankPersonId() == null || user.getBankPersonId().isBlank())) {
            user.setBankPersonId(generateUniqueBankPersonId());
            user = userRepository.save(user);
        }
        
        return user;
    }

    @Override
    public User findByUserId(String userId) {
        try {
            System.out.println("UserServiceImpl: Finding user by userId: " + userId);
            if (userId == null || userId.trim().isEmpty()) {
                System.out.println("UserServiceImpl: userId is null or empty");
                return null;
            }
            Optional<User> userOpt = userRepository.findByUserId(userId.trim());
            User user = userOpt.orElse(null);
            System.out.println("UserServiceImpl: Found user: " + (user != null ? user.getUsername() : "null"));
            return user;
        } catch (Exception e) {
            System.err.println("UserServiceImpl: Error finding user by userId: " + userId + ", Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User findByBankPersonId(String bankPersonId) {
        return userRepository.findByBankPersonId(bankPersonId).orElse(null);
    }

    @Override
    public List<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findByUserId(userId).orElse(null);
        if (user != null) {
            // Delete account and transactions
            accountRepository.findByUserId(user.getId()).ifPresent(account -> {
                accountRepository.delete(account);
            });
            userRepository.delete(user);
        }
    }
    
    private String generateUniqueUserId() {
        // Generate a unique 8-character alphanumeric userId
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        java.util.Random random = new java.util.Random();
        StringBuilder userId = new StringBuilder();
        
        // Try to generate a unique userId
        for (int attempt = 0; attempt < 10; attempt++) {
            userId.setLength(0);
            for (int i = 0; i < 8; i++) {
                userId.append(chars.charAt(random.nextInt(chars.length())));
            }
            String candidate = userId.toString();
            // Check if this userId already exists
            if (userRepository.findByUserId(candidate).isEmpty()) {
                return candidate;
            }
        }
        // Fallback: use timestamp-based userId
        return "USR" + String.valueOf(System.currentTimeMillis()).substring(5);
    }
    
    private String generateUniqueBankPersonId() {
        // Generate a unique 6-character alphanumeric bank person ID
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        java.util.Random random = new java.util.Random();
        StringBuilder bankPersonId = new StringBuilder();
        
        // Try to generate a unique bank person ID
        for (int attempt = 0; attempt < 10; attempt++) {
            bankPersonId.setLength(0);
            for (int i = 0; i < 6; i++) {
                bankPersonId.append(chars.charAt(random.nextInt(chars.length())));
            }
            String candidate = bankPersonId.toString();
            // Check if this bank person ID already exists
            if (userRepository.findByBankPersonId(candidate).isEmpty()) {
                return candidate;
            }
        }
        // Fallback: use timestamp-based bank person ID
        return "BP" + String.valueOf(System.currentTimeMillis()).substring(7);
    }
    
    private String generateUniqueAccountNumber(){
        java.util.Random r = new java.util.Random();
        for(int i=0;i<10;i++){
            String candidate = String.format("%011d", Math.abs(r.nextLong()) % 100_000_000_000L);
            if (accountRepository.findByAccountNumber(candidate).isEmpty()) return candidate;
        }
        return String.valueOf(System.currentTimeMillis()).substring(0,11);
    }
}



