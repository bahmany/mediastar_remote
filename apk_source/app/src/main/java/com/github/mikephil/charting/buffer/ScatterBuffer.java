package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.Entry;
import java.util.List;

/* loaded from: classes.dex */
public class ScatterBuffer extends AbstractBuffer<Entry> {
    public ScatterBuffer(int size) {
        super(size);
    }

    protected void addForm(float x, float y) {
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
        float size = entries.size() * this.phaseX;
        for (int i = 0; i < size; i++) {
            Entry e = entries.get(i);
            addForm(e.getXIndex(), e.getVal() * this.phaseY);
        }
        reset();
    }
}
