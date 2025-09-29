package com.yourcompany.bankmanagement.repository;

import com.yourcompany.bankmanagement.data.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByStatus(String status);
}




