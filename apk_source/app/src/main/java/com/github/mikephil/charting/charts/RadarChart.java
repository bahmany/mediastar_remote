package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import com.alibaba.fastjson.asm.Opcodes;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.renderer.RadarChartRenderer;
import com.github.mikephil.charting.renderer.XAxisRendererRadarChart;
import com.github.mikephil.charting.renderer.YAxisRendererRadarChart;
import com.github.mikephil.charting.utils.Utils;

/* loaded from: classes.dex */
public class RadarChart extends PieRadarChartBase<RadarData> {
    private boolean mDrawWeb;
    private float mInnerWebLineWidth;
    private int mSkipWebLineCount;
    private int mWebAlpha;
    private int mWebColor;
    private int mWebColorInner;
    private float mWebLineWidth;
    private XAxis mXAxis;
    protected XAxisRendererRadarChart mXAxisRenderer;
    private YAxis mYAxis;
    protected YAxisRendererRadarChart mYAxisRenderer;

    public RadarChart(Context context) {
        super(context);
        this.mWebLineWidth = 2.5f;
        this.mInnerWebLineWidth = 1.5f;
        this.mWebColor = Color.rgb(122, 122, 122);
        this.mWebColorInner = Color.rgb(122, 122, 122);
        this.mWebAlpha = Opcodes.FCMPG;
        this.mDrawWeb = true;
        this.mSkipWebLineCount = 0;
    }

