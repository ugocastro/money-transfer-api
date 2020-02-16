package com.revolut.service;

import com.revolut.domain.entities.Account;
import com.revolut.domain.repository.AccountDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static com.revolut.utils.Constants.DECIMAL_PLACES;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccountServiceTest {

    private AccountDao accountDao;
    private AccountService accountService;

    @BeforeEach
    void beforeEach() {
        this.accountDao = mock(AccountDao.class);
        this.accountService = new AccountServiceImpl(this.accountDao);
    }

    @Test
    public void testShouldRaiseAnErrorIfOwnerIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> this.accountService.create(null),
            "Account owner must be non-null");
    }

    @Test
    public void testShouldRaiseAnErrorIfOwnerIsEmpty() {
        assertThrows(IllegalArgumentException.class,
            () -> this.accountService.create(" "),
            "Account owner must be non-empty");
    }

    @Test
    public void testShouldCreateAccount() throws Exception {
        final String owner = "John Doe";
        when(this.accountDao.save(any())).thenReturn(new Account(owner));
        final Account savedAccount = this.accountService.create(owner);

        assertNotNull(savedAccount, "Account must be saved");
        assertEquals(owner, savedAccount.getOwner(), "Saved account must have provided owner");
        assertNotNull(savedAccount.getNumber(), "Saved account must have a number");
        assertEquals(ZERO.setScale(DECIMAL_PLACES, HALF_UP), savedAccount.getBalance(),
            "Saved account must have initial balance as zero");
    }

    @Test
    public void testShouldRaiseAnErrorIfAccountNumberForDepositIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> this.accountService.deposit(null, ONE),
            "Account number must be non-null");
    }

    @Test
    public void testShouldRaiseAnErrorIfAmountForDepositIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> this.accountService.deposit(randomUUID().toString(), null),
            "Amount for deposit must be non-null");
    }

    @Test
    public void testShouldRaiseAnErrorIfAccountForDepositIsNotFound() throws Exception {
        final String nonExistentAccountNumber = randomUUID().toString();
        when(this.accountDao.findById(nonExistentAccountNumber)).thenThrow(new NoSuchElementException());

        assertThrows(NoSuchElementException.class,
            () -> this.accountService.deposit(nonExistentAccountNumber, ONE),
            "Account for deposit must exist");
    }

    @Test
    public void testShouldRaiseAnErrorIfAmountForDepositIsNegative() throws Exception {
        final String accountNumber = randomUUID().toString();
        when(this.accountDao.findById(accountNumber)).thenReturn(new Account("John Doe"));

        assertThrows(IllegalArgumentException.class,
            () -> this.accountService.deposit(accountNumber, new BigDecimal(-10d)),
            "Amount for deposit must be greater than zero");
    }

    @Test
    public void testShouldRaiseAnErrorIfAmountForDepositIsZero() throws Exception {
        final String accountNumber = randomUUID().toString();
        when(this.accountDao.findById(accountNumber)).thenReturn(new Account("John Doe"));

        assertThrows(IllegalArgumentException.class,
            () -> this.accountService.deposit(accountNumber, ZERO),
            "Amount for deposit must be greater than zero");
    }

    @Test
    public void testShouldIncreaseAccountBalanceAfterDeposit() throws Exception {
        final String accountNumber = randomUUID().toString();
        final BigDecimal amount = ONE;
        final Account account = new Account("John Doe");
        final BigDecimal previousBalance = account.getBalance();
        when(this.accountDao.findById(accountNumber)).thenReturn(account);

        this.accountService.deposit(accountNumber, amount);

        assertTrue(account.getBalance().compareTo(previousBalance) > 0,
            "Account balance must increase after deposit");
        assertEquals(previousBalance.add(amount), account.getBalance(),
            "Account balance must increase by given amount");
    }

    @Test
    public void testShouldRaiseAnErrorIfAccountNumberForWithdrawIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> this.accountService.withdraw(null, ONE),
            "Account number must be non-null");
    }

    @Test
    public void testShouldRaiseAnErrorIfAmountForWithdrawIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> this.accountService.withdraw(randomUUID().toString(), null),
            "Amount for withdraw must be non-null");
    }

    @Test
    public void testShouldRaiseAnErrorIfAccountForWithdrawIsNotFound() throws Exception {
        final String nonExistentAccountNumber = randomUUID().toString();
        when(this.accountDao.findById(nonExistentAccountNumber)).thenThrow(new NoSuchElementException());

        assertThrows(NoSuchElementException.class,
            () -> this.accountService.withdraw(nonExistentAccountNumber, ONE),
            "Account for withdraw must exist");
    }

    @Test
    public void testShouldRaiseAnErrorIfAmountForWithdrawIsNegative() throws Exception {
        final String accountNumber = randomUUID().toString();
        when(this.accountDao.findById(accountNumber)).thenReturn(new Account("John Doe"));

        assertThrows(IllegalArgumentException.class,
            () -> this.accountService.withdraw(accountNumber, new BigDecimal(-10d)),
            "Amount for withdraw must be greater than zero");
    }

    @Test
    public void testShouldRaiseAnErrorIfAmountForWithdrawIsZero() throws Exception {
        final String accountNumber = randomUUID().toString();
        when(this.accountDao.findById(accountNumber)).thenReturn(new Account("John Doe"));

        assertThrows(IllegalArgumentException.class,
            () -> this.accountService.withdraw(accountNumber, ZERO),
            "Amount for withdraw must be greater than zero");
    }

    @Test
    public void testShouldRaiseAnErrorIfAmountForWithdrawIsGreaterThanAccountBalance() throws Exception {
        final String accountNumber = randomUUID().toString();
        when(this.accountDao.findById(accountNumber)).thenReturn(new Account("John Doe"));

        assertThrows(IllegalArgumentException.class,
            () -> this.accountService.withdraw(accountNumber, ONE),
            "Account must contain specified amount for withdraw");
    }

    @Test
    public void testShouldDecreaseAccountBalanceAfterWithdraw() throws Exception {
        final String accountNumber = randomUUID().toString();
        final BigDecimal amount = ONE;
        final Account account = new Account("John Doe");
        account.deposit(TEN);
        final BigDecimal previousBalance = account.getBalance();
        when(this.accountDao.findById(accountNumber)).thenReturn(account);

        this.accountService.withdraw(accountNumber, amount);

        assertTrue(account.getBalance().compareTo(previousBalance) < 0,
            "Account balance must decrease after deposit");
        assertEquals(previousBalance.subtract(amount), account.getBalance(),
            "Account balance must decrease by given amount");
    }
}
