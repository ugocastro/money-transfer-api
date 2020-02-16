package com.moneytransfer;

import com.google.gson.Gson;
import com.moneytransfer.domain.repository.AccountDao;
import com.moneytransfer.domain.repository.TransactionDao;
import com.moneytransfer.domain.request.TransferRequest;
import com.moneytransfer.service.AccountServiceImpl;
import com.moneytransfer.service.TransactionServiceImpl;
import com.moneytransfer.utils.JsonTransformer;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static spark.Spark.exception;
import static spark.Spark.path;
import static spark.Spark.post;
import static spark.Spark.put;

public class ApiServer {

    public static void main(String[] args) {

        path("/api", () -> {
            path("/accounts", () -> {
                post("",
                    (req, res) -> new AccountServiceImpl(new AccountDao())
                        .create(req.body()),
                    new JsonTransformer());
                put("/:id/deposit",
                    (req, res) -> new AccountServiceImpl(new AccountDao())
                        .deposit(req.params(":id"), new BigDecimal(req.body())),
                    new JsonTransformer());
                put("/:id/withdraw",
                    (req, res) -> new AccountServiceImpl(new AccountDao())
                        .withdraw(req.params(":id"), new BigDecimal(req.body())),
                    new JsonTransformer());
            });

            path("/transactions", () ->
                post("/transfers",
                    (req, res) -> {
                        final TransferRequest payload =
                            new Gson().fromJson(req.body(), TransferRequest.class);
                        return new TransactionServiceImpl(new AccountDao(), new TransactionDao())
                            .transfer(payload.getOriginAccountNumber(),
                                payload.getDestinationAccountNumber(), payload.getAmount());
                    }, new JsonTransformer()));
        });

        exception(NoSuchElementException.class, (exc, req, res) -> {
            res.status(404);
            res.body(exc.getMessage());
        });

        exception(IllegalArgumentException.class, (exc, req, res) -> {
            res.status(400);
            res.body(exc.getMessage());
        });
    }
}