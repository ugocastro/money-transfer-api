package com.moneytransfer.domain.repository;

public interface SessionFactory<T extends Object> {

    T getSessionFactory();
}
