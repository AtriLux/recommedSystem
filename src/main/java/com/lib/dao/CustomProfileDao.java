package com.lib.dao;

import com.lib.common.vectors.CustomVector;
import com.lib.dao.global.DataAccessObject;
import com.lib.dao.global.Hibernate;
import jakarta.persistence.TypedQuery;

public class CustomProfileDao extends DataAccessObject<CustomVector> {

    @Override
    public boolean contains(CustomVector entity) {
        CustomVector object = null;
        try {
            TypedQuery<CustomVector> typedQuery = Hibernate.getEntityManager().createQuery(
                    "FROM " + getTableName() + " WHERE vector_id = " + entity.getId(), getType());
            object = typedQuery.getSingleResult();
            Hibernate.getEntityManager().close();
        }
        catch (Exception ignored) {}
        return object != null;
    }

    @Override
    protected Class<CustomVector> getType() {
        return CustomVector.class;
    }
}
