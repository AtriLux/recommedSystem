package com.lib;

import com.lib.Recommendation.Algorithm.*;
import com.lib.Recommendation.AlgorithmType;
import com.lib.Recommendation.ParamWithRange;
import com.lib.Vectors.*;

import java.util.*;

public class RecommendationSystem {
    private CustomVector customProfile;
    private ArrayList<FrequentMiningVector> frequentMiningProfile = new ArrayList<>();
    private Map<ParamWithRange, Double> template = new HashMap<>(); // [[ParamName, RangeNum], Lift]
    private Map<String, Range> range = new HashMap<>(); // [ParamName, [N, min, range1, ..., rangeN-1, max]]

    private AlgorithmType algorithmType = AlgorithmType.FrequentMining;
    private int order = 2;
    private double minSimilarity = 0.4; // interval => [0;1]
    private double maxDegree = (1 - minSimilarity) * 90;
    private Map<String, Integer> rangeCnt;

    public RecommendationSystem(Map<String, Integer> rangeCnt) {
        this.rangeCnt = rangeCnt;

        for (Map.Entry<String, Integer> entry : rangeCnt.entrySet()) {
            Range r = new Range(entry.getValue());
            range.put(entry.getKey(), r);
        }

        customProfile = new CustomVector(range);

        loadDB();
    }

    public RecommendationSystem(Map<String, Integer> rangeCnt, int order, double apriori) {
        this.rangeCnt = rangeCnt;
        this.order = order;
        this.minSimilarity = apriori;

        if (minSimilarity < 0) minSimilarity = 0;
        if (minSimilarity > 1) minSimilarity = 1;
        maxDegree = (1 - minSimilarity) * 90;

        for (Map.Entry<String, Integer> entry : rangeCnt.entrySet()) {
            Range r = new Range(entry.getValue());
            range.put(entry.getKey(), r);
        }

        customProfile = new CustomVector(range);

        loadDB();
    }

    public RecommendationSystem(Map<String, Integer> rangeCnt, int order, double apriori, AlgorithmType algorithmType) {
        this.rangeCnt = rangeCnt;
        this.algorithmType = algorithmType;
        this.order = order;
        this.minSimilarity = apriori;

        if (minSimilarity < 0) minSimilarity = 0;
        if (minSimilarity > 1) minSimilarity = 1;
        maxDegree = (1 - minSimilarity) * 90;

        for (Map.Entry<String, Integer> entry : rangeCnt.entrySet()) {
            Range r = new Range(entry.getValue());
            range.put(entry.getKey(), r);
        }

        customProfile = new CustomVector(range);

        loadDB();
    }

    private void loadDB() {
        // TODO load private fields from database
        // TODO init range
        // TODO frequentMiningProfile.resize()
    }
    
    public void addObject(RecommendationObject object) {
        if (object != null) {
            updateRange(object);

            customProfile.addToProfile(object, 1);

            FrequentMiningVector fVector = new FrequentMiningVector(object, range);
            frequentMiningProfile.add(fVector);

            template = FrequentMining.findTemplate(frequentMiningProfile, rangeCnt, order, minSimilarity);

            // TODO frequentMiningProfile.saveDB();
            // TODO customProfile.saveDB();
        }
    }
    
    public boolean isObjectRecommend(RecommendationObject object) {
        AbstractVector vector;

        if (updateRange(object))
            template = FrequentMining.findTemplate(frequentMiningProfile, rangeCnt, order, minSimilarity);

        switch (algorithmType) {
            case FrequentMining -> {
                vector = new FrequentMiningVector(object, range);
                return FrequentMining.isTemplateWithLift((FrequentMiningVector) vector, template, minSimilarity);
            }
            case Custom -> {
                vector = new CustomVector(object, range);
                return Custom.isMaxDegree((CustomVector) vector, customProfile, range, maxDegree);
            }
            default -> {
                return false;
            }
        }
    }

    public ArrayList<RecommendationObject> filterRecommendObjects(ArrayList<RecommendationObject> objects) {
        ArrayList<RecommendationObject> list = new ArrayList<>();
        for (RecommendationObject object : objects) {
            if (updateRange(object))
                template = FrequentMining.findTemplate(frequentMiningProfile, rangeCnt, order, minSimilarity);

            switch (algorithmType) {
                case FrequentMining -> {
                    if (FrequentMining.isTemplateWithLift(new FrequentMiningVector(object, range), template, minSimilarity))
                        list.add(object);
                }
                case Custom -> {
                    if (Custom.isMaxDegree(new CustomVector(object, range), customProfile, range, maxDegree))
                        list.add(object);
                }
            }
        }
        return list;
    }

    public ArrayList<RecommendationObject> sortRecommendObjects(ArrayList<RecommendationObject> objects) {
        ArrayList<AbstractVector> list = new ArrayList<>();
        boolean isChange = false;
        for (RecommendationObject object : objects) {
            if (updateRange(object))
                isChange = true;

            switch (algorithmType) {
                case FrequentMining -> list.add(new FrequentMiningVector(object, range));
                case Custom -> list.add(new CustomVector(object, range));
            }
        }

        if (isChange)
            template = FrequentMining.findTemplate(frequentMiningProfile, rangeCnt, order, minSimilarity);

        switch (algorithmType) {
            case FrequentMining -> {
                if (!template.isEmpty()) FrequentMining.sortByLift(list, template);
            }
            case Custom -> Custom.sortByDegree(list, customProfile, range);
        }

        ArrayList<RecommendationObject> objList = new ArrayList<>();
        for (AbstractVector vector : list)
            objList.add(vector.getObject());

        return objList;
    }

    private boolean updateRange(RecommendationObject object) {
        boolean isChange = false;

        if (frequentMiningProfile.isEmpty()) {
            for (Map.Entry<String, Range> entry : range.entrySet()) {
                String key = entry.getKey();
                Integer value = object.getNumParams().get(key);
                Range ran = entry.getValue();

                if (ran.getMin().equals(ran.getMax()) && ran.getMin() == -1) {
                    ran.init(value);
                    isChange = true;
                }
            }
        }
        else {
            for (Map.Entry<String, Range> entry : range.entrySet()) {
                String key = entry.getKey();
                Integer value = object.getNumParams().get(key);
                Range ran = entry.getValue();

                if (value < ran.getMin()) ran.setMin(value);
                else if (value > ran.getMax()) ran.setMax(value);
                ran.resize();

                isChange = true;
            }

            if (isChange) {
                for (FrequentMiningVector vector : frequentMiningProfile) {
                    vector.resize(range);
                }
            }
        }

        if (isChange) {
            // TODO frequentMiningProfile.saveDB();
            // TODO range.saveDB();
        }

        return isChange;
    }

    public AlgorithmType getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(AlgorithmType algorithmType) {
        this.algorithmType = algorithmType;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public double getMinSimilarity() {
        return minSimilarity;
    }

    public double getMaxDegree() {
        return maxDegree;
    }

    public void setMinSimilarity(double minSimilarity) {
        if (minSimilarity < 0) minSimilarity = 0;
        if (minSimilarity > 1) minSimilarity = 1;
        this.minSimilarity = minSimilarity;
        this.maxDegree = (1 - minSimilarity) * 90;
    }

    public void setMaxDegree(double maxDegree) {
        if (maxDegree < 0) maxDegree = 0;
        if (maxDegree > 180) maxDegree = 180;
        this.maxDegree = maxDegree;
        this.minSimilarity = maxDegree / 180;
    }
}
