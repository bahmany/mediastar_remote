package com.github.mikephil.charting.charts;

import android.content.Context;
import android.util.AttributeSet;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.highlight.CombinedHighlighter;
import com.github.mikephil.charting.interfaces.BarDataProvider;
import com.github.mikephil.charting.interfaces.BubbleDataProvider;
import com.github.mikephil.charting.interfaces.CandleDataProvider;
import com.github.mikephil.charting.interfaces.LineDataProvider;
import com.github.mikephil.charting.interfaces.ScatterDataProvider;
import com.github.mikephil.charting.renderer.CombinedChartRenderer;

/* loaded from: classes.dex */
public class CombinedChart extends BarLineChartBase<CombinedData> implements LineDataProvider, BarDataProvider, ScatterDataProvider, CandleDataProvider, BubbleDataProvider {
    private boolean mDrawBarShadow;
    private boolean mDrawHighlightArrow;
    protected DrawOrder[] mDrawOrder;
    private boolean mDrawValueAboveBar;

    public enum DrawOrder {
        BAR,
        BUBBLE,
        LINE,
        CANDLE,
        SCATTER;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static DrawOrder[] valuesCustom() {
            DrawOrder[] drawOrderArrValuesCustom = values();
            int length = drawOrderArrValuesCustom.length;
            DrawOrder[] drawOrderArr = new DrawOrder[length];
            System.arraycopy(drawOrderArrValuesCustom, 0, drawOrderArr, 0, length);
            return drawOrderArr;
        }
    }

    public CombinedChart(Context context) {
        super(context);
        this.mDrawHighlightArrow = false;
        this.mDrawValueAboveBar = true;
        this.mDrawBarShadow = false;
        this.mDrawOrder = new DrawOrder[]{DrawOrder.BAR, DrawOrder.BUBBLE, DrawOrder.LINE, DrawOrder.CANDLE, DrawOrder.SCATTER};
    }

    public CombinedChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDrawHighlightArrow = false;
        this.mDrawValueAboveBar = true;
        this.mDrawBarShadow = false;
        this.mDrawOrder = new DrawOrder[]{DrawOrder.BAR, DrawOrder.BUBBLE, DrawOrder.LINE, DrawOrder.CANDLE, DrawOrder.SCATTER};
    }

    public CombinedChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mDrawHighlightArrow = false;
        this.mDrawValueAboveBar = true;
        this.mDrawBarShadow = false;
        this.mDrawOrder = new DrawOrder[]{DrawOrder.BAR, DrawOrder.BUBBLE, DrawOrder.LINE, DrawOrder.CANDLE, DrawOrder.SCATTER};
    }

    @Override // com.github.mikephil.charting.charts.BarLineChartBase, com.github.mikephil.charting.charts.Chart
    protected void init() {
        super.init();
        this.mHighlighter = new CombinedHighlighter(this);
    }

    @Override // com.github.mikephil.charting.charts.BarLineChartBase, com.github.mikephil.charting.charts.Chart
    protected void calcMinMax() {
        super.calcMinMax();
        if (getBarData() != null || getCandleData() != null || getBubbleData() != null) {
            this.mXChartMin = -0.5f;
            this.mXChartMax = ((CombinedData) this.mData).getXVals().size() - 0.5f;
            if (getBubbleData() != null) {
                for (T set : getBubbleData().getDataSets()) {
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
        }
        this.mDeltaX = Math.abs(this.mXChartMax - this.mXChartMin);
        if (this.mDeltaX == 0.0f && getLineData() != null && getLineData().getYValCount() > 0) {
            this.mDeltaX = 1.0f;
        }
    }

    @Override // com.github.mikephil.charting.charts.Chart
    public void setData(CombinedData data) {
        this.mData = null;
        this.mRenderer = null;
        super.setData((CombinedChart) data);
        this.mRenderer = new CombinedChartRenderer(this, this.mAnimator, this.mViewPortHandler);
        this.mRenderer.initBuffers();
    }

    @Override // com.github.mikephil.charting.interfaces.LineDataProvider
    public LineData getLineData() {
        if (this.mData == 0) {
            return null;
        }
        return ((CombinedData) this.mData).getLineData();
    }

    @Override // com.github.mikephil.charting.interfaces.BarDataProvider
    public BarData getBarData() {
        if (this.mData == 0) {
            return null;
        }
        return ((CombinedData) this.mData).getBarData();
    }

    @Override // com.github.mikephil.charting.interfaces.ScatterDataProvider
    public ScatterData getScatterData() {
        if (this.mData == 0) {
            return null;
        }
        return ((CombinedData) this.mData).getScatterData();
    }

    @Override // com.github.mikephil.charting.interfaces.CandleDataProvider
    public CandleData getCandleData() {
        if (this.mData == 0) {
            return null;
        }
        return ((CombinedData) this.mData).getCandleData();
    }

    @Override // com.github.mikephil.charting.interfaces.BubbleDataProvider
    public BubbleData getBubbleData() {
        if (this.mData == 0) {
            return null;
        }
        return ((CombinedData) this.mData).getBubbleData();
    }

    @Override // com.github.mikephil.charting.interfaces.BarDataProvider
    public boolean isDrawBarShadowEnabled() {
        return this.mDrawBarShadow;
    }

    @Override // com.github.mikephil.charting.interfaces.BarDataProvider
    public boolean isDrawValueAboveBarEnabled() {
        return this.mDrawValueAboveBar;
    }

    @Override // com.github.mikephil.charting.interfaces.BarDataProvider
    public boolean isDrawHighlightArrowEnabled() {
        return this.mDrawHighlightArrow;
    }

    public void setDrawHighlightArrow(boolean enabled) {
        this.mDrawHighlightArrow = enabled;
    }

    public void setDrawValueAboveBar(boolean enabled) {
        this.mDrawValueAboveBar = enabled;
    }

    public void setDrawBarShadow(boolean enabled) {
        this.mDrawBarShadow = enabled;
    }

    public DrawOrder[] getDrawOrder() {
        return this.mDrawOrder;
    }

    public void setDrawOrder(DrawOrder[] order) {
        if (order != null && order.length > 0) {
            this.mDrawOrder = order;
        }
    }
}
