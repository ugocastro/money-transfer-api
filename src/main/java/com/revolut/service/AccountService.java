package com.revolut.service;

import com.revolut.domain.entities.Account;
import lombok.NonNull;

import java.math.BigDecimal;

public interface AccountService {

    Account create(@NonNull final String owner) throws Exception;

    Account deposit(@NonNull final String accountNumber,
                    @NonNull final BigDecimal amount) throws Exception;

    Account withdraw(@NonNull final String accountNumber,
                     @NonNull final BigDecimal amount) throws Exception;
}
