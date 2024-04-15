package com.lib.common.vectors;

import com.lib.common.Range;
import com.lib.common.RecommendationObject;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Map;

// TODO recommendation by strParam

@MappedSuperclass
public abstract class AbstractVector implements Comparable<AbstractVector> {
    @Id
    @GeneratedValue(generator = "increment")
    @Column(name="vector_id")
    private int vector_id;

    @Column(name="profile_id")
    protected int profile_id;

    public abstract void resize(ArrayList<Range> rangeList);

    public abstract RecommendationObject getObject();

    public abstract Map<String, Integer> getNumParams();

    public int getId() {
        return vector_id;
    }

    public int getProfileId() {
        return profile_id;
    }

    public void setProfileId(int profile_id) {
        this.profile_id = profile_id;
    }
}
