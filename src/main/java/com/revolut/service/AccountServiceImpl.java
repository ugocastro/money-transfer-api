package com.revolut.service;

import com.revolut.domain.entities.Account;
import com.revolut.domain.repository.AccountDao;
import lombok.NonNull;

import java.math.BigDecimal;

public class AccountServiceImpl implements AccountService {

    private AccountDao accountDao;

    public AccountServiceImpl(@NonNull final AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public Account create(@NonNull final String owner) throws Exception {
        return this.accountDao.save(new Account(owner));
    }

    @Override
    public synchronized Account deposit(@NonNull final String accountNumber,
                                        @NonNull final BigDecimal amount) throws Exception {

        final Account account = this.accountDao.findById(accountNumber);
        account.deposit(amount);

        return this.accountDao.save(account);
    }

    @Override
    public synchronized Account withdraw(@NonNull final String accountNumber,
                                         @NonNull final BigDecimal amount) throws Exception {

        final Account account = this.accountDao.findById(accountNumber);
        account.withdraw(amount);

        return this.accountDao.save(account);
    }
}
