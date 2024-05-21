package com.lib.common.vectors;

import com.lib.common.RecommendationObject;
import com.lib.common.Range;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name="frequent_mining_profile")
public class FrequentMiningVector extends AbstractVector {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_id")
    RecommendationObject object;

    @Transient // use only for sort ArrayList<Vector>
    private double lift;

    @ElementCollection
    @CollectionTable(name = "f_vector_param_value_mapping",
            joinColumns = {@JoinColumn(name = "vector_id", referencedColumnName = "vector_id")})
    @MapKeyColumn(name = "param")
    @Column(name = "value")
    protected Map<String, Integer> numParams;

    public FrequentMiningVector(RecommendationObject object, ArrayList<Range> rangeList, int id) {
        this.profile_id = id;
        this.object = object;
        this.lift = 0d;
        this.numParams = new HashMap<>();

        for (Range range : rangeList) {
            numParams.put(range.getName(), -1);
        }

        resize(rangeList);
    }

    public FrequentMiningVector() {

    }

    @Override
    public void resize(ArrayList<Range> rangeList) {
        lift = 0d;

        for (Range range : rangeList) {
            boolean isFind = false;
            Integer value = object.getNumParams().get(range.getName());
            for (int i = 0; i < range.getSize(); i++) {
                if (value < range.getRangeByIndex(i)) {
                    numParams.replace(range.getName(), i);
                    isFind = true;
                    break;
                }
            }

            if (!isFind) numParams.replace(range.getName(), range.getSize());
        }
    }

    // compareTo return inverse value
    @Override
    public int compareTo(AbstractVector v) {
        FrequentMiningVector fmv = (FrequentMiningVector) v;
        if (this.lift < fmv.lift)
            return 1;
        else if (fmv.lift < this.lift)
            return -1;
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder str =  new StringBuilder();
        for (Map.Entry<String, Integer> entry : numParams.entrySet()) {
            str.append(entry.getKey()).append("_").append(entry.getValue()).append(" ");
        }

        return "AbstractVector{lift=" + lift + ", numParams=[" + str + "], " + "object=" + object + "}";
    }

    public double getLift() {
        return lift;
    }

    public void setLift(double lift) {
        this.lift = lift;
    }

    public void addLift(double lift) {
        this.lift += lift;
    }

    @Override
    public RecommendationObject getObject() {
        return object;
    }

    public void setObject(RecommendationObject object) {
        this.object = object;
    }

    public Map<String, Integer> getNumParams() {
        return numParams;
    }

    public void setNumParams(Map<String, Integer> numParams) {
        this.numParams = numParams;
    }
}
