package com.moneytransfer.domain.repository;

import java.io.Serializable;

public interface BaseDao<T extends Serializable> {

    T save(T clazz) throws Exception;

    T findById(String id) throws Exception;
}
