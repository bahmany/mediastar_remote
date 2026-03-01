package mktvsmart.screen.dataconvert.model;

/* loaded from: classes.dex */
public class DataConvertDebugModel {
    private int isEnableDebug;
    private int mRequestDataFrom;
    private int mRequestDataTo;
    private int totalDataLength = 0;

    public int getTotalDataLength() {
        return this.totalDataLength;
    }

    public void setTotalDataLength(int length) {
        this.totalDataLength = length;
    }

    public int getDebugValue() {
        return this.isEnableDebug;
    }

    public void setDebugValue(int value) {
        this.isEnableDebug = value;
    }

    public int getRequestDataFrom() {
        return this.mRequestDataFrom;
    }

    public void setRequestDataFrom(int requestDataFrom) {
        this.mRequestDataFrom = requestDataFrom;
    }

    public int getRequestDataTo() {
        return this.mRequestDataTo;
    }

    public void setRequestDataTo(int requestDataTo) {
        this.mRequestDataTo = requestDataTo;
    }
}
