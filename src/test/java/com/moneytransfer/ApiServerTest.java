package com.moneytransfer;

import com.despegar.http.client.HttpClientException;
import com.despegar.http.client.HttpResponse;
import com.despegar.http.client.PostMethod;
import com.despegar.http.client.PutMethod;
import com.despegar.sparkjava.test.SparkServer;
import com.google.gson.Gson;
import com.moneytransfer.domain.entities.Account;
import com.moneytransfer.domain.request.AccountRequest;
import com.moneytransfer.domain.request.TransferRequest;
import com.moneytransfer.domain.request.UpdateBalanceRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spark.servlet.SparkApplication;

import java.math.BigDecimal;

import static com.moneytransfer.ApiServer.initialize;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.UUID.randomUUID;
import static org.eclipse.jetty.http.HttpStatus.BAD_REQUEST_400;
import static org.eclipse.jetty.http.HttpStatus.OK_200;
import static org.eclipse.jetty.http.HttpStatus.NOT_FOUND_404;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static spark.Spark.awaitInitialization;
import static spark.Spark.stop;

public class ApiServerTest {

    private static SparkServer<ApiServerTestApplication> testServer;

    public static class ApiServerTestApplication implements SparkApplication {
        @Override
        public void init() {
            initialize();
        }
    }

    @BeforeAll
    static void setUp() {
        initialize();
        awaitInitialization();
        testServer = new SparkServer<>(ApiServerTest.ApiServerTestApplication.class, 4567);
    }

    @AfterAll
    static void tearDown() {
        stop();
    }

    @Test
    public void testCreateAccountShouldReturnBadRequestIfOwnerIsNotSpecified() throws HttpClientException {
        final PostMethod createAccount = testServer.post("/api/accounts", "", false);
        final HttpResponse createAccountResponse = testServer.execute(createAccount);

        assertEquals(BAD_REQUEST_400, createAccountResponse.code());
    }

    @Test
    public void testCreateAccountShouldReturnSuccess() throws HttpClientException {
        final PostMethod createAccount = testServer.post("/api/accounts",
            new Gson().toJson(new AccountRequest("John Doe")), false);
        final HttpResponse createAccountResponse = testServer.execute(createAccount);

        assertEquals(OK_200, createAccountResponse.code());
    }

    @Test
    public void testDepositShouldReturnNotFoundIfAccountDoesNotExist() throws HttpClientException {
        final String depositBody = new Gson().toJson(new UpdateBalanceRequest(TEN));
        final PutMethod deposit = testServer.put(
            "/api/accounts/" + randomUUID().toString() + "/deposit", depositBody, false);
        final HttpResponse depositResponse = testServer.execute(deposit);

        assertEquals(NOT_FOUND_404, depositResponse.code());
    }

    @Test
    public void testDepositShouldReturnBadRequestIfAmountIsNotSpecified() throws HttpClientException {
        final PostMethod createAccount = testServer.post("/api/accounts",
            new Gson().toJson(new AccountRequest("John Doe")), false);
        final HttpResponse createAccountResponse = testServer.execute(createAccount);

        final Account account = new Gson().fromJson(
            new String(createAccountResponse.body(), UTF_8), Account.class);

        final PutMethod deposit = testServer.put(
            "/api/accounts/" + account.getNumber() + "/deposit", "", false);
        final HttpResponse depositResponse = testServer.execute(deposit);

        assertEquals(BAD_REQUEST_400, depositResponse.code());
    }

    @Test
    public void testDepositShouldReturnBadRequestIfAmountIsNegative() throws HttpClientException {
        final PostMethod createAccount = testServer.post("/api/accounts",
            new Gson().toJson(new AccountRequest("John Doe")), false);
        final HttpResponse createAccountResponse = testServer.execute(createAccount);

        final Account account = new Gson().fromJson(
            new String(createAccountResponse.body(), UTF_8), Account.class);

        final String depositBody = new Gson().toJson(new UpdateBalanceRequest(new BigDecimal(-10d)));
        final PutMethod deposit = testServer.put(
            "/api/accounts/" + account.getNumber() + "/deposit", depositBody, false);
        final HttpResponse depositResponse = testServer.execute(deposit);

        assertEquals(BAD_REQUEST_400, depositResponse.code());
    }

