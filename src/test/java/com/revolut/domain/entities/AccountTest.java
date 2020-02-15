package com.revolut.domain.entities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.revolut.utils.Constants.DECIMAL_PLACES;
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

    private Account account;

    @BeforeEach
    void beforeEach() {
        this.account = new Account();
    }

    @AfterEach
    void afterEach() {
        this.account = null;
    }

    @Test
    public void testNewAccountShouldHaveANumberAndBalanceShouldBeZero() {
        assertNotNull(this.account.getNumber(), "Account must have a number");
        assertEquals(ZERO.setScale(DECIMAL_PLACES), this.account.getBalance(),
            "Account must be initialized with a balance of zero");
    }

    @Test
    public void testNewAccountsShouldHaveDifferentNumbers() {
        assertNotEquals(this.account.getNumber(), new Account().getNumber());
    }

    @Test
    public void testErrorShouldBeRaisedIfDepositIsEqualToNull() {
        assertThrows(IllegalArgumentException.class,
            () -> this.account.deposit(null),
            "Deposit must contain a non-null value");
    }

    @Test
    public void testErrorShouldBeRaisedIfDepositHasNegativeValue() {
        assertThrows(IllegalArgumentException.class,
            () -> this.account.deposit(new BigDecimal(-10d)),
            "Deposit must contain a value greater than zero");
    }

    @Test
    public void testErrorShouldBeRaisedIfDepositIsEqualToZero() {
        assertThrows(IllegalArgumentException.class,
            () -> this.account.deposit(ZERO),
            "Deposit must contain a value greater than zero");
    }

    @Test
    public void testDepositShouldIncreaseBalance() {
        final BigDecimal previousBalance = this.account.getBalance();
        this.account.deposit(TEN);
        final BigDecimal newBalance = this.account.getBalance();

        assertTrue(newBalance.compareTo(previousBalance) > 0,
            "Deposit must increase balance");
    }

    @Test
    public void testBalanceShouldAlwaysHaveTwoDecimalPlaces() {
        assertEquals(DECIMAL_PLACES, this.account.getBalance().scale(),
            "Balance must have two decimal places");
        this.account.deposit(new BigDecimal(5.376d));
        assertEquals(DECIMAL_PLACES, this.account.getBalance().scale(),
            "Balance must have two decimal places");
        this.account.withdraw(new BigDecimal(2.5d));
        assertEquals(DECIMAL_PLACES, this.account.getBalance().scale(),
            "Balance must have two decimal places");
    }

    @Test
    public void testBalanceShouldBeRoundedUp() {
        this.account.deposit(new BigDecimal(5.375d));
        assertEquals(new BigDecimal(5.38d).setScale(DECIMAL_PLACES, HALF_UP),
            this.account.getBalance(),
            "Balance must be rounded up");
    }

    @Test
    public void testBalanceShouldBeRoundedDown() {
        this.account.deposit(new BigDecimal(5.374d));
        assertEquals(new BigDecimal(5.37d).setScale(DECIMAL_PLACES, HALF_UP),
            this.account.getBalance(),
            "Balance must be rounded down");
    }

    @Test
    public void testErrorShouldBeRaisedIfWithdrawIsEqualToNull() {
        assertThrows(IllegalArgumentException.class,
            () -> this.account.withdraw(null),
            "Withdraw must contain a non-null value");
    }

    @Test
    public void testErrorShouldBeRaisedIfWithdrawHasNegativeValue() {
        assertThrows(IllegalArgumentException.class,
            () -> this.account.withdraw(new BigDecimal(-10d)),
            "Withdraw must contain a value greater than zero");
    }

    @Test
    public void testErrorShouldBeRaisedIfWithdrawIsEqualToZero() {
        assertThrows(IllegalArgumentException.class,
            () -> this.account.withdraw(ZERO),
            "Withdraw must contain a value greater than zero");
    }

    @Test
    public void testErrorShouldBeRaisedIfWithdrawIsGreaterThanBalance() {
        assertThrows(IllegalArgumentException.class,
            () -> this.account.withdraw(ONE),
            "Account balance must contain value for withdraw");
    }

    @Test
    public void testWithdrawShouldDecreaseBalance() {
        this.account.deposit(TEN);
        final BigDecimal previousBalance = this.account.getBalance();
        this.account.withdraw(ONE);
        final BigDecimal newBalance = this.account.getBalance();

        assertTrue(newBalance.compareTo(previousBalance) < 0,
            "Withdraw must decrease balance");
    }
}
