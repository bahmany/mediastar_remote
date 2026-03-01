package com.google.android.gms.internal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.MotionEvent;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServer;
import java.util.Map;

@ez
/* loaded from: classes.dex */
public final class gm {
    private final Context mContext;
    private int mState;
    private final float ri;
    private String ws;
    private float wt;
    private float wu;
    private float wv;

    public gm(Context context) {
        this.mState = 0;
        this.mContext = context;
        this.ri = context.getResources().getDisplayMetrics().density;
    }

    public gm(Context context, String str) {
        this(context);
        this.ws = str;
    }

    private void a(int i, float f, float f2) {
        if (i == 0) {
            this.mState = 0;
            this.wt = f;
            this.wu = f2;
            this.wv = f2;
            return;
        }
        if (this.mState != -1) {
            if (i != 2) {
                if (i == 1 && this.mState == 4) {
                    showDialog();
                    return;
                }
                return;
            }
            if (f2 > this.wu) {
                this.wu = f2;
            } else if (f2 < this.wv) {
                this.wv = f2;
            }
            if (this.wu - this.wv > 30.0f * this.ri) {
                this.mState = -1;
                return;
            }
            if (this.mState == 0 || this.mState == 2) {
                if (f - this.wt >= 50.0f * this.ri) {
                    this.wt = f;
                    this.mState++;
                }
            } else if ((this.mState == 1 || this.mState == 3) && f - this.wt <= (-50.0f) * this.ri) {
                this.wt = f;
                this.mState++;
            }
            if (this.mState == 1 || this.mState == 3) {
                if (f > this.wt) {
                    this.wt = f;
                }
            } else {
                if (this.mState != 2 || f >= this.wt) {
                    return;
                }
                this.wt = f;
            }
        }
    }

    private void showDialog() {
        final String strTrim;
        if (TextUtils.isEmpty(this.ws)) {
            strTrim = "No debug information";
        } else {
            Uri uriBuild = new Uri.Builder().encodedQuery(this.ws).build();
            StringBuilder sb = new StringBuilder();
            Map<String, String> mapC = gj.c(uriBuild);
            for (String str : mapC.keySet()) {
                sb.append(str).append(" = ").append(mapC.get(str)).append("\n\n");
            }
            strTrim = sb.toString().trim();
            if (TextUtils.isEmpty(strTrim)) {
                strTrim = "No debug information";
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setMessage(strTrim);
        builder.setTitle("Ad Information");
        builder.setPositiveButton("Share", new DialogInterface.OnClickListener() { // from class: com.google.android.gms.internal.gm.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                gm.this.mContext.startActivity(Intent.createChooser(new Intent("android.intent.action.SEND").setType(HttpServer.MIME_PLAINTEXT).putExtra("android.intent.extra.TEXT", strTrim), "Share via"));
            }
        });
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() { // from class: com.google.android.gms.internal.gm.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }

    public void Q(String str) {
        this.ws = str;
    }

    public void c(MotionEvent motionEvent) {
        int historySize = motionEvent.getHistorySize();
        for (int i = 0; i < historySize; i++) {
            a(motionEvent.getActionMasked(), motionEvent.getHistoricalX(0, i), motionEvent.getHistoricalY(0, i));
        }
        a(motionEvent.getActionMasked(), motionEvent.getX(), motionEvent.getY());
    }
}
