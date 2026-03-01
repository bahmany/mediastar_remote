package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.BarEntry;
import java.util.List;

/* loaded from: classes.dex */
public class BarBuffer extends AbstractBuffer<BarEntry> {
    protected float mBarSpace;
    protected boolean mContainsStacks;
    protected int mDataSetCount;
    protected int mDataSetIndex;
    protected float mGroupSpace;
    protected boolean mInverted;

    public BarBuffer(int size, float groupspace, int dataSetCount, boolean containsStacks) {
        super(size);
        this.mBarSpace = 0.0f;
        this.mGroupSpace = 0.0f;
        this.mDataSetIndex = 0;
        this.mDataSetCount = 1;
        this.mContainsStacks = false;
        this.mInverted = false;
        this.mGroupSpace = groupspace;
        this.mDataSetCount = dataSetCount;
        this.mContainsStacks = containsStacks;
    }

    public void setBarSpace(float barspace) {
        this.mBarSpace = barspace;
    }

    public void setDataSet(int index) {
        this.mDataSetIndex = index;
    }

    public void setInverted(boolean inverted) {
        this.mInverted = inverted;
    }

    protected void addBar(float left, float top, float right, float bottom) {
        float[] fArr = this.buffer;
        int i = this.index;
        this.index = i + 1;
        fArr[i] = left;
        float[] fArr2 = this.buffer;
        int i2 = this.index;
        this.index = i2 + 1;
        fArr2[i2] = top;
        float[] fArr3 = this.buffer;
        int i3 = this.index;
        this.index = i3 + 1;
        fArr3[i3] = right;
        float[] fArr4 = this.buffer;
        int i4 = this.index;
        this.index = i4 + 1;
        fArr4[i4] = bottom;
    }

    @Override // com.github.mikephil.charting.buffer.AbstractBuffer
    public void feed(List<BarEntry> entries) {
        float top;
        float bottom;
        float y;
        float yStart;
        float top2;
        float bottom2;
        float size = entries.size() * this.phaseX;
        int dataSetOffset = this.mDataSetCount - 1;
        float barSpaceHalf = this.mBarSpace / 2.0f;
        float groupSpaceHalf = this.mGroupSpace / 2.0f;
        for (int i = 0; i < size; i++) {
            BarEntry e = entries.get(i);
            float x = e.getXIndex() + (e.getXIndex() * dataSetOffset) + this.mDataSetIndex + (this.mGroupSpace * e.getXIndex()) + groupSpaceHalf;
            float y2 = e.getVal();
            float[] vals = e.getVals();
            if (!this.mContainsStacks || vals == null) {
                float left = (x - 0.5f) + barSpaceHalf;
                float right = (x + 0.5f) - barSpaceHalf;
                if (this.mInverted) {
                    bottom = y2 >= 0.0f ? y2 : 0.0f;
                    top = y2 <= 0.0f ? y2 : 0.0f;
                } else {
                    top = y2 >= 0.0f ? y2 : 0.0f;
                    bottom = y2 <= 0.0f ? y2 : 0.0f;
                }
                if (top > 0.0f) {
                    top *= this.phaseY;
                } else {
                    bottom *= this.phaseY;
                }
                addBar(left, top, right, bottom);
            } else {
                float posY = 0.0f;
                float negY = -e.getNegativeSum();
                for (float value : vals) {
                    if (value >= 0.0f) {
                        y = posY;
                        yStart = posY + value;
                        posY = yStart;
                    } else {
                        y = negY;
                        yStart = negY + Math.abs(value);
                        negY += Math.abs(value);
                    }
                    float left2 = (x - 0.5f) + barSpaceHalf;
                    float right2 = (x + 0.5f) - barSpaceHalf;
                    if (this.mInverted) {
                        bottom2 = y >= yStart ? y : yStart;
                        top2 = y <= yStart ? y : yStart;
                    } else {
                        top2 = y >= yStart ? y : yStart;
                        bottom2 = y <= yStart ? y : yStart;
                    }
                    addBar(left2, top2 * this.phaseY, right2, bottom2 * this.phaseY);
                }
            }
        }
        reset();
    }
}
