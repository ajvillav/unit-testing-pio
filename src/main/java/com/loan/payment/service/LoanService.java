package com.loan.payment.service;

import com.loan.payment.dto.LoanDto;
import com.loan.payment.service.exception.AccountException;
import com.loan.payment.service.exception.LoanException;
import com.loan.payment.service.exception.UserException;
import com.loan.payment.model.Account;
import com.loan.payment.model.Loan;
import com.loan.payment.repository.AccountRepository;
import com.loan.payment.repository.LoanRepository;
import com.loan.payment.repository.UsersRepository;
import com.loan.payment.transformer.LoanTransformer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final UsersRepository usersRepository;
    private final AccountRepository accountRepository;
    private static final double MINIMUM_PERCENTAGE = 0.3;
    private static final double DISCOUNT = 0.9;

    public LoanService(LoanRepository loanRepository, UsersRepository userRepository, AccountRepository accountRepository) {
        this.loanRepository = loanRepository;
        this.usersRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    public void bulkLoanDeletion() {
        CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
                loanRepository.deleteAll();
                System.out.println("DELETED");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public List<LoanDto> getAllLoans() throws LoanException {
        List<Loan> loanList = loanRepository.findAll();

        if(loanList.isEmpty()) {
            throw new LoanException("No loans found!");
        }

        return LoanTransformer.transformLoanListToDto(loanList);
    }

    public LoanDto requestLoan(LoanDto loanDto) throws LoanException, AccountException, UserException {
        // Validate that conditions for request a loan are right
        executeLoanRequestValidations(loanDto);

        // Validate if user exists
        Integer requesterId = loanDto.getLoanOwner();
        usersRepository.findById(requesterId).orElseThrow(
                () -> new UserException("Loan requester does not have an account!"));

        // Validate account liquidity
        executeAccountValidation(loanDto, requesterId);

        Loan loanToSave = LoanTransformer.transformDtoToLoan(loanDto);

        // Validate number of payments (benefits)
        if(loanDto.getPaymentsNumber() <= 3) {
            loanToSave.setPendingAmount((int)(loanToSave.getTotalAmount() * DISCOUNT));
        }

        return LoanTransformer.transformLoanToDto(loanRepository.save(loanToSave));
    }

    private void executeLoanRequestValidations(LoanDto loanDto) throws LoanException {
        Integer paymentsNumber = loanDto.getPaymentsNumber();
        if(paymentsNumber < 1 || paymentsNumber > 12) {
            throw new LoanException("Only installment loans of 1 to 12 months are allowed!");
        }

        if (loanDto.getTotalAmount() <= 0) {
            throw new LoanException("A loan equal to or less than zero dollars cannot be made!");
        }
    }

    private void executeAccountValidation(LoanDto loanDto, Integer requesterId) throws AccountException {
        Account requesterAccount = accountRepository.findByAccountOwner(requesterId);
        if(requesterAccount.getAccountBalance() < MINIMUM_PERCENTAGE * loanDto.getTotalAmount()){
            throw new AccountException("The balance in your account is insufficient to qualify for the loan!");
        }
    }

    /**
     * Method for making a single payment of a loan
     * @param loanId
     * @param paymentAmount
     * @throws LoanException
     */
    @Transactional
    public void makeLoanPayment(UUID loanId, int paymentAmount) throws LoanException {
        Optional<Loan> optionalLoan = loanRepository.findById(loanId);
        if(optionalLoan.isEmpty()) {
            throw new LoanException("Loan does not exist!");
        }

        // Set loan updated information
        Loan loan = optionalLoan.get();
        loan.setPendingAmount(loan.getPendingAmount() - paymentAmount);
        loan.setPaymentsNumber(loan.getPaymentsNumber() - 1);

        // Set account new balance after payment
        Account account = accountRepository.findByAccountOwner(loan.getLoanOwner());
        int newBalance = account.getAccountBalance() - paymentAmount;
        account.setAccountBalance(newBalance);

        loanRepository.save(loan);
        accountRepository.save(account);
    }
}
