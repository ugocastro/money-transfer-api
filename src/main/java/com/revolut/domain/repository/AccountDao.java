package com.revolut.domain.repository;

import com.revolut.domain.entities.Account;
import lombok.extern.slf4j.Slf4j;

import java.util.NoSuchElementException;

@Slf4j
public class AccountDao extends HibernateDao<Account> {

    public Account save(final Account account) throws Exception {
        return super.save(account);
    }

    public Account findById(final String accountNumber) throws Exception {
        Account account = super.findById(accountNumber);

        if (account == null) {
            throw new NoSuchElementException("Account not found for given number");
        }

        return account;
    }
}
