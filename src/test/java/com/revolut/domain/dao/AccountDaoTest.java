package com.revolut.domain.dao;

import com.revolut.domain.entities.Account;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountDaoTest {

    private static AccountDao accountDao;

    @BeforeAll
    static void beforeAll() {
        accountDao = new AccountDaoImpl();
    }

    @AfterAll
    static void afterAll() {
        accountDao = null;
    }

    @Test
    public void testSaveAccountShouldCreateAccount() throws Exception {
        final Account newAccount = new Account();
        this.accountDao.save(newAccount);
        final Account savedAccount = this.accountDao.getByNumber(newAccount.getNumber());

        assertNotNull(savedAccount, "Saved account must be retrieved from database");
    }

    @Test
    public void testGetAccountByNumberShouldRaiseErrorIfNotFound() {
        assertThrows(NoSuchElementException.class,
            () -> this.accountDao.getByNumber(UUID.randomUUID().toString()),
            "Account must not be found for nonexistent number");
    }

    @Test
    public void testGetAccountByNumberShouldRetrieveAccount() throws Exception {
        final Account newAccount1 = new Account();
        final Account newAccount2 = new Account();
        this.accountDao.save(newAccount1);
        this.accountDao.save(newAccount2);
        final Account retrievedAccount = this.accountDao.getByNumber(newAccount1.getNumber());

        assertNotNull(retrievedAccount, "Saved account must be retrieved from database");
        assertEquals(newAccount1, retrievedAccount, "Accounts must be the same");
    }
}