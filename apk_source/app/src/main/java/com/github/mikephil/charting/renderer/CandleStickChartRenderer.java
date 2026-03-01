package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.CandleBodyBuffer;
import com.github.mikephil.charting.buffer.CandleShadowBuffer;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.CandleDataProvider;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.util.List;

/* loaded from: classes.dex */
public class CandleStickChartRenderer extends LineScatterCandleRadarRenderer {
    private CandleBodyBuffer[] mBodyBuffers;
    protected CandleDataProvider mChart;
    private CandleShadowBuffer[] mShadowBuffers;

    public CandleStickChartRenderer(CandleDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.mChart = chart;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.github.mikephil.charting.renderer.DataRenderer
    public void initBuffers() {
        CandleData candleData = this.mChart.getCandleData();
        this.mShadowBuffers = new CandleShadowBuffer[candleData.getDataSetCount()];
        this.mBodyBuffers = new CandleBodyBuffer[candleData.getDataSetCount()];
        for (int i = 0; i < this.mShadowBuffers.length; i++) {
            CandleDataSet set = (CandleDataSet) candleData.getDataSetByIndex(i);
            this.mShadowBuffers[i] = new CandleShadowBuffer(set.getValueCount() * 4);
            this.mBodyBuffers[i] = new CandleBodyBuffer(set.getValueCount() * 4);
        }
    }

    @Override // com.github.mikephil.charting.renderer.DataRenderer
    public void drawData(Canvas c) {
        CandleData candleData = this.mChart.getCandleData();
        for (T set : candleData.getDataSets()) {
            if (set.isVisible() && set.getEntryCount() > 0) {
                drawDataSet(c, set);
            }
        }
    }

    protected void drawDataSet(Canvas c, CandleDataSet dataSet) {
        int shadowColor;
        int shadowColor2;
        int increasingColor;
        int decreasingColor;
        Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
        float phaseX = this.mAnimator.getPhaseX();
        float phaseY = this.mAnimator.getPhaseY();
        int dataSetIndex = this.mChart.getCandleData().getIndexOfDataSet(dataSet);
        List<T> yVals = dataSet.getYVals();
        int minx = Math.max(this.mMinX, 0);
        int maxx = Math.min(this.mMaxX + 1, yVals.size());
        int range = (maxx - minx) * 4;
        int to = (int) Math.ceil(((maxx - minx) * phaseX) + minx);
        CandleBodyBuffer candleBodyBuffer = this.mBodyBuffers[dataSetIndex];
        candleBodyBuffer.setBodySpace(dataSet.getBodySpace());
        candleBodyBuffer.setPhases(phaseX, phaseY);
        candleBodyBuffer.limitFrom(minx);
        candleBodyBuffer.limitTo(maxx);
        candleBodyBuffer.feed(yVals);
        trans.pointValuesToPixel(candleBodyBuffer.buffer);
        CandleShadowBuffer candleShadowBuffer = this.mShadowBuffers[dataSetIndex];
        candleShadowBuffer.setPhases(phaseX, phaseY);
        candleShadowBuffer.limitFrom(minx);
        candleShadowBuffer.limitTo(maxx);
        candleShadowBuffer.feed(yVals);
        trans.pointValuesToPixel(candleShadowBuffer.buffer);
        this.mRenderPaint.setStrokeWidth(dataSet.getShadowWidth());
        for (int j = 0; j < range; j += 4) {
            CandleEntry e = (CandleEntry) yVals.get((j / 4) + minx);
            if (fitsBounds(e.getXIndex(), this.mMinX, to)) {
                if (!dataSet.getShadowColorSameAsCandle()) {
                    Paint paint = this.mRenderPaint;
                    if (dataSet.getShadowColor() == -1) {
                        shadowColor = dataSet.getColor(j);
                    } else {
                        shadowColor = dataSet.getShadowColor();
                    }
                    paint.setColor(shadowColor);
                } else if (e.getOpen() > e.getClose()) {
                    Paint paint2 = this.mRenderPaint;
                    if (dataSet.getDecreasingColor() == -1) {
                        decreasingColor = dataSet.getColor(j);
                    } else {
                        decreasingColor = dataSet.getDecreasingColor();
                    }
                    paint2.setColor(decreasingColor);
                } else if (e.getOpen() < e.getClose()) {
                    Paint paint3 = this.mRenderPaint;
                    if (dataSet.getIncreasingColor() == -1) {
                        increasingColor = dataSet.getColor(j);
                    } else {
                        increasingColor = dataSet.getIncreasingColor();
                    }
                    paint3.setColor(increasingColor);
                } else {
                    Paint paint4 = this.mRenderPaint;
                    if (dataSet.getShadowColor() == -1) {
                        shadowColor2 = dataSet.getColor(j);
                    } else {
                        shadowColor2 = dataSet.getShadowColor();
                    }
                    paint4.setColor(shadowColor2);
                }
                this.mRenderPaint.setStyle(Paint.Style.STROKE);
                c.drawLine(candleShadowBuffer.buffer[j], candleShadowBuffer.buffer[j + 1], candleShadowBuffer.buffer[j + 2], candleShadowBuffer.buffer[j + 3], this.mRenderPaint);
                float leftBody = candleBodyBuffer.buffer[j];
                float open = candleBodyBuffer.buffer[j + 1];
                float rightBody = candleBodyBuffer.buffer[j + 2];
                float close = candleBodyBuffer.buffer[j + 3];
                if (open > close) {
                    if (dataSet.getDecreasingColor() == -1) {
                        this.mRenderPaint.setColor(dataSet.getColor((j / 4) + minx));
                    } else {
                        this.mRenderPaint.setColor(dataSet.getDecreasingColor());
                    }
                    this.mRenderPaint.setStyle(dataSet.getDecreasingPaintStyle());
                    c.drawRect(leftBody, close, rightBody, open, this.mRenderPaint);
                } else if (open < close) {
                    if (dataSet.getIncreasingColor() == -1) {
                        this.mRenderPaint.setColor(dataSet.getColor((j / 4) + minx));
                    } else {
                        this.mRenderPaint.setColor(dataSet.getIncreasingColor());
                    }
                    this.mRenderPaint.setStyle(dataSet.getIncreasingPaintStyle());
                    c.drawRect(leftBody, open, rightBody, close, this.mRenderPaint);
                } else {
                    this.mRenderPaint.setColor(dataSet.getShadowColor());
                    c.drawLine(leftBody, open, rightBody, close, this.mRenderPaint);
                }
            }
        }
    }

    @Override // com.github.mikephil.charting.renderer.DataRenderer
    public void drawValues(Canvas c) {
        if (this.mChart.getCandleData().getYValCount() < this.mChart.getMaxVisibleCount() * this.mViewPortHandler.getScaleX()) {
            List<T> dataSets = this.mChart.getCandleData().getDataSets();
            for (int i = 0; i < dataSets.size(); i++) {
                DataSet<?> dataSet = (CandleDataSet) dataSets.get(i);
                if (dataSet.isDrawValuesEnabled() && dataSet.getEntryCount() != 0) {
                    applyValueTextStyle(dataSet);
                    Transformer transformer = this.mChart.getTransformer(dataSet.getAxisDependency());
                    List<T> yVals = dataSet.getYVals();
                    int minx = Math.max(this.mMinX, 0);
                    int maxx = Math.min(this.mMaxX + 1, yVals.size());
                    float[] positions = transformer.generateTransformedValuesCandle(yVals, this.mAnimator.getPhaseX(), this.mAnimator.getPhaseY(), minx, maxx);
                    float yOffset = Utils.convertDpToPixel(5.0f);
                    for (int j = 0; j < positions.length; j += 2) {
                        float x = positions[j];
                        float y = positions[j + 1];
                        if (this.mViewPortHandler.isInBoundsRight(x)) {
                            if (this.mViewPortHandler.isInBoundsLeft(x) && this.mViewPortHandler.isInBoundsY(y)) {
                                CandleEntry entry = (CandleEntry) yVals.get((j / 2) + minx);
                                drawValue(c, dataSet.getValueFormatter(), entry.getHigh(), entry, i, x, y - yOffset);
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
        CandleEntry e;
        for (int i = 0; i < indices.length; i++) {
            int xIndex = indices[i].getXIndex();
            CandleDataSet set = (CandleDataSet) this.mChart.getCandleData().getDataSetByIndex(indices[i].getDataSetIndex());
            if (set != null && set.isHighlightEnabled() && (e = (CandleEntry) set.getEntryForXIndex(xIndex)) != null && e.getXIndex() == xIndex) {
                float low = e.getLow() * this.mAnimator.getPhaseY();
                float high = e.getHigh() * this.mAnimator.getPhaseY();
                float y = (low + high) / 2.0f;
                this.mChart.getYChartMin();
                this.mChart.getYChartMax();
                float[] pts = {xIndex, y};
                this.mChart.getTransformer(set.getAxisDependency()).pointValuesToPixel(pts);
                drawHighlightLines(c, pts, set);
            }
        }
    }
}
