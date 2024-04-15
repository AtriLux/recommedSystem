package com.lib.dao.global;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public class Hibernate {
    private static EntityManagerFactory entityManagerFactory;
    private static EntityManager entityManager;
    private static EntityTransaction transaction;

    public static void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        Hibernate.entityManagerFactory = entityManagerFactory;
    }

    public static void setEntityManager(EntityManager entityManager) {
        Hibernate.entityManager = entityManager;
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public static EntityManager getEntityManager() {
        if (entityManager == null || !entityManager.isOpen())
            entityManager = getEntityManagerFactory().createEntityManager();
        return entityManager;
    }

    public static EntityTransaction getTransaction() {
        if (transaction == null || !transaction.isActive())
            transaction = getEntityManager().getTransaction();
        return transaction;
    }
}
