package com.yourcompany.bankmanagement.presentation;

import com.yourcompany.bankmanagement.data.Loan;
import com.yourcompany.bankmanagement.data.Transaction;
import com.yourcompany.bankmanagement.data.User;
import com.yourcompany.bankmanagement.service.UserServiceInterface;
import com.yourcompany.bankmanagement.service.AccountServiceInterface;
import com.yourcompany.bankmanagement.service.LoanServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import java.security.Principal;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Collections;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserServiceInterface userService;

    @Autowired
    private AccountServiceInterface accountService;

    @Autowired
    private LoanServiceInterface loanService;

    @GetMapping("/balance")
    public BigDecimal getBalance(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return BigDecimal.ZERO;
        }
        return accountService.getAccountBalanceByUserId(user.getId());
    }

    @GetMapping("/account-number")
    public String getAccountNumber(Principal principal){
        User user = userService.findByUsername(principal.getName());
        if(user == null) return "";
        return accountService.getAccountNumberByUserId(user.getId());
    }

    @GetMapping("/user-id")
    public String getUserId(Principal principal){
        User user = userService.findByUsername(principal.getName());
        if(user == null) return "";
        return user.getUserId() != null ? user.getUserId() : "";
    }

    @GetMapping("/transactions")
    public List<Transaction> getTransactions(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return Collections.emptyList();
        }
        return accountService.getTransactionHistoryByUserId(user.getId());
    }

    @GetMapping("/loans")
    public List<Loan> getLoans(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return Collections.emptyList();
        }
        return loanService.getLoansByUserId(user.getId());
    }

    @PostMapping("/loan/apply")
    public String applyForLoan(Principal principal, @RequestBody Loan loan) {
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return "User not found.";
        }
        Loan savedLoan = loanService.applyForLoan(user.getId(), loan);
        if (savedLoan != null) {
            return "Loan application submitted successfully.";
        }
        return "Failed to submit loan application.";
    }
}