package com.github.mikephil.charting.data;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class RadarData extends ChartData<RadarDataSet> {
    public RadarData() {
    }

    public RadarData(List<String> xVals) {
        super(xVals);
    }

    public RadarData(String[] xVals) {
        super(xVals);
    }

    public RadarData(List<String> xVals, List<RadarDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public RadarData(String[] xVals, List<RadarDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public RadarData(List<String> xVals, RadarDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    public RadarData(String[] xVals, RadarDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    private static List<RadarDataSet> toList(RadarDataSet dataSet) {
        List<RadarDataSet> sets = new ArrayList<>();
        sets.add(dataSet);
        return sets;
    }
}
