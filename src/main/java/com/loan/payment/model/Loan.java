package com.loan.payment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "LOAN")
public class Loan {

    @Id
    @Column(name = "loan_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID loanId;

    @Column(name = "loan_owner")
    private Integer loanOwner;

    @Column(name = "total_amount")
    private Integer totalAmount;

    @Column(name = "pending_amount")
    private Integer pendingAmount;

    @Column(name = "payments_number")
    private Integer paymentsNumber;
}
