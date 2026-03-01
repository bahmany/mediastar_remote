package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.BubbleDataProvider;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.util.List;

/* loaded from: classes.dex */
public class BubbleChartRenderer extends DataRenderer {
    private float[] _hsvBuffer;
    protected BubbleDataProvider mChart;
    private float[] pointBuffer;
    private float[] sizeBuffer;

    public BubbleChartRenderer(BubbleDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.sizeBuffer = new float[4];
        this.pointBuffer = new float[2];
        this._hsvBuffer = new float[3];
        this.mChart = chart;
        this.mRenderPaint.setStyle(Paint.Style.FILL);
        this.mHighlightPaint.setStyle(Paint.Style.STROKE);
        this.mHighlightPaint.setStrokeWidth(Utils.convertDpToPixel(1.5f));
    }

    @Override // com.github.mikephil.charting.renderer.DataRenderer
    public void initBuffers() {
    }

    @Override // com.github.mikephil.charting.renderer.DataRenderer
    public void drawData(Canvas c) {
        BubbleData bubbleData = this.mChart.getBubbleData();
        for (T set : bubbleData.getDataSets()) {
            if (set.isVisible() && set.getEntryCount() > 0) {
                drawDataSet(c, set);
            }
        }
    }

    protected float getShapeSize(float entrySize, float maxSize, float reference) {
        float factor = maxSize == 0.0f ? 1.0f : (float) Math.sqrt(entrySize / maxSize);
        float shapeSize = reference * factor;
        return shapeSize;
    }

    protected void drawDataSet(Canvas c, BubbleDataSet dataSet) {
        Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
        float phaseX = this.mAnimator.getPhaseX();
        float phaseY = this.mAnimator.getPhaseY();
        List yVals = dataSet.getYVals();
        Entry entryFrom = dataSet.getEntryForXIndex(this.mMinX);
        Entry entryTo = dataSet.getEntryForXIndex(this.mMaxX);
        int minx = Math.max(dataSet.getEntryPosition(entryFrom), 0);
        int maxx = Math.min(dataSet.getEntryPosition(entryTo) + 1, yVals.size());
        this.sizeBuffer[0] = 0.0f;
        this.sizeBuffer[2] = 1.0f;
        trans.pointValuesToPixel(this.sizeBuffer);
        float maxBubbleWidth = Math.abs(this.sizeBuffer[2] - this.sizeBuffer[0]);
        float maxBubbleHeight = Math.abs(this.mViewPortHandler.contentBottom() - this.mViewPortHandler.contentTop());
        float referenceSize = Math.min(maxBubbleHeight, maxBubbleWidth);
        for (int j = minx; j < maxx; j++) {
            BubbleEntry entry = (BubbleEntry) yVals.get(j);
            this.pointBuffer[0] = ((entry.getXIndex() - minx) * phaseX) + minx;
            this.pointBuffer[1] = entry.getVal() * phaseY;
            trans.pointValuesToPixel(this.pointBuffer);
            float shapeHalf = getShapeSize(entry.getSize(), dataSet.getMaxSize(), referenceSize) / 2.0f;
            if (this.mViewPortHandler.isInBoundsTop(this.pointBuffer[1] + shapeHalf) && this.mViewPortHandler.isInBoundsBottom(this.pointBuffer[1] - shapeHalf) && this.mViewPortHandler.isInBoundsLeft(this.pointBuffer[0] + shapeHalf)) {
                if (this.mViewPortHandler.isInBoundsRight(this.pointBuffer[0] - shapeHalf)) {
                    int color = dataSet.getColor(entry.getXIndex());
                    this.mRenderPaint.setColor(color);
                    c.drawCircle(this.pointBuffer[0], this.pointBuffer[1], shapeHalf, this.mRenderPaint);
                } else {
                    return;
                }
            }
        }
    }

