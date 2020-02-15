package com.revolut.domain.dao;

import com.revolut.domain.entities.Account;

public interface AccountDao {

    Account save(final Account account) throws Exception;

    Account getByNumber(final String accountNumber) throws Exception;
}
