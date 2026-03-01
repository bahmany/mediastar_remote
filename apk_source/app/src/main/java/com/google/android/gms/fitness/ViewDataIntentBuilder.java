package com.google.android.gms.fitness;

import android.content.Context;
import android.content.Intent;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.common.internal.safeparcel.c;
import com.google.android.gms.fitness.data.DataSource;

/* loaded from: classes.dex */
public class ViewDataIntentBuilder {
    private long KL;
    private DataSource Sh;
    private long Si;
    private String Sj;
    private final Context mContext;

    public ViewDataIntentBuilder(Context context) {
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
        n.a(this.Sh != null, "Data source must be set");
        n.a(this.KL > 0, "Start time must be set");
        n.a(this.Si > this.KL, "End time must be set and after start time");
        Intent intent = new Intent(FitnessIntents.ACTION_VIEW);
        intent.setType(FitnessIntents.getDataTypeMimeType(this.Sh.getDataType()));
        intent.putExtra(FitnessIntents.EXTRA_START_TIME, this.KL);
        intent.putExtra(FitnessIntents.EXTRA_END_TIME, this.Si);
        c.a(this.Sh, intent, FitnessIntents.EXTRA_DATA_SOURCE);
        return i(intent);
    }

    public ViewDataIntentBuilder setDataSource(DataSource dataSource) {
        this.Sh = dataSource;
        return this;
    }

    public ViewDataIntentBuilder setPreferredApplication(String packageName) {
        this.Sj = packageName;
        return this;
    }

    public ViewDataIntentBuilder setTimeInterval(long startTimeMillis, long endTimeMillis) {
        this.KL = startTimeMillis;
        this.Si = endTimeMillis;
        return this;
    }
}
