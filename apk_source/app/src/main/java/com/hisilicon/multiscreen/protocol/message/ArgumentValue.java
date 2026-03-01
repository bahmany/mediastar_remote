package com.hisilicon.multiscreen.protocol.message;

import com.hisilicon.multiscreen.protocol.utils.LogTool;

/* loaded from: classes.dex */
public class ArgumentValue {
    protected String mKey;
    private String mType;
    protected Object mValue;

    public ArgumentValue(String key, Object value) {
        this.mKey = null;
        this.mValue = null;
        this.mType = null;
        this.mKey = key;
        this.mValue = value;
    }

    public ArgumentValue() {
        this.mKey = null;
        this.mValue = null;
        this.mType = null;
    }

    public void setKey(String key) {
        this.mKey = key;
    }

    public String getKey() {
        return this.mKey;
    }

    public void setValue(Object obj) {
        this.mValue = obj;
    }

    public Object getVaule() {
        return this.mValue;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getType() {
        if (this.mValue != null) {
            this.mType = this.mValue.getClass().getSimpleName();
        }
        return this.mType;
    }

    public void debugInfor() {
        if (this.mValue != null) {
            LogTool.v("key=" + this.mKey);
            LogTool.v("type=" + getType());
            LogTool.v("value=" + this.mValue.toString());
            return;
        }
        LogTool.e("ArgumentValue is null");
    }
}
