package com.lib.dao;

import com.lib.common.RecommendationObject;
import com.lib.dao.global.DataAccessObject;
import com.lib.dao.global.Hibernate;
import jakarta.persistence.TypedQuery;

public class RecommendationObjectDao extends DataAccessObject<RecommendationObject> {

    @Override
    public boolean contains(RecommendationObject entity) {
        RecommendationObject object = null;
        try {
            TypedQuery<RecommendationObject> typedQuery = Hibernate.getEntityManager().createQuery(
                    "FROM " + getTableName() + " WHERE object_id = " + entity.getId(), getType());
            object = typedQuery.getSingleResult();
            Hibernate.getEntityManager().close();
        }
        catch (Exception ignored) {}
        return object != null;
    }

    @Override
    protected Class<RecommendationObject> getType() { return RecommendationObject.class; }
}
