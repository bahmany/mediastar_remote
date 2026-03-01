package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.interfaces.BarLineScatterCandleBubbleDataProvider;
import com.github.mikephil.charting.utils.SelectionDetail;
import com.github.mikephil.charting.utils.Utils;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ChartHighlighter<T extends BarLineScatterCandleBubbleDataProvider> {
    protected T mChart;

    public ChartHighlighter(T chart) {
        this.mChart = chart;
    }

    public Highlight getHighlight(float x, float y) {
        int dataSetIndex;
        int xIndex = getXIndex(x);
        if (xIndex == -2147483647 || (dataSetIndex = getDataSetIndex(xIndex, x, y)) == -2147483647) {
            return null;
        }
        return new Highlight(xIndex, dataSetIndex);
    }

    protected int getXIndex(float x) {
        float[] pts = {x, 0.0f};
        this.mChart.getTransformer(YAxis.AxisDependency.LEFT).pixelsToValue(pts);
        return Math.round(pts[0]);
    }

    protected int getDataSetIndex(int xIndex, float x, float y) {
        List<SelectionDetail> valsAtIndex = getSelectionDetailsAtIndex(xIndex);
        float leftdist = Utils.getMinimumDistance(valsAtIndex, y, YAxis.AxisDependency.LEFT);
        float rightdist = Utils.getMinimumDistance(valsAtIndex, y, YAxis.AxisDependency.RIGHT);
        YAxis.AxisDependency axis = leftdist < rightdist ? YAxis.AxisDependency.LEFT : YAxis.AxisDependency.RIGHT;
        int dataSetIndex = Utils.getClosestDataSetIndex(valsAtIndex, y, axis);
        return dataSetIndex;
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.github.mikephil.charting.data.DataSet] */
    protected List<SelectionDetail> getSelectionDetailsAtIndex(int xIndex) {
        List<SelectionDetail> vals = new ArrayList<>();
        float[] pts = new float[2];
        for (int i = 0; i < this.mChart.getData().getDataSetCount(); i++) {
            ?? dataSetByIndex = this.mChart.getData().getDataSetByIndex(i);
            if (dataSetByIndex.isHighlightEnabled()) {
                float yVal = dataSetByIndex.getYValForXIndex(xIndex);
                if (yVal != Float.NaN) {
                    pts[1] = yVal;
                    this.mChart.getTransformer(dataSetByIndex.getAxisDependency()).pointValuesToPixel(pts);
                    if (!Float.isNaN(pts[1])) {
                        vals.add(new SelectionDetail(pts[1], i, dataSetByIndex));
                    }
                }
            }
        }
        return vals;
    }
}
