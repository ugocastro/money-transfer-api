package com.revolut.domain.repository;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

@Slf4j
public abstract class HibernateDao<T extends Serializable> implements BaseDao<T> {

    private final SessionFactory sessionFactory = new HibernateSessionFactory();

    public T save(final T clazz) throws Exception {
        Session session = null;

        try {
            session = ((org.hibernate.SessionFactory) this.sessionFactory.getSessionFactory())
                .openSession();
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

    public T findById(final String id) throws Exception {
        Session session = null;

        try {
            session = ((org.hibernate.SessionFactory) this.sessionFactory.getSessionFactory())
                .openSession();
            Class clazz = ((Class) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
            return (T) session.get(clazz, id);
        } catch (Exception e) {
            log.error("Error retrieving information from database", e.getMessage());

            if (session != null) {
                session.close();
            }

            throw new Exception("Error retrieving information from database");
        }
    }
}
