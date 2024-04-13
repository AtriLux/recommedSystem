package com.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecommendationObject {
    private final int id;
    private Map<String, String> strParams = new HashMap<>();
    private Map<String, Integer> numParams = new HashMap<>();

    public RecommendationObject(int id, Map<String, String> strParams, Map<String, Integer> numParams) {
        this.id = id;
        this.strParams.putAll(strParams);
        this.numParams.putAll(numParams);
    }

    public RecommendationObject(int id) {
        this.id = id;
    }

    public void addNumParam(Map<String, Integer> numParams) {
        this.numParams.putAll(numParams);
    }

    public void addNumParam(String name, Integer value) {
        this.numParams.put(name, value);
    }

    public void addNumParam(ArrayList<String> names, ArrayList<Integer> values) {
        int minSize = Math.min(names.size(), values.size());

        for (int i = 0; i < minSize; i++) {
            this.numParams.put(names.get(i), values.get(i));
        }
    }

    public void addStrParam(Map<String, String> strParams) {
        this.strParams.putAll(strParams);
    }

    public void addStrParam(String name, String value) {
        this.strParams.put(name, value);
    }

    public void addStrParam(ArrayList<String> names, ArrayList<String> values) {
        int minSize = Math.min(names.size(), values.size());

        for (int i = 0; i < minSize; i++) {
            this.strParams.put(names.get(i), values.get(i));
        }
    }

    @Override
    public String toString() {
        return "RecommendationObject{" +
                "id=" + id +
                ", numParams=" + numParams +
                ", strParams=" + strParams +
                '}';
    }

    public int getId() {
        return id;
    }

    public Map<String, String> getStrParams() {
        return strParams;
    }

    public void setStrParams(Map<String, String> strParams) {
        this.strParams = strParams;
    }

    public Map<String, Integer> getNumParams() {
        return numParams;
    }

    public void setNumParams(Map<String, Integer> numParams) {
        this.numParams = numParams;
    }
}