    @Test
    public void testDepositShouldReturnBadRequestIfAmountIsZero() throws HttpClientException {
        final PostMethod createAccount = testServer.post("/api/accounts",
            new Gson().toJson(new AccountRequest("John Doe")), false);
        final HttpResponse createAccountResponse = testServer.execute(createAccount);

        final Account account = new Gson().fromJson(
            new String(createAccountResponse.body(), UTF_8), Account.class);

        final String depositBody = new Gson().toJson(new UpdateBalanceRequest(ZERO));
        final PutMethod deposit = testServer.put(
            "/api/accounts/" + account.getNumber() + "/deposit", depositBody, false);
        final HttpResponse depositResponse = testServer.execute(deposit);

        assertEquals(BAD_REQUEST_400, depositResponse.code());
    }

    @Test
    public void testDepositShouldReturnSuccessIfAmountIsValid() throws HttpClientException {
        final PostMethod createAccount = testServer.post("/api/accounts",
            new Gson().toJson(new AccountRequest("John Doe")), false);
        final HttpResponse createAccountResponse = testServer.execute(createAccount);

        final Account account = new Gson().fromJson(
            new String(createAccountResponse.body(), UTF_8), Account.class);

        final String depositBody = new Gson().toJson(new UpdateBalanceRequest(TEN));
        final PutMethod deposit = testServer.put(
            "/api/accounts/" + account.getNumber() + "/deposit", depositBody, false);
        final HttpResponse depositResponse = testServer.execute(deposit);

        assertEquals(OK_200, depositResponse.code());
    }

    @Test
    public void testWithdrawShouldReturnNotFoundIfAccountDoesNotExist() throws HttpClientException {
        final String withdrawBody = new Gson().toJson(new UpdateBalanceRequest(TEN));
        final PutMethod withdraw = testServer.put(
            "/api/accounts/" + randomUUID().toString() + "/withdraw", withdrawBody, false);
        final HttpResponse withdrawResponse = testServer.execute(withdraw);

        assertEquals(NOT_FOUND_404, withdrawResponse.code());
    }

    @Test
    public void testWithdrawShouldReturnBadRequestIfAmountIsNotSpecified() throws HttpClientException {
        final PostMethod createAccount = testServer.post("/api/accounts",
            new Gson().toJson(new AccountRequest("John Doe")), false);
        final HttpResponse createAccountResponse = testServer.execute(createAccount);

        final Account account = new Gson().fromJson(
            new String(createAccountResponse.body(), UTF_8), Account.class);

        final PutMethod withdraw = testServer.put(
            "/api/accounts/" + account.getNumber() + "/withdraw", "", false);
        final HttpResponse withdrawResponse = testServer.execute(withdraw);

        assertEquals(BAD_REQUEST_400, withdrawResponse.code());
    }

    @Test
    public void testWithdrawShouldReturnBadRequestIfAmountIsNegative() throws HttpClientException {
        final PostMethod createAccount = testServer.post("/api/accounts",
            new Gson().toJson(new AccountRequest("John Doe")), false);
        final HttpResponse createAccountResponse = testServer.execute(createAccount);

        final Account account = new Gson().fromJson(
            new String(createAccountResponse.body(), UTF_8), Account.class);

        final String withdrawBody = new Gson().toJson(new UpdateBalanceRequest(new BigDecimal( -10d)));
        final PutMethod withdraw = testServer.put(
            "/api/accounts/" + account.getNumber() + "/withdraw", withdrawBody, false);
        final HttpResponse withdrawResponse = testServer.execute(withdraw);

        assertEquals(BAD_REQUEST_400, withdrawResponse.code());
    }

