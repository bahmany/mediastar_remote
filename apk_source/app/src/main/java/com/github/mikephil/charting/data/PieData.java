package com.github.mikephil.charting.data;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class PieData extends ChartData<PieDataSet> {
    public PieData() {
    }

    public PieData(List<String> xVals) {
        super(xVals);
    }

    public PieData(String[] xVals) {
        super(xVals);
    }

    public PieData(List<String> xVals, PieDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    public PieData(String[] xVals, PieDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    private static List<PieDataSet> toList(PieDataSet dataSet) {
        List<PieDataSet> sets = new ArrayList<>();
        sets.add(dataSet);
        return sets;
    }

    public void setDataSet(PieDataSet dataSet) {
        this.mDataSets.clear();
        this.mDataSets.add(dataSet);
        init();
    }

    public PieDataSet getDataSet() {
        return (PieDataSet) this.mDataSets.get(0);
    }

    @Override // com.github.mikephil.charting.data.ChartData
    public PieDataSet getDataSetByIndex(int index) {
        if (index == 0) {
            return getDataSet();
        }
        return null;
    }

    @Override // com.github.mikephil.charting.data.ChartData
    public PieDataSet getDataSetByLabel(String label, boolean ignorecase) {
        if (ignorecase) {
            if (label.equalsIgnoreCase(((PieDataSet) this.mDataSets.get(0)).getLabel())) {
                return (PieDataSet) this.mDataSets.get(0);
            }
            return null;
        }
        if (label.equals(((PieDataSet) this.mDataSets.get(0)).getLabel())) {
            return (PieDataSet) this.mDataSets.get(0);
        }
        return null;
    }
}
