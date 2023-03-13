package com.loan.payment.repository;

import com.loan.payment.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Account findByAccountOwner(Integer accountOwner);
}