    @Test
    public void testWithdrawShouldReturnBadRequestIfAmountIsZero() throws HttpClientException {
        final PostMethod createAccount = testServer.post("/api/accounts",
            new Gson().toJson(new AccountRequest("John Doe")), false);
        final HttpResponse createAccountResponse = testServer.execute(createAccount);

        final Account account = new Gson().fromJson(
            new String(createAccountResponse.body(), UTF_8), Account.class);

        final String withdrawBody = new Gson().toJson(new UpdateBalanceRequest(ZERO));
        final PutMethod withdraw = testServer.put(
            "/api/accounts/" + account.getNumber() + "/withdraw", withdrawBody, false);
        final HttpResponse withdrawResponse = testServer.execute(withdraw);

        assertEquals(BAD_REQUEST_400, withdrawResponse.code());
    }

    @Test
    public void testWithdrawShouldReturnBadRequestIfAccountHasInsufficientBalance() throws HttpClientException {
        final PostMethod createAccount = testServer.post("/api/accounts",
            new Gson().toJson(new AccountRequest("John Doe")), false);
        final HttpResponse createAccountResponse = testServer.execute(createAccount);

        final Account account = new Gson().fromJson(
            new String(createAccountResponse.body(), UTF_8), Account.class);

        final String withdrawBody = new Gson().toJson(new UpdateBalanceRequest(TEN));
        final PutMethod withdraw = testServer.put(
            "/api/accounts/" + account.getNumber() + "/withdraw", withdrawBody, false);
        final HttpResponse withdrawResponse = testServer.execute(withdraw);

        assertEquals(BAD_REQUEST_400, withdrawResponse.code());
    }

    @Test
    public void testWithdrawShouldReturnSuccessIfAmountIsValidAndAccountHasBalance() throws HttpClientException {
        final PostMethod createAccount = testServer.post("/api/accounts",
            new Gson().toJson(new AccountRequest("John Doe")), false);
        final HttpResponse createAccountResponse = testServer.execute(createAccount);

        final Account account = new Gson().fromJson(
            new String(createAccountResponse.body(), UTF_8), Account.class);

        final String depositBody = new Gson().toJson(new UpdateBalanceRequest(TEN));
        final PutMethod deposit = testServer.put(
            "/api/accounts/" + account.getNumber() + "/deposit", depositBody, false);
        testServer.execute(deposit);

        final String withdrawBody = new Gson().toJson(new UpdateBalanceRequest(ONE));
        final PutMethod withdraw = testServer.put(
            "/api/accounts/" + account.getNumber() + "/withdraw", withdrawBody, false);
        final HttpResponse withdrawResponse = testServer.execute(withdraw);

        assertEquals(OK_200, withdrawResponse.code());
    }

    @Test
    public void testTransferShouldReturnNotFoundIfOriginAccountDoesNotExist() throws HttpClientException {
        final PostMethod createAccount = testServer.post("/api/accounts",
            new Gson().toJson(new AccountRequest("John Doe")), false);
        final HttpResponse createAccountResponse = testServer.execute(createAccount);

        final Account destinationAccount = new Gson().fromJson(
            new String(createAccountResponse.body(), UTF_8), Account.class);

        final String transferBody = new Gson().toJson(
            new TransferRequest(randomUUID().toString(), destinationAccount.getNumber(), TEN));
        final PostMethod transfer = testServer.post("/api/transfers", transferBody, false);
        final HttpResponse transferResponse = testServer.execute(transfer);

        assertEquals(NOT_FOUND_404, transferResponse.code());
    }

