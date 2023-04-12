package com.loan.payment.service;

import com.loan.payment.model.Account;
import com.loan.payment.model.Transaction;
import com.loan.payment.repository.AccountRepository;
import com.loan.payment.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;


    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Transaction makeTransaction(UUID sourceAccountNumber, UUID destinationAccountNumber, int amount) {
        Optional<Account> sourceAccount = accountRepository.findById(sourceAccountNumber);
        Optional<Account> destinationAccount = accountRepository.findById(destinationAccountNumber);
        Transaction transaction = createTransaction(sourceAccountNumber, destinationAccountNumber, amount);

        if (sourceAccount.isEmpty()) {
            throw new IllegalArgumentException("Source account does not exist!");
        }

        if (destinationAccount.isEmpty()) {
            throw new IllegalArgumentException("Destination account does not exist!");
        }

        if (sourceAccount.get().getAccountBalance() < amount) {
            throw new IllegalArgumentException("Source account does not have enough balance!");
        }

        sourceAccount.get().setAccountBalance(sourceAccount.get().getAccountBalance() - amount);
        destinationAccount.get().setAccountBalance(destinationAccount.get().getAccountBalance() + amount);

        accountRepository.save(sourceAccount.get());
        accountRepository.save(destinationAccount.get());
        transactionRepository.save(transaction);

        return getTransaction(transaction.getTransactionId());
    }

    public Transaction getTransaction(UUID transactionID) {
        Optional<Transaction> transaction = transactionRepository.findById(transactionID);
        if(transaction.isEmpty()) {
            throw new NoSuchElementException("Transaction not found!");
        }
        return transaction.get();
    }

    private Transaction createTransaction(UUID originAccount, UUID destinationAccount, int amount) {
        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(amount);
        transaction.setOriginAccount(originAccount);
        transaction.setDestinationAccount(destinationAccount);
        return transaction;
    }


}
