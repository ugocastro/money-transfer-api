package com.moneytransfer;

import com.google.gson.Gson;
import com.moneytransfer.domain.repository.AccountDao;
import com.moneytransfer.domain.repository.TransactionDao;
import com.moneytransfer.domain.request.AccountRequest;
import com.moneytransfer.domain.request.TransferRequest;
import com.moneytransfer.domain.request.UpdateBalanceRequest;
import com.moneytransfer.domain.response.ErrorResponse;
import com.moneytransfer.service.AccountServiceImpl;
import com.moneytransfer.service.TransactionServiceImpl;
import com.moneytransfer.utils.JsonTransformer;
import lombok.extern.slf4j.Slf4j;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.eclipse.jetty.http.HttpStatus.BAD_REQUEST_400;
import static org.eclipse.jetty.http.HttpStatus.NOT_FOUND_404;
import static spark.Spark.exception;
import static spark.Spark.initExceptionHandler;
import static spark.Spark.internalServerError;
import static spark.Spark.notFound;
import static spark.Spark.path;
import static spark.Spark.post;
import static spark.Spark.put;

@Slf4j
public class ApiServer {

    public static void main(String[] args) {
        initialize();
    }

    public static void initialize() {
        initExceptionHandler((e) -> log.error("Error starting server", e.getMessage()));

        path("/api", () -> {
            path("/accounts", () -> {
                post("", (req, res) -> {
                    res.type("application/json");
                    return new AccountServiceImpl(new AccountDao()).create(
                        Optional.ofNullable(new Gson().fromJson(req.body(), AccountRequest.class))
                            .map(AccountRequest::getOwner).orElse(null));
                }, new JsonTransformer());
                put("/:id/deposit", (req, res) -> {
                    res.type("application/json");
                    return new AccountServiceImpl(new AccountDao()).deposit(req.params(":id"),
                        Optional.ofNullable(new Gson().fromJson(req.body(), UpdateBalanceRequest.class))
                            .map(UpdateBalanceRequest::getAmount).orElse(null));
                }, new JsonTransformer());
                put("/:id/withdraw", (req, res) -> {
                    res.type("application/json");
                    return new AccountServiceImpl(new AccountDao()).withdraw(req.params(":id"),
                        Optional.ofNullable(new Gson().fromJson(req.body(), UpdateBalanceRequest.class))
                            .map(UpdateBalanceRequest::getAmount).orElse(null));
                }, new JsonTransformer());
            });

            post("/transfers", (req, res) -> {
                res.type("application/json");
                final Optional<TransferRequest> payload = Optional.ofNullable(
                    new Gson().fromJson(req.body(), TransferRequest.class));
                return new TransactionServiceImpl(new AccountDao(), new TransactionDao()).transfer(
                    payload.map(TransferRequest::getOriginAccountNumber).orElse(null),
                    payload.map(TransferRequest::getDestinationAccountNumber).orElse(null),
                    payload.map(TransferRequest::getAmount).orElse(null));
            }, new JsonTransformer());
        });

        notFound((req, res) -> {
            res.type("application/json");
            return new Gson().toJson(new ErrorResponse("Endpoint not found"));
        });

        exception(NoSuchElementException.class, (exc, req, res) -> {
            res.type("application/json");
            res.status(NOT_FOUND_404);
            res.body(new Gson().toJson(new ErrorResponse(exc.getMessage())));
        });

        exception(IllegalArgumentException.class, (exc, req, res) -> {
            res.type("application/json");
            res.status(BAD_REQUEST_400);
            res.body(new Gson().toJson(new ErrorResponse(exc.getMessage())));
        });

        internalServerError((req, res) -> {
            res.type("application/json");
            return new Gson().toJson(new ErrorResponse("Unexpected error"));
        });
    }
}
