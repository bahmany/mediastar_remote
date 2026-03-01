package com.google.android.gms.fitness;

import android.content.Context;
import android.content.Intent;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.common.internal.safeparcel.c;
import com.google.android.gms.fitness.data.Session;

/* loaded from: classes.dex */
public class ViewSessionIntentBuilder {
    private String Sj;
    private Session Sk;
    private boolean Sl = false;
    private final Context mContext;

    public ViewSessionIntentBuilder(Context context) {
        this.mContext = context;
    }

    private Intent i(Intent intent) {
        if (this.Sj == null) {
            return intent;
        }
        Intent intent2 = new Intent(intent).setPackage(this.Sj);
        return this.mContext.getPackageManager().resolveActivity(intent2, 0) != null ? intent2 : intent;
    }

    public Intent build() {
        n.a(this.Sk != null, "Session must be set");
        Intent intent = new Intent(FitnessIntents.ACTION_VIEW);
        intent.setType(FitnessIntents.getSessionMimeType(this.Sk.getActivity()));
        c.a(this.Sk, intent, FitnessIntents.EXTRA_SESSION);
        if (!this.Sl) {
            this.Sj = this.Sk.getAppPackageName();
        }
        return i(intent);
    }

    public ViewSessionIntentBuilder setPreferredApplication(String packageName) {
        this.Sj = packageName;
        this.Sl = true;
        return this;
    }

    public ViewSessionIntentBuilder setSession(Session session) {
        this.Sk = session;
        return this;
    }
}
