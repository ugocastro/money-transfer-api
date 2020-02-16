package com.moneytransfer.domain.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.moneytransfer.utils.Constants.DECIMAL_PLACES;
import static java.math.RoundingMode.HALF_UP;
import static java.util.UUID.randomUUID;

@Entity
@EqualsAndHashCode
@Getter
@ToString
public class Transaction implements Serializable {

    private static final long serialVersionUID = -4857050094419687194L;

    @Id
    private String id;

    @ManyToOne
    private Account origin;

    @ManyToOne
    private Account destination;

    private BigDecimal amount;

    private LocalDateTime date;

    public Transaction() { }

    public Transaction(@NonNull final Account origin,
                       @NonNull final Account destination,
                       @NonNull final BigDecimal amount) {
        this.id = randomUUID().toString();
        this.origin = origin;
        this.destination = destination;
        this.amount = amount.setScale(DECIMAL_PLACES, HALF_UP);
        this.date = LocalDateTime.now();
    }

    public void transfer() {
        this.origin.withdraw(amount);
        this.destination.deposit(amount);
    }
}
