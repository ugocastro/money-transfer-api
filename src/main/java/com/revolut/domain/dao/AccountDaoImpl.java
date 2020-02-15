package com.revolut.domain.dao;

import com.revolut.domain.entities.Account;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

import java.util.NoSuchElementException;

import static com.revolut.domain.repository.SessionFactory.getSessionFactory;

@Slf4j
public class AccountDaoImpl extends HibernateDao implements AccountDao {

    @Override
    public Account save(final Account account) throws Exception {
        return (Account) super.save(account);
    }

    @Override
    public Account getByNumber(final String accountNumber) throws Exception {
        Session session = null;
        Account account;
        try {
            session = getSessionFactory().openSession();
            account = session.get(Account.class, accountNumber);
        } catch (Exception e) {
            log.error("Error retrieving information from database", e.getMessage());

            if (session != null) {
                session.close();
            }

            throw new Exception("Error retrieving information from database");
        }

        if (account == null) {
            throw new NoSuchElementException("Account not found for given number");
        }

        return account;
    }
}
