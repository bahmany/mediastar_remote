package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.util.List;

/* loaded from: classes.dex */
public class YAxisRendererRadarChart extends YAxisRenderer {
    private RadarChart mChart;

    public YAxisRendererRadarChart(ViewPortHandler viewPortHandler, YAxis yAxis, RadarChart chart) {
        super(viewPortHandler, yAxis, null);
        this.mChart = chart;
    }

    @Override // com.github.mikephil.charting.renderer.YAxisRenderer
    public void computeAxis(float yMin, float yMax) {
        computeAxisValues(yMin, yMax);
    }

    @Override // com.github.mikephil.charting.renderer.YAxisRenderer
    protected void computeAxisValues(float min, float max) {
        int labelCount = this.mYAxis.getLabelCount();
        double range = Math.abs(max - min);
        if (labelCount == 0 || range <= 0.0d) {
            this.mYAxis.mEntries = new float[0];
            this.mYAxis.mEntryCount = 0;
            return;
        }
        double rawInterval = range / labelCount;
        double interval = Utils.roundToNextSignificant(rawInterval);
        double intervalMagnitude = Math.pow(10.0d, (int) Math.log10(interval));
        int intervalSigDigit = (int) (interval / intervalMagnitude);
        if (intervalSigDigit > 5) {
            interval = Math.floor(10.0d * intervalMagnitude);
        }
        if (this.mYAxis.isForceLabelsEnabled()) {
            float step = ((float) range) / (labelCount - 1);
            this.mYAxis.mEntryCount = labelCount;
            if (this.mYAxis.mEntries.length < labelCount) {
                this.mYAxis.mEntries = new float[labelCount];
            }
            float v = min;
            for (int i = 0; i < labelCount; i++) {
                this.mYAxis.mEntries[i] = v;
                v += step;
            }
        } else if (this.mYAxis.isShowOnlyMinMaxEnabled()) {
            this.mYAxis.mEntryCount = 2;
            this.mYAxis.mEntries = new float[2];
            this.mYAxis.mEntries[0] = min;
            this.mYAxis.mEntries[1] = max;
        } else {
            double rawCount = min / interval;
            double first = rawCount < 0.0d ? Math.floor(rawCount) * interval : Math.ceil(rawCount) * interval;
            if (first < min && this.mYAxis.isStartAtZeroEnabled()) {
                first = min;
            }
            if (first == 0.0d) {
                first = 0.0d;
            }
            double last = Utils.nextUp(Math.floor(max / interval) * interval);
            int n = 0;
            for (double f = first; f <= last; f += interval) {
                n++;
            }
            if (Float.isNaN(this.mYAxis.getAxisMaxValue())) {
                n++;
            }
            this.mYAxis.mEntryCount = n;
            if (this.mYAxis.mEntries.length < n) {
                this.mYAxis.mEntries = new float[n];
            }
            double f2 = first;
            for (int i2 = 0; i2 < n; i2++) {
                this.mYAxis.mEntries[i2] = (float) f2;
                f2 += interval;
            }
        }
        if (interval < 1.0d) {
            this.mYAxis.mDecimals = (int) Math.ceil(-Math.log10(interval));
        } else {
            this.mYAxis.mDecimals = 0;
        }
        if (!this.mYAxis.isStartAtZeroEnabled() && this.mYAxis.mEntries[0] < min) {
            this.mYAxis.mAxisMinimum = this.mYAxis.mEntries[0];
        }
        this.mYAxis.mAxisMaximum = this.mYAxis.mEntries[this.mYAxis.mEntryCount - 1];
        this.mYAxis.mAxisRange = Math.abs(this.mYAxis.mAxisMaximum - this.mYAxis.mAxisMinimum);
    }

    @Override // com.github.mikephil.charting.renderer.YAxisRenderer, com.github.mikephil.charting.renderer.AxisRenderer
    public void renderAxisLabels(Canvas c) {
        if (this.mYAxis.isEnabled() && this.mYAxis.isDrawLabelsEnabled()) {
            this.mAxisLabelPaint.setTypeface(this.mYAxis.getTypeface());
            this.mAxisLabelPaint.setTextSize(this.mYAxis.getTextSize());
            this.mAxisLabelPaint.setColor(this.mYAxis.getTextColor());
            PointF center = this.mChart.getCenterOffsets();
            float factor = this.mChart.getFactor();
            int labelCount = this.mYAxis.mEntryCount;
            for (int j = 0; j < labelCount; j++) {
                if (j != labelCount - 1 || this.mYAxis.isDrawTopYLabelEntryEnabled()) {
                    float r = (this.mYAxis.mEntries[j] - this.mYAxis.mAxisMinimum) * factor;
                    PointF p = Utils.getPosition(center, r, this.mChart.getRotationAngle());
                    String label = this.mYAxis.getFormattedLabel(j);
                    c.drawText(label, p.x + 10.0f, p.y, this.mAxisLabelPaint);
                } else {
                    return;
                }
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.github.mikephil.charting.renderer.YAxisRenderer, com.github.mikephil.charting.renderer.AxisRenderer
    public void renderLimitLines(Canvas c) {
        List<LimitLine> limitLines = this.mYAxis.getLimitLines();
        if (limitLines != null) {
            float sliceangle = this.mChart.getSliceAngle();
            float factor = this.mChart.getFactor();
            PointF center = this.mChart.getCenterOffsets();
            for (int i = 0; i < limitLines.size(); i++) {
                LimitLine l = limitLines.get(i);
                if (l.isEnabled()) {
                    this.mLimitLinePaint.setColor(l.getLineColor());
                    this.mLimitLinePaint.setPathEffect(l.getDashPathEffect());
                    this.mLimitLinePaint.setStrokeWidth(l.getLineWidth());
                    float r = (l.getLimit() - this.mChart.getYChartMin()) * factor;
                    Path limitPath = new Path();
                    for (int j = 0; j < ((RadarData) this.mChart.getData()).getXValCount(); j++) {
                        PointF p = Utils.getPosition(center, r, (j * sliceangle) + this.mChart.getRotationAngle());
                        if (j == 0) {
                            limitPath.moveTo(p.x, p.y);
                        } else {
                            limitPath.lineTo(p.x, p.y);
                        }
                    }
                    limitPath.close();
                    c.drawPath(limitPath, this.mLimitLinePaint);
                }
            }
        }
    }
}
