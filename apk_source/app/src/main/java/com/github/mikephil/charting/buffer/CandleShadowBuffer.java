package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.CandleEntry;
import java.util.List;

/* loaded from: classes.dex */
public class CandleShadowBuffer extends AbstractBuffer<CandleEntry> {
    public CandleShadowBuffer(int size) {
        super(size);
    }

    private void addShadow(float x1, float y1, float x2, float y2) {
        float[] fArr = this.buffer;
        int i = this.index;
        this.index = i + 1;
        fArr[i] = x1;
        float[] fArr2 = this.buffer;
        int i2 = this.index;
        this.index = i2 + 1;
        fArr2[i2] = y1;
        float[] fArr3 = this.buffer;
        int i3 = this.index;
        this.index = i3 + 1;
        fArr3[i3] = x2;
        float[] fArr4 = this.buffer;
        int i4 = this.index;
        this.index = i4 + 1;
        fArr4[i4] = y2;
    }

    @Override // com.github.mikephil.charting.buffer.AbstractBuffer
    public void feed(List<CandleEntry> entries) {
        int size = (int) Math.ceil(((this.mTo - this.mFrom) * this.phaseX) + this.mFrom);
        for (int i = this.mFrom; i < size; i++) {
            CandleEntry e = entries.get(i);
            addShadow(e.getXIndex(), e.getHigh() * this.phaseY, e.getXIndex(), e.getLow() * this.phaseY);
        }
        reset();
    }
}
