package com.moneytransfer.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class UpdateBalanceRequest {

    private BigDecimal amount;
}
