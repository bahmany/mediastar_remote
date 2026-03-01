package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.interfaces.BarDataProvider;

/* loaded from: classes.dex */
public class HorizontalBarHighlighter extends BarHighlighter {
    public HorizontalBarHighlighter(BarDataProvider chart) {
        super(chart);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.github.mikephil.charting.highlight.BarHighlighter, com.github.mikephil.charting.highlight.ChartHighlighter
    public Highlight getHighlight(float x, float y) {
        Highlight h = super.getHighlight(x, y);
        if (h != null) {
            BarDataSet set = (BarDataSet) ((BarDataProvider) this.mChart).getBarData().getDataSetByIndex(h.getDataSetIndex());
            if (set.isStacked()) {
                float[] pts = {y, 0.0f};
                ((BarDataProvider) this.mChart).getTransformer(set.getAxisDependency()).pixelsToValue(pts);
                return getStackedHighlight(h, set, h.getXIndex(), h.getDataSetIndex(), pts[0]);
            }
            return h;
        }
        return h;
    }

    @Override // com.github.mikephil.charting.highlight.BarHighlighter, com.github.mikephil.charting.highlight.ChartHighlighter
    protected int getXIndex(float x) {
        if (!((BarDataProvider) this.mChart).getBarData().isGrouped()) {
            float[] pts = {0.0f, x};
            ((BarDataProvider) this.mChart).getTransformer(YAxis.AxisDependency.LEFT).pixelsToValue(pts);
            return Math.round(pts[1]);
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

    @Override // com.github.mikephil.charting.highlight.BarHighlighter
    protected float getBase(float y) {
        float[] pts = {0.0f, y};
        ((BarDataProvider) this.mChart).getTransformer(YAxis.AxisDependency.LEFT).pixelsToValue(pts);
        float yVal = pts[1];
        int setCount = ((BarDataProvider) this.mChart).getBarData().getDataSetCount();
        int steps = (int) (yVal / (((BarDataProvider) this.mChart).getBarData().getGroupSpace() + setCount));
        float groupSpaceSum = ((BarDataProvider) this.mChart).getBarData().getGroupSpace() * steps;
        float baseNoSpace = yVal - groupSpaceSum;
        return baseNoSpace;
    }
}