    @Test
    public void testTransferShouldReturnNotFoundIfDestinationAccountDoesNotExist() throws HttpClientException {
        final PostMethod createAccount = testServer.post("/api/accounts",
            new Gson().toJson(new AccountRequest("John Doe")), false);
        final HttpResponse createAccountResponse = testServer.execute(createAccount);

        final Account originAccount = new Gson().fromJson(
            new String(createAccountResponse.body(), UTF_8), Account.class);

        final String transferBody = new Gson().toJson(
            new TransferRequest(originAccount.getNumber(), randomUUID().toString(), TEN));
        final PostMethod transfer = testServer.post("/api/transfers", transferBody, false);
        final HttpResponse transferResponse = testServer.execute(transfer);

        assertEquals(NOT_FOUND_404, transferResponse.code());
    }

    @Test
    public void testTransferShouldReturnBadRequestIfAmountIsNotSpecified() throws HttpClientException {
        final PostMethod createOriginAccount = testServer.post("/api/accounts",
            new Gson().toJson(new AccountRequest("John Doe")), false);
        final HttpResponse createOriginAccountResponse = testServer.execute(createOriginAccount);

        final PostMethod createDestinationAccount = testServer.post("/api/accounts",
            new Gson().toJson(new AccountRequest("Joseph Doe")), false);
        final HttpResponse createDestinationAccountResponse = testServer.execute(createDestinationAccount);

        final Account originAccount = new Gson().fromJson(
            new String(createOriginAccountResponse.body(), UTF_8), Account.class);
        final Account destinationAccount = new Gson().fromJson(
            new String(createDestinationAccountResponse.body(), UTF_8), Account.class);

        final String transferBody = new Gson().toJson(
            new TransferRequest(originAccount.getNumber(), destinationAccount.getNumber(), null));
        final PostMethod transfer = testServer.post("/api/transfers", transferBody, false);
        final HttpResponse transferResponse = testServer.execute(transfer);

        assertEquals(BAD_REQUEST_400, transferResponse.code());
    }

    @Test
    public void testTransferShouldReturnBadRequestIfAmountIsNegative() throws HttpClientException {
        final PostMethod createOriginAccount = testServer.post("/api/accounts",
            new Gson().toJson(new AccountRequest("John Doe")), false);
        final HttpResponse createOriginAccountResponse = testServer.execute(createOriginAccount);

        final PostMethod createDestinationAccount = testServer.post("/api/accounts",
            new Gson().toJson(new AccountRequest("Joseph Doe")), false);
        final HttpResponse createDestinationAccountResponse = testServer.execute(createDestinationAccount);

        final Account originAccount = new Gson().fromJson(
            new String(createOriginAccountResponse.body(), UTF_8), Account.class);
        final Account destinationAccount = new Gson().fromJson(
            new String(createDestinationAccountResponse.body(), UTF_8), Account.class);

        final String transferBody = new Gson().toJson(
            new TransferRequest(originAccount.getNumber(), destinationAccount.getNumber(), new BigDecimal(-10d)));
        final PostMethod transfer = testServer.post("/api/transfers", transferBody, false);
        final HttpResponse transferResponse = testServer.execute(transfer);

        assertEquals(BAD_REQUEST_400, transferResponse.code());
    }

    @Test
    public void testTransferShouldReturnBadRequestIfAmountIsZero() throws HttpClientException {
        final PostMethod createOriginAccount = testServer.post("/api/accounts",
            new Gson().toJson(new AccountRequest("John Doe")), false);
        final HttpResponse createOriginAccountResponse = testServer.execute(createOriginAccount);

        final PostMethod createDestinationAccount = testServer.post("/api/accounts",
            new Gson().toJson(new AccountRequest("Joseph Doe")), false);
        final HttpResponse createDestinationAccountResponse = testServer.execute(createDestinationAccount);

        final Account originAccount = new Gson().fromJson(
            new String(createOriginAccountResponse.body(), UTF_8), Account.class);
        final Account destinationAccount = new Gson().fromJson(
            new String(createDestinationAccountResponse.body(), UTF_8), Account.class);

        final String transferBody = new Gson().toJson(
            new TransferRequest(originAccount.getNumber(), destinationAccount.getNumber(), ZERO));
        final PostMethod transfer = testServer.post("/api/transfers", transferBody, false);
        final HttpResponse transferResponse = testServer.execute(transfer);

        assertEquals(BAD_REQUEST_400, transferResponse.code());
    }

