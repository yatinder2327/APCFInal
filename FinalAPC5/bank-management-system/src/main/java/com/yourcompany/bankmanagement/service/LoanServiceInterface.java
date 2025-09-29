package com.yourcompany.bankmanagement.service;

import com.yourcompany.bankmanagement.data.Loan;
import java.util.List;

public interface LoanServiceInterface {
    List<Loan> getPendingLoans();
    List<Loan> getAllLoans();
    List<Loan> getLoansByUserId(Long userId);
    Loan applyForLoan(Long userId, Loan loan);
    Loan approveLoan(Long loanId);
    Loan denyLoan(Long loanId);
    Loan findById(Long loanId);
}




