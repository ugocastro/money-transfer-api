package com.revolut.domain.repository;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

@Slf4j
public class HibernateSessionFactory implements SessionFactory<org.hibernate.SessionFactory> {

    private StandardServiceRegistry registry;
    private org.hibernate.SessionFactory sessionFactory;

    @Override
    public org.hibernate.SessionFactory getSessionFactory() {
        if (this.sessionFactory == null) {
            try {
                this.registry = new StandardServiceRegistryBuilder().configure().build();
                MetadataSources sources = new MetadataSources(this.registry);
                Metadata metadata = sources.getMetadataBuilder().build();
                this.sessionFactory = metadata.getSessionFactoryBuilder().build();
            } catch (Exception e) {
                log.error("Error creating session factory", e.getMessage());
                shutdown();
            }
        }

        return this.sessionFactory;
    }

    private void shutdown() {
        if (this.registry != null) {
            StandardServiceRegistryBuilder.destroy(this.registry);
        }
    }
}
