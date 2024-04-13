package com.lib.Vectors;

import com.lib.Range;
import com.lib.RecommendationObject;

import java.util.Map;

// TODO добавить вес действия
// TODO рекомендации на основе текстовых параметров

public abstract class AbstractVector implements Comparable<AbstractVector> {
    RecommendationObject object;
    Map<String, Integer> numParams;

    public abstract void resize(Map<String, Range> map);

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
