package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.Entry;
import java.util.List;

/* loaded from: classes.dex */
public class CircleBuffer extends AbstractBuffer<Entry> {
    public CircleBuffer(int size) {
        super(size);
    }

    protected void addCircle(float x, float y) {
        float[] fArr = this.buffer;
        int i = this.index;
        this.index = i + 1;
        fArr[i] = x;
        float[] fArr2 = this.buffer;
        int i2 = this.index;
        this.index = i2 + 1;
        fArr2[i2] = y;
    }

    @Override // com.github.mikephil.charting.buffer.AbstractBuffer
    public void feed(List<Entry> entries) {
        int size = (int) Math.ceil(((this.mTo - this.mFrom) * this.phaseX) + this.mFrom);
        for (int i = this.mFrom; i < size; i++) {
            Entry e = entries.get(i);
            addCircle(e.getXIndex(), e.getVal() * this.phaseY);
        }
        reset();
    }
}
