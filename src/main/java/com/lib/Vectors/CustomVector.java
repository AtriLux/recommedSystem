package com.lib.Vectors;

import com.lib.RecommendationObject;
import com.lib.Range;

import java.util.HashMap;
import java.util.Map;

public class CustomVector extends AbstractVector {
    private int id;
    Map<String, Double> normNumParams;
    private int weight;
    private double degree;

    // TODO ENUM-обработчик

    // init profile
    public CustomVector(Map<String, Range> range) {
        this.id = 0;
        this.weight = 0;
        this.object = null;
        this.degree = 0d;
        this.numParams = new HashMap<>();
        this.normNumParams = new HashMap<>();

        for (Map.Entry<String, Range> entry : range.entrySet()) {
            String key = entry.getKey();
            numParams.put(key, 0);
            normNumParams.put(key, 0d);
        }
    }

    // init vector
    public CustomVector(RecommendationObject object, Map<String, Range> range) {
        this.id = object.getId();
        this.object = object;
        this.weight = 1;
        this.degree = 0d;
        this.numParams = new HashMap<>();
        this.normNumParams = new HashMap<>();

        for (Map.Entry<String, Range> entry : range.entrySet()) {
            String key = entry.getKey();
            Integer value = object.getNumParams().get(key);
            numParams.put(key, value);
            normNumParams.put(key, 0d);
        }

        resize(range);
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
    public void resize(Map<String, Range> map) {
        for (Map.Entry<String, Range> entry : map.entrySet()) {
            String key = entry.getKey();
            Range range = entry.getValue();
            int value = numParams.get(key) / weight;
            double normValue = (double) (value - range.getMin()) / (range.getMax() - range.getMin());
            if (range.getMax().equals(range.getMin())) normValue = 1;
            normNumParams.replace(key, normValue);
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

        return "AbstractVector{id=" + id + ", weight=" + weight + ", degree=" + roundDegree + ", numParams=[" + str + "]}";
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
