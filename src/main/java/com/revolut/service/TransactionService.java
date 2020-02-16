package com.revolut.service;

import com.revolut.domain.entities.Transaction;
import lombok.NonNull;

import java.math.BigDecimal;

public interface TransactionService {

    Transaction transfer(@NonNull final String originAccountNumber,
                         @NonNull final String destinationAccountNumber,
                         @NonNull final BigDecimal amount) throws Exception;
}
