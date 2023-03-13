package com.loan.payment.service;

import com.loan.payment.dto.LoanDto;
import com.loan.payment.service.exception.AccountException;
import com.loan.payment.service.exception.LoanException;
import com.loan.payment.service.exception.UserException;
import com.loan.payment.model.Account;
import com.loan.payment.model.Loan;
import com.loan.payment.model.User;
import com.loan.payment.repository.AccountRepository;
import com.loan.payment.repository.LoanRepository;
import com.loan.payment.repository.UsersRepository;
import com.loan.payment.transformer.LoanTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
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
}
