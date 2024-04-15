package com.lib.common;

import jakarta.persistence.*;

import java.util.Arrays;

@Entity
@Table(name="param_range")
public class Range {
    @Id
    @GeneratedValue(generator = "increment")
    @Column(name="range_id")
    private int range_id;
    @Column(name="name")
    private String name;
    @Column(name="num")
    private int num;
    @Column(name="min")
    private Integer min;
    @Column(name="max")
    private Integer max;

    @Transient
    private Integer[] range = null;

    public Range(String name, Integer size) {
        this.name = name;
        this.num = size;
        range = new Integer[size-1];
        min = -1;
        max = -1;
    }

    public Range() {}

    public void initValue(Integer value) {
        min = value;
        max = value;
        Arrays.fill(range, value);
    }

    public void initSize() {
        if (range == null) {
            range = new Integer[num - 1];
        }
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
        resize();
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
        resize();
    }

    public Integer[] getRange() {
        return range;
    }

    public Integer getRangeByIndex(int index) {
        if (index >= range.length) return -1;
        return range[index];
    }

    public void setRange(Integer[] range) {
        this.range = range;
    }

    public int getId() {
        return range_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
