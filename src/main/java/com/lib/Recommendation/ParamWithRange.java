package com.lib.Recommendation;

import java.util.ArrayList;
import java.util.List;

public class ParamWithRange {
    private ArrayList<String> name;
    private ArrayList<Integer> range;

    public ParamWithRange(ArrayList<String> name, ArrayList<Integer> range) {
        this.name = name;
        this.range = range;
    }

    public ArrayList<ParamWithRange> convertToList() {
        ArrayList<ParamWithRange> list = new ArrayList<>();
        for (int i = 0; i < getSize(); i++) {
            ParamWithRange param = new ParamWithRange(
                    new ArrayList<>(List.of(getName().get(i))), new ArrayList<>(List.of(getRange().get(i))));
            list.add(param);
        }
        return list;
    }

    public int getSize() {
        return range.size();
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParamWithRange that = (ParamWithRange) o;
        if (that.name.size() != name.size() || that.range.size() != range.size()) return false;
        boolean isEquals = true;
        for (int i = 0; i < name.size(); i++) {
            if (!that.name.get(i).equals(name.get(i)) || !that.range.get(i).equals(range.get(i))) {
                isEquals = false;
                break;
            }
        }
        return isEquals;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < range.size(); i++) {
            hash += name.get(i).hashCode() + range.get(i).hashCode();
        }
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("[");
        for (int i = 0; i < range.size(); i++) {
            str.append(name.get(i)).append("_").append(range.get(i));
            if (i != range.size() - 1) str.append(", ");
        }
        str.append("]");
        return str.toString();
    }

    public ArrayList<String> getName() {
        return name;
    }

    public void setName(ArrayList<String> name) {
        this.name = name;
    }

    public ArrayList<Integer> getRange() {
        return range;
    }

    public void setRange(ArrayList<Integer> range) {
        this.range = range;
    }
}
