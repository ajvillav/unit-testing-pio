package com.loan.payment.service;

import com.loan.payment.dto.LoanDto;
import com.loan.payment.model.Account;
import com.loan.payment.model.Loan;
import com.loan.payment.model.User;
import com.loan.payment.repository.AccountRepository;
import com.loan.payment.repository.LoanRepository;
import com.loan.payment.repository.UsersRepository;
import com.loan.payment.service.exception.AccountException;
import com.loan.payment.service.exception.LoanException;
import com.loan.payment.service.exception.UserException;
import com.loan.payment.transformer.LoanTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Loan Service Unit Tests")
public class LoanServiceTest {

    private LoanRepository loanRepository;
    private UsersRepository usersRepository;
    private AccountRepository accountRepository;

    private LoanService loanService;

    @BeforeEach
    void init() {
        loanRepository = mock(LoanRepository.class);
        usersRepository = mock(UsersRepository.class);
        accountRepository = mock(AccountRepository.class);
        loanService = new LoanService(loanRepository, usersRepository, accountRepository);
    }

    @Test
    @DisplayName("requestLoan - Must return loan dto from created loan in DB and apply all validations")
    void requestLoanAppliesAllValidationsAndCreateLoan() throws AccountException, LoanException, UserException {
        //Given
        int loanOwner = 1020486382;

        LoanDto loanDto = new LoanDto();
        loanDto.setLoanOwner(loanOwner);
        loanDto.setTotalAmount(1000);
        loanDto.setPendingAmount(800);
        loanDto.setPaymentsNumber(7);

        UUID userAccountId = UUID.randomUUID();
        User user = new User();
        user.setUserName("Andres Villa");
        user.setUserAccount(userAccountId);

        Account userAccount = new Account();
        userAccount.setAccountNumber(user.getUserAccount());
        userAccount.setAccountOwner(loanOwner);
        userAccount.setAccountBalance(500);

        Loan loanToSave = LoanTransformer.transformDtoToLoan(loanDto);

        when(usersRepository.findById(loanOwner)).thenReturn(Optional.of(user));
        when(accountRepository.findByAccountOwner(loanOwner)).thenReturn(userAccount);
        when(loanRepository.save(loanToSave)).thenReturn(loanToSave);

        //When
        LoanDto savedLoanDto = loanService.requestLoan(loanDto);

        //Then
        assertNotNull(savedLoanDto);
        assertAll(
                () -> assertEquals(loanDto.getLoanId(), savedLoanDto.getLoanId()),
                () -> assertEquals(loanDto.getLoanOwner(), savedLoanDto.getLoanOwner()),
                () -> assertEquals(loanDto.getTotalAmount(), savedLoanDto.getTotalAmount()),
                () -> assertEquals(loanDto.getPendingAmount(), savedLoanDto.getPendingAmount()),
                () -> assertEquals(loanDto.getPaymentsNumber(), savedLoanDto.getPaymentsNumber())
        );
    }

    @Test
    @DisplayName("requestLoan - Must return loan dto from created loan in DB and apply all validations")
    void requestLoanAppliesAllValidationsAndCreateLoan1() throws AccountException, LoanException, UserException {
        //Given
        int loanOwner = 1020486382;

        LoanDto loanDto = new LoanDto();
        loanDto.setLoanOwner(loanOwner);
        loanDto.setTotalAmount(1000);
        loanDto.setPendingAmount(800);
        loanDto.setPaymentsNumber(3);

        UUID userAccountId = UUID.randomUUID();
        User user = new User();
        user.setUserName("Andres Villa");
        user.setUserAccount(userAccountId);

        Account userAccount = new Account();
        userAccount.setAccountNumber(user.getUserAccount());
        userAccount.setAccountOwner(loanOwner);
        userAccount.setAccountBalance(500);

        Loan loanToSave = LoanTransformer.transformDtoToLoan(loanDto);
        loanToSave.setPendingAmount((int)(loanToSave.getTotalAmount()*0.9));

        when(usersRepository.findById(loanOwner)).thenReturn(Optional.of(user));
        when(accountRepository.findByAccountOwner(loanOwner)).thenReturn(userAccount);
        when(loanRepository.save(loanToSave)).thenReturn(loanToSave);

        //When
        LoanDto savedLoanDto = loanService.requestLoan(loanDto);

        //Then
        assertNotNull(savedLoanDto);
        assertAll(
                () -> assertEquals(loanDto.getLoanId(), savedLoanDto.getLoanId()),
                () -> assertEquals(loanDto.getLoanOwner(), savedLoanDto.getLoanOwner()),
                () -> assertEquals(loanDto.getTotalAmount(), savedLoanDto.getTotalAmount()),
                () -> assertEquals((int)(0.9*loanDto.getTotalAmount()), savedLoanDto.getPendingAmount()),
                () -> assertEquals(loanDto.getPaymentsNumber(), savedLoanDto.getPaymentsNumber())
        );
    }

    @Test
    @DisplayName("requestLoan - Must return loan dto from created loan in DB and apply all validations")
    void requestLoanAppliesAllValidationsAndCreateLoan2() {
        //Given
        LoanDto loanDto = new LoanDto();
        loanDto.setLoanOwner(1020486382);
        loanDto.setTotalAmount(1000);
        loanDto.setPendingAmount(800);
        loanDto.setPaymentsNumber(7);

        when(usersRepository.findById(1020486382)).thenReturn(Optional.empty());

        //When-Then
        var exception = assertThrows(UserException.class, () -> loanService.requestLoan(loanDto));

        assertEquals("Loan requester does not have an account!", exception.getMessage());

    }

