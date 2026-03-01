package com.hisilicon.dlna.dmc.gui.customview;

/* loaded from: classes.dex */
public class AdapterItem {
    protected Object m_data;

    public AdapterItem(Object data) {
        this.m_data = data;
    }

    public Object getData() {
        return this.m_data;
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof AdapterItem)) {
            return false;
        }
        AdapterItem other = (AdapterItem) o;
        if (other.m_data == null) {
            return this.m_data == null;
        }
        if (this.m_data != null) {
            return other.m_data.equals(this.m_data);
        }
        return false;
    }
}
