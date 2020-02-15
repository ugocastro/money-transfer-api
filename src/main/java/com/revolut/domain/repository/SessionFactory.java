package com.revolut.domain.repository;

public interface SessionFactory<T extends Object> {

    T getSessionFactory();
}
