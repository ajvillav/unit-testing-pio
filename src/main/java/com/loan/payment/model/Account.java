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
@Table(name = "ACCOUNT")
@Entity
public class Account {

    @Id
    @Column(name = "account_number")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID accountNumber = UUID.randomUUID();

    @Column(name = "account_owner")
    private Integer accountOwner;

    @Column(name = "account_balance")
    private Integer accountBalance;
}
