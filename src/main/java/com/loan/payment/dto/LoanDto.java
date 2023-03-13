package com.loan.payment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoanDto {
    private Integer loanOwner;
    private Integer totalAmount;
    private Integer pendingAmount;
    private Integer paymentsNumber;
}
