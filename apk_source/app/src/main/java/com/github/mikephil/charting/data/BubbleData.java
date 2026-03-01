package com.github.mikephil.charting.data;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class BubbleData extends BarLineScatterCandleBubbleData<BubbleDataSet> {
    public BubbleData() {
    }

    public BubbleData(List<String> xVals) {
        super(xVals);
    }

    public BubbleData(String[] xVals) {
        super(xVals);
    }

    public BubbleData(List<String> xVals, List<BubbleDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public BubbleData(String[] xVals, List<BubbleDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public BubbleData(List<String> xVals, BubbleDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    public BubbleData(String[] xVals, BubbleDataSet dataSet) {
        super(xVals, toList(dataSet));
    }

    private static List<BubbleDataSet> toList(BubbleDataSet dataSet) {
        List<BubbleDataSet> sets = new ArrayList<>();
        sets.add(dataSet);
        return sets;
    }

    public void setHighlightCircleWidth(float width) {
        for (T set : this.mDataSets) {
            set.setHighlightCircleWidth(width);
        }
    }
}
