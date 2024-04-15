package com.lib;

import com.lib.common.Range;
import com.lib.common.RecommendationObject;
import com.lib.common.recommendation.algorithm.*;
import com.lib.common.recommendation.*;
import com.lib.common.vectors.*;
import com.lib.dao.CustomProfileDao;
import com.lib.dao.FrequentMiningProfileDao;
import com.lib.dao.RangeDao;
import com.lib.dao.RecommendationObjectDao;
import com.lib.dao.global.Hibernate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.*;

public class RecommendationSystem {
    private final int id;
    private CustomVector customProfile;
    private ArrayList<FrequentMiningVector> frequentMiningProfile = new ArrayList<>();
    private Map<ParamWithRange, Double> template = new HashMap<>(); // [[ParamName, RangeNum], Lift]
    private final ArrayList<Range> rangeList = new ArrayList<>();

    private AlgorithmType algorithmType = AlgorithmType.FrequentMining;
    private int order = 2;
    private double minSimilarity = 0.4; // interval => [0;1]
    private double maxDegree = (1 - minSimilarity) * 90; // interval => [0;90]
    private final Map<String, Integer> rangeCnt;

    private static CustomProfileDao customProfileDao;
    private static FrequentMiningProfileDao frequentMiningProfileDao;
    private static RangeDao rangeDao;
    private static RecommendationObjectDao recommendationObjectDao;

    public RecommendationSystem(int id,
                                Map<String, Integer> rangeCnt,
                                EntityManagerFactory entityManagerFactory,
                                EntityManager entityManager) {
        this.id = id;
        this.rangeCnt = rangeCnt;

        loadDB(entityManagerFactory, entityManager);
    }

    public RecommendationSystem(int id,
                                Map<String, Integer> rangeCnt,
                                int order,
                                double apriori,
                                EntityManagerFactory entityManagerFactory,
                                EntityManager entityManager) {
        this.id = id;
        this.rangeCnt = rangeCnt;
        this.order = order;
        this.minSimilarity = apriori;

        if (minSimilarity < 0) minSimilarity = 0;
        if (minSimilarity > 1) minSimilarity = 1;
        maxDegree = (1 - minSimilarity) * 90;

        loadDB(entityManagerFactory, entityManager);
    }

    public RecommendationSystem(int id,
                                Map<String, Integer> rangeCnt,
                                int order,
                                double apriori,
                                AlgorithmType algorithmType,
                                EntityManagerFactory entityManagerFactory,
                                EntityManager entityManager) {
        this.id = id;
        this.rangeCnt = rangeCnt;
        this.algorithmType = algorithmType;
        this.order = order;
        this.minSimilarity = apriori;

        if (minSimilarity < 0) minSimilarity = 0;
        if (minSimilarity > 1) minSimilarity = 1;
        maxDegree = (1 - minSimilarity) * 90;

        loadDB(entityManagerFactory, entityManager);
    }

    public void clearProfile() {
        customProfileDao.delete(customProfile);
        for (FrequentMiningVector vector : frequentMiningProfile) {
            frequentMiningProfileDao.delete(vector);
            recommendationObjectDao.delete(vector.getObject());
        }

        customProfile = new CustomVector(rangeList, id);
        customProfileDao.insert(customProfile);

        frequentMiningProfile = new ArrayList<>();
    }

    private void loadDB(EntityManagerFactory entityManagerFactory,
                        EntityManager entityManager) {
        Hibernate.setEntityManagerFactory(entityManagerFactory);
        Hibernate.setEntityManager(entityManager);

        customProfileDao = new CustomProfileDao();
        frequentMiningProfileDao = new FrequentMiningProfileDao();
        rangeDao = new RangeDao();
        recommendationObjectDao = new RecommendationObjectDao();

        rangeList.addAll(rangeDao.findAll());
        if (rangeList.isEmpty()) {
            for (Map.Entry<String, Integer> entry : rangeCnt.entrySet()) {
                Range range = new Range(entry.getKey(), entry.getValue());
                rangeList.add(range);
                rangeDao.insert(range);
            }
        }
        else {
            for (Range range : rangeList) {
                range.initSize();
                range.resize();
            }
        }

        CustomVector tempCustomProfile = customProfileDao.findByField("profile_id", id, false);
        if (tempCustomProfile == null) {
            customProfile = new CustomVector(rangeList, id);
            customProfileDao.insert(customProfile);
        } else {
            customProfile = tempCustomProfile;
            customProfile.initNormNumParam();
        }

        frequentMiningProfile.addAll(frequentMiningProfileDao.findByFieldAll("profile_id", id, false));

        if (!frequentMiningProfile.isEmpty()) {
            for (FrequentMiningVector vector : frequentMiningProfile) {
                vector.resize(rangeList);
            }
            template = FrequentMining.findTemplate(frequentMiningProfile, rangeCnt, order, minSimilarity);
        }
    }
    
