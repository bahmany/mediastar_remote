package com.github.mikephil.charting.data;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class LineData extends BarLineScatterCandleBubbleData<LineDataSet> {
    public LineData() {
    }

    public LineData(List<String> xVals) {
        super(xVals);
    }

    public LineData(String[] xVals) {
        super(xVals);
    }

    public LineData(List<String> xVals, List<LineDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public LineData(String[] xVals, List<LineDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public LineData(List<String> xVals, LineDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    public LineData(String[] xVals, LineDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    private static List<LineDataSet> toList(LineDataSet dataSet) {
        List<LineDataSet> sets = new ArrayList<>();
        sets.add(dataSet);
        return sets;
    }
}
