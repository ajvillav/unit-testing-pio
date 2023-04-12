package com.loan.payment.transformer;

import com.loan.payment.dto.LoanDto;
import com.loan.payment.model.Loan;

import java.util.List;
import java.util.stream.Collectors;

public class LoanTransformer {

    public static LoanDto transformLoanToDto(Loan loan) {
        LoanDto loanDto = new LoanDto();
        loanDto.setLoanId(loan.getLoanId());
        loanDto.setLoanOwner(loan.getLoanOwner());
        loanDto.setTotalAmount(loan.getTotalAmount());
        loanDto.setPendingAmount(loan.getPendingAmount());
        loanDto.setPaymentsNumber(loan.getPaymentsNumber());
        return loanDto;
    }

    public static List<LoanDto> transformLoanListToDto(List<Loan> loans) {
        return loans.stream()
                .map(LoanTransformer::transformLoanToDto)
                .collect(Collectors.toList());
    }

    public static Loan transformDtoToLoan(LoanDto loanDto) {
        Loan loan = new Loan();
        loan.setLoanId(loanDto.getLoanId());
        loan.setLoanOwner(loanDto.getLoanOwner());
        loan.setTotalAmount(loanDto.getTotalAmount());
        loan.setPendingAmount(loanDto.getPendingAmount());
        loan.setPaymentsNumber(loanDto.getPaymentsNumber());
        return loan;
    }
}
