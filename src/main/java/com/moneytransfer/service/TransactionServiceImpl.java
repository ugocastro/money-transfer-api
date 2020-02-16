package com.moneytransfer.service;

import com.moneytransfer.domain.entities.Account;
import com.moneytransfer.domain.entities.Transaction;
import com.moneytransfer.domain.repository.BaseDao;
import lombok.NonNull;

import java.math.BigDecimal;

public class TransactionServiceImpl implements TransactionService {

    private BaseDao<Account> accountDao;
    private BaseDao<Transaction> transactionDao;

    public TransactionServiceImpl(@NonNull final BaseDao<Account> accountDao,
                                  @NonNull final BaseDao<Transaction> transactionDao) {
        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
    }

    @Override
    public synchronized Transaction transfer(@NonNull final String originAccountNumber,
                                             @NonNull final String destinationAccountNumber,
                                             @NonNull final BigDecimal amount) throws Exception {

        final Account origin = this.accountDao.findById(originAccountNumber);
        final Account destination = this.accountDao.findById(destinationAccountNumber);

        Transaction transaction = new Transaction(origin, destination, amount);
        transaction.transfer();

        this.accountDao.save(origin);
        this.accountDao.save(destination);

        return this.transactionDao.save(transaction);
    }
}
