package com.revolut.domain.entities;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.revolut.utils.Constants.DECIMAL_PLACES;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransactionTest {

    @Test
    public void testNewTransactionShouldRaiseErrorIfOriginAccountIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new Transaction(null, new Account(), ONE),
            "Origin account must be non-null");
    }

    @Test
    public void testNewTransactionShouldRaiseErrorIfDestinationAccountIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new Transaction(new Account(), null, ONE),
            "Destination account must be non-null");
    }

    @Test
    public void testNewTransactionShouldRaiseErrorIfAmountIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new Transaction(new Account(), new Account(), null),
            "Transaction amount must be non-null");
    }

    @Test
    public void testNewTransactionShouldHaveAnIdentifierAndDate() {
        final Transaction transaction = new Transaction(new Account(), new Account(), ONE);
        assertNotNull(transaction.getId(), "Transaction must have an identifier");
        assertNotNull(transaction.getDate(), "Transaction must have a date");
    }

    @Test
    public void testTransferShouldRaiseErrorIfAmountIsNegative() {
        assertThrows(IllegalArgumentException.class,
            () -> new Transaction(new Account(), new Account(), new BigDecimal(-10d)).transfer(),
            "Transaction amount must be greater than zero");
    }

    @Test
    public void testTransferShouldRaiseErrorIfAmountIsZero() {
        assertThrows(IllegalArgumentException.class,
            () -> new Transaction(new Account(), new Account(), ZERO).transfer(),
            "Transaction amount must be greater than zero");
    }

    @Test
    public void testTransferShouldRaiseErrorIfOriginAccountHasInsufficientBalance() {
        assertThrows(IllegalArgumentException.class,
            () -> new Transaction(new Account(), new Account(), ONE).transfer(),
            "Origin account must have sufficient balance");
    }

    @Test
    public void testTransferShouldWithdrawFromOriginAccountAndDepositOnDestination() {
        final Account origin = new Account();
        origin.deposit(TEN);

        final Account destination = new Account();

        new Transaction(origin, destination, ONE).transfer();

        assertEquals(TEN.subtract(ONE).setScale(DECIMAL_PLACES, HALF_UP), origin.getBalance(),
            "Origin account balance should be decreased by amount");
        assertEquals(ZERO.add(ONE).setScale(DECIMAL_PLACES, HALF_UP), destination.getBalance(),
            "Destination account balance should be increased by amount");
    }
}
