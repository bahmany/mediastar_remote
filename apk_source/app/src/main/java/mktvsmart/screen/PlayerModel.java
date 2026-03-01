package mktvsmart.screen;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/* loaded from: classes.dex */
public class PlayerModel {
    private String mActivityName;
    private Drawable mAppIcon;
    private String mAppLabel;
    private Intent mIntent;
    private String mPkgName;

    public String getmActivityName() {
        return this.mActivityName;
    }

    public void setmActivityName(String mActivityName) {
        this.mActivityName = mActivityName;
    }

    public String getmAppLabel() {
        return this.mAppLabel;
    }

    public void setmAppLabel(String mAppLabel) {
        this.mAppLabel = mAppLabel;
    }

    public Drawable getmAppIcon() {
        return this.mAppIcon;
    }

    public void setmAppIcon(Drawable mAppIcon) {
        this.mAppIcon = mAppIcon;
    }

    public Intent getmIntent() {
        return this.mIntent;
    }

    public void setmIntent(Intent mIntent) {
        this.mIntent = mIntent;
    }

    public String getmPkgName() {
        return this.mPkgName;
    }

    public void setmPkgName(String mPkgName) {
        this.mPkgName = mPkgName;
    }
}