    public void addObject(RecommendationObject object) {
        if (object != null) {
            updateRange(object);

            customProfile.addToProfile(object, 1);

            FrequentMiningVector fVector = new FrequentMiningVector(object, rangeList, id);
            frequentMiningProfile.add(fVector);

            template = FrequentMining.findTemplate(frequentMiningProfile, rangeCnt, order, minSimilarity);

            recommendationObjectDao.insert(object);
            frequentMiningProfileDao.insert(fVector);
            customProfileDao.update(customProfile);
        }
    }
    
    public boolean isObjectRecommend(RecommendationObject object) {
        AbstractVector vector;

        if (updateRange(object))
            template = FrequentMining.findTemplate(frequentMiningProfile, rangeCnt, order, minSimilarity);

        switch (algorithmType) {
            case FrequentMining -> {
                vector = new FrequentMiningVector(object, rangeList, id);
                return FrequentMining.isTemplateWithLift((FrequentMiningVector) vector, template, minSimilarity);
            }
            case Custom -> {
                vector = new CustomVector(object, rangeList);
                return Custom.isMaxDegree((CustomVector) vector, customProfile, rangeList, maxDegree);
            }
            default -> {
                return false;
            }
        }
    }

    public ArrayList<RecommendationObject> filterRecommendObjects(ArrayList<RecommendationObject> objects) {
        ArrayList<RecommendationObject> list = new ArrayList<>();

        boolean isChange = false;
        for (RecommendationObject object : objects) {
            if (updateRange(object)) isChange = true;
        }

        if (isChange)
            template = FrequentMining.findTemplate(frequentMiningProfile, rangeCnt, order, minSimilarity);

        for (RecommendationObject object : objects) {
            switch (algorithmType) {
                case FrequentMining -> {
                    if (FrequentMining.isTemplateWithLift(new FrequentMiningVector(object, rangeList, id), template, minSimilarity))
                        list.add(object);
                }
                case Custom -> {
                    if (Custom.isMaxDegree(new CustomVector(object, rangeList), customProfile, rangeList, maxDegree))
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
                case FrequentMining -> list.add(new FrequentMiningVector(object, rangeList, id));
                case Custom -> list.add(new CustomVector(object, rangeList));
            }
        }

        if (isChange)
            template = FrequentMining.findTemplate(frequentMiningProfile, rangeCnt, order, minSimilarity);

        switch (algorithmType) {
            case FrequentMining -> {
                if (!template.isEmpty()) FrequentMining.sortByLift(list, template);
            }
            case Custom -> Custom.sortByDegree(list, customProfile, rangeList);
        }

        ArrayList<RecommendationObject> objList = new ArrayList<>();
        for (AbstractVector vector : list)
            objList.add(vector.getObject());

        return objList;
    }

    private boolean updateRange(RecommendationObject object) {
        boolean isChange = false;

        if (frequentMiningProfile.isEmpty()) {
            for (Range range : rangeList) {
                if (range.getMin().equals(range.getMax()) && range.getMin() == -1) {
                    Integer value = object.getNumParams().get(range.getName());
                    range.initValue(value);
                    isChange = true;
                }
            }
        }
        else {
            for (Range range : rangeList) {
                Integer value = object.getNumParams().get(range.getName());

                if (value < range.getMin())  {
                    range.setMin(value);
                    isChange = true;
                }
                else if (value > range.getMax()) {
                    range.setMax(value);
                    isChange = true;
                }
            }

            if (isChange) {
                for (FrequentMiningVector vector : frequentMiningProfile) {
                    vector.resize(rangeList);
                    frequentMiningProfileDao.update(vector);
                }
            }
        }

        if (isChange) {
            for (Range range : rangeList) {
                System.out.println("update range");
                rangeDao.update(range);
            }
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
        if (maxDegree > 90) maxDegree = 90;
        this.maxDegree = maxDegree;
        this.minSimilarity = maxDegree / 90;
    }

    public int getId() {
        return id;
    }
}
