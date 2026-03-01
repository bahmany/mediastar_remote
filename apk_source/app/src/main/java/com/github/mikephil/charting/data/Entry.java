package com.github.mikephil.charting.data;

import android.os.Parcel;
import android.os.ParcelFormatException;
import android.os.Parcelable;

/* loaded from: classes.dex */
public class Entry implements Parcelable {
    public static final Parcelable.Creator<Entry> CREATOR = new Parcelable.Creator<Entry>() { // from class: com.github.mikephil.charting.data.Entry.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Entry createFromParcel(Parcel source) {
            return new Entry(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };
    private Object mData;
    private float mVal;
    private int mXIndex;

    public Entry(float val, int xIndex) {
        this.mVal = 0.0f;
        this.mXIndex = 0;
        this.mData = null;
        this.mVal = val;
        this.mXIndex = xIndex;
    }

    public Entry(float val, int xIndex, Object data) {
        this(val, xIndex);
        this.mData = data;
    }

    public int getXIndex() {
        return this.mXIndex;
    }

    public void setXIndex(int x) {
        this.mXIndex = x;
    }

    public float getVal() {
        return this.mVal;
    }

    public void setVal(float val) {
        this.mVal = val;
    }

    public Object getData() {
        return this.mData;
    }

    public void setData(Object data) {
        this.mData = data;
    }

    public Entry copy() {
        Entry e = new Entry(this.mVal, this.mXIndex, this.mData);
        return e;
    }

    public boolean equalTo(Entry e) {
        return e != null && e.mData == this.mData && e.mXIndex == this.mXIndex && Math.abs(e.mVal - this.mVal) <= 1.0E-5f;
    }

    public String toString() {
        return "Entry, xIndex: " + this.mXIndex + " val (sum): " + getVal();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.mVal);
        dest.writeInt(this.mXIndex);
        if (this.mData != null) {
            if (this.mData instanceof Parcelable) {
                dest.writeInt(1);
                dest.writeParcelable((Parcelable) this.mData, flags);
                return;
            }
            throw new ParcelFormatException("Cannot parcel an Entry with non-parcelable data");
        }
        dest.writeInt(0);
    }

    protected Entry(Parcel in) {
        this.mVal = 0.0f;
        this.mXIndex = 0;
        this.mData = null;
        this.mVal = in.readFloat();
        this.mXIndex = in.readInt();
        if (in.readInt() == 1) {
            this.mData = in.readParcelable(Object.class.getClassLoader());
        }
    }
}
