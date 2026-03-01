package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.BarHighlighter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.BarDataProvider;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.renderer.XAxisRendererBarChart;

/* loaded from: classes.dex */
public class BarChart extends BarLineChartBase<BarData> implements BarDataProvider {
    private boolean mDrawBarShadow;
    private boolean mDrawHighlightArrow;
    private boolean mDrawValueAboveBar;

    public BarChart(Context context) {
        super(context);
        this.mDrawHighlightArrow = false;
        this.mDrawValueAboveBar = true;
        this.mDrawBarShadow = false;
    }

    public BarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDrawHighlightArrow = false;
        this.mDrawValueAboveBar = true;
        this.mDrawBarShadow = false;
    }

    public BarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mDrawHighlightArrow = false;
        this.mDrawValueAboveBar = true;
        this.mDrawBarShadow = false;
    }

    @Override // com.github.mikephil.charting.charts.BarLineChartBase, com.github.mikephil.charting.charts.Chart
    protected void init() {
        super.init();
        this.mRenderer = new BarChartRenderer(this, this.mAnimator, this.mViewPortHandler);
        this.mXAxisRenderer = new XAxisRendererBarChart(this.mViewPortHandler, this.mXAxis, this.mLeftAxisTransformer, this);
        this.mHighlighter = new BarHighlighter(this);
        this.mXChartMin = -0.5f;
    }

    @Override // com.github.mikephil.charting.charts.BarLineChartBase, com.github.mikephil.charting.charts.Chart
    protected void calcMinMax() {
        super.calcMinMax();
        this.mDeltaX += 0.5f;
        this.mDeltaX = ((BarData) this.mData).getDataSetCount() * this.mDeltaX;
        float groupSpace = ((BarData) this.mData).getGroupSpace();
        this.mDeltaX = (((BarData) this.mData).getXValCount() * groupSpace) + this.mDeltaX;
        this.mXChartMax = this.mDeltaX - this.mXChartMin;
    }

    @Override // com.github.mikephil.charting.charts.BarLineChartBase
    public Highlight getHighlightByTouchPoint(float x, float y) {
        if (!this.mDataNotSet && this.mData != 0) {
            return this.mHighlighter.getHighlight(x, y);
        }
        Log.e(Chart.LOG_TAG, "Can't select by touch. No data set.");
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public RectF getBarBounds(BarEntry e) {
        BarDataSet set = (BarDataSet) ((BarData) this.mData).getDataSetForEntry(e);
        if (set == null) {
            return null;
        }
        float barspace = set.getBarSpace();
        float y = e.getVal();
        float x = e.getXIndex();
        float spaceHalf = barspace / 2.0f;
        float left = (x - 0.5f) + spaceHalf;
        float right = (x + 0.5f) - spaceHalf;
        float top = y >= 0.0f ? y : 0.0f;
        float bottom = y <= 0.0f ? y : 0.0f;
        RectF bounds = new RectF(left, top, right, bottom);
        getTransformer(set.getAxisDependency()).rectValueToPixel(bounds);
        return bounds;
    }

    public void setDrawHighlightArrow(boolean enabled) {
        this.mDrawHighlightArrow = enabled;
    }

    @Override // com.github.mikephil.charting.interfaces.BarDataProvider
    public boolean isDrawHighlightArrowEnabled() {
        return this.mDrawHighlightArrow;
    }

    public void setDrawValueAboveBar(boolean enabled) {
        this.mDrawValueAboveBar = enabled;
    }

    @Override // com.github.mikephil.charting.interfaces.BarDataProvider
    public boolean isDrawValueAboveBarEnabled() {
        return this.mDrawValueAboveBar;
    }

    public void setDrawBarShadow(boolean enabled) {
        this.mDrawBarShadow = enabled;
    }

    @Override // com.github.mikephil.charting.interfaces.BarDataProvider
    public boolean isDrawBarShadowEnabled() {
        return this.mDrawBarShadow;
    }

    @Override // com.github.mikephil.charting.interfaces.BarDataProvider
    public BarData getBarData() {
        return (BarData) this.mData;
    }

    @Override // com.github.mikephil.charting.charts.BarLineChartBase, com.github.mikephil.charting.interfaces.BarLineScatterCandleBubbleDataProvider
    public int getLowestVisibleXIndex() {
        float step = ((BarData) this.mData).getDataSetCount();
        float div = step <= 1.0f ? 1.0f : step + ((BarData) this.mData).getGroupSpace();
        float[] pts = {this.mViewPortHandler.contentLeft(), this.mViewPortHandler.contentBottom()};
        getTransformer(YAxis.AxisDependency.LEFT).pixelsToValue(pts);
        return (int) (pts[0] <= getXChartMin() ? 0.0f : (pts[0] / div) + 1.0f);
    }

    @Override // com.github.mikephil.charting.charts.BarLineChartBase, com.github.mikephil.charting.interfaces.BarLineScatterCandleBubbleDataProvider
    public int getHighestVisibleXIndex() {
        float step = ((BarData) this.mData).getDataSetCount();
        float div = step > 1.0f ? step + ((BarData) this.mData).getGroupSpace() : 1.0f;
        float[] pts = {this.mViewPortHandler.contentRight(), this.mViewPortHandler.contentBottom()};
        getTransformer(YAxis.AxisDependency.LEFT).pixelsToValue(pts);
        return (int) (pts[0] >= getXChartMax() ? getXChartMax() / div : pts[0] / div);
    }
}
