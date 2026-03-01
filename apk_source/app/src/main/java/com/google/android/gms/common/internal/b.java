package com.google.android.gms.common.internal;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

/* loaded from: classes.dex */
public class b implements DialogInterface.OnClickListener {
    private final Fragment Ll;
    private final int Lm;
    private final Intent mIntent;
    private final Activity nr;

    public b(Activity activity, Intent intent, int i) {
        this.nr = activity;
        this.Ll = null;
        this.mIntent = intent;
        this.Lm = i;
    }

    public b(Fragment fragment, Intent intent, int i) {
        this.nr = null;
        this.Ll = fragment;
        this.mIntent = intent;
        this.Lm = i;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        try {
            if (this.mIntent != null && this.Ll != null) {
                this.Ll.startActivityForResult(this.mIntent, this.Lm);
            } else if (this.mIntent != null) {
                this.nr.startActivityForResult(this.mIntent, this.Lm);
            }
            dialog.dismiss();
        } catch (ActivityNotFoundException e) {
            Log.e("SettingsRedirect", "Can't redirect to app settings for Google Play services");
        }
    }
}