    public RadarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mWebLineWidth = 2.5f;
        this.mInnerWebLineWidth = 1.5f;
        this.mWebColor = Color.rgb(122, 122, 122);
        this.mWebColorInner = Color.rgb(122, 122, 122);
        this.mWebAlpha = Opcodes.FCMPG;
        this.mDrawWeb = true;
        this.mSkipWebLineCount = 0;
    }

    public RadarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mWebLineWidth = 2.5f;
        this.mInnerWebLineWidth = 1.5f;
        this.mWebColor = Color.rgb(122, 122, 122);
        this.mWebColorInner = Color.rgb(122, 122, 122);
        this.mWebAlpha = Opcodes.FCMPG;
        this.mDrawWeb = true;
        this.mSkipWebLineCount = 0;
    }

    @Override // com.github.mikephil.charting.charts.PieRadarChartBase, com.github.mikephil.charting.charts.Chart
    protected void init() {
        super.init();
        this.mYAxis = new YAxis(YAxis.AxisDependency.LEFT);
        this.mXAxis = new XAxis();
        this.mXAxis.setSpaceBetweenLabels(0);
        this.mWebLineWidth = Utils.convertDpToPixel(1.5f);
        this.mInnerWebLineWidth = Utils.convertDpToPixel(0.75f);
        this.mRenderer = new RadarChartRenderer(this, this.mAnimator, this.mViewPortHandler);
        this.mYAxisRenderer = new YAxisRendererRadarChart(this.mViewPortHandler, this.mYAxis, this);
        this.mXAxisRenderer = new XAxisRendererRadarChart(this.mViewPortHandler, this.mXAxis, this);
    }

    @Override // com.github.mikephil.charting.charts.PieRadarChartBase, com.github.mikephil.charting.charts.Chart
    protected void calcMinMax() {
        super.calcMinMax();
        float minLeft = ((RadarData) this.mData).getYMin(YAxis.AxisDependency.LEFT);
        float maxLeft = ((RadarData) this.mData).getYMax(YAxis.AxisDependency.LEFT);
        this.mXChartMax = ((RadarData) this.mData).getXVals().size() - 1;
        this.mDeltaX = Math.abs(this.mXChartMax - this.mXChartMin);
        float leftRange = Math.abs(maxLeft - (this.mYAxis.isStartAtZeroEnabled() ? 0.0f : minLeft));
        float topSpaceLeft = (leftRange / 100.0f) * this.mYAxis.getSpaceTop();
        float bottomSpaceLeft = (leftRange / 100.0f) * this.mYAxis.getSpaceBottom();
        this.mXChartMax = ((RadarData) this.mData).getXVals().size() - 1;
        this.mDeltaX = Math.abs(this.mXChartMax - this.mXChartMin);
        if (!this.mYAxis.isStartAtZeroEnabled()) {
            this.mYAxis.mAxisMinimum = !Float.isNaN(this.mYAxis.getAxisMinValue()) ? this.mYAxis.getAxisMinValue() : minLeft - bottomSpaceLeft;
            this.mYAxis.mAxisMaximum = !Float.isNaN(this.mYAxis.getAxisMaxValue()) ? this.mYAxis.getAxisMaxValue() : maxLeft + topSpaceLeft;
        } else if (minLeft < 0.0f && maxLeft < 0.0f) {
            this.mYAxis.mAxisMinimum = Math.min(0.0f, !Float.isNaN(this.mYAxis.getAxisMinValue()) ? this.mYAxis.getAxisMinValue() : minLeft - bottomSpaceLeft);
            this.mYAxis.mAxisMaximum = 0.0f;
        } else if (minLeft >= 0.0d) {
            this.mYAxis.mAxisMinimum = 0.0f;
            this.mYAxis.mAxisMaximum = Math.max(0.0f, !Float.isNaN(this.mYAxis.getAxisMaxValue()) ? this.mYAxis.getAxisMaxValue() : maxLeft + topSpaceLeft);
        } else {
            this.mYAxis.mAxisMinimum = Math.min(0.0f, !Float.isNaN(this.mYAxis.getAxisMinValue()) ? this.mYAxis.getAxisMinValue() : minLeft - bottomSpaceLeft);
            this.mYAxis.mAxisMaximum = Math.max(0.0f, !Float.isNaN(this.mYAxis.getAxisMaxValue()) ? this.mYAxis.getAxisMaxValue() : maxLeft + topSpaceLeft);
        }
        this.mYAxis.mAxisRange = Math.abs(this.mYAxis.mAxisMaximum - this.mYAxis.mAxisMinimum);
    }

    @Override // com.github.mikephil.charting.charts.Chart
    protected float[] getMarkerPosition(Entry e, Highlight highlight) {
        float angle = (getSliceAngle() * e.getXIndex()) + getRotationAngle();
        float val = e.getVal() * getFactor();
        PointF c = getCenterOffsets();
        PointF p = new PointF((float) (c.x + (val * Math.cos(Math.toRadians(angle)))), (float) (c.y + (val * Math.sin(Math.toRadians(angle)))));
        return new float[]{p.x, p.y};
    }

    @Override // com.github.mikephil.charting.charts.PieRadarChartBase, com.github.mikephil.charting.charts.Chart
    public void notifyDataSetChanged() {
        if (!this.mDataNotSet) {
            calcMinMax();
            this.mYAxisRenderer.computeAxis(this.mYAxis.mAxisMinimum, this.mYAxis.mAxisMaximum);
            this.mXAxisRenderer.computeAxis(((RadarData) this.mData).getXValAverageLength(), ((RadarData) this.mData).getXVals());
            if (this.mLegend != null && !this.mLegend.isLegendCustom()) {
                this.mLegendRenderer.computeLegend(this.mData);
            }
            calculateOffsets();
        }
    }

    @Override // com.github.mikephil.charting.charts.Chart, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!this.mDataNotSet) {
            this.mXAxisRenderer.renderAxisLabels(canvas);
            if (this.mDrawWeb) {
                this.mRenderer.drawExtras(canvas);
            }
            this.mYAxisRenderer.renderLimitLines(canvas);
            this.mRenderer.drawData(canvas);
            if (valuesToHighlight()) {
                this.mRenderer.drawHighlighted(canvas, this.mIndicesToHighlight);
            }
            this.mYAxisRenderer.renderAxisLabels(canvas);
            this.mRenderer.drawValues(canvas);
            this.mLegendRenderer.renderLegend(canvas);
            drawDescription(canvas);
            drawMarkers(canvas);
        }
    }

    public float getFactor() {
        RectF content = this.mViewPortHandler.getContentRect();
        return Math.min(content.width() / 2.0f, content.height() / 2.0f) / this.mYAxis.mAxisRange;
    }

    public float getSliceAngle() {
        return 360.0f / ((RadarData) this.mData).getXValCount();
    }

    @Override // com.github.mikephil.charting.charts.PieRadarChartBase
    public int getIndexForAngle(float angle) {
        float a = Utils.getNormalizedAngle(angle - getRotationAngle());
        float sliceangle = getSliceAngle();
        for (int i = 0; i < ((RadarData) this.mData).getXValCount(); i++) {
            if (((i + 1) * sliceangle) - (sliceangle / 2.0f) > a) {
                return i;
            }
        }
        return 0;
    }

    public YAxis getYAxis() {
        return this.mYAxis;
    }

    public XAxis getXAxis() {
        return this.mXAxis;
    }

    public void setWebLineWidth(float width) {
        this.mWebLineWidth = Utils.convertDpToPixel(width);
    }

    public float getWebLineWidth() {
        return this.mWebLineWidth;
    }

    public void setWebLineWidthInner(float width) {
        this.mInnerWebLineWidth = Utils.convertDpToPixel(width);
    }

    public float getWebLineWidthInner() {
        return this.mInnerWebLineWidth;
    }

    public void setWebAlpha(int alpha) {
        this.mWebAlpha = alpha;
    }

    public int getWebAlpha() {
        return this.mWebAlpha;
    }

    public void setWebColor(int color) {
        this.mWebColor = color;
    }

    public int getWebColor() {
        return this.mWebColor;
    }

    public void setWebColorInner(int color) {
        this.mWebColorInner = color;
    }

    public int getWebColorInner() {
        return this.mWebColorInner;
    }

    public void setDrawWeb(boolean enabled) {
        this.mDrawWeb = enabled;
    }

    public void setSkipWebLineCount(int count) {
        this.mSkipWebLineCount = Math.max(0, count);
    }

    public int getSkipWebLineCount() {
        return this.mSkipWebLineCount;
    }

    @Override // com.github.mikephil.charting.charts.PieRadarChartBase
    protected float getRequiredLegendOffset() {
        return this.mLegendRenderer.getLabelPaint().getTextSize() * 4.0f;
    }

    @Override // com.github.mikephil.charting.charts.PieRadarChartBase
    protected float getRequiredBaseOffset() {
        if (this.mXAxis.isEnabled() && this.mXAxis.isDrawLabelsEnabled()) {
            return this.mXAxis.mLabelRotatedWidth;
        }
        return Utils.convertDpToPixel(10.0f);
    }

    @Override // com.github.mikephil.charting.charts.PieRadarChartBase
    public float getRadius() {
        RectF content = this.mViewPortHandler.getContentRect();
        return Math.min(content.width() / 2.0f, content.height() / 2.0f);
    }

    @Override // com.github.mikephil.charting.charts.PieRadarChartBase, com.github.mikephil.charting.interfaces.ChartInterface
    public float getYChartMax() {
        return this.mYAxis.mAxisMaximum;
    }

    @Override // com.github.mikephil.charting.charts.PieRadarChartBase, com.github.mikephil.charting.interfaces.ChartInterface
    public float getYChartMin() {
        return this.mYAxis.mAxisMinimum;
    }

    public float getYRange() {
        return this.mYAxis.mAxisRange;
    }
}
