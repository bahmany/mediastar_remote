package com.github.mikephil.charting.data;

import android.graphics.Color;
import com.github.mikephil.charting.utils.Utils;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class BubbleDataSet extends BarLineScatterCandleBubbleDataSet<BubbleEntry> {
    private float mHighlightCircleWidth;
    protected float mMaxSize;
    protected float mXMax;
    protected float mXMin;

    public BubbleDataSet(List<BubbleEntry> yVals, String label) {
        super(yVals, label);
        this.mHighlightCircleWidth = 2.5f;
    }

    public void setHighlightCircleWidth(float width) {
        this.mHighlightCircleWidth = Utils.convertDpToPixel(width);
    }

    public float getHighlightCircleWidth() {
        return this.mHighlightCircleWidth;
    }

    public void setColor(int color, int alpha) {
        super.setColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
    }

    @Override // com.github.mikephil.charting.data.DataSet
    protected void calcMinMax(int start, int end) {
        if (this.mYVals.size() != 0) {
            List<T> yVals = getYVals();
            int endValue = end == 0 ? this.mYVals.size() - 1 : end;
            this.mLastStart = start;
            this.mLastEnd = endValue;
            this.mYMin = yMin((BubbleEntry) yVals.get(start));
            this.mYMax = yMax((BubbleEntry) yVals.get(start));
            for (int i = start; i <= endValue; i++) {
                BubbleEntry entry = (BubbleEntry) yVals.get(i);
                float ymin = yMin(entry);
                float ymax = yMax(entry);
                if (ymin < this.mYMin) {
                    this.mYMin = ymin;
                }
                if (ymax > this.mYMax) {
                    this.mYMax = ymax;
                }
                float xmin = xMin(entry);
                float xmax = xMax(entry);
                if (xmin < this.mXMin) {
                    this.mXMin = xmin;
                }
                if (xmax > this.mXMax) {
                    this.mXMax = xmax;
                }
                float size = largestSize(entry);
                if (size > this.mMaxSize) {
                    this.mMaxSize = size;
                }
            }
        }
    }

    @Override // com.github.mikephil.charting.data.DataSet
    public DataSet<BubbleEntry> copy() {
        List<BubbleEntry> yVals = new ArrayList<>();
        for (int i = 0; i < this.mYVals.size(); i++) {
            yVals.add(((BubbleEntry) this.mYVals.get(i)).copy());
        }
        BubbleDataSet copied = new BubbleDataSet(yVals, getLabel());
        copied.mColors = this.mColors;
        copied.mHighLightColor = this.mHighLightColor;
        return copied;
    }

    public float getXMax() {
        return this.mXMax;
    }

    public float getXMin() {
        return this.mXMin;
    }

    public float getMaxSize() {
        return this.mMaxSize;
    }

    private float yMin(BubbleEntry entry) {
        return entry.getVal();
    }

    private float yMax(BubbleEntry entry) {
        return entry.getVal();
    }

    private float xMin(BubbleEntry entry) {
        return entry.getXIndex();
    }

    private float xMax(BubbleEntry entry) {
        return entry.getXIndex();
    }

    private float largestSize(BubbleEntry entry) {
        return entry.getSize();
    }
}
