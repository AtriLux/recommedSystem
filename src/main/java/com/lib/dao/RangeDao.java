package com.lib.dao;

import com.lib.common.Range;
import com.lib.dao.global.DataAccessObject;
import com.lib.dao.global.Hibernate;
import jakarta.persistence.TypedQuery;

public class RangeDao extends DataAccessObject<Range> {

    @Override
    public boolean contains(Range entity) {
        Range range = null;
        try {
            TypedQuery<Range> typedQuery = Hibernate.getEntityManager().createQuery(
                    "FROM " + getTableName() + " WHERE name = '" + entity.getName() + "'", getType());
            range = typedQuery.getSingleResult();
            Hibernate.getEntityManager().close();
        }
        catch (Exception ignored) {}
        return range != null;
    }

    @Override
    protected Class<Range> getType() { return Range.class; }
}
