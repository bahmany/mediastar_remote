package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.ScatterBuffer;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.ScatterDataProvider;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.util.List;

/* loaded from: classes.dex */
public class ScatterChartRenderer extends LineScatterCandleRadarRenderer {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$github$mikephil$charting$charts$ScatterChart$ScatterShape;
    protected ScatterDataProvider mChart;
    protected ScatterBuffer[] mScatterBuffers;

    static /* synthetic */ int[] $SWITCH_TABLE$com$github$mikephil$charting$charts$ScatterChart$ScatterShape() {
        int[] iArr = $SWITCH_TABLE$com$github$mikephil$charting$charts$ScatterChart$ScatterShape;
        if (iArr == null) {
            iArr = new int[ScatterChart.ScatterShape.valuesCustom().length];
            try {
                iArr[ScatterChart.ScatterShape.CIRCLE.ordinal()] = 3;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[ScatterChart.ScatterShape.CROSS.ordinal()] = 1;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[ScatterChart.ScatterShape.SQUARE.ordinal()] = 4;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[ScatterChart.ScatterShape.TRIANGLE.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            $SWITCH_TABLE$com$github$mikephil$charting$charts$ScatterChart$ScatterShape = iArr;
        }
        return iArr;
    }

    public ScatterChartRenderer(ScatterDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.mChart = chart;
        this.mRenderPaint.setStrokeWidth(Utils.convertDpToPixel(1.0f));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.github.mikephil.charting.renderer.DataRenderer
    public void initBuffers() {
        ScatterData scatterData = this.mChart.getScatterData();
        this.mScatterBuffers = new ScatterBuffer[scatterData.getDataSetCount()];
        for (int i = 0; i < this.mScatterBuffers.length; i++) {
            ScatterDataSet set = (ScatterDataSet) scatterData.getDataSetByIndex(i);
            this.mScatterBuffers[i] = new ScatterBuffer(set.getEntryCount() * 2);
        }
    }

    @Override // com.github.mikephil.charting.renderer.DataRenderer
    public void drawData(Canvas c) {
        ScatterData scatterData = this.mChart.getScatterData();
        for (T set : scatterData.getDataSets()) {
            if (set.isVisible()) {
                drawDataSet(c, set);
            }
        }
    }

    protected void drawDataSet(Canvas c, ScatterDataSet dataSet) {
        Transformer trans = this.mChart.getTransformer(dataSet.getAxisDependency());
        float phaseX = this.mAnimator.getPhaseX();
        float phaseY = this.mAnimator.getPhaseY();
        List<T> yVals = dataSet.getYVals();
        float shapeHalf = dataSet.getScatterShapeSize() / 2.0f;
        ScatterChart.ScatterShape shape = dataSet.getScatterShape();
        ScatterBuffer scatterBuffer = this.mScatterBuffers[this.mChart.getScatterData().getIndexOfDataSet(dataSet)];
        scatterBuffer.setPhases(phaseX, phaseY);
        scatterBuffer.feed(yVals);
        trans.pointValuesToPixel(scatterBuffer.buffer);
        switch ($SWITCH_TABLE$com$github$mikephil$charting$charts$ScatterChart$ScatterShape()[shape.ordinal()]) {
            case 1:
                this.mRenderPaint.setStyle(Paint.Style.STROKE);
                for (int i = 0; i < scatterBuffer.size() && this.mViewPortHandler.isInBoundsRight(scatterBuffer.buffer[i]); i += 2) {
                    if (this.mViewPortHandler.isInBoundsLeft(scatterBuffer.buffer[i]) && this.mViewPortHandler.isInBoundsY(scatterBuffer.buffer[i + 1])) {
                        this.mRenderPaint.setColor(dataSet.getColor(i / 2));
                        c.drawLine(scatterBuffer.buffer[i] - shapeHalf, scatterBuffer.buffer[i + 1], scatterBuffer.buffer[i] + shapeHalf, scatterBuffer.buffer[i + 1], this.mRenderPaint);
                        c.drawLine(scatterBuffer.buffer[i], scatterBuffer.buffer[i + 1] - shapeHalf, scatterBuffer.buffer[i], scatterBuffer.buffer[i + 1] + shapeHalf, this.mRenderPaint);
                    }
                }
                break;
            case 2:
                this.mRenderPaint.setStyle(Paint.Style.FILL);
                Path tri = new Path();
                for (int i2 = 0; i2 < scatterBuffer.size() && this.mViewPortHandler.isInBoundsRight(scatterBuffer.buffer[i2]); i2 += 2) {
                    if (this.mViewPortHandler.isInBoundsLeft(scatterBuffer.buffer[i2]) && this.mViewPortHandler.isInBoundsY(scatterBuffer.buffer[i2 + 1])) {
                        this.mRenderPaint.setColor(dataSet.getColor(i2 / 2));
                        tri.moveTo(scatterBuffer.buffer[i2], scatterBuffer.buffer[i2 + 1] - shapeHalf);
                        tri.lineTo(scatterBuffer.buffer[i2] + shapeHalf, scatterBuffer.buffer[i2 + 1] + shapeHalf);
                        tri.lineTo(scatterBuffer.buffer[i2] - shapeHalf, scatterBuffer.buffer[i2 + 1] + shapeHalf);
                        tri.close();
                        c.drawPath(tri, this.mRenderPaint);
                        tri.reset();
                    }
                }
                break;
            case 3:
                this.mRenderPaint.setStyle(Paint.Style.FILL);
                for (int i3 = 0; i3 < scatterBuffer.size() && this.mViewPortHandler.isInBoundsRight(scatterBuffer.buffer[i3]); i3 += 2) {
                    if (this.mViewPortHandler.isInBoundsLeft(scatterBuffer.buffer[i3]) && this.mViewPortHandler.isInBoundsY(scatterBuffer.buffer[i3 + 1])) {
                        this.mRenderPaint.setColor(dataSet.getColor(i3 / 2));
                        c.drawCircle(scatterBuffer.buffer[i3], scatterBuffer.buffer[i3 + 1], shapeHalf, this.mRenderPaint);
                    }
                }
                break;
            case 4:
                this.mRenderPaint.setStyle(Paint.Style.FILL);
                for (int i4 = 0; i4 < scatterBuffer.size() && this.mViewPortHandler.isInBoundsRight(scatterBuffer.buffer[i4]); i4 += 2) {
                    if (this.mViewPortHandler.isInBoundsLeft(scatterBuffer.buffer[i4]) && this.mViewPortHandler.isInBoundsY(scatterBuffer.buffer[i4 + 1])) {
                        this.mRenderPaint.setColor(dataSet.getColor(i4 / 2));
                        c.drawRect(scatterBuffer.buffer[i4] - shapeHalf, scatterBuffer.buffer[i4 + 1] - shapeHalf, scatterBuffer.buffer[i4] + shapeHalf, scatterBuffer.buffer[i4 + 1] + shapeHalf, this.mRenderPaint);
                    }
                }
                break;
        }
    }

    @Override // com.github.mikephil.charting.renderer.DataRenderer
    public void drawValues(Canvas c) {
        if (this.mChart.getScatterData().getYValCount() < this.mChart.getMaxVisibleCount() * this.mViewPortHandler.getScaleX()) {
            List<T> dataSets = this.mChart.getScatterData().getDataSets();
            for (int i = 0; i < this.mChart.getScatterData().getDataSetCount(); i++) {
                ScatterDataSet dataSet = (ScatterDataSet) dataSets.get(i);
                if (dataSet.isDrawValuesEnabled() && dataSet.getEntryCount() != 0) {
                    applyValueTextStyle(dataSet);
                    List<T> yVals = dataSet.getYVals();
                    float[] positions = this.mChart.getTransformer(dataSet.getAxisDependency()).generateTransformedValuesScatter(yVals, this.mAnimator.getPhaseY());
                    float shapeSize = dataSet.getScatterShapeSize();
                    for (int j = 0; j < positions.length * this.mAnimator.getPhaseX() && this.mViewPortHandler.isInBoundsRight(positions[j]); j += 2) {
                        if (this.mViewPortHandler.isInBoundsLeft(positions[j]) && this.mViewPortHandler.isInBoundsY(positions[j + 1])) {
                            Entry entry = (Entry) yVals.get(j / 2);
                            drawValue(c, dataSet.getValueFormatter(), entry.getVal(), entry, i, positions[j], positions[j + 1] - shapeSize);
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
        for (int i = 0; i < indices.length; i++) {
            ScatterDataSet set = (ScatterDataSet) this.mChart.getScatterData().getDataSetByIndex(indices[i].getDataSetIndex());
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
}
