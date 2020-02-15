package com.revolut.domain.entities;

import com.revolut.utils.Constants;
import lombok.Getter;
import lombok.NonNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class Account implements Serializable {

    @Getter
    private String number;

    private BigDecimal balance;

    public Account() {
        this.number = UUID.randomUUID().toString();
        setBalance(BigDecimal.ZERO);
    }

    public void deposit(@NonNull BigDecimal amount) throws IllegalArgumentException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount for deposit should be greater than zero");
        }

        BigDecimal newBalance = this.balance.add(amount);
        setBalance(newBalance);
    }

    public void withdraw(@NonNull BigDecimal amount) throws IllegalArgumentException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount for withdraw should be greater than zero");
        }

        if (amount.compareTo(this.balance) > 0) {
            throw new IllegalArgumentException("Account balance should contain value for withdraw");
        }

        BigDecimal newBalance = this.balance.subtract(amount);
        setBalance(newBalance);
    }

    public BigDecimal getBalance() {
        return this.balance.setScale(Constants.DECIMAL_PLACES, RoundingMode.HALF_UP);
    }

    private void setBalance(@NonNull BigDecimal balance) {
        this.balance = balance.setScale(Constants.DECIMAL_PLACES, RoundingMode.HALF_UP);
    }
}
