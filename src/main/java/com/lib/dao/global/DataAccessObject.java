package com.lib.dao.global;

import jakarta.persistence.TypedQuery;

import java.util.List;

public abstract class DataAccessObject<T> {
    public DataAccessObject() {
        Hibernate.getEntityManager();
    }

    public List<T> findAll() {
        TypedQuery<T> typedQuery = Hibernate.getEntityManager().createQuery("FROM " + getTableName(), getType());
        List<T> list = typedQuery.getResultList();
        Hibernate.getEntityManager().close();
        return list;
    }

    public List<T> findByFieldAll(String parameter, Object object, boolean isString) {
        TypedQuery <T> typedQuery;
        if (isString) {
            typedQuery = Hibernate.getEntityManager().createQuery("FROM " + getTableName() +
                    " WHERE " + parameter + " = \'" + object + "\'", getType());
        }
        else {
            typedQuery = Hibernate.getEntityManager().createQuery("FROM " + getTableName() +
                    " WHERE " + parameter + " = " + object, getType());
        }
        List<T> list = typedQuery.getResultList();
        Hibernate.getEntityManager().close();
        return list;
    }

    public T findByField(String parameter, Object object, boolean isString) {
        TypedQuery<T> typedQuery;
        if (isString) {
            typedQuery = Hibernate.getEntityManager().createQuery("FROM " + getTableName() +
                    " WHERE " + parameter + " = \'" + object + "\'", getType());
        } else {
            typedQuery = Hibernate.getEntityManager().createQuery("FROM " + getTableName() +
                    " WHERE " + parameter + " = " + object, getType());
        }

        T result = null;
        try {
            result = typedQuery.getSingleResult();
            System.out.println("find single result by " + parameter);
        }
        catch (Exception e) {
            try {
                result = typedQuery.getResultList().get(0);
            }
            catch (Exception ignored) {}
        }

        Hibernate.getEntityManager().close();
        return result;
    }

    public void insert(T entity) {
        try {
            Hibernate.getTransaction().begin();
            Hibernate.getEntityManager().persist(entity);
            Hibernate.getTransaction().commit();
        } finally {
            if (Hibernate.getTransaction().isActive()){
                Hibernate.getTransaction().rollback();
            }
            Hibernate.getEntityManager().close();
        }
    }

    public void update(T entity) {
        try {
            Hibernate.getTransaction().begin();
            Hibernate.getEntityManager().merge(entity);
            Hibernate.getTransaction().commit();
        } finally {
            if (Hibernate.getTransaction().isActive()){
                Hibernate.getTransaction().rollback();
            }
            Hibernate.getEntityManager().close();
        }
    }

    public void delete(T entity) {
        try {
            Hibernate.getTransaction().begin();
            Hibernate.getEntityManager().remove(Hibernate.getEntityManager().contains(entity) ?
                    entity : Hibernate.getEntityManager().merge(entity));
            Hibernate.getTransaction().commit();
        } finally {
            if (Hibernate.getTransaction().isActive()){
                Hibernate.getTransaction().rollback();
            }
            Hibernate.getEntityManager().close();
        }
    }

    public abstract boolean contains(T entity);

    protected abstract Class<T> getType();

    protected String getTableName() {
        return getType().getSimpleName();
    }
}
