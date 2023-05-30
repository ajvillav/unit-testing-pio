package com.loan.payment.controller;

import com.loan.payment.dto.LoanDto;
import com.loan.payment.repository.LoanRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoanControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int port;

    @Test
    @DisplayName("getAllLoanDtos - Must return 200 and product dto list of given brand")
    public void shouldReturn200WhenSendingRequestToGetAllLoans() {
        //Given-When
        ResponseEntity<List> response = this.testRestTemplate.getForEntity(
                "http://localhost:" + port + "/loans", List.class);

        //Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertAll(
                () -> assertEquals(1, response.getBody().size()),
                () -> assertEquals(HttpStatus.OK, response.getStatusCode())
        );
    }

    @Test
    @DisplayName("requestLoan - Must return 200 and loanDto with given data")
    public void shouldReturn200WhenSendingRequestToRequestLoan() {
        //Given
        LoanDto loanDto = new LoanDto();
        loanDto.setLoanOwner(1020486382);
        loanDto.setTotalAmount(1000);
        loanDto.setPendingAmount(1000);
        loanDto.setPaymentsNumber(4);

        //When
        ResponseEntity<LoanDto> response = this.testRestTemplate.postForEntity(
                "http://localhost:" + port + "/loans", loanDto, LoanDto.class);

        //Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody().getLoanId()),
                () -> assertEquals(loanDto.getLoanOwner(), response.getBody().getLoanOwner()),
                () -> assertEquals(loanDto.getTotalAmount(), response.getBody().getTotalAmount()),
                () -> assertEquals(loanDto.getPendingAmount(), response.getBody().getPendingAmount()),
                () -> assertEquals(loanDto.getPaymentsNumber(), response.getBody().getPaymentsNumber())
        );
    }

    @Test
    @DisplayName("requestLoan - Must return 400 when user is not registered in DB")
    public void shouldReturn400WhenSendingRequestToRequestLoan1() {
        //Given
        LoanDto loanDto = new LoanDto();
        loanDto.setLoanOwner(1030486382);
        loanDto.setTotalAmount(1000);
        loanDto.setPendingAmount(1000);
        loanDto.setPaymentsNumber(4);

        //When
        ResponseEntity<LoanDto> response = this.testRestTemplate.postForEntity(
                "http://localhost:" + port + "/loans", loanDto, LoanDto.class);

        //Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
