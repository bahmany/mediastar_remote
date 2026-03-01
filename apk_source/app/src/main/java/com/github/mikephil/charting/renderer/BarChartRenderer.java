package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.BarDataProvider;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.ContentTree;
import com.hisilicon.multiscreen.protocol.message.KeyInfo;
import java.util.List;

/* loaded from: classes.dex */
public class BarChartRenderer extends DataRenderer {
    protected BarBuffer[] mBarBuffers;
    protected RectF mBarRect;
    protected BarDataProvider mChart;
    protected Paint mShadowPaint;

    public BarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.mBarRect = new RectF();
        this.mChart = chart;
        this.mHighlightPaint = new Paint(1);
        this.mHighlightPaint.setStyle(Paint.Style.FILL);
        this.mHighlightPaint.setColor(Color.rgb(0, 0, 0));
        this.mHighlightPaint.setAlpha(KeyInfo.KEYCODE_ASK);
        this.mShadowPaint = new Paint(1);
        this.mShadowPaint.setStyle(Paint.Style.FILL);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.github.mikephil.charting.renderer.DataRenderer
    public void initBuffers() {
        BarData barData = this.mChart.getBarData();
        this.mBarBuffers = new BarBuffer[barData.getDataSetCount()];
        for (int i = 0; i < this.mBarBuffers.length; i++) {
            BarDataSet set = (BarDataSet) barData.getDataSetByIndex(i);
            this.mBarBuffers[i] = new BarBuffer(set.getValueCount() * 4 * set.getStackSize(), barData.getGroupSpace(), barData.getDataSetCount(), set.isStacked());
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.github.mikephil.charting.renderer.DataRenderer
    public void drawData(Canvas c) {
        BarData barData = this.mChart.getBarData();
        for (int i = 0; i < barData.getDataSetCount(); i++) {
            BarDataSet set = (BarDataSet) barData.getDataSetByIndex(i);
            if (set.isVisible() && set.getEntryCount() > 0) {
                drawDataSet(c, set, i);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void drawDataSet(Canvas c, BarDataSet dataSet, int index) {
        Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
        this.mShadowPaint.setColor(dataSet.getBarShadowColor());
        float phaseX = this.mAnimator.getPhaseX();
        float phaseY = this.mAnimator.getPhaseY();
        List<T> yVals = dataSet.getYVals();
        BarBuffer barBuffer = this.mBarBuffers[index];
        barBuffer.setPhases(phaseX, phaseY);
        barBuffer.setBarSpace(dataSet.getBarSpace());
        barBuffer.setDataSet(index);
        barBuffer.setInverted(this.mChart.isInverted(dataSet.getAxisDependency()));
        barBuffer.feed(yVals);
        trans.pointValuesToPixel(barBuffer.buffer);
        if (dataSet.getColors().size() > 1) {
            for (int j = 0; j < barBuffer.size(); j += 4) {
                if (this.mViewPortHandler.isInBoundsLeft(barBuffer.buffer[j + 2])) {
                    if (this.mViewPortHandler.isInBoundsRight(barBuffer.buffer[j])) {
                        if (this.mChart.isDrawBarShadowEnabled()) {
                            c.drawRect(barBuffer.buffer[j], this.mViewPortHandler.contentTop(), barBuffer.buffer[j + 2], this.mViewPortHandler.contentBottom(), this.mShadowPaint);
                        }
                        this.mRenderPaint.setColor(dataSet.getColor(j / 4));
                        c.drawRect(barBuffer.buffer[j], barBuffer.buffer[j + 1], barBuffer.buffer[j + 2], barBuffer.buffer[j + 3], this.mRenderPaint);
                    } else {
                        return;
                    }
                }
            }
            return;
        }
        this.mRenderPaint.setColor(dataSet.getColor());
        for (int j2 = 0; j2 < barBuffer.size(); j2 += 4) {
            if (this.mViewPortHandler.isInBoundsLeft(barBuffer.buffer[j2 + 2])) {
                if (this.mViewPortHandler.isInBoundsRight(barBuffer.buffer[j2])) {
                    if (this.mChart.isDrawBarShadowEnabled()) {
                        c.drawRect(barBuffer.buffer[j2], this.mViewPortHandler.contentTop(), barBuffer.buffer[j2 + 2], this.mViewPortHandler.contentBottom(), this.mShadowPaint);
                    }
                    c.drawRect(barBuffer.buffer[j2], barBuffer.buffer[j2 + 1], barBuffer.buffer[j2 + 2], barBuffer.buffer[j2 + 3], this.mRenderPaint);
                } else {
                    return;
                }
            }
        }
    }

    protected void prepareBarHighlight(float x, float y1, float y2, float barspaceHalf, Transformer trans) {
        float left = (x - 0.5f) + barspaceHalf;
        float right = (x + 0.5f) - barspaceHalf;
        this.mBarRect.set(left, y1, right, y2);
        trans.rectValueToPixel(this.mBarRect, this.mAnimator.getPhaseY());
    }

    @Override // com.github.mikephil.charting.renderer.DataRenderer
    public void drawValues(Canvas c) {
        float y;
        if (passesCheck()) {
            List<T> dataSets = this.mChart.getBarData().getDataSets();
            float valueOffsetPlus = Utils.convertDpToPixel(4.5f);
            boolean drawValueAboveBar = this.mChart.isDrawValueAboveBarEnabled();
            for (int i = 0; i < this.mChart.getBarData().getDataSetCount(); i++) {
                BarDataSet dataSet = (BarDataSet) dataSets.get(i);
                if (dataSet.isDrawValuesEnabled() && dataSet.getEntryCount() != 0) {
                    applyValueTextStyle(dataSet);
                    boolean isInverted = this.mChart.isInverted(dataSet.getAxisDependency());
                    float valueTextHeight = Utils.calcTextHeight(this.mValuePaint, ContentTree.AUDIO_FOLDER_ID);
                    float posOffset = drawValueAboveBar ? -valueOffsetPlus : valueTextHeight + valueOffsetPlus;
                    float negOffset = drawValueAboveBar ? valueTextHeight + valueOffsetPlus : -valueOffsetPlus;
                    if (isInverted) {
                        posOffset = (-posOffset) - valueTextHeight;
                        negOffset = (-negOffset) - valueTextHeight;
                    }
                    Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
                    List<T> yVals = dataSet.getYVals();
                    float[] valuePoints = getTransformedValues(trans, yVals, i);
                    if (!dataSet.isStacked()) {
                        for (int j = 0; j < valuePoints.length * this.mAnimator.getPhaseX() && this.mViewPortHandler.isInBoundsRight(valuePoints[j]); j += 2) {
                            if (this.mViewPortHandler.isInBoundsY(valuePoints[j + 1]) && this.mViewPortHandler.isInBoundsLeft(valuePoints[j])) {
                                Entry entry = (BarEntry) yVals.get(j / 2);
                                float val = entry.getVal();
                                drawValue(c, dataSet.getValueFormatter(), val, entry, i, valuePoints[j], valuePoints[j + 1] + (val >= 0.0f ? posOffset : negOffset));
                            }
                        }
                    } else {
                        for (int j2 = 0; j2 < (valuePoints.length - 1) * this.mAnimator.getPhaseX(); j2 += 2) {
                            BarEntry entry2 = (BarEntry) yVals.get(j2 / 2);
                            float[] vals = entry2.getVals();
                            if (vals == null) {
                                if (this.mViewPortHandler.isInBoundsRight(valuePoints[j2])) {
                                    if (this.mViewPortHandler.isInBoundsY(valuePoints[j2 + 1]) && this.mViewPortHandler.isInBoundsLeft(valuePoints[j2])) {
                                        drawValue(c, dataSet.getValueFormatter(), entry2.getVal(), entry2, i, valuePoints[j2], valuePoints[j2 + 1] + (entry2.getVal() >= 0.0f ? posOffset : negOffset));
                                    }
                                }
                            } else {
                                float[] transformed = new float[vals.length * 2];
                                float posY = 0.0f;
                                float negY = -entry2.getNegativeSum();
                                int k = 0;
                                int idx = 0;
                                while (k < transformed.length) {
                                    float value = vals[idx];
                                    if (value >= 0.0f) {
                                        posY += value;
                                        y = posY;
                                    } else {
                                        y = negY;
                                        negY -= value;
                                    }
                                    transformed[k + 1] = this.mAnimator.getPhaseY() * y;
                                    k += 2;
                                    idx++;
                                }
                                trans.pointValuesToPixel(transformed);
                                for (int k2 = 0; k2 < transformed.length; k2 += 2) {
                                    float x = valuePoints[j2];
                                    float y2 = transformed[k2 + 1] + (vals[k2 / 2] >= 0.0f ? posOffset : negOffset);
                                    if (this.mViewPortHandler.isInBoundsRight(x)) {
                                        if (this.mViewPortHandler.isInBoundsY(y2) && this.mViewPortHandler.isInBoundsLeft(x)) {
                                            drawValue(c, dataSet.getValueFormatter(), vals[k2 / 2], entry2, i, x, y2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.github.mikephil.charting.renderer.DataRenderer
    public void drawHighlighted(Canvas c, Highlight[] indices) {
        BarEntry e;
        float y1;
        float y2;
        int setCount = this.mChart.getBarData().getDataSetCount();
        for (Highlight h : indices) {
            int index = h.getXIndex();
            int dataSetIndex = h.getDataSetIndex();
            BarDataSet set = (BarDataSet) this.mChart.getBarData().getDataSetByIndex(dataSetIndex);
            if (set != null && set.isHighlightEnabled()) {
                float barspaceHalf = set.getBarSpace() / 2.0f;
                Transformer trans = this.mChart.getTransformer(set.getAxisDependency());
                this.mHighlightPaint.setColor(set.getHighLightColor());
                this.mHighlightPaint.setAlpha(set.getHighLightAlpha());
                if (index >= 0 && index < (this.mChart.getXChartMax() * this.mAnimator.getPhaseX()) / setCount && (e = (BarEntry) set.getEntryForXIndex(index)) != null && e.getXIndex() == index) {
                    float groupspace = this.mChart.getBarData().getGroupSpace();
                    boolean isStack = h.getStackIndex() >= 0;
                    float x = (index * setCount) + dataSetIndex + (groupspace / 2.0f) + (index * groupspace);
                    if (isStack) {
                        y1 = h.getRange().from;
                        y2 = h.getRange().to;
                    } else {
                        y1 = e.getVal();
                        y2 = 0.0f;
                    }
                    prepareBarHighlight(x, y1, y2, barspaceHalf, trans);
                    c.drawRect(this.mBarRect, this.mHighlightPaint);
                    if (this.mChart.isDrawHighlightArrowEnabled()) {
                        this.mHighlightPaint.setAlpha(255);
                        float offsetY = this.mAnimator.getPhaseY() * 0.07f;
                        float[] values = new float[9];
                        trans.getPixelToValueMatrix().getValues(values);
                        float xToYRel = Math.abs(values[4] / values[0]);
                        float arrowWidth = set.getBarSpace() / 2.0f;
                        float arrowHeight = arrowWidth * xToYRel;
                        if (y1 > (-y2)) {
                        }
                        float yArrow = y1 * this.mAnimator.getPhaseY();
                        Path arrow = new Path();
                        arrow.moveTo(0.4f + x, yArrow + offsetY);
                        arrow.lineTo(0.4f + x + arrowWidth, (yArrow + offsetY) - arrowHeight);
                        arrow.lineTo(0.4f + x + arrowWidth, yArrow + offsetY + arrowHeight);
                        trans.pathValueToPixel(arrow);
                        c.drawPath(arrow, this.mHighlightPaint);
                    }
                }
            }
        }
    }

    public float[] getTransformedValues(Transformer trans, List<BarEntry> entries, int dataSetIndex) {
        return trans.generateTransformedValuesBarChart(entries, dataSetIndex, this.mChart.getBarData(), this.mAnimator.getPhaseY());
    }

    protected boolean passesCheck() {
        return ((float) this.mChart.getBarData().getYValCount()) < ((float) this.mChart.getMaxVisibleCount()) * this.mViewPortHandler.getScaleX();
    }

    @Override // com.github.mikephil.charting.renderer.DataRenderer
    public void drawExtras(Canvas c) {
    }
}
