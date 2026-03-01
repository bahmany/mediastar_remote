package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.BarDataProvider;

/* loaded from: classes.dex */
public class BarHighlighter extends ChartHighlighter<BarDataProvider> {
    public BarHighlighter(BarDataProvider chart) {
        super(chart);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.github.mikephil.charting.highlight.ChartHighlighter
    public Highlight getHighlight(float x, float y) {
        Highlight h = super.getHighlight(x, y);
        if (h != null) {
            BarDataSet set = (BarDataSet) ((BarDataProvider) this.mChart).getBarData().getDataSetByIndex(h.getDataSetIndex());
            if (set.isStacked()) {
                float[] pts = {0.0f, y};
                ((BarDataProvider) this.mChart).getTransformer(set.getAxisDependency()).pixelsToValue(pts);
                return getStackedHighlight(h, set, h.getXIndex(), h.getDataSetIndex(), pts[1]);
            }
            return h;
        }
        return h;
    }

    @Override // com.github.mikephil.charting.highlight.ChartHighlighter
    protected int getXIndex(float x) {
        if (!((BarDataProvider) this.mChart).getBarData().isGrouped()) {
            return super.getXIndex(x);
        }
        float baseNoSpace = getBase(x);
        int setCount = ((BarDataProvider) this.mChart).getBarData().getDataSetCount();
        int xIndex = ((int) baseNoSpace) / setCount;
        int valCount = ((BarDataProvider) this.mChart).getData().getXValCount();
        if (xIndex < 0) {
            return 0;
        }
        if (xIndex >= valCount) {
            return valCount - 1;
        }
        return xIndex;
    }

    @Override // com.github.mikephil.charting.highlight.ChartHighlighter
    protected int getDataSetIndex(int xIndex, float x, float y) {
        if (!((BarDataProvider) this.mChart).getBarData().isGrouped()) {
            return 0;
        }
        float baseNoSpace = getBase(x);
        int setCount = ((BarDataProvider) this.mChart).getBarData().getDataSetCount();
        int dataSetIndex = ((int) baseNoSpace) % setCount;
        if (dataSetIndex < 0) {
            return 0;
        }
        if (dataSetIndex >= setCount) {
            return setCount - 1;
        }
        return dataSetIndex;
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected Highlight getStackedHighlight(Highlight old, BarDataSet set, int xIndex, int dataSetIndex, double yValue) {
        BarEntry entry = (BarEntry) set.getEntryForXIndex(xIndex);
        if (entry == null || entry.getVals() == null) {
            return old;
        }
        Range[] ranges = getRanges(entry);
        int stackIndex = getClosestStackIndex(ranges, (float) yValue);
        return new Highlight(xIndex, dataSetIndex, stackIndex, ranges[stackIndex]);
    }

    protected int getClosestStackIndex(Range[] ranges, float value) {
        if (ranges == null) {
            return 0;
        }
        int stackIndex = 0;
        for (Range range : ranges) {
            if (range.contains(value)) {
                return stackIndex;
            }
            stackIndex++;
        }
        int length = Math.max(ranges.length - 1, 0);
        if (value <= ranges[length].to) {
            length = 0;
        }
        return length;
    }

    protected float getBase(float x) {
        float[] pts = {x, 0.0f};
        ((BarDataProvider) this.mChart).getTransformer(YAxis.AxisDependency.LEFT).pixelsToValue(pts);
        float xVal = pts[0];
        int setCount = ((BarDataProvider) this.mChart).getBarData().getDataSetCount();
        int steps = (int) (xVal / (((BarDataProvider) this.mChart).getBarData().getGroupSpace() + setCount));
        float groupSpaceSum = ((BarDataProvider) this.mChart).getBarData().getGroupSpace() * steps;
        float baseNoSpace = xVal - groupSpaceSum;
        return baseNoSpace;
    }

    protected Range[] getRanges(BarEntry entry) {
        float[] values = entry.getVals();
        if (values == null) {
            return null;
        }
        float negRemain = -entry.getNegativeSum();
        float posRemain = 0.0f;
        Range[] ranges = new Range[values.length];
        for (int i = 0; i < ranges.length; i++) {
            float value = values[i];
            if (value < 0.0f) {
                ranges[i] = new Range(negRemain, Math.abs(value) + negRemain);
                negRemain += Math.abs(value);
            } else {
                ranges[i] = new Range(posRemain, posRemain + value);
                posRemain += value;
            }
        }
        return ranges;
    }
}
