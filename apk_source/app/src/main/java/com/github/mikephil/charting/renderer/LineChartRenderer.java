package com.github.mikephil.charting.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.CircleBuffer;
import com.github.mikephil.charting.buffer.LineBuffer;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.LineDataProvider;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.util.List;

/* loaded from: classes.dex */
public class LineChartRenderer extends LineScatterCandleRadarRenderer {
    protected Path cubicFillPath;
    protected Path cubicPath;
    protected Canvas mBitmapCanvas;
    protected LineDataProvider mChart;
    protected CircleBuffer[] mCircleBuffers;
    protected Paint mCirclePaintInner;
    protected Bitmap mDrawBitmap;
    protected LineBuffer[] mLineBuffers;

    public LineChartRenderer(LineDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.cubicPath = new Path();
        this.cubicFillPath = new Path();
        this.mChart = chart;
        this.mCirclePaintInner = new Paint(1);
        this.mCirclePaintInner.setStyle(Paint.Style.FILL);
        this.mCirclePaintInner.setColor(-1);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.github.mikephil.charting.renderer.DataRenderer
    public void initBuffers() {
        LineData lineData = this.mChart.getLineData();
        this.mLineBuffers = new LineBuffer[lineData.getDataSetCount()];
        this.mCircleBuffers = new CircleBuffer[lineData.getDataSetCount()];
        for (int i = 0; i < this.mLineBuffers.length; i++) {
            LineDataSet set = (LineDataSet) lineData.getDataSetByIndex(i);
            this.mLineBuffers[i] = new LineBuffer((set.getEntryCount() * 4) - 4);
            this.mCircleBuffers[i] = new CircleBuffer(set.getEntryCount() * 2);
        }
    }

    @Override // com.github.mikephil.charting.renderer.DataRenderer
    public void drawData(Canvas c) {
        int width = (int) this.mViewPortHandler.getChartWidth();
        int height = (int) this.mViewPortHandler.getChartHeight();
        if (this.mDrawBitmap == null || this.mDrawBitmap.getWidth() != width || this.mDrawBitmap.getHeight() != height) {
            if (width > 0 && height > 0) {
                this.mDrawBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
                this.mBitmapCanvas = new Canvas(this.mDrawBitmap);
            } else {
                return;
            }
        }
        this.mDrawBitmap.eraseColor(0);
        LineData lineData = this.mChart.getLineData();
        for (T set : lineData.getDataSets()) {
            if (set.isVisible() && set.getEntryCount() > 0) {
                drawDataSet(c, set);
            }
        }
        c.drawBitmap(this.mDrawBitmap, 0.0f, 0.0f, this.mRenderPaint);
    }

    protected void drawDataSet(Canvas c, LineDataSet dataSet) {
        List<Entry> entries = dataSet.getYVals();
        if (entries.size() >= 1) {
            this.mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
            this.mRenderPaint.setPathEffect(dataSet.getDashPathEffect());
            if (dataSet.isDrawCubicEnabled()) {
                drawCubic(c, dataSet, entries);
            } else {
                drawLinear(c, dataSet, entries);
            }
            this.mRenderPaint.setPathEffect(null);
        }
    }

    protected void drawCubic(Canvas c, LineDataSet dataSet, List<Entry> entries) {
        Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
        Entry entryFrom = dataSet.getEntryForXIndex(this.mMinX);
        Entry entryTo = dataSet.getEntryForXIndex(this.mMaxX);
        int diff = entryFrom == entryTo ? 1 : 0;
        int minx = Math.max(dataSet.getEntryPosition(entryFrom) - diff, 0);
        int maxx = Math.min(Math.max(minx + 2, dataSet.getEntryPosition(entryTo) + 1), entries.size());
        float phaseX = this.mAnimator.getPhaseX();
        float phaseY = this.mAnimator.getPhaseY();
        float intensity = dataSet.getCubicIntensity();
        this.cubicPath.reset();
        int size = (int) Math.ceil(((maxx - minx) * phaseX) + minx);
        if (size - minx >= 2) {
            entries.get(minx);
            Entry prev = entries.get(minx);
            Entry cur = entries.get(minx);
            Entry next = entries.get(minx + 1);
            this.cubicPath.moveTo(cur.getXIndex(), cur.getVal() * phaseY);
            float prevDx = (cur.getXIndex() - prev.getXIndex()) * intensity;
            float prevDy = (cur.getVal() - prev.getVal()) * intensity;
            float curDx = (next.getXIndex() - cur.getXIndex()) * intensity;
            float curDy = (next.getVal() - cur.getVal()) * intensity;
            this.cubicPath.cubicTo(prev.getXIndex() + prevDx, (prev.getVal() + prevDy) * phaseY, cur.getXIndex() - curDx, (cur.getVal() - curDy) * phaseY, cur.getXIndex(), cur.getVal() * phaseY);
            int j = minx + 1;
            int count = Math.min(size, entries.size() - 1);
            while (j < count) {
                Entry prevPrev = entries.get(j == 1 ? 0 : j - 2);
                Entry prev2 = entries.get(j - 1);
                Entry cur2 = entries.get(j);
                Entry next2 = entries.get(j + 1);
                float prevDx2 = (cur2.getXIndex() - prevPrev.getXIndex()) * intensity;
                float prevDy2 = (cur2.getVal() - prevPrev.getVal()) * intensity;
                float curDx2 = (next2.getXIndex() - prev2.getXIndex()) * intensity;
                float curDy2 = (next2.getVal() - prev2.getVal()) * intensity;
                this.cubicPath.cubicTo(prev2.getXIndex() + prevDx2, (prev2.getVal() + prevDy2) * phaseY, cur2.getXIndex() - curDx2, (cur2.getVal() - curDy2) * phaseY, cur2.getXIndex(), cur2.getVal() * phaseY);
                j++;
            }
            if (size > entries.size() - 1) {
                Entry prevPrev2 = entries.get(entries.size() >= 3 ? entries.size() - 3 : entries.size() - 2);
                Entry prev3 = entries.get(entries.size() - 2);
                Entry cur3 = entries.get(entries.size() - 1);
                float prevDx3 = (cur3.getXIndex() - prevPrev2.getXIndex()) * intensity;
                float prevDy3 = (cur3.getVal() - prevPrev2.getVal()) * intensity;
                float curDx3 = (cur3.getXIndex() - prev3.getXIndex()) * intensity;
                float curDy3 = (cur3.getVal() - prev3.getVal()) * intensity;
                this.cubicPath.cubicTo(prev3.getXIndex() + prevDx3, (prev3.getVal() + prevDy3) * phaseY, cur3.getXIndex() - curDx3, (cur3.getVal() - curDy3) * phaseY, cur3.getXIndex(), cur3.getVal() * phaseY);
            }
        }
        if (dataSet.isDrawFilledEnabled()) {
            this.cubicFillPath.reset();
            this.cubicFillPath.addPath(this.cubicPath);
            drawCubicFill(this.mBitmapCanvas, dataSet, this.cubicFillPath, trans, entryFrom.getXIndex(), entryFrom.getXIndex() + size);
        }
        this.mRenderPaint.setColor(dataSet.getColor());
        this.mRenderPaint.setStyle(Paint.Style.STROKE);
        trans.pathValueToPixel(this.cubicPath);
        this.mBitmapCanvas.drawPath(this.cubicPath, this.mRenderPaint);
        this.mRenderPaint.setPathEffect(null);
    }

    protected void drawCubicFill(Canvas c, LineDataSet dataSet, Path spline, Transformer trans, int from, int to) {
        if (to - from > 1) {
            float fillMin = dataSet.getFillFormatter().getFillLinePosition(dataSet, this.mChart);
            spline.lineTo(to - 1, fillMin);
            spline.lineTo(from, fillMin);
            spline.close();
            trans.pathValueToPixel(spline);
            drawFilledPath(c, spline, dataSet.getFillColor(), dataSet.getFillAlpha());
        }
    }

    protected void drawLinear(Canvas c, LineDataSet dataSet, List<Entry> entries) {
        Canvas canvas;
        int dataSetIndex = this.mChart.getLineData().getIndexOfDataSet(dataSet);
        Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
        float phaseX = this.mAnimator.getPhaseX();
        float phaseY = this.mAnimator.getPhaseY();
        this.mRenderPaint.setStyle(Paint.Style.STROKE);
        if (dataSet.isDashedLineEnabled()) {
            canvas = this.mBitmapCanvas;
        } else {
            canvas = c;
        }
        Entry entryFrom = dataSet.getEntryForXIndex(this.mMinX);
        Entry entryTo = dataSet.getEntryForXIndex(this.mMaxX);
        int diff = entryFrom == entryTo ? 1 : 0;
        int minx = Math.max(dataSet.getEntryPosition(entryFrom) - diff, 0);
        int maxx = Math.min(Math.max(minx + 2, dataSet.getEntryPosition(entryTo) + 1), entries.size());
        int range = ((maxx - minx) * 4) - 4;
        LineBuffer buffer = this.mLineBuffers[dataSetIndex];
        buffer.setPhases(phaseX, phaseY);
        buffer.limitFrom(minx);
        buffer.limitTo(maxx);
        buffer.feed(entries);
        trans.pointValuesToPixel(buffer.buffer);
        if (dataSet.getColors().size() > 1) {
            for (int j = 0; j < range && this.mViewPortHandler.isInBoundsRight(buffer.buffer[j]); j += 4) {
                if (this.mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]) && ((this.mViewPortHandler.isInBoundsTop(buffer.buffer[j + 1]) || this.mViewPortHandler.isInBoundsBottom(buffer.buffer[j + 3])) && (this.mViewPortHandler.isInBoundsTop(buffer.buffer[j + 1]) || this.mViewPortHandler.isInBoundsBottom(buffer.buffer[j + 3])))) {
                    this.mRenderPaint.setColor(dataSet.getColor((j / 4) + minx));
                    canvas.drawLine(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2], buffer.buffer[j + 3], this.mRenderPaint);
                }
            }
        } else {
            this.mRenderPaint.setColor(dataSet.getColor());
            canvas.drawLines(buffer.buffer, 0, range, this.mRenderPaint);
        }
        this.mRenderPaint.setPathEffect(null);
        if (dataSet.isDrawFilledEnabled() && entries.size() > 0) {
            drawLinearFill(c, dataSet, entries, minx, maxx, trans);
        }
    }

    protected void drawLinearFill(Canvas c, LineDataSet dataSet, List<Entry> entries, int minx, int maxx, Transformer trans) {
        Path filled = generateFilledPath(entries, dataSet.getFillFormatter().getFillLinePosition(dataSet, this.mChart), minx, maxx);
        trans.pathValueToPixel(filled);
        drawFilledPath(c, filled, dataSet.getFillColor(), dataSet.getFillAlpha());
    }

    protected void drawFilledPath(Canvas c, Path filledPath, int fillColor, int fillAlpha) {
        c.save();
        c.clipPath(filledPath);
        int color = (fillAlpha << 24) | (16777215 & fillColor);
        c.drawColor(color);
        c.restore();
    }

    private Path generateFilledPath(List<Entry> entries, float fillMin, int from, int to) {
        float phaseX = this.mAnimator.getPhaseX();
        float phaseY = this.mAnimator.getPhaseY();
        Path filled = new Path();
        filled.moveTo(entries.get(from).getXIndex(), fillMin);
        filled.lineTo(entries.get(from).getXIndex(), entries.get(from).getVal() * phaseY);
        int count = (int) Math.ceil(((to - from) * phaseX) + from);
        for (int x = from + 1; x < count; x++) {
            Entry e = entries.get(x);
            filled.lineTo(e.getXIndex(), e.getVal() * phaseY);
        }
        filled.lineTo(entries.get(Math.max(Math.min(((int) Math.ceil(((to - from) * phaseX) + from)) - 1, entries.size() - 1), 0)).getXIndex(), fillMin);
        filled.close();
        return filled;
    }

    @Override // com.github.mikephil.charting.renderer.DataRenderer
    public void drawValues(Canvas c) {
        if (this.mChart.getLineData().getYValCount() < this.mChart.getMaxVisibleCount() * this.mViewPortHandler.getScaleX()) {
            List<T> dataSets = this.mChart.getLineData().getDataSets();
            for (int i = 0; i < dataSets.size(); i++) {
                LineDataSet dataSet = (LineDataSet) dataSets.get(i);
                if (dataSet.isDrawValuesEnabled() && dataSet.getEntryCount() != 0) {
                    applyValueTextStyle(dataSet);
                    Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
                    int valOffset = (int) (dataSet.getCircleSize() * 1.75f);
                    if (!dataSet.isDrawCirclesEnabled()) {
                        valOffset /= 2;
                    }
                    List<? extends Entry> yVals = dataSet.getYVals();
                    Entry entryFrom = dataSet.getEntryForXIndex(this.mMinX);
                    Entry entryTo = dataSet.getEntryForXIndex(this.mMaxX);
                    int diff = entryFrom == entryTo ? 1 : 0;
                    int minx = Math.max(dataSet.getEntryPosition(entryFrom) - diff, 0);
                    int maxx = Math.min(Math.max(minx + 2, dataSet.getEntryPosition(entryTo) + 1), yVals.size());
                    float[] positions = trans.generateTransformedValuesLine(yVals, this.mAnimator.getPhaseX(), this.mAnimator.getPhaseY(), minx, maxx);
                    for (int j = 0; j < positions.length; j += 2) {
                        float x = positions[j];
                        float y = positions[j + 1];
                        if (this.mViewPortHandler.isInBoundsRight(x)) {
                            if (this.mViewPortHandler.isInBoundsLeft(x) && this.mViewPortHandler.isInBoundsY(y)) {
                                Entry entry = yVals.get((j / 2) + minx);
                                drawValue(c, dataSet.getValueFormatter(), entry.getVal(), entry, i, x, y - valOffset);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override // com.github.mikephil.charting.renderer.DataRenderer
    public void drawExtras(Canvas c) {
        drawCircles(c);
    }

    protected void drawCircles(Canvas c) {
        this.mRenderPaint.setStyle(Paint.Style.FILL);
        float phaseX = this.mAnimator.getPhaseX();
        float phaseY = this.mAnimator.getPhaseY();
        List<T> dataSets = this.mChart.getLineData().getDataSets();
        for (int i = 0; i < dataSets.size(); i++) {
            LineDataSet dataSet = (LineDataSet) dataSets.get(i);
            if (dataSet.isVisible() && dataSet.isDrawCirclesEnabled() && dataSet.getEntryCount() != 0) {
                this.mCirclePaintInner.setColor(dataSet.getCircleHoleColor());
                Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
                List<Entry> entries = dataSet.getYVals();
                Entry entryFrom = dataSet.getEntryForXIndex(this.mMinX < 0 ? 0 : this.mMinX);
                Entry entryTo = dataSet.getEntryForXIndex(this.mMaxX);
                int diff = entryFrom == entryTo ? 1 : 0;
                int minx = Math.max(dataSet.getEntryPosition(entryFrom) - diff, 0);
                int maxx = Math.min(Math.max(minx + 2, dataSet.getEntryPosition(entryTo) + 1), entries.size());
                CircleBuffer buffer = this.mCircleBuffers[i];
                buffer.setPhases(phaseX, phaseY);
                buffer.limitFrom(minx);
                buffer.limitTo(maxx);
                buffer.feed(entries);
                trans.pointValuesToPixel(buffer.buffer);
                float halfsize = dataSet.getCircleSize() / 2.0f;
                int count = ((int) Math.ceil(((maxx - minx) * phaseX) + minx)) * 2;
                for (int j = 0; j < count; j += 2) {
                    float x = buffer.buffer[j];
                    float y = buffer.buffer[j + 1];
                    if (this.mViewPortHandler.isInBoundsRight(x)) {
                        if (this.mViewPortHandler.isInBoundsLeft(x) && this.mViewPortHandler.isInBoundsY(y)) {
                            int circleColor = dataSet.getCircleColor((j / 2) + minx);
                            this.mRenderPaint.setColor(circleColor);
                            c.drawCircle(x, y, dataSet.getCircleSize(), this.mRenderPaint);
                            if (dataSet.isDrawCircleHoleEnabled() && circleColor != this.mCirclePaintInner.getColor()) {
                                c.drawCircle(x, y, halfsize, this.mCirclePaintInner);
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
        for (int i = 0; i < indices.length; i++) {
            LineDataSet set = (LineDataSet) this.mChart.getLineData().getDataSetByIndex(indices[i].getDataSetIndex());
            if (set != null && set.isHighlightEnabled()) {
                int xIndex = indices[i].getXIndex();
                if (xIndex <= this.mChart.getXChartMax() * this.mAnimator.getPhaseX()) {
                    float yVal = set.getYValForXIndex(xIndex);
                    if (yVal != Float.NaN) {
                        float y = yVal * this.mAnimator.getPhaseY();
                        float[] pts = {xIndex, y};
                        this.mChart.getTransformer(set.getAxisDependency()).pointValuesToPixel(pts);
                        drawHighlightLines(c, pts, set);
                    }
                }
            }
        }
    }

    public void releaseBitmap() {
        if (this.mDrawBitmap != null) {
            this.mDrawBitmap.recycle();
            this.mDrawBitmap = null;
        }
    }
}
