package com.revolut.domain.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

import static com.revolut.utils.Constants.DECIMAL_PLACES;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import static java.util.UUID.randomUUID;

@Entity
@EqualsAndHashCode
@Getter
@ToString
public class Account implements Serializable {

    private static final long serialVersionUID = -5933713453986435201L;

    @Id
    private String number;

    private BigDecimal balance;

    public Account() {
        this.number = randomUUID().toString();
        setBalance(ZERO);
    }

    public void deposit(@NonNull final BigDecimal amount) throws IllegalArgumentException {
        if (amount.compareTo(ZERO) <= 0) {
            throw new IllegalArgumentException("Amount for deposit should be greater than zero");
        }

        final BigDecimal newBalance = this.balance.add(amount);
        setBalance(newBalance);
    }

    public void withdraw(@NonNull final BigDecimal amount) throws IllegalArgumentException {
        if (amount.compareTo(ZERO) <= 0) {
            throw new IllegalArgumentException("Amount for withdraw should be greater than zero");
        }

        if (amount.compareTo(this.balance) > 0) {
            throw new IllegalArgumentException("Account balance should contain value for withdraw");
        }

        final BigDecimal newBalance = this.balance.subtract(amount);
        setBalance(newBalance);
    }

    private void setBalance(@NonNull final BigDecimal balance) {
        this.balance = balance.setScale(DECIMAL_PLACES, HALF_UP);
    }
}
