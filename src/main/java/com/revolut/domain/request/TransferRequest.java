package com.revolut.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransferRequest {

    private String originAccountNumber;

    private String destinationAccountNumber;

    private BigDecimal amount;
}
