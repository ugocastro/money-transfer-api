package com.revolut.domain.repository;

import com.revolut.domain.entities.Account;
import com.revolut.domain.entities.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransactionDaoTest {

    private static TransactionDao transactionDao;

    @BeforeAll
    static void beforeAll() {
        transactionDao = new TransactionDao();
    }

    @AfterAll
    static void afterAll() {
        transactionDao = null;
    }

    @Test
    public void testSaveTransactionShouldRaiseErrorIfOriginAccountDoesNotExist() {
        assertThrows(Exception.class,
            () -> this.transactionDao.save(new Transaction(new Account(), new Account(), ONE)),
            "Origin account must exist");
    }

    @Test
    public void testSaveTransactionShouldRaiseErrorIfDestinationAccountDoesNotExist() throws Exception {
        final AccountDao accountDao = new AccountDao();
        final Account savedAccount = accountDao.save(new Account());
        assertThrows(Exception.class,
            () -> this.transactionDao.save(new Transaction(savedAccount, new Account(), ONE)),
            "Destination account must exist");
    }

    @Test
    public void testSaveTransactionShouldCreateEntryOnDatabase() throws Exception {
        final AccountDao accountDao = new AccountDao();
        final Account originAccount = accountDao.save(new Account());
        final Account destinationAccount = accountDao.save(new Account());
        final Transaction newTransaction = new Transaction(originAccount, destinationAccount, ONE);
        this.transactionDao.save(newTransaction);
        final Transaction savedTransaction = this.transactionDao.findById(newTransaction.getId());

        assertNotNull(savedTransaction, "Saved transaction must be retrieved from database");
    }

    @Test
    public void testGetTransactionByIdentifierShouldRaiseErrorIfNotFound() {
        assertThrows(NoSuchElementException.class,
            () -> this.transactionDao.findById(randomUUID().toString()),
            "Transaction must not be found for nonexistent identifier");
    }

    @Test
    public void testGetTransactionByIdentifierShouldRetrieveData() throws Exception {
        final AccountDao accountDao = new AccountDao();
        final Account originAccount = accountDao.save(new Account());
        final Account destinationAccount = accountDao.save(new Account());
        final Transaction newTransaction1 = new Transaction(originAccount, destinationAccount, ONE);
        final Transaction newTransaction2 = new Transaction(originAccount, destinationAccount, TEN);
        this.transactionDao.save(newTransaction1);
        this.transactionDao.save(newTransaction2);
        final Transaction retrievedTransaction = this.transactionDao.findById(newTransaction1.getId());

        assertNotNull(retrievedTransaction, "Saved transaction must be retrieved from database");
        assertEquals(newTransaction1, retrievedTransaction, "Transactions must be the same");
        assertNotEquals(newTransaction2, retrievedTransaction, "Transactions must be different");
    }
}
