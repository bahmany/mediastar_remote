package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.CandleEntry;
import java.util.List;

/* loaded from: classes.dex */
public class CandleBodyBuffer extends AbstractBuffer<CandleEntry> {
    private float mBodySpace;

    public CandleBodyBuffer(int size) {
        super(size);
        this.mBodySpace = 0.0f;
    }

    public void setBodySpace(float bodySpace) {
        this.mBodySpace = bodySpace;
    }

    private void addBody(float left, float top, float right, float bottom) {
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
    public void feed(List<CandleEntry> entries) {
        int size = (int) Math.ceil(((this.mTo - this.mFrom) * this.phaseX) + this.mFrom);
        for (int i = this.mFrom; i < size; i++) {
            CandleEntry e = entries.get(i);
            addBody((e.getXIndex() - 0.5f) + this.mBodySpace, e.getClose() * this.phaseY, (e.getXIndex() + 0.5f) - this.mBodySpace, e.getOpen() * this.phaseY);
        }
        reset();
    }
}
