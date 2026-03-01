package com.google.android.gms.ads;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.internal.dr;
import com.google.android.gms.internal.ds;
import com.google.android.gms.internal.gs;

/* loaded from: classes.dex */
public final class AdActivity extends Activity {
    public static final String CLASS_NAME = "com.google.android.gms.ads.AdActivity";
    public static final String SIMPLE_CLASS_NAME = "AdActivity";
    private ds lc;

    private void U() {
        if (this.lc != null) {
            try {
                this.lc.U();
            } catch (RemoteException e) {
                gs.d("Could not forward setContentViewSet to ad overlay:", e);
            }
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.lc = dr.b(this);
        if (this.lc == null) {
            gs.W("Could not create ad overlay.");
            finish();
            return;
        }
        try {
            this.lc.onCreate(savedInstanceState);
        } catch (RemoteException e) {
            gs.d("Could not forward onCreate to ad overlay:", e);
            finish();
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        try {
            if (this.lc != null) {
                this.lc.onDestroy();
            }
        } catch (RemoteException e) {
            gs.d("Could not forward onDestroy to ad overlay:", e);
        }
        super.onDestroy();
    }

    @Override // android.app.Activity
    protected void onPause() {
        try {
            if (this.lc != null) {
                this.lc.onPause();
            }
        } catch (RemoteException e) {
            gs.d("Could not forward onPause to ad overlay:", e);
            finish();
        }
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onRestart() {
        super.onRestart();
        try {
            if (this.lc != null) {
                this.lc.onRestart();
            }
        } catch (RemoteException e) {
            gs.d("Could not forward onRestart to ad overlay:", e);
            finish();
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        try {
            if (this.lc != null) {
                this.lc.onResume();
            }
        } catch (RemoteException e) {
            gs.d("Could not forward onResume to ad overlay:", e);
            finish();
        }
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle outState) {
        try {
            if (this.lc != null) {
                this.lc.onSaveInstanceState(outState);
            }
        } catch (RemoteException e) {
            gs.d("Could not forward onSaveInstanceState to ad overlay:", e);
            finish();
        }
        super.onSaveInstanceState(outState);
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        try {
            if (this.lc != null) {
                this.lc.onStart();
            }
        } catch (RemoteException e) {
            gs.d("Could not forward onStart to ad overlay:", e);
            finish();
        }
    }

    @Override // android.app.Activity
    protected void onStop() {
        try {
            if (this.lc != null) {
                this.lc.onStop();
            }
        } catch (RemoteException e) {
            gs.d("Could not forward onStop to ad overlay:", e);
            finish();
        }
        super.onStop();
    }

    @Override // android.app.Activity
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        U();
    }

    @Override // android.app.Activity
    public void setContentView(View view) {
        super.setContentView(view);
        U();
    }

    @Override // android.app.Activity
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        U();
    }
}