    @Test
    public void testTransferShouldReturnBadRequestIfOriginAccountHasInsufficientBalance() throws HttpClientException {
        final PostMethod createOriginAccount = testServer.post("/api/accounts",
            new Gson().toJson(new AccountRequest("John Doe")), false);
        final HttpResponse createOriginAccountResponse = testServer.execute(createOriginAccount);

        final PostMethod createDestinationAccount = testServer.post("/api/accounts",
            new Gson().toJson(new AccountRequest("Joseph Doe")), false);
        final HttpResponse createDestinationAccountResponse = testServer.execute(createDestinationAccount);

        final Account originAccount = new Gson().fromJson(
            new String(createOriginAccountResponse.body(), UTF_8), Account.class);
        final Account destinationAccount = new Gson().fromJson(
            new String(createDestinationAccountResponse.body(), UTF_8), Account.class);

        final String transferBody = new Gson().toJson(
            new TransferRequest(originAccount.getNumber(), destinationAccount.getNumber(), TEN));
        final PostMethod transfer = testServer.post("/api/transfers", transferBody, false);
        final HttpResponse transferResponse = testServer.execute(transfer);

        assertEquals(BAD_REQUEST_400, transferResponse.code());
    }

    @Test
    public void testTransferShouldReturnBadRequestIfOriginAndDestinationAccountsAreSame() throws HttpClientException {
        final PostMethod createOriginAccount = testServer.post("/api/accounts",
            new Gson().toJson(new AccountRequest("John Doe")), false);
        final HttpResponse createOriginAccountResponse = testServer.execute(createOriginAccount);

        final Account originAccount = new Gson().fromJson(
            new String(createOriginAccountResponse.body(), UTF_8), Account.class);

        final String transferBody = new Gson().toJson(
            new TransferRequest(originAccount.getNumber(), originAccount.getNumber(), ONE));
        final PostMethod transfer = testServer.post("/api/transfers", transferBody, false);
        final HttpResponse transferResponse = testServer.execute(transfer);

        assertEquals(BAD_REQUEST_400, transferResponse.code());
    }

    @Test
    public void testTransferShouldReturnSuccessIfOriginAccountHasSufficientBalance() throws HttpClientException {
        final PostMethod createOriginAccount = testServer.post("/api/accounts",
            new Gson().toJson(new AccountRequest("John Doe")), false);
        final HttpResponse createOriginAccountResponse = testServer.execute(createOriginAccount);

        final PostMethod createDestinationAccount = testServer.post("/api/accounts",
            new Gson().toJson(new AccountRequest("Joseph Doe")), false);
        final HttpResponse createDestinationAccountResponse = testServer.execute(createDestinationAccount);

        final Account originAccount = new Gson().fromJson(
            new String(createOriginAccountResponse.body(), UTF_8), Account.class);
        final Account destinationAccount = new Gson().fromJson(
            new String(createDestinationAccountResponse.body(), UTF_8), Account.class);

        final String depositBody = new Gson().toJson(new UpdateBalanceRequest(TEN));
        final PutMethod deposit = testServer.put(
            "/api/accounts/" + originAccount.getNumber() + "/deposit", depositBody, false);
        testServer.execute(deposit);

        final String transferBody = new Gson().toJson(
            new TransferRequest(originAccount.getNumber(), destinationAccount.getNumber(), ONE));
        final PostMethod transfer = testServer.post("/api/transfers", transferBody, false);
        final HttpResponse transferResponse = testServer.execute(transfer);

        assertEquals(OK_200, transferResponse.code());
    }
}
