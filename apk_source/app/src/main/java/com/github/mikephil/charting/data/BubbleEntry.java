package com.github.mikephil.charting.data;

/* loaded from: classes.dex */
public class BubbleEntry extends Entry {
    private float mSize;

    public BubbleEntry(int xIndex, float val, float size) {
        super(val, xIndex);
        this.mSize = 0.0f;
        this.mSize = size;
    }

    public BubbleEntry(int xIndex, float val, float size, Object data) {
        super(val, xIndex, data);
        this.mSize = 0.0f;
        this.mSize = size;
    }

    @Override // com.github.mikephil.charting.data.Entry
    public BubbleEntry copy() {
        BubbleEntry c = new BubbleEntry(getXIndex(), getVal(), this.mSize, getData());
        return c;
    }

    public float getSize() {
        return this.mSize;
    }

    public void setSize(float size) {
        this.mSize = size;
    }
}
