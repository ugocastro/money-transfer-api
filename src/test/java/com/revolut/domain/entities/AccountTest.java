package com.revolut.domain.entities;

import com.revolut.utils.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountTest {

    private static Account account;

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
        assertEquals(BigDecimal.ZERO.setScale(Constants.DECIMAL_PLACES), this.account.getBalance(),
            "Account must be initialized with a balance of zero");
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
            () -> this.account.deposit(new BigDecimal(-10)),
            "Deposit must contain a value greater than zero");
    }

    @Test
    public void testErrorShouldBeRaisedIfDepositIsEqualToZero() {
        assertThrows(IllegalArgumentException.class,
            () -> this.account.deposit(BigDecimal.ZERO),
            "Deposit must contain a value greater than zero");
    }

    @Test
    public void testDepositShouldIncreaseBalance() {
        BigDecimal previousBalance = this.account.getBalance();
        this.account.deposit(BigDecimal.TEN);
        BigDecimal newBalance = this.account.getBalance();
        assertTrue(newBalance.compareTo(previousBalance) > 0,
            "Deposit must increase balance");
    }

    @Test
    public void testBalanceShouldHaveTwoDecimalPlacesBeforeAndAfterDeposit() {
        assertEquals(Constants.DECIMAL_PLACES, this.account.getBalance().scale(),
            "Balance must have two decimal places");
        this.account.deposit(new BigDecimal(5.376f));
        assertEquals(Constants.DECIMAL_PLACES, this.account.getBalance().scale(),
            "Balance must have two decimal places");
    }

    @Test
    public void testBalanceShouldBeRoundedUp() {
        this.account.deposit(new BigDecimal(5.375f));
        assertEquals(new BigDecimal(5.38f).setScale(Constants.DECIMAL_PLACES, RoundingMode.HALF_UP),
            this.account.getBalance(),
            "Balance must be rounded up");
    }

    @Test
    public void testBalanceShouldBeRoundedDown() {
        this.account.deposit(new BigDecimal(5.374f));
        assertEquals(new BigDecimal(5.37f).setScale(Constants.DECIMAL_PLACES, RoundingMode.HALF_UP),
            this.account.getBalance(),
            "Balance must be rounded down");
    }
}
