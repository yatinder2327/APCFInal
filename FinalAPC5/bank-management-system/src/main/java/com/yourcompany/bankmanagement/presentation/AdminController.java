package com.yourcompany.bankmanagement.presentation;

import com.yourcompany.bankmanagement.data.Loan;
import com.yourcompany.bankmanagement.data.Account;
import com.yourcompany.bankmanagement.data.User;
import com.yourcompany.bankmanagement.service.UserServiceInterface;
import com.yourcompany.bankmanagement.service.AccountServiceInterface;
import com.yourcompany.bankmanagement.service.LoanServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserServiceInterface userService;

    @Autowired
    private AccountServiceInterface accountService;

    @Autowired
    private LoanServiceInterface loanService;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        try {
            System.out.println("AdminController: Getting all users");
            List<User> users = userService.getAllUsers();
            System.out.println("AdminController: Found " + (users != null ? users.size() : 0) + " users");
            return users != null ? users : new java.util.ArrayList<>();
        } catch (Exception e) {
            System.err.println("AdminController: Error getting all users, Error: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    @GetMapping("/accounts")
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/loans")
    public List<Loan> getAllLoans() {
        return loanService.getAllLoans();
    }

    @GetMapping("/bank-persons")
    public List<User> getAllBankPersons() {
        return userService.findByRole("BANK_PERSON");
    }

    @GetMapping("/loans/pending")
    public List<Loan> getPendingLoans() {
        return loanService.getPendingLoans();
    }

    // Test endpoint to check if user exists
    @GetMapping("/test-user/{userId}")
    public ResponseEntity<Map<String, Object>> testUser(@PathVariable String userId) {
        try {
            System.out.println("AdminController: Testing user existence for userId: " + userId);
            User user = userService.findByUserId(userId);
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("exists", user != null);
            response.put("userId", userId);
            if (user != null) {
                response.put("username", user.getUsername());
                response.put("role", user.getRole());
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("AdminController: Error testing user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // User details (works for any user role)
    @GetMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> getUserDetails(@PathVariable String userId) {
        try {
            System.out.println("AdminController: Getting user details for userId: " + userId);
            User user = userService.findByUserId(userId);
            if (user == null) {
                System.out.println("AdminController: User not found for userId: " + userId);
                return ResponseEntity.notFound().build();
            }
            System.out.println("AdminController: Found user: " + user.getUsername() + " with role: " + user.getRole());
            
            Account account = accountService.findByUserId(user.getId());
            System.out.println("AdminController: Account found: " + (account != null ? account.getAccountNumber() : "null"));
            
            // Create user map with null-safe values
            Map<String, Object> userMap = new java.util.HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("userId", user.getUserId() != null ? user.getUserId() : "");
            userMap.put("role", user.getRole() != null ? user.getRole() : "");
            userMap.put("bankPersonId", user.getBankPersonId() != null ? user.getBankPersonId() : "");
            
            // Create account map with null-safe values
            Map<String, Object> accountMap = null;
            if (account != null) {
                accountMap = new java.util.HashMap<>();
                accountMap.put("id", account.getId());
                accountMap.put("accountNumber", account.getAccountNumber() != null ? account.getAccountNumber() : "");
                accountMap.put("balance", account.getBalance() != null ? account.getBalance() : java.math.BigDecimal.ZERO);
            }
            
            Map<String, Object> body = new java.util.HashMap<>();
            body.put("user", userMap);
            body.put("account", accountMap);
            
            System.out.println("AdminController: Returning user details: " + body);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            System.err.println("AdminController: Error getting user details for userId: " + userId + ", Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    // User transactions
    @GetMapping("/users/{userId}/transactions")
    public ResponseEntity<List<com.yourcompany.bankmanagement.data.Transaction>> getUserTransactions(@PathVariable String userId) {
        try {
            System.out.println("AdminController: Getting transactions for userId: " + userId);
            User user = userService.findByUserId(userId);
            if (user == null) {
                System.out.println("AdminController: User not found for transactions, userId: " + userId);
                return ResponseEntity.notFound().build();
            }
            List<com.yourcompany.bankmanagement.data.Transaction> transactions = accountService.getTransactionHistoryByUserId(user.getId());
            System.out.println("AdminController: Found " + (transactions != null ? transactions.size() : 0) + " transactions for user: " + user.getUsername());
            return ResponseEntity.ok(transactions != null ? transactions : new java.util.ArrayList<>());
        } catch (Exception e) {
            System.err.println("AdminController: Error getting transactions for userId: " + userId + ", Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(new java.util.ArrayList<>());
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        User user = userService.findByUserId(userId);
        if (user == null) return ResponseEntity.badRequest().body("User not found");

        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted");
    }

    @PostMapping("/loans/{loanId}/approve")
    public ResponseEntity<String> approveLoan(@PathVariable Long loanId) {
        Loan loan = loanService.approveLoan(loanId);
        if (loan == null) return ResponseEntity.badRequest().body("Loan not found or not in PENDING status");
        return ResponseEntity.ok("Loan approved");
    }

    @PostMapping("/loans/{loanId}/decline")
    public ResponseEntity<String> declineLoan(@PathVariable Long loanId) {
        Loan loan = loanService.denyLoan(loanId);
        if (loan == null) return ResponseEntity.badRequest().body("Loan not found or not in PENDING status");
        return ResponseEntity.ok("Loan declined");
    }
}