package com.moneytransfer.service;

import com.moneytransfer.domain.entities.Account;
import com.moneytransfer.domain.entities.Transaction;
import com.moneytransfer.domain.repository.AccountDao;
import com.moneytransfer.domain.repository.TransactionDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static com.moneytransfer.utils.Constants.DECIMAL_PLACES;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransactionServiceTest {

    private AccountDao accountDao;
    private TransactionDao transactionDao;
    private TransactionService transactionService;

    @BeforeEach
    void beforeEach() {
        this.accountDao = mock(AccountDao.class);
        this.transactionDao = mock(TransactionDao.class);
        this.transactionService = new TransactionServiceImpl(this.accountDao, this.transactionDao);
    }

    @Test
    public void testTransferShouldRaiseErrorIfOriginAccountNumberIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> transactionService.transfer(null, randomUUID().toString(), ONE),
            "Origin account number must be non-null");
    }

    @Test
    public void testTransferShouldRaiseErrorIfDestinationAccountNumberIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> transactionService.transfer(randomUUID().toString(), null, ONE),
            "Destination account number must be non-null");
    }

    @Test
    public void testTransferShouldRaiseErrorIfAmountIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> transactionService.transfer(randomUUID().toString(), randomUUID().toString(), null),
            "Transfer amount must be non-null");
    }

    @Test
    public void testTransferShouldRaiseErrorIfOriginAccountIsNotFound() throws Exception {
        final String nonExistentAccountNumber = randomUUID().toString();
        when(this.accountDao.findById(nonExistentAccountNumber)).thenThrow(new NoSuchElementException());

        assertThrows(NoSuchElementException.class,
            () -> transactionService.transfer(nonExistentAccountNumber, randomUUID().toString(), ONE),
            "Origin account must exist");
    }

    @Test
    public void testTransferShouldRaiseErrorIfDestinationAccountIsNotFound() throws Exception {
        final Account origin = new Account("John Doe");
        final String destinationAccountNumber = randomUUID().toString();

        when(this.accountDao.findById(origin.getNumber())).thenReturn(origin);
        when(this.accountDao.findById(destinationAccountNumber)).thenThrow(new NoSuchElementException());

        assertThrows(NoSuchElementException.class,
            () -> transactionService.transfer(randomUUID().toString(), destinationAccountNumber, ONE),
            "Destination account must exist");
    }

    @Test
    public void testTransferShouldRaiseErrorIfOriginAndDestinationAccountsAreSame() throws Exception {
        final Account origin = new Account("John Doe");

        when(this.accountDao.findById(origin.getNumber())).thenReturn(origin);

        assertThrows(IllegalArgumentException.class,
            () -> transactionService.transfer(origin.getNumber(), origin.getNumber(), ONE),
            "Accounts must be different");
    }

    @Test
    public void testTransferShouldRaiseErrorIfAmountIsNegative() throws Exception {
        final Account origin = new Account("John Doe");
        origin.deposit(TEN);
        final Account destination = new Account("Joseph Doe");

        when(this.accountDao.findById(origin.getNumber())).thenReturn(origin);
        when(this.accountDao.findById(destination.getNumber())).thenReturn(destination);

        assertThrows(IllegalArgumentException.class,
            () -> transactionService.transfer(origin.getNumber(), destination.getNumber(), new BigDecimal(-10d)),
            "Transfer amount must be greater than zero");
    }

    @Test
    public void testTransferShouldRaiseErrorIfAmountIsZero() throws Exception {
        final Account origin = new Account("John Doe");
        origin.deposit(TEN);
        final Account destination = new Account("Joseph Doe");

        when(this.accountDao.findById(origin.getNumber())).thenReturn(origin);
        when(this.accountDao.findById(destination.getNumber())).thenReturn(destination);

        assertThrows(IllegalArgumentException.class,
            () -> transactionService.transfer(origin.getNumber(), destination.getNumber(), ZERO),
            "Transfer amount must be greater than zero");
    }

    @Test
    public void testTransferShouldRaiseErrorIfOriginAccountHasInsufficientBalance() throws Exception {
        final Account origin = new Account("John Doe");
        final Account destination = new Account("Joseph Doe");

        when(this.accountDao.findById(origin.getNumber())).thenReturn(origin);
        when(this.accountDao.findById(destination.getNumber())).thenReturn(destination);

        assertThrows(IllegalArgumentException.class,
            () -> transactionService.transfer(origin.getNumber(), destination.getNumber(), ONE),
            "Origin account must have sufficient balance");
    }

    @Test
    public void testTransferShouldWithdrawFromOriginAccountAndDepositOnDestination() throws Exception {
        final Account origin = new Account("John Doe");
        origin.deposit(TEN);
        final Account destination = new Account("Joseph Doe");
        final Transaction transaction = new Transaction(origin, destination, ONE);

        when(this.accountDao.findById(origin.getNumber())).thenReturn(origin);
        when(this.accountDao.findById(destination.getNumber())).thenReturn(destination);

        transaction.transfer();

        assertEquals(TEN.subtract(ONE).setScale(DECIMAL_PLACES, HALF_UP), origin.getBalance(),
            "Origin account balance should be decreased by amount");
        assertEquals(ZERO.add(ONE).setScale(DECIMAL_PLACES, HALF_UP), destination.getBalance(),
            "Destination account balance should be increased by amount");
    }
}
