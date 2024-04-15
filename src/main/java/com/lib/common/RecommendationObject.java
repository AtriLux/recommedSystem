package com.lib.common;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name="lib")
public class RecommendationObject {
    @Id
    @GeneratedValue(generator = "increment")
    @Column(name="object_id")
    private int object_id;

    @Transient // TODO change to '@ElementCollection/@CollectionTable/@MapKeyColumn/@Column' when strParams will be useful
    private Map<String, String> strParams = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "object_param_value_mapping",
            joinColumns = {@JoinColumn(name = "object_id", referencedColumnName = "object_id")})
    @MapKeyColumn(name = "param")
    @Column(name = "value")
    private Map<String, Integer> numParams = new HashMap<>();

    @Transient
    private int innerId;

    public RecommendationObject(int innerId, Map<String, String> strParams, Map<String, Integer> numParams) {
        this.innerId = innerId;
        this.strParams.putAll(strParams);
        this.numParams.putAll(numParams);
    }

    public RecommendationObject(int id) {
        this.innerId = id;
    }

    public RecommendationObject() {

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
                "id=" + innerId +
                ", numParams=" + numParams +
                ", strParams=" + strParams +
                '}';
    }

    public int getInnerId() {
        return innerId;
    }

    public int getId() {
        return object_id;
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