    @Test
    @DisplayName("makeLoanPayment - Must update loan pendingAmount and accountBalance after make the payment")
    void makeLoanPaymentMustUpdateLoanAndAccountWhenEverythingIsRight() throws LoanException {
        //Given
        int paymentAmount = 100;

        //Create loan and account objects
        int loanOwner = 1020486382;

        Loan loan = new Loan();
        loan.setLoanId(UUID.randomUUID());
        loan.setLoanOwner(loanOwner);
        loan.setTotalAmount(1000);
        loan.setPendingAmount(800);
        loan.setPaymentsNumber(7);

        UUID userAccountId = UUID.randomUUID();
        User user = new User();
        user.setUserName("Andres Villa");
        user.setUserAccount(userAccountId);

        Account userAccount = new Account();
        userAccount.setAccountNumber(user.getUserAccount());
        userAccount.setAccountOwner(loanOwner);
        userAccount.setAccountBalance(500);

        //Create spies to verify that the loan and account objects were updated correctly
        Loan spyLoan = spy(loan);
        Account spyAccount = spy(userAccount);

        //Set up the mocks to return the loan and account objects when called
        when(loanRepository.findById(loan.getLoanId())).thenReturn(Optional.of(spyLoan));
        when(accountRepository.findByAccountOwner(loan.getLoanOwner())).thenReturn(spyAccount);

        //When
        loanService.makeLoanPayment(loan.getLoanId(), paymentAmount);

        //Then
        verify(spyLoan).setPendingAmount(700);
        verify(spyLoan).setPaymentsNumber(6);
        verify(loanRepository).save(spyLoan);

        verify(spyAccount).setAccountBalance(400);
        verify(accountRepository).save(spyAccount);
    }

    @Test
    @DisplayName("makeLoanPayment - Must update loan pendingAmount and accountBalance after make the payment")
    void makeLoanPaymentMustUpdateLoanAndAccountWhenEverythingIsRightWithCaptor() throws LoanException {
        //Given
        int paymentAmount = 100;
        int loanOwner = 1020486382;

        Loan loan = new Loan();
        loan.setLoanId(UUID.randomUUID());
        loan.setLoanOwner(loanOwner);
        loan.setTotalAmount(1000);
        loan.setPendingAmount(800);
        loan.setPaymentsNumber(7);

        UUID userAccountId = UUID.randomUUID();
        User user = new User();
        user.setUserName("Andres Villa");
        user.setUserAccount(userAccountId);

        Account userAccount = new Account();
        userAccount.setAccountNumber(user.getUserAccount());
        userAccount.setAccountOwner(loanOwner);
        userAccount.setAccountBalance(500);

        ArgumentCaptor<Loan> argumentCaptorLoan = ArgumentCaptor.forClass(Loan.class);
        ArgumentCaptor<Account> argumentCaptorAccount = ArgumentCaptor.forClass(Account.class);

        //Set up the mocks to return the loan and account objects when called
        when(loanRepository.findById(loan.getLoanId())).thenReturn(Optional.of(loan));
        when(accountRepository.findByAccountOwner(loan.getLoanOwner())).thenReturn(userAccount);

        //When
        loanService.makeLoanPayment(loan.getLoanId(), paymentAmount);

        //Then
        verify(loanRepository).save(argumentCaptorLoan.capture());
        verify(accountRepository).save(argumentCaptorAccount.capture());

        assertAll(
                () -> assertEquals(700, argumentCaptorLoan.getValue().getPendingAmount()),
                () -> assertEquals(6, argumentCaptorLoan.getValue().getPaymentsNumber()),
                () -> assertEquals(400, argumentCaptorAccount.getValue().getAccountBalance())
        );
    }


    @Test
    @DisplayName("makeLoanPayment - Must update loan pendingAmount and accountBalance after make the payment")
    void makeLoanPaymentMustUpdateLoanAndAccountWhenEverythingIsRightWithAnswer() throws LoanException {
        //Given
        int paymentAmount = 100;
        int loanOwner = 1020486382;

        Loan loan = new Loan();
        loan.setLoanId(UUID.randomUUID());
        loan.setLoanOwner(loanOwner);
        loan.setTotalAmount(1000);
        loan.setPendingAmount(800);
        loan.setPaymentsNumber(7);

        UUID userAccountId = UUID.randomUUID();
        User user = new User();
        user.setUserName("Andres Villa");
        user.setUserAccount(userAccountId);

        Account userAccount = new Account();
        userAccount.setAccountNumber(user.getUserAccount());
        userAccount.setAccountOwner(loanOwner);
        userAccount.setAccountBalance(500);

        //Set up the mocks to return the loan and account objects when called
        when(loanRepository.findById(loan.getLoanId())).thenReturn(Optional.of(loan));
        when(accountRepository.findByAccountOwner(loan.getLoanOwner())).thenReturn(userAccount);

        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> {
            var argument = invocation.getArgument(0, Loan.class);
            assertEquals(700, argument.getPendingAmount());
            assertEquals(6, argument.getPaymentsNumber());
            return argument;
        });

        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            var argument = invocation.getArgument(0, Account.class);
            assertEquals(400, argument.getAccountBalance());
            return argument;
        });

        //When
        loanService.makeLoanPayment(loan.getLoanId(), paymentAmount);

        //Then
        verify(loanRepository).save(any(Loan.class));
        verify(accountRepository).save(any(Account.class));
    }
}