    @Override // com.github.mikephil.charting.renderer.DataRenderer
    public void drawValues(Canvas c) {
        BubbleData bubbleData = this.mChart.getBubbleData();
        if (bubbleData != null && bubbleData.getYValCount() < ((int) Math.ceil(this.mChart.getMaxVisibleCount() * this.mViewPortHandler.getScaleX()))) {
            List<T> dataSets = bubbleData.getDataSets();
            float lineHeight = Utils.calcTextHeight(this.mValuePaint, "1");
            for (int i = 0; i < dataSets.size(); i++) {
                DataSet<?> dataSet = (BubbleDataSet) dataSets.get(i);
                if (dataSet.isDrawValuesEnabled() && dataSet.getEntryCount() != 0) {
                    applyValueTextStyle(dataSet);
                    float phaseX = this.mAnimator.getPhaseX();
                    float phaseY = this.mAnimator.getPhaseY();
                    float alpha = phaseX == 1.0f ? phaseY : phaseX;
                    int valueTextColor = dataSet.getValueTextColor();
                    this.mValuePaint.setColor(Color.argb(Math.round(255.0f * alpha), Color.red(valueTextColor), Color.green(valueTextColor), Color.blue(valueTextColor)));
                    List<? extends Entry> yVals = dataSet.getYVals();
                    Entry entryFrom = dataSet.getEntryForXIndex(this.mMinX);
                    Entry entryTo = dataSet.getEntryForXIndex(this.mMaxX);
                    int minx = dataSet.getEntryPosition(entryFrom);
                    int maxx = Math.min(dataSet.getEntryPosition(entryTo) + 1, dataSet.getEntryCount());
                    float[] positions = this.mChart.getTransformer(dataSet.getAxisDependency()).generateTransformedValuesBubble(yVals, phaseX, phaseY, minx, maxx);
                    for (int j = 0; j < positions.length; j += 2) {
                        float x = positions[j];
                        float y = positions[j + 1];
                        if (this.mViewPortHandler.isInBoundsRight(x)) {
                            if (this.mViewPortHandler.isInBoundsLeft(x) && this.mViewPortHandler.isInBoundsY(y)) {
                                BubbleEntry entry = (BubbleEntry) yVals.get((j / 2) + minx);
                                drawValue(c, dataSet.getValueFormatter(), entry.getSize(), entry, i, x, y + (0.5f * lineHeight));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override // com.github.mikephil.charting.renderer.DataRenderer
    public void drawExtras(Canvas c) {
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.github.mikephil.charting.renderer.DataRenderer
    public void drawHighlighted(Canvas c, Highlight[] indices) {
        BubbleData bubbleData = this.mChart.getBubbleData();
        float phaseX = this.mAnimator.getPhaseX();
        float phaseY = this.mAnimator.getPhaseY();
        for (Highlight indice : indices) {
            BubbleDataSet dataSet = (BubbleDataSet) bubbleData.getDataSetByIndex(indice.getDataSetIndex());
            if (dataSet != null && dataSet.isHighlightEnabled()) {
                Entry entryFrom = dataSet.getEntryForXIndex(this.mMinX);
                Entry entryTo = dataSet.getEntryForXIndex(this.mMaxX);
                int minx = dataSet.getEntryPosition(entryFrom);
                int maxx = Math.min(dataSet.getEntryPosition(entryTo) + 1, dataSet.getEntryCount());
                BubbleEntry entry = (BubbleEntry) bubbleData.getEntryForHighlight(indice);
                if (entry != null && entry.getXIndex() == indice.getXIndex()) {
                    Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
                    this.sizeBuffer[0] = 0.0f;
                    this.sizeBuffer[2] = 1.0f;
                    trans.pointValuesToPixel(this.sizeBuffer);
                    float maxBubbleWidth = Math.abs(this.sizeBuffer[2] - this.sizeBuffer[0]);
                    float maxBubbleHeight = Math.abs(this.mViewPortHandler.contentBottom() - this.mViewPortHandler.contentTop());
                    float referenceSize = Math.min(maxBubbleHeight, maxBubbleWidth);
                    this.pointBuffer[0] = ((entry.getXIndex() - minx) * phaseX) + minx;
                    this.pointBuffer[1] = entry.getVal() * phaseY;
                    trans.pointValuesToPixel(this.pointBuffer);
                    float shapeHalf = getShapeSize(entry.getSize(), dataSet.getMaxSize(), referenceSize) / 2.0f;
                    if (this.mViewPortHandler.isInBoundsTop(this.pointBuffer[1] + shapeHalf) && this.mViewPortHandler.isInBoundsBottom(this.pointBuffer[1] - shapeHalf) && this.mViewPortHandler.isInBoundsLeft(this.pointBuffer[0] + shapeHalf)) {
                        if (this.mViewPortHandler.isInBoundsRight(this.pointBuffer[0] - shapeHalf)) {
                            if (indice.getXIndex() >= minx && indice.getXIndex() < maxx) {
                                int originalColor = dataSet.getColor(entry.getXIndex());
                                Color.RGBToHSV(Color.red(originalColor), Color.green(originalColor), Color.blue(originalColor), this._hsvBuffer);
                                float[] fArr = this._hsvBuffer;
                                fArr[2] = fArr[2] * 0.5f;
                                int color = Color.HSVToColor(Color.alpha(originalColor), this._hsvBuffer);
                                this.mHighlightPaint.setColor(color);
                                this.mHighlightPaint.setStrokeWidth(dataSet.getHighlightCircleWidth());
                                c.drawCircle(this.pointBuffer[0], this.pointBuffer[1], shapeHalf, this.mHighlightPaint);
                            }
                        } else {
                            return;
                        }
                    }
                }
            }
        }
    }
}
