package com.loan.payment.transformer;

import com.loan.payment.dto.LoanDto;
import com.loan.payment.model.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Loan Transformer Test class")
public class LoanTransformerTest {

    @Test
    @DisplayName("transformLoanToDto - Must return dto from loan with the same original values")
    void transformLoanToDtoHasTheCorrectValuesWhenTransfromLoanToDto() {
        //Given  Arrange
        Loan loan = new Loan();
        loan.setLoanId(UUID.randomUUID());
        loan.setTotalAmount(1200);
        loan.setLoanOwner(1020486382);
        loan.setPendingAmount(1000);
        loan.setPaymentsNumber(7);

        //When  Act
        LoanDto loanDto = LoanTransformer.transformLoanToDto(loan);

        //Then  Assert
        assertNotNull(loanDto);
        assertAll(
                () -> assertEquals(loan.getLoanOwner(), loanDto.getLoanOwner()),
                () -> assertEquals(loan.getTotalAmount(), loanDto.getTotalAmount()),
                () -> assertEquals(loan.getPendingAmount(), loanDto.getPendingAmount()),
                () -> assertEquals(loan.getPaymentsNumber(), loanDto.getPaymentsNumber())
        );
    }


}
