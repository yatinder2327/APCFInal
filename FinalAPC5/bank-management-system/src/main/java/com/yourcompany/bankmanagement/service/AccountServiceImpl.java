package com.yourcompany.bankmanagement.service;

import com.yourcompany.bankmanagement.data.Account;
import com.yourcompany.bankmanagement.data.Transaction;
import com.yourcompany.bankmanagement.repository.AccountRepository;
import com.yourcompany.bankmanagement.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountServiceInterface {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public BigDecimal getAccountBalance(Long accountId) {
        Account account = accountRepository.findById(accountId).orElse(null);
        return (account != null) ? account.getBalance() : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getAccountBalanceByUserId(Long userId) {
        Account account = accountRepository.findByUserId(userId).orElse(null);
        return (account != null) ? account.getBalance() : BigDecimal.ZERO;
    }

    @Override
    public String getAccountNumberByUserId(Long userId) {
        Account account = accountRepository.findByUserId(userId).orElse(null);
        return (account != null) ? account.getAccountNumber() : "";
    }

    @Override
    public List<Transaction> getTransactionHistory(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    @Override
    public List<Transaction> getTransactionHistoryByUserId(Long userId) {
        Account account = accountRepository.findByUserId(userId).orElse(null);
        if (account != null) {
            return transactionRepository.findByAccountId(account.getId());
        }
        return List.of();
    }

    @Override
    public Account findByUserId(Long userId) {
        try {
            System.out.println("AccountServiceImpl: Finding account by userId: " + userId);
            if (userId == null) {
                System.out.println("AccountServiceImpl: userId is null");
                return null;
            }
            Optional<Account> accountOpt = accountRepository.findByUserId(userId);
            Account account = accountOpt.orElse(null);
            System.out.println("AccountServiceImpl: Found account: " + (account != null ? account.getAccountNumber() : "null"));
            return account;
        } catch (Exception e) {
            System.err.println("AccountServiceImpl: Error finding account by userId: " + userId + ", Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Account findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber).orElse(null);
    }

    @Override
    public void creditAccount(Long userId, BigDecimal amount, String description) {
        Account account = accountRepository.findByUserId(userId).orElse(null);
        if (account != null) {
            account.setBalance(account.getBalance().add(amount));
            accountRepository.save(account);

            // Create transaction record
            Transaction transaction = new Transaction();
            transaction.setAccount(account);
            transaction.setAmount(amount);
            transaction.setType("CREDIT");
            transaction.setDescription(description);
            transaction.setTimestamp(LocalDateTime.now());
            transactionRepository.save(transaction);
        }
    }

    @Override
    public void debitAccount(Long userId, BigDecimal amount, String description) {
        Account account = accountRepository.findByUserId(userId).orElse(null);
        if (account != null && account.getBalance().compareTo(amount) >= 0) {
            account.setBalance(account.getBalance().subtract(amount));
            accountRepository.save(account);

            // Create transaction record
            Transaction transaction = new Transaction();
            transaction.setAccount(account);
            transaction.setAmount(amount);
            transaction.setType("DEBIT");
            transaction.setDescription(description);
            transaction.setTimestamp(LocalDateTime.now());
            transactionRepository.save(transaction);
        }
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
}
