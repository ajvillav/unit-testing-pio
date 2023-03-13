package com.loan.payment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class AccountDto {
    private UUID accountDtoNumber;
    private Integer accountDtoBalance;
}
