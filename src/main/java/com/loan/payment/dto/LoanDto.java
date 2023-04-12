package com.loan.payment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class LoanDto {
    private UUID loanId;
    private Integer loanOwner;
    private Integer totalAmount;
    private Integer pendingAmount;
    private Integer paymentsNumber;
}
