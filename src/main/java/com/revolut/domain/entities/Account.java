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
        this.balance = BigDecimal.ZERO;
    }

    public void deposit(@NonNull() BigDecimal amount) throws IllegalArgumentException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount for deposit should be greater than zero");
        }

        this.balance = this.balance.add(amount).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getBalance() {
        return this.balance.setScale(Constants.DECIMAL_PLACES, RoundingMode.HALF_UP);
    }
}
