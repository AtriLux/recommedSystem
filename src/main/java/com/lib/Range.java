package com.lib;

import java.util.Arrays;

public class Range {
    private Integer min, max;
    private Integer[] range;

    public Range(Integer size) {
        range = new Integer[size-1];
        min = -1;
        max = -1;
    }

    public void init(Integer value) {
        min = value;
        max = value;
        Arrays.fill(range, value);
    }

    public void resize() {
        int diff = max - min;
        double interval = (double) diff / getFullSize();

        for (int i = 0; i < getSize(); i++) {
            range[i] = Math.toIntExact(Math.round(min + interval * (i + 1)));
        }
    }

    public int getFullSize() { return range.length + 1; }

    public int getSize() { return range.length; }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer[] getRange() {
        return range;
    }

    public void setRange(Integer[] range) {
        this.range = range;
    }
}
