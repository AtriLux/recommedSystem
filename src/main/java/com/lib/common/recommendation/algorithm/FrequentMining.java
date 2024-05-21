package com.lib.common.recommendation.algorithm;

import com.lib.common.recommendation.ParamWithRange;
import com.lib.common.vectors.AbstractVector;
import com.lib.common.vectors.FrequentMiningVector;

import java.util.*;

public class FrequentMining {
    private static boolean debug = false;

    public static boolean isTemplate(FrequentMiningVector object, Map<ParamWithRange, Double> template) {
        for (Map.Entry<ParamWithRange, Double> entry : template.entrySet()) {
            ArrayList<String> param = entry.getKey().getName();
            ArrayList<Integer> range = entry.getKey().getRange();

            int i;
            for (i = 0; i < entry.getKey().getSize(); i++) {
                if (!object.getNumParams().get(param.get(i)).equals(range.get(i))) break;
            }
            if (i < entry.getKey().getSize()) continue;

            return entry.getValue() >= 0;
        }
        return false;
    }

    public static boolean isTemplateWithLift(FrequentMiningVector object, Map<ParamWithRange, Double> template, Double minLift) {
        for (Map.Entry<ParamWithRange, Double> entry : template.entrySet()) {
            if (entry.getValue() > 0 && entry.getValue() < minLift) continue;

            ArrayList<String> param = entry.getKey().getName();
            ArrayList<Integer> range = entry.getKey().getRange();

            int i;
            for (i = 0; i < entry.getKey().getSize(); i++) {
                if (!object.getNumParams().get(param.get(i)).equals(range.get(i))) break;
            }
            if (i < entry.getKey().getSize()) continue;

            return entry.getValue() >= 0;
        }
        return false;
    }

    public static void sortByLift(ArrayList<AbstractVector> list, Map<ParamWithRange, Double> template) {
        for (AbstractVector vector : list) {
            for (Map.Entry<ParamWithRange, Double> entry : template.entrySet()) {
                ArrayList<String> param = entry.getKey().getName();
                ArrayList<Integer> range = entry.getKey().getRange();

                int i;
                for (i = 0; i < entry.getKey().getSize(); i++) {
                    if (!vector.getNumParams().get(param.get(i)).equals(range.get(i))) break;
                }
                if (i < entry.getKey().getSize()) continue;

                ((FrequentMiningVector) vector).addLift(entry.getValue());
            }
        }
        Collections.sort(list);
    }

    public static Map<ParamWithRange, Double> findTemplate(ArrayList<FrequentMiningVector> profile, Map<String, Integer> rangeCnt, int order, double aprioriMinSupport) {

        Map<ParamWithRange, Double> basisTemplate = findSupport(profile, rangeCnt, null);
        Map<ParamWithRange, Double> template = apriori(basisTemplate, aprioriMinSupport);

        if (debug) printMap(basisTemplate);

        for (int i = 1; i < order && i < rangeCnt.size(); i++) {
            template = findSupport(profile, rangeCnt, template);
            template = apriori(template, aprioriMinSupport);

            // check support after apriori
            if (debug) printMap(template);
        }

        template = findLift(basisTemplate, template);

        if (debug) printMap(template);

        return template;
    }

    private static void printMap(Map<ParamWithRange, Double> map) {
        System.out.println("----------------------------");
        for (Map.Entry<ParamWithRange, Double> entry : map.entrySet()) {
            System.out.println("key: " + entry.getKey() + ", value: " + entry.getValue());
        }
        System.out.println("Size: " + map.size());
    }

    private static Double convertLift(Double lift) {
        if (lift >= 1) return lift - 1;
        else return 1 - lift;
    }

    private static Map<ParamWithRange, Double> apriori(Map<ParamWithRange, Double> support, double aprioriMinSupport) {
        Map<ParamWithRange, Double> filteredSupport = new HashMap<>();

        for (Map.Entry<ParamWithRange, Double> entry : support.entrySet())
            if (entry.getValue() >= aprioriMinSupport)
                filteredSupport.put(entry.getKey(), entry.getValue());

        return filteredSupport;
    }

