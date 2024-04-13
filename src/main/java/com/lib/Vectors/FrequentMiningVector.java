package com.lib.Vectors;

import com.lib.RecommendationObject;
import com.lib.Range;

import java.util.HashMap;
import java.util.Map;

public class FrequentMiningVector extends AbstractVector {
    private double lift;

    public FrequentMiningVector(RecommendationObject object, Map<String, Range> range) {
        this.object = object;
        this.lift = 0d;
        this.numParams = new HashMap<>();

        for (Map.Entry<String, Range> entry : range.entrySet()) {
            numParams.put(entry.getKey(), -1);
        }

        resize(range);
    }

    @Override
    public void resize(Map<String, Range> map) {
        lift = 0d;

        for (Map.Entry<String, Range> entry : map.entrySet()) {
            String key = entry.getKey();
            Range range = entry.getValue();

            boolean isFind = false;
            Integer value = object.getNumParams().get(key);
            for (int i = 0; i < range.getSize(); i++) {
                if (value < range.getRange()[i]) {
                    numParams.replace(key, i);
                    isFind = true;
                    break;
                }
            }

            if (!isFind) numParams.replace(key, range.getSize());
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
}
