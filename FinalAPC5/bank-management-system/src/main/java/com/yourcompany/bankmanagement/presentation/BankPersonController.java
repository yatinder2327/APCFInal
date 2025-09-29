package com.yourcompany.bankmanagement.presentation;

import com.yourcompany.bankmanagement.data.User;
import com.yourcompany.bankmanagement.data.Account;
import com.yourcompany.bankmanagement.data.Transaction;
import com.yourcompany.bankmanagement.data.Loan;
import com.yourcompany.bankmanagement.service.UserServiceInterface;
import com.yourcompany.bankmanagement.service.AccountServiceInterface;
import com.yourcompany.bankmanagement.service.LoanServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/bank-person")
public class BankPersonController {

    @Autowired
    private UserServiceInterface userService;

    @Autowired
    private AccountServiceInterface accountService;

    @Autowired
    private LoanServiceInterface loanService;

    // Test endpoint to verify the controller is working
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Bank Person Controller is working!");
    }

    // Get bank person's own ID
    @GetMapping("/bank-person-id")
    public ResponseEntity<String> getBankPersonId(Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.badRequest().body("Not authenticated");
            }
            
            User bankPerson = userService.findByUsername(authentication.getName());
            if (bankPerson == null || !"BANK_PERSON".equalsIgnoreCase(bankPerson.getRole())) {
                return ResponseEntity.badRequest().body("Not authorized");
            }
            return ResponseEntity.ok(bankPerson.getBankPersonId() != null ? bankPerson.getBankPersonId() : "");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Get all users (for bank person to view)
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }
            
            User bankPerson = userService.findByUsername(authentication.getName());
            if (bankPerson == null || !"BANK_PERSON".equalsIgnoreCase(bankPerson.getRole())) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }
            
            List<User> users = userService.findByRole("USER");
            // Remove sensitive information
            users.forEach(user -> {
                user.setPassword(null);
            });
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    // Delete a user (and related data)
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId, Authentication authentication) {
        User bankPerson = userService.findByUsername(authentication.getName());
        if (bankPerson == null || !"BANK_PERSON".equalsIgnoreCase(bankPerson.getRole())) {
            return ResponseEntity.badRequest().body("Not authorized");
        }

        User user = userService.findByUserId(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

    // List pending loans for approval workflow
    @GetMapping("/loans/pending")
    public ResponseEntity<List<Loan>> getPendingLoans(Authentication authentication) {
        User bankPerson = userService.findByUsername(authentication.getName());
        if (bankPerson == null || !"BANK_PERSON".equalsIgnoreCase(bankPerson.getRole())) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        return ResponseEntity.ok(loanService.getPendingLoans());
    }

    // Approve a loan
    @PostMapping("/loans/{loanId}/approve")
    public ResponseEntity<String> approveLoan(@PathVariable Long loanId, Authentication authentication) {
        User bankPerson = userService.findByUsername(authentication.getName());
        if (bankPerson == null || !"BANK_PERSON".equalsIgnoreCase(bankPerson.getRole())) {
            return ResponseEntity.badRequest().body("Not authorized");
        }
        Loan loan = loanService.approveLoan(loanId);
        if (loan == null) return ResponseEntity.badRequest().body("Loan not found or not in PENDING status");
        return ResponseEntity.ok("Loan approved");
    }

    // Decline a loan
    @PostMapping("/loans/{loanId}/decline")
    public ResponseEntity<String> declineLoan(@PathVariable Long loanId, Authentication authentication) {
        User bankPerson = userService.findByUsername(authentication.getName());
        if (bankPerson == null || !"BANK_PERSON".equalsIgnoreCase(bankPerson.getRole())) {
            return ResponseEntity.badRequest().body("Not authorized");
        }
        Loan loan = loanService.denyLoan(loanId);
        if (loan == null) return ResponseEntity.badRequest().body("Loan not found or not in PENDING status");
        return ResponseEntity.ok("Loan declined");
    }

    // Get user details with account information
    @GetMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> getUserDetails(@PathVariable String userId, Authentication authentication) {
        User bankPerson = userService.findByUsername(authentication.getName());
        if (bankPerson == null || !"BANK_PERSON".equalsIgnoreCase(bankPerson.getRole())) {
            return ResponseEntity.badRequest().body(null);
        }

        User user = userService.findByUserId(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Account account = accountService.findByUserId(user.getId());
        
        Map<String, Object> userDetails = Map.of(
            "user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "userId", user.getUserId(),
                "role", user.getRole()
            ),
            "account", account != null ? Map.of(
                "id", account.getId(),
                "accountNumber", account.getAccountNumber(),
                "balance", account.getBalance()
            ) : null
        );

        return ResponseEntity.ok(userDetails);
    }

    // Credit money to user account
    @PostMapping("/users/{userId}/credit")
    public ResponseEntity<String> creditAccount(@PathVariable String userId, 
                                               @RequestBody Map<String, Object> request,
                                               Authentication authentication) {
        User bankPerson = userService.findByUsername(authentication.getName());
        if (bankPerson == null || !"BANK_PERSON".equalsIgnoreCase(bankPerson.getRole())) {
            return ResponseEntity.badRequest().body("Not authorized");
        }

        User user = userService.findByUserId(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        Account account = accountService.findByUserId(user.getId());
        if (account == null) {
            return ResponseEntity.badRequest().body("Account not found");
        }

        try {
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("Amount must be positive");
            }

            accountService.creditAccount(user.getId(), amount, "Credit by Bank Person: " + bankPerson.getBankPersonId());

            return ResponseEntity.ok("Successfully credited " + amount + " to account " + account.getAccountNumber());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid amount format");
        }
    }

    // Debit money from user account
    @PostMapping("/users/{userId}/debit")
    public ResponseEntity<String> debitAccount(@PathVariable String userId, 
                                              @RequestBody Map<String, Object> request,
                                              Authentication authentication) {
        User bankPerson = userService.findByUsername(authentication.getName());
        if (bankPerson == null || !"BANK_PERSON".equalsIgnoreCase(bankPerson.getRole())) {
            return ResponseEntity.badRequest().body("Not authorized");
        }

        User user = userService.findByUserId(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        Account account = accountService.findByUserId(user.getId());
        if (account == null) {
            return ResponseEntity.badRequest().body("Account not found");
        }

        try {
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("Amount must be positive");
            }

            if (account.getBalance().compareTo(amount) < 0) {
                return ResponseEntity.badRequest().body("Insufficient balance");
            }

            accountService.debitAccount(user.getId(), amount, "Debit by Bank Person: " + bankPerson.getBankPersonId());

            return ResponseEntity.ok("Successfully debited " + amount + " from account " + account.getAccountNumber());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid amount format");
        }
    }

    // Get user's transaction history
    @GetMapping("/users/{userId}/transactions")
    public ResponseEntity<List<Transaction>> getUserTransactions(@PathVariable String userId, Authentication authentication) {
        User bankPerson = userService.findByUsername(authentication.getName());
        if (bankPerson == null || !"BANK_PERSON".equalsIgnoreCase(bankPerson.getRole())) {
            return ResponseEntity.badRequest().body(null);
        }

        User user = userService.findByUserId(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Account account = accountService.findByUserId(user.getId());
        if (account == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<Transaction> transactions = accountService.getTransactionHistoryByUserId(user.getId());
        return ResponseEntity.ok(transactions);
    }
}
