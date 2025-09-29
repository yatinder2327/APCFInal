package com.yourcompany.bankmanagement.service;

import com.yourcompany.bankmanagement.data.Account;
import com.yourcompany.bankmanagement.data.Transaction;
import java.math.BigDecimal;
import java.util.List;

public interface AccountServiceInterface {
    BigDecimal getAccountBalance(Long accountId);
    BigDecimal getAccountBalanceByUserId(Long userId);
    String getAccountNumberByUserId(Long userId);
    List<Transaction> getTransactionHistory(Long accountId);
    List<Transaction> getTransactionHistoryByUserId(Long userId);
    Account findByUserId(Long userId);
    Account findByAccountNumber(String accountNumber);
    void creditAccount(Long userId, BigDecimal amount, String description);
    void debitAccount(Long userId, BigDecimal amount, String description);
    List<Account> getAllAccounts();
}
