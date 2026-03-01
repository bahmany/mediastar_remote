package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.buffer.HorizontalBarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.BarDataProvider;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.ContentTree;
import java.util.List;

/* loaded from: classes.dex */
public class HorizontalBarChartRenderer extends BarChartRenderer {
    public HorizontalBarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
        this.mValuePaint.setTextAlign(Paint.Align.LEFT);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.github.mikephil.charting.renderer.BarChartRenderer, com.github.mikephil.charting.renderer.DataRenderer
    public void initBuffers() {
        BarData barData = this.mChart.getBarData();
        this.mBarBuffers = new HorizontalBarBuffer[barData.getDataSetCount()];
        for (int i = 0; i < this.mBarBuffers.length; i++) {
            BarDataSet set = (BarDataSet) barData.getDataSetByIndex(i);
            this.mBarBuffers[i] = new HorizontalBarBuffer(set.getValueCount() * 4 * set.getStackSize(), barData.getGroupSpace(), barData.getDataSetCount(), set.isStacked());
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.github.mikephil.charting.renderer.BarChartRenderer
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
        for (int j = 0; j < barBuffer.size() && this.mViewPortHandler.isInBoundsTop(barBuffer.buffer[j + 3]); j += 4) {
            if (this.mViewPortHandler.isInBoundsBottom(barBuffer.buffer[j + 1])) {
                if (this.mChart.isDrawBarShadowEnabled()) {
                    c.drawRect(this.mViewPortHandler.contentLeft(), barBuffer.buffer[j + 1], this.mViewPortHandler.contentRight(), barBuffer.buffer[j + 3], this.mShadowPaint);
                }
                this.mRenderPaint.setColor(dataSet.getColor(j / 4));
                c.drawRect(barBuffer.buffer[j], barBuffer.buffer[j + 1], barBuffer.buffer[j + 2], barBuffer.buffer[j + 3], this.mRenderPaint);
            }
        }
    }

    @Override // com.github.mikephil.charting.renderer.BarChartRenderer, com.github.mikephil.charting.renderer.DataRenderer
    public void drawValues(Canvas c) {
        float y;
        if (passesCheck()) {
            List<T> dataSets = this.mChart.getBarData().getDataSets();
            float valueOffsetPlus = Utils.convertDpToPixel(5.0f);
            boolean drawValueAboveBar = this.mChart.isDrawValueAboveBarEnabled();
            for (int i = 0; i < this.mChart.getBarData().getDataSetCount(); i++) {
                BarDataSet dataSet = (BarDataSet) dataSets.get(i);
                if (dataSet.isDrawValuesEnabled() && dataSet.getEntryCount() != 0) {
                    boolean isInverted = this.mChart.isInverted(dataSet.getAxisDependency());
                    applyValueTextStyle(dataSet);
                    float halfTextHeight = Utils.calcTextHeight(this.mValuePaint, ContentTree.PLAYLIST_ID) / 2.0f;
                    ValueFormatter formatter = dataSet.getValueFormatter();
                    Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
                    List<T> yVals = dataSet.getYVals();
                    float[] valuePoints = getTransformedValues(trans, yVals, i);
                    if (!dataSet.isStacked()) {
                        for (int j = 0; j < valuePoints.length * this.mAnimator.getPhaseX() && this.mViewPortHandler.isInBoundsTop(valuePoints[j + 1]); j += 2) {
                            if (this.mViewPortHandler.isInBoundsX(valuePoints[j]) && this.mViewPortHandler.isInBoundsBottom(valuePoints[j + 1])) {
                                BarEntry e = (BarEntry) yVals.get(j / 2);
                                float val = e.getVal();
                                String formattedValue = formatter.getFormattedValue(val, e, i, this.mViewPortHandler);
                                float valueTextWidth = Utils.calcTextWidth(this.mValuePaint, formattedValue);
                                float posOffset = drawValueAboveBar ? valueOffsetPlus : -(valueTextWidth + valueOffsetPlus);
                                float negOffset = drawValueAboveBar ? -(valueTextWidth + valueOffsetPlus) : valueOffsetPlus;
                                if (isInverted) {
                                    posOffset = (-posOffset) - valueTextWidth;
                                    negOffset = (-negOffset) - valueTextWidth;
                                }
                                drawValue(c, formattedValue, (val >= 0.0f ? posOffset : negOffset) + valuePoints[j], valuePoints[j + 1] + halfTextHeight);
                            }
                        }
                    } else {
                        for (int j2 = 0; j2 < (valuePoints.length - 1) * this.mAnimator.getPhaseX(); j2 += 2) {
                            BarEntry e2 = (BarEntry) yVals.get(j2 / 2);
                            float[] vals = e2.getVals();
                            if (vals == null) {
                                if (this.mViewPortHandler.isInBoundsTop(valuePoints[j2 + 1])) {
                                    if (this.mViewPortHandler.isInBoundsX(valuePoints[j2]) && this.mViewPortHandler.isInBoundsBottom(valuePoints[j2 + 1])) {
                                        String formattedValue2 = formatter.getFormattedValue(e2.getVal(), e2, i, this.mViewPortHandler);
                                        float valueTextWidth2 = Utils.calcTextWidth(this.mValuePaint, formattedValue2);
                                        float posOffset2 = drawValueAboveBar ? valueOffsetPlus : -(valueTextWidth2 + valueOffsetPlus);
                                        float negOffset2 = drawValueAboveBar ? -(valueTextWidth2 + valueOffsetPlus) : valueOffsetPlus;
                                        if (isInverted) {
                                            posOffset2 = (-posOffset2) - valueTextWidth2;
                                            negOffset2 = (-negOffset2) - valueTextWidth2;
                                        }
                                        drawValue(c, formattedValue2, (e2.getVal() >= 0.0f ? posOffset2 : negOffset2) + valuePoints[j2], valuePoints[j2 + 1] + halfTextHeight);
                                    }
                                }
                            } else {
                                float[] transformed = new float[vals.length * 2];
                                float posY = 0.0f;
                                float negY = -e2.getNegativeSum();
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
                                    transformed[k] = this.mAnimator.getPhaseY() * y;
                                    k += 2;
                                    idx++;
                                }
                                trans.pointValuesToPixel(transformed);
                                for (int k2 = 0; k2 < transformed.length; k2 += 2) {
                                    float val2 = vals[k2 / 2];
                                    String formattedValue3 = formatter.getFormattedValue(val2, e2, i, this.mViewPortHandler);
                                    float valueTextWidth3 = Utils.calcTextWidth(this.mValuePaint, formattedValue3);
                                    float posOffset3 = drawValueAboveBar ? valueOffsetPlus : -(valueTextWidth3 + valueOffsetPlus);
                                    float negOffset3 = drawValueAboveBar ? -(valueTextWidth3 + valueOffsetPlus) : valueOffsetPlus;
                                    if (isInverted) {
                                        posOffset3 = (-posOffset3) - valueTextWidth3;
                                        negOffset3 = (-negOffset3) - valueTextWidth3;
                                    }
                                    float x = transformed[k2] + (val2 >= 0.0f ? posOffset3 : negOffset3);
                                    float y2 = valuePoints[j2 + 1];
                                    if (this.mViewPortHandler.isInBoundsTop(y2)) {
                                        if (this.mViewPortHandler.isInBoundsX(x) && this.mViewPortHandler.isInBoundsBottom(y2)) {
                                            drawValue(c, formattedValue3, x, y2 + halfTextHeight);
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

    protected void drawValue(Canvas c, String valueText, float x, float y) {
        c.drawText(valueText, x, y, this.mValuePaint);
    }

    @Override // com.github.mikephil.charting.renderer.BarChartRenderer
    protected void prepareBarHighlight(float x, float y1, float y2, float barspaceHalf, Transformer trans) {
        float top = (x - 0.5f) + barspaceHalf;
        float bottom = (x + 0.5f) - barspaceHalf;
        this.mBarRect.set(y1, top, y2, bottom);
        trans.rectValueToPixelHorizontal(this.mBarRect, this.mAnimator.getPhaseY());
    }

    @Override // com.github.mikephil.charting.renderer.BarChartRenderer
    public float[] getTransformedValues(Transformer trans, List<BarEntry> entries, int dataSetIndex) {
        return trans.generateTransformedValuesHorizontalBarChart(entries, dataSetIndex, this.mChart.getBarData(), this.mAnimator.getPhaseY());
    }

    @Override // com.github.mikephil.charting.renderer.BarChartRenderer
    protected boolean passesCheck() {
        return ((float) this.mChart.getBarData().getYValCount()) < ((float) this.mChart.getMaxVisibleCount()) * this.mViewPortHandler.getScaleY();
    }
}
