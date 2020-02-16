package com.moneytransfer.domain.entities;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.moneytransfer.utils.Constants.DECIMAL_PLACES;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountTest {

    @Test
    public void testNewAccountShouldRaiseErrorIfOwnerIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new Account(null),
            "Account must have an owner");
    }

    @Test
    public void testNewAccountShouldRaiseErrorIfOwnerIsEmpty() {
        assertThrows(IllegalArgumentException.class,
            () -> new Account("  "),
            "Account owner must not be empty");
    }

    @Test
    public void testNewAccountShouldHaveANumberAndBalanceShouldBeZero() {
        final Account account = new Account("John Doe");

        assertNotNull(account.getNumber(), "Account must have a number");
        assertEquals(ZERO.setScale(DECIMAL_PLACES), account.getBalance(),
            "Account must be initialized with a balance of zero");
    }

    @Test
    public void testNewAccountsShouldHaveDifferentNumbers() {
        final Account account1 = new Account("John Doe");
        final Account account2 = new Account("Joseph Doe");

        assertNotEquals(account1.getNumber(), account2.getNumber());
    }

    @Test
    public void testErrorShouldBeRaisedIfDepositIsEqualToNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new Account("John Doe").deposit(null),
            "Deposit must contain a non-null value");
    }

    @Test
    public void testErrorShouldBeRaisedIfDepositHasNegativeValue() {
        assertThrows(IllegalArgumentException.class,
            () -> new Account("John Doe").deposit(new BigDecimal(-10d)),
            "Deposit must contain a value greater than zero");
    }

    @Test
    public void testErrorShouldBeRaisedIfDepositIsEqualToZero() {
        assertThrows(IllegalArgumentException.class,
            () -> new Account("John Doe").deposit(ZERO),
            "Deposit must contain a value greater than zero");
    }

    @Test
    public void testDepositShouldIncreaseBalance() {
        final Account account = new Account("John Doe");
        final BigDecimal previousBalance = account.getBalance();
        account.deposit(TEN);
        final BigDecimal newBalance = account.getBalance();

        assertTrue(newBalance.compareTo(previousBalance) > 0,
            "Deposit must increase balance");
    }

    @Test
    public void testBalanceShouldAlwaysHaveTwoDecimalPlaces() {
        final Account account = new Account("John Doe");
        assertEquals(DECIMAL_PLACES, account.getBalance().scale(),
            "Balance must have two decimal places");

        account.deposit(new BigDecimal(5.376d));
        assertEquals(DECIMAL_PLACES, account.getBalance().scale(),
            "Balance must have two decimal places");

        account.withdraw(new BigDecimal(2.5d));
        assertEquals(DECIMAL_PLACES, account.getBalance().scale(),
            "Balance must have two decimal places");
    }

    @Test
    public void testBalanceShouldBeRoundedUp() {
        final Account account = new Account("John Doe");
        account.deposit(new BigDecimal(5.375d));

        assertEquals(new BigDecimal(5.38d).setScale(DECIMAL_PLACES, HALF_UP),
            account.getBalance(),
            "Balance must be rounded up");
    }

    @Test
    public void testBalanceShouldBeRoundedDown() {
        final Account account = new Account("John Doe");
        account.deposit(new BigDecimal(5.374d));

        assertEquals(new BigDecimal(5.37d).setScale(DECIMAL_PLACES, HALF_UP),
            account.getBalance(),
            "Balance must be rounded down");
    }

    @Test
    public void testErrorShouldBeRaisedIfWithdrawIsEqualToNull() {
        final Account account = new Account("John Doe");

        assertThrows(IllegalArgumentException.class,
            () -> account.withdraw(null),
            "Withdraw must contain a non-null value");
    }

    @Test
    public void testErrorShouldBeRaisedIfWithdrawHasNegativeValue() {
        final Account account = new Account("John Doe");

        assertThrows(IllegalArgumentException.class,
            () -> account.withdraw(new BigDecimal(-10d)),
            "Withdraw must contain a value greater than zero");
    }

    @Test
    public void testErrorShouldBeRaisedIfWithdrawIsEqualToZero() {
        final Account account = new Account("John Doe");

        assertThrows(IllegalArgumentException.class,
            () -> account.withdraw(ZERO),
            "Withdraw must contain a value greater than zero");
    }

    @Test
    public void testErrorShouldBeRaisedIfWithdrawIsGreaterThanBalance() {
        final Account account = new Account("John Doe");

        assertThrows(IllegalArgumentException.class,
            () -> account.withdraw(ONE),
            "Account balance must contain value for withdraw");
    }

    @Test
    public void testWithdrawShouldDecreaseBalance() {
        final Account account = new Account("John Doe");
        account.deposit(TEN);
        final BigDecimal previousBalance = account.getBalance();
        account.withdraw(ONE);
        final BigDecimal newBalance = account.getBalance();

        assertTrue(newBalance.compareTo(previousBalance) < 0,
            "Withdraw must decrease balance");
    }
}
