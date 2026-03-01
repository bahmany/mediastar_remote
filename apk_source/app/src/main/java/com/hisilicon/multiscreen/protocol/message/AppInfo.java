package com.hisilicon.multiscreen.protocol.message;

import java.io.Serializable;

/* loaded from: classes.dex */
public class AppInfo implements Serializable {
    private static final long serialVersionUID = 2225651111100652973L;
    private String mAppName = "";
    private String mPackageName = "";
    private int mIndex = 0;
    private byte[] mIcon = null;

    public void setPackageName(String name) {
        this.mPackageName = name;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public void setAppName(String name) {
        this.mAppName = name;
    }

    public String getAppName() {
        return this.mAppName;
    }

    public void setPackageIcon(byte[] icon) {
        this.mIcon = new byte[icon.length];
        this.mIcon = icon;
    }

    public byte[] getPackageIcon() {
        return this.mIcon;
    }

    public void setPackageIndex(int index) {
        this.mIndex = index;
    }

    public int getPackageIndex() {
        return this.mIndex;
    }
}
