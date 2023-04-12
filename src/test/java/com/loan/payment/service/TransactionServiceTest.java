package com.loan.payment.service;

import com.loan.payment.model.Account;
import com.loan.payment.model.Transaction;
import com.loan.payment.repository.AccountRepository;
import com.loan.payment.repository.TransactionRepository;
import com.loan.payment.service.exception.LoanException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Transaction Service Test")
public class TransactionServiceTest {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private TransactionService transactionService;

    @BeforeEach
    void init() {
        accountRepository = mock(AccountRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        transactionService = new TransactionService(accountRepository, transactionRepository);
    }

    @Test
    @DisplayName("LoanTransaction...")
    void makeTransactionWorksProperly() {
        //Given
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        UUID transactionToSaveId = UUID.randomUUID();

        Account senderAccount = new Account();
        senderAccount.setAccountNumber(senderId);
        senderAccount.setAccountOwner(1020453685);
        senderAccount.setAccountBalance(2500);

        Account receiverAccount = new Account();
        receiverAccount.setAccountNumber(receiverId);
        receiverAccount.setAccountOwner(1020453686);
        receiverAccount.setAccountBalance(500);

        Transaction transactionToSave = new Transaction();
        transactionToSave.setTransactionId(transactionToSaveId);
        transactionToSave.setOriginAccount(senderId);
        transactionToSave.setDestinationAccount(receiverId);
        transactionToSave.setTransactionAmount(200);


        when(accountRepository.findById(senderId)).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findById(receiverId)).thenReturn(Optional.of(receiverAccount));

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            var argument = invocation.getArgument(0, Transaction.class);
            argument.setTransactionId(transactionToSaveId);
            return argument;
        });

        when(transactionRepository.findById(transactionToSaveId)).thenReturn(Optional.of(transactionToSave));

        //When
        Transaction savedTransaction = transactionService.makeTransaction(senderId, receiverId, 100);

        //Then
        assertNotNull(savedTransaction);
        assertEquals(transactionToSave, savedTransaction);
    }



}
