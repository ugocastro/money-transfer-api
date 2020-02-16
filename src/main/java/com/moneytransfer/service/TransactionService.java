package com.moneytransfer.service;

import com.moneytransfer.domain.entities.Transaction;
import lombok.NonNull;

import java.math.BigDecimal;

public interface TransactionService {

    Transaction transfer(@NonNull final String originAccountNumber,
                         @NonNull final String destinationAccountNumber,
                         @NonNull final BigDecimal amount) throws Exception;
}
