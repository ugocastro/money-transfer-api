package com.moneytransfer.utils;

import com.google.gson.Gson;
import com.moneytransfer.domain.entities.Account;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonTransformerTest {

    @Test
    public void testObjectShouldBeConvertedIntoJson() {
        final Account account = new Account("John Doe");
        String json = new Gson().toJson(account);

        assertEquals(json, new JsonTransformer().render(account));
    }
}
