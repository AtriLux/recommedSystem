package com.lib.dao;

import com.lib.common.vectors.FrequentMiningVector;
import com.lib.dao.global.DataAccessObject;
import com.lib.dao.global.Hibernate;
import jakarta.persistence.TypedQuery;

public class FrequentMiningProfileDao extends DataAccessObject<FrequentMiningVector> {

    @Override
    public boolean contains(FrequentMiningVector entity) {
        FrequentMiningVector object = null;
        try {
            TypedQuery<FrequentMiningVector> typedQuery = Hibernate.getEntityManager().createQuery(
                    "FROM " + getTableName() + " WHERE vector_id = " + entity.getId(), getType());
            object = typedQuery.getSingleResult();
            Hibernate.getEntityManager().close();
        }
        catch (Exception ignored) {}
        return object != null;
    }

    @Override
    protected Class<FrequentMiningVector> getType() { return FrequentMiningVector.class; }
}