    private static Map<ParamWithRange, Double> fillParamWithRange(Map<String, Integer> numParams, Map<String, Integer> rangeCnt, Map<ParamWithRange, Double> map) {
        Map<ParamWithRange, Double> newMap = new HashMap<>();

        if (map == null) {
            for (Map.Entry<String, Integer> entry : numParams.entrySet()) {
                String key = entry.getKey();
                for (int r = 0; r < rangeCnt.get(key); r++) {
                    ParamWithRange param = new ParamWithRange(
                            new ArrayList<>(List.of(key)), new ArrayList<>(List.of(r)));
                    newMap.put(param, 0d);
                }
            }
        }
        else {
            for (Map.Entry<ParamWithRange, Double> entry : map.entrySet()) {
                ArrayList<String> nameList = new ArrayList<>(entry.getKey().getName());
                ArrayList<Integer> rangeList = new ArrayList<>(entry.getKey().getRange());

                for (Map.Entry<String, Integer> param : numParams.entrySet()) {
                    String key = param.getKey();
                    if (nameList.contains(key)) continue;

                    for (int r = 0; r < rangeCnt.get(key); r++) {
                        ArrayList<String> newNameList = new ArrayList<>(nameList);
                        ArrayList<Integer> newRangeList = new ArrayList<>(rangeList);
                        newNameList.add(param.getKey());
                        newRangeList.add(r);
                        ParamWithRange paramWithRange = new ParamWithRange(newNameList, newRangeList);
                        newMap.put(paramWithRange, 0d);
                    }
                }
            }
        }

        return newMap;
    }

    private static Map<ParamWithRange, Double> findSupport(ArrayList<FrequentMiningVector> profile, Map<String, Integer> rangeCnt, Map<ParamWithRange, Double> support) {
        Map<ParamWithRange, Double> newSupport = fillParamWithRange(profile.get(0).getNumParams(), rangeCnt, support);

        if (support == null) {
            for (FrequentMiningVector vector : profile) {
                for (Map.Entry<String, Integer> entry : vector.getNumParams().entrySet()) {
                    ParamWithRange param = new ParamWithRange(
                            new ArrayList<>(List.of(entry.getKey())), new ArrayList<>(List.of(entry.getValue())));
                    newSupport.replace(param, newSupport.get(param) + 1d / profile.size());
                }
            }
        }
        else {
            for (FrequentMiningVector vector : profile) {
                ArrayList<String> checkedNameList = new ArrayList<>();

                for (Map.Entry<ParamWithRange, Double> list : support.entrySet()) {
                    ArrayList<String> nameList = new ArrayList<>(list.getKey().getName());
                    ArrayList<Integer> rangeList = new ArrayList<>(list.getKey().getRange());

                    int i;
                    for (i = 0; i < list.getKey().getSize(); i++) {
                        if (!vector.getNumParams().get(nameList.get(i)).equals(rangeList.get(i))) break;
                    }
                    if (i < list.getKey().getSize()) continue;

                    checkedNameList.addAll(list.getKey().getName());

                    for (Map.Entry<String, Integer> entry : vector.getNumParams().entrySet()) {
                        if (nameList.contains(entry.getKey())) continue;
                        if (checkedNameList.contains(entry.getKey())) continue;

                        ArrayList<String> newNameList = new ArrayList<>(nameList);
                        ArrayList<Integer> newRangeList = new ArrayList<>(rangeList);
                        newNameList.add(entry.getKey());
                        newRangeList.add(entry.getValue());
                        ParamWithRange param = new ParamWithRange(newNameList, newRangeList);
                        newSupport.replace(param, newSupport.get(param) + 1d / profile.size());
                    }
                }
            }
        }

        return newSupport;
    }

    private static Map<ParamWithRange, Double> findLift(Map<ParamWithRange, Double> basisTemplate, Map<ParamWithRange, Double> template) {
        Map<ParamWithRange, Double> newTemplate = new HashMap<>();

        for (Map.Entry<ParamWithRange, Double> list : template.entrySet()) {
            Double basisSupport = 1d;
            ParamWithRange paramWithRange = list.getKey();
            ArrayList<ParamWithRange> param = paramWithRange.convertToList();
            for (ParamWithRange entry : param) {
                basisSupport *= basisTemplate.get(entry);
            }
            double lift = convertLift(list.getValue()/basisSupport);
            if (lift != 0) newTemplate.put(list.getKey(), lift);
        }

        return newTemplate;
    }
}
