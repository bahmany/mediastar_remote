package com.github.mikephil.charting.charts;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.listener.PieRadarChartTouchListener;
import com.github.mikephil.charting.utils.SelectionDetail;
import com.github.mikephil.charting.utils.Utils;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public abstract class PieRadarChartBase<T extends ChartData<? extends DataSet<? extends Entry>>> extends Chart<T> {
    protected float mMinOffset;
    private float mRawRotationAngle;
    protected boolean mRotateEnabled;
    private float mRotationAngle;

    public abstract int getIndexForAngle(float f);

    public abstract float getRadius();

    protected abstract float getRequiredBaseOffset();

    protected abstract float getRequiredLegendOffset();

    public PieRadarChartBase(Context context) {
        super(context);
        this.mRotationAngle = 270.0f;
        this.mRawRotationAngle = 270.0f;
        this.mRotateEnabled = true;
        this.mMinOffset = 0.0f;
    }

    public PieRadarChartBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mRotationAngle = 270.0f;
        this.mRawRotationAngle = 270.0f;
        this.mRotateEnabled = true;
        this.mMinOffset = 0.0f;
    }

    public PieRadarChartBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mRotationAngle = 270.0f;
        this.mRawRotationAngle = 270.0f;
        this.mRotateEnabled = true;
        this.mMinOffset = 0.0f;
    }

    @Override // com.github.mikephil.charting.charts.Chart
    protected void init() {
        super.init();
        this.mChartTouchListener = new PieRadarChartTouchListener(this);
    }

    @Override // com.github.mikephil.charting.charts.Chart
    protected void calcMinMax() {
        this.mDeltaX = this.mData.getXVals().size() - 1;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        return (!this.mTouchEnabled || this.mChartTouchListener == null) ? super.onTouchEvent(event) : this.mChartTouchListener.onTouch(this, event);
    }

    @Override // android.view.View
    public void computeScroll() {
        if (this.mChartTouchListener instanceof PieRadarChartTouchListener) {
            ((PieRadarChartTouchListener) this.mChartTouchListener).computeScroll();
        }
    }

    @Override // com.github.mikephil.charting.charts.Chart
    public void notifyDataSetChanged() {
        if (!this.mDataNotSet) {
            calcMinMax();
            if (this.mLegend != null) {
                this.mLegendRenderer.computeLegend(this.mData);
            }
            calculateOffsets();
        }
    }

    @Override // com.github.mikephil.charting.charts.Chart
    public void calculateOffsets() {
        float legendLeft = 0.0f;
        float legendRight = 0.0f;
        float legendBottom = 0.0f;
        float legendTop = 0.0f;
        if (this.mLegend != null && this.mLegend.isEnabled()) {
            float fullLegendWidth = Math.min(this.mLegend.mNeededWidth, this.mViewPortHandler.getChartWidth() * this.mLegend.getMaxSizePercent()) + this.mLegend.getFormSize() + this.mLegend.getFormToTextSpace();
            if (this.mLegend.getPosition() == Legend.LegendPosition.RIGHT_OF_CHART_CENTER) {
                float spacing = Utils.convertDpToPixel(13.0f);
                legendRight = fullLegendWidth + spacing;
            } else if (this.mLegend.getPosition() == Legend.LegendPosition.RIGHT_OF_CHART) {
                float spacing2 = Utils.convertDpToPixel(8.0f);
                float legendWidth = fullLegendWidth + spacing2;
                float legendHeight = this.mLegend.mNeededHeight + this.mLegend.mTextHeightMax;
                PointF c = getCenter();
                PointF bottomRight = new PointF((getWidth() - legendWidth) + 15.0f, 15.0f + legendHeight);
                float distLegend = distanceToCenter(bottomRight.x, bottomRight.y);
                PointF reference = getPosition(c, getRadius(), getAngleForPoint(bottomRight.x, bottomRight.y));
                float distReference = distanceToCenter(reference.x, reference.y);
                float min = Utils.convertDpToPixel(5.0f);
                if (distLegend < distReference) {
                    float diff = distReference - distLegend;
                    legendRight = min + diff;
                }
                if (bottomRight.y >= c.y && getHeight() - legendWidth > getWidth()) {
                    legendRight = legendWidth;
                }
            } else if (this.mLegend.getPosition() == Legend.LegendPosition.LEFT_OF_CHART_CENTER) {
                float spacing3 = Utils.convertDpToPixel(13.0f);
                legendLeft = fullLegendWidth + spacing3;
            } else if (this.mLegend.getPosition() == Legend.LegendPosition.LEFT_OF_CHART) {
                float spacing4 = Utils.convertDpToPixel(8.0f);
                float legendWidth2 = fullLegendWidth + spacing4;
                float legendHeight2 = this.mLegend.mNeededHeight + this.mLegend.mTextHeightMax;
                PointF c2 = getCenter();
                PointF bottomLeft = new PointF(legendWidth2 - 15.0f, 15.0f + legendHeight2);
                float distLegend2 = distanceToCenter(bottomLeft.x, bottomLeft.y);
                PointF reference2 = getPosition(c2, getRadius(), getAngleForPoint(bottomLeft.x, bottomLeft.y));
                float distReference2 = distanceToCenter(reference2.x, reference2.y);
                float min2 = Utils.convertDpToPixel(5.0f);
                if (distLegend2 < distReference2) {
                    float diff2 = distReference2 - distLegend2;
                    legendLeft = min2 + diff2;
                }
                if (bottomLeft.y >= c2.y && getHeight() - legendWidth2 > getWidth()) {
                    legendLeft = legendWidth2;
                }
            } else if (this.mLegend.getPosition() == Legend.LegendPosition.BELOW_CHART_LEFT || this.mLegend.getPosition() == Legend.LegendPosition.BELOW_CHART_RIGHT || this.mLegend.getPosition() == Legend.LegendPosition.BELOW_CHART_CENTER) {
                float yOffset = getRequiredLegendOffset();
                legendBottom = Math.min(this.mLegend.mNeededHeight + yOffset, this.mViewPortHandler.getChartHeight() * this.mLegend.getMaxSizePercent());
            } else if (this.mLegend.getPosition() == Legend.LegendPosition.ABOVE_CHART_LEFT || this.mLegend.getPosition() == Legend.LegendPosition.ABOVE_CHART_RIGHT || this.mLegend.getPosition() == Legend.LegendPosition.ABOVE_CHART_CENTER) {
                float yOffset2 = getRequiredLegendOffset();
                legendTop = Math.min(this.mLegend.mNeededHeight + yOffset2, this.mViewPortHandler.getChartHeight() * this.mLegend.getMaxSizePercent());
            }
            legendLeft += getRequiredBaseOffset();
            legendRight += getRequiredBaseOffset();
            legendTop += getRequiredBaseOffset();
        }
        float minOffset = Utils.convertDpToPixel(this.mMinOffset);
        if (this instanceof RadarChart) {
            XAxis x = ((RadarChart) this).getXAxis();
            if (x.isEnabled() && x.isDrawLabelsEnabled()) {
                minOffset = Math.max(minOffset, x.mLabelRotatedWidth);
            }
        }
        float legendTop2 = legendTop + getExtraTopOffset();
        float legendRight2 = legendRight + getExtraRightOffset();
        float legendBottom2 = legendBottom + getExtraBottomOffset();
        float offsetLeft = Math.max(minOffset, legendLeft + getExtraLeftOffset());
        float offsetTop = Math.max(minOffset, legendTop2);
        float offsetRight = Math.max(minOffset, legendRight2);
        float offsetBottom = Math.max(minOffset, Math.max(getRequiredBaseOffset(), legendBottom2));
        this.mViewPortHandler.restrainViewPort(offsetLeft, offsetTop, offsetRight, offsetBottom);
        if (this.mLogEnabled) {
            Log.i(Chart.LOG_TAG, "offsetLeft: " + offsetLeft + ", offsetTop: " + offsetTop + ", offsetRight: " + offsetRight + ", offsetBottom: " + offsetBottom);
        }
    }

    public float getAngleForPoint(float x, float y) {
        PointF c = getCenterOffsets();
        double tx = x - c.x;
        double ty = y - c.y;
        double length = Math.sqrt((tx * tx) + (ty * ty));
        double r = Math.acos(ty / length);
        float angle = (float) Math.toDegrees(r);
        if (x > c.x) {
            angle = 360.0f - angle;
        }
        float angle2 = angle + 90.0f;
        if (angle2 > 360.0f) {
            return angle2 - 360.0f;
        }
        return angle2;
    }

    protected PointF getPosition(PointF center, float dist, float angle) {
        PointF p = new PointF((float) (center.x + (dist * Math.cos(Math.toRadians(angle)))), (float) (center.y + (dist * Math.sin(Math.toRadians(angle)))));
        return p;
    }

    public float distanceToCenter(float x, float y) {
        float xDist;
        float yDist;
        PointF c = getCenterOffsets();
        if (x > c.x) {
            xDist = x - c.x;
        } else {
            xDist = c.x - x;
        }
        if (y > c.y) {
            yDist = y - c.y;
        } else {
            yDist = c.y - y;
        }
        float dist = (float) Math.sqrt(Math.pow(xDist, 2.0d) + Math.pow(yDist, 2.0d));
        return dist;
    }

    public void setRotationAngle(float angle) {
        this.mRawRotationAngle = angle;
        this.mRotationAngle = Utils.getNormalizedAngle(this.mRawRotationAngle);
    }

    public float getRawRotationAngle() {
        return this.mRawRotationAngle;
    }

    public float getRotationAngle() {
        return this.mRotationAngle;
    }

    public void setRotationEnabled(boolean enabled) {
        this.mRotateEnabled = enabled;
    }

    public boolean isRotationEnabled() {
        return this.mRotateEnabled;
    }

    public float getMinOffset() {
        return this.mMinOffset;
    }

    public void setMinOffset(float minOffset) {
        this.mMinOffset = minOffset;
    }

    public float getDiameter() {
        RectF content = this.mViewPortHandler.getContentRect();
        return Math.min(content.width(), content.height());
    }

    @Override // com.github.mikephil.charting.interfaces.ChartInterface
    public float getYChartMax() {
        return 0.0f;
    }

    @Override // com.github.mikephil.charting.interfaces.ChartInterface
    public float getYChartMin() {
        return 0.0f;
    }

    public List<SelectionDetail> getSelectionDetailsAtIndex(int xIndex) {
        List<SelectionDetail> vals = new ArrayList<>();
        for (int i = 0; i < this.mData.getDataSetCount(); i++) {
            DataSet<?> dataSet = this.mData.getDataSetByIndex(i);
            float yVal = dataSet.getYValForXIndex(xIndex);
            if (yVal != Float.NaN) {
                vals.add(new SelectionDetail(yVal, i, dataSet));
            }
        }
        return vals;
    }

    @SuppressLint({"NewApi"})
    public void spin(int durationmillis, float fromangle, float toangle, Easing.EasingOption easing) {
        if (Build.VERSION.SDK_INT >= 11) {
            setRotationAngle(fromangle);
            ObjectAnimator spinAnimator = ObjectAnimator.ofFloat(this, "rotationAngle", fromangle, toangle);
            spinAnimator.setDuration(durationmillis);
            spinAnimator.setInterpolator(Easing.getEasingFunctionFromOption(easing));
            spinAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.github.mikephil.charting.charts.PieRadarChartBase.1
                AnonymousClass1() {
                }

                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    PieRadarChartBase.this.postInvalidate();
                }
            });
            spinAnimator.start();
        }
    }

    /* renamed from: com.github.mikephil.charting.charts.PieRadarChartBase$1 */
    class AnonymousClass1 implements ValueAnimator.AnimatorUpdateListener {
        AnonymousClass1() {
        }

        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator animation) {
            PieRadarChartBase.this.postInvalidate();
        }
    }
}
