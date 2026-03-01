package com.github.mikephil.charting.charts;

import android.content.Context;
import android.util.AttributeSet;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.interfaces.BubbleDataProvider;
import com.github.mikephil.charting.renderer.BubbleChartRenderer;

/* loaded from: classes.dex */
public class BubbleChart extends BarLineChartBase<BubbleData> implements BubbleDataProvider {
    public BubbleChart(Context context) {
        super(context);
    }

    public BubbleChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BubbleChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // com.github.mikephil.charting.charts.BarLineChartBase, com.github.mikephil.charting.charts.Chart
    protected void init() {
        super.init();
        this.mRenderer = new BubbleChartRenderer(this, this.mAnimator, this.mViewPortHandler);
    }

    @Override // com.github.mikephil.charting.charts.BarLineChartBase, com.github.mikephil.charting.charts.Chart
    protected void calcMinMax() {
        super.calcMinMax();
        if (this.mDeltaX == 0.0f && ((BubbleData) this.mData).getYValCount() > 0) {
            this.mDeltaX = 1.0f;
        }
        this.mXChartMin = -0.5f;
        this.mXChartMax = ((BubbleData) this.mData).getXValCount() - 0.5f;
        if (this.mRenderer != null) {
            for (T set : ((BubbleData) this.mData).getDataSets()) {
                float xmin = set.getXMin();
                float xmax = set.getXMax();
                if (xmin < this.mXChartMin) {
                    this.mXChartMin = xmin;
                }
                if (xmax > this.mXChartMax) {
                    this.mXChartMax = xmax;
                }
            }
        }
        this.mDeltaX = Math.abs(this.mXChartMax - this.mXChartMin);
    }

    @Override // com.github.mikephil.charting.interfaces.BubbleDataProvider
    public BubbleData getBubbleData() {
        return (BubbleData) this.mData;
    }
}
