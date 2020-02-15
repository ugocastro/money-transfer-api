package com.revolut.domain.dao;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

import java.io.Serializable;

import static com.revolut.domain.repository.SessionFactory.getSessionFactory;

@Slf4j
public abstract class HibernateDao<T extends Serializable> {

    public T save(final T clazz) throws Exception {
        Session session = null;
        try {
            session = getSessionFactory().openSession();
            session.beginTransaction();
            session.saveOrUpdate(clazz);
            session.getTransaction().commit();

            return clazz;
        } catch (Exception e) {
            log.error("Error saving " + clazz.getClass().getName(), e.getMessage());

            if (session != null) {
                if (session.getTransaction() != null) {
                    session.getTransaction().rollback();
                }
                session.close();
            }

            throw new Exception("Error saving " + clazz.getClass().getName());
        }
    }
}
