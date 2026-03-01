package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.Entry;
import java.util.List;

/* loaded from: classes.dex */
public class LineBuffer extends AbstractBuffer<Entry> {
    public LineBuffer(int size) {
        super(size < 4 ? 4 : size);
    }

    public void moveTo(float x, float y) {
        if (this.index == 0) {
            float[] fArr = this.buffer;
            int i = this.index;
            this.index = i + 1;
            fArr[i] = x;
            float[] fArr2 = this.buffer;
            int i2 = this.index;
            this.index = i2 + 1;
            fArr2[i2] = y;
            this.buffer[this.index] = x;
            this.buffer[this.index + 1] = y;
        }
    }

    public void lineTo(float x, float y) {
        if (this.index == 2) {
            float[] fArr = this.buffer;
            int i = this.index;
            this.index = i + 1;
            fArr[i] = x;
            float[] fArr2 = this.buffer;
            int i2 = this.index;
            this.index = i2 + 1;
            fArr2[i2] = y;
            return;
        }
        float prevX = this.buffer[this.index - 2];
        float prevY = this.buffer[this.index - 1];
        float[] fArr3 = this.buffer;
        int i3 = this.index;
        this.index = i3 + 1;
        fArr3[i3] = prevX;
        float[] fArr4 = this.buffer;
        int i4 = this.index;
        this.index = i4 + 1;
        fArr4[i4] = prevY;
        float[] fArr5 = this.buffer;
        int i5 = this.index;
        this.index = i5 + 1;
        fArr5[i5] = x;
        float[] fArr6 = this.buffer;
        int i6 = this.index;
        this.index = i6 + 1;
        fArr6[i6] = y;
    }

    @Override // com.github.mikephil.charting.buffer.AbstractBuffer
    public void feed(List<Entry> entries) {
        moveTo(entries.get(this.mFrom).getXIndex(), entries.get(this.mFrom).getVal() * this.phaseY);
        int size = (int) Math.ceil(((this.mTo - this.mFrom) * this.phaseX) + this.mFrom);
        int from = this.mFrom + 1;
        for (int i = from; i < size; i++) {
            Entry e = entries.get(i);
            lineTo(e.getXIndex(), e.getVal() * this.phaseY);
        }
        reset();
    }
}
