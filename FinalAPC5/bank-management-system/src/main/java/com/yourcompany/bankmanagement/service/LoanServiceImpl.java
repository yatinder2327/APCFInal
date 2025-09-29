package com.yourcompany.bankmanagement.service;

import com.yourcompany.bankmanagement.data.Loan;
import com.yourcompany.bankmanagement.data.User;
import com.yourcompany.bankmanagement.repository.LoanRepository;
import com.yourcompany.bankmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanServiceImpl implements LoanServiceInterface {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Loan> getPendingLoans() {
        return loanRepository.findByStatus("PENDING");
    }

    @Override
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    @Override
    public List<Loan> getLoansByUserId(Long userId) {
        return loanRepository.findAll().stream()
                .filter(loan -> loan.getUser() != null && loan.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Loan applyForLoan(Long userId, Loan loan) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            loan.setUser(user);
            loan.setStatus("PENDING");
            return loanRepository.save(loan);
        }
        return null;
    }

    @Override
    public Loan approveLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElse(null);
        if (loan != null && "PENDING".equals(loan.getStatus())) {
            loan.setStatus("APPROVED");
            return loanRepository.save(loan);
        }
        return loan;
    }

    @Override
    public Loan denyLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElse(null);
        if (loan != null && "PENDING".equals(loan.getStatus())) {
            loan.setStatus("DENIED");
            return loanRepository.save(loan);
        }
        return loan;
    }

    @Override
    public Loan findById(Long loanId) {
        return loanRepository.findById(loanId).orElse(null);
    }
}




