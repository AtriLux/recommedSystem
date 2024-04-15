package com.lib.common.vectors;

import com.lib.common.RecommendationObject;
import com.lib.common.Range;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name="custom_profile")
public class CustomVector extends AbstractVector {
    @Transient
    RecommendationObject object;
    @Transient
    Map<String, Double> normNumParams = new HashMap<>();
    @Column(name="weight")
    private int weight;
    @Transient
    private double degree;


    @ElementCollection
    @CollectionTable(name = "c_vector_param_value_mapping",
            joinColumns = {@JoinColumn(name = "vector_id", referencedColumnName = "vector_id")})
    @MapKeyColumn(name = "param")
    @Column(name = "value")
    protected Map<String, Integer> numParams = new HashMap<>();;

    // init profile
    public CustomVector(ArrayList<Range> rangeList, int id) {
        this.profile_id = id;
        this.weight = 0;
        this.object = null;
        this.degree = 0d;

        for (Range range : rangeList) {
            String name = range.getName();
            numParams.put(name, 0);
            normNumParams.put(name, 0d);
        }
    }

    // init vector
    public CustomVector(RecommendationObject object, ArrayList<Range> rangeList) {
        this.object = object;
        this.weight = 1;
        this.degree = 0d;

        for (Range range : rangeList) {
            String name = range.getName();
            Integer value = object.getNumParams().get(name);
            numParams.put(name, value);
            normNumParams.put(name, 0d);
        }

        resize(rangeList);
    }

    public CustomVector() {

    }

    public void addToProfile(RecommendationObject object, int weight) {
        for (Map.Entry<String, Integer> entry : numParams.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            entry.setValue(value + object.getNumParams().get(key));
        }
        this.weight += weight;
    }

    @Override
    public void resize(ArrayList<Range> rangeList) {
        for (Range range : rangeList) {
            int value = numParams.get(range.getName()) / weight;
            double normValue = (double) (value - range.getMin()) / (range.getMax() - range.getMin());
            if (range.getMax().equals(range.getMin())) normValue = 1;
            normNumParams.replace(range.getName(), normValue);
        }
    }

    public void initNormNumParam() {
        for (Map.Entry<String, Integer> entry : numParams.entrySet()) {
            normNumParams.put(entry.getKey(), 0d);
        }
    }

    @Override
    public int compareTo(AbstractVector v) {
        CustomVector cv = (CustomVector) v;
        if (this.degree < cv.degree)
            return -1;
        else if (cv.degree < this.degree)
            return 1;
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder str =  new StringBuilder();
        for (Map.Entry<String, Integer> entry : numParams.entrySet()) {
            str.append(entry.getKey()).append("=").append(entry.getValue()/weight).append(" ");
        }

        double roundDegree = Math.ceil(degree * 1000) / 1000;

        return "AbstractVector{weight=" + weight + ", degree=" + roundDegree + ", numParams=[" + str + "]}";
    }

    public Map<String, Integer> getNumParams() {
        return numParams;
    }

    public void setNumParams(Map<String, Integer> numParams) {
        this.numParams = numParams;
    }

    public Map<String, Double> getNormNumParams() {
        return normNumParams;
    }

    public void setNormNumParams(Map<String, Double> normNumParams) {
        this.normNumParams = normNumParams;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public double getDegree() {
        return degree;
    }

    public void setDegree(double cos) {
        this.degree = Math.toDegrees(Math.acos(cos));
    }

    @Override
    public RecommendationObject getObject() {
        return object;
    }

    public void setObject(RecommendationObject object) {
        this.object = object;
    }
}
