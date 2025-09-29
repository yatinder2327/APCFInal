package com.yourcompany.bankmanagement.repository;

import com.yourcompany.bankmanagement.data.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountId(Long accountId);
}




