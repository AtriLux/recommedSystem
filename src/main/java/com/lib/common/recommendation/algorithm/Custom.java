package com.lib.common.recommendation.algorithm;

import com.lib.common.Range;
import com.lib.common.vectors.AbstractVector;
import com.lib.common.vectors.CustomVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class Custom {
    private static boolean debug = false;

    public static boolean isMaxDegree(CustomVector object, CustomVector profile, ArrayList<Range> range, double maxDegree) {
        profile.resize(range);

        findDegree(object, profile);

        return object.getDegree() <= maxDegree;
    }

    public static void sortByDegree(ArrayList<AbstractVector> list, CustomVector profile, ArrayList<Range> range) {
        profile.resize(range);

        for (AbstractVector object : list) {
            findDegree((CustomVector) object, profile);
        }

        Collections.sort(list);
    }

    private static void findDegree(CustomVector object, CustomVector profile) {
        double num = 0, objDen = 0, profileDen = 0;

        for (Map.Entry<String, Double> entry : profile.getNormNumParams().entrySet()) {
            String key = entry.getKey();
            double profileValue = entry.getValue();
            double value = object.getNormNumParams().get(key);
            num += value * profileValue;
            objDen += Math.pow(value, 2);
            profileDen += Math.pow(profileValue, 2);
        }

        double den = Math.sqrt(objDen) * Math.sqrt(profileDen);
        if (objDen == profileDen) den = objDen;
        object.setDegree(num / den);
    }
}
