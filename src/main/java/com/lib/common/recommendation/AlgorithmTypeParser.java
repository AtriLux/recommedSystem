package com.lib.common.recommendation;

public class AlgorithmTypeParser {
    private static final String noneName = "Нет";
    private static final String customName = "Векторизация";
    private static final String frequentMiningName = "Шаблоны";

    public static String parse(AlgorithmType type) {
        switch (type) {
            case None -> {
                return noneName;
            }
            case Custom -> {
                return customName;
            }
            case FrequentMining -> {
                return frequentMiningName;
            }
            default -> {
                return null;
            }
        }
    }

    public static AlgorithmType parse(String type) {
        switch (type) {
            case noneName -> {
                return AlgorithmType.None;
            }
            case customName -> {
                return AlgorithmType.Custom;
            }
            case frequentMiningName -> {
                return AlgorithmType.FrequentMining;
            }
            default -> {
                return AlgorithmType.valueOf(type);
            }
        }
    }

    public static AlgorithmType parse(int type) {
        if (type >= AlgorithmType.values().length)
            return null;
        else
            return AlgorithmType.values()[type];
    }
}
