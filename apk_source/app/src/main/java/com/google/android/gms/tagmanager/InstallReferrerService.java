package com.google.android.gms.tagmanager;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import com.google.android.gms.analytics.CampaignTrackingService;
import java.io.IOException;

/* loaded from: classes.dex */
public final class InstallReferrerService extends IntentService {
    CampaignTrackingService apl;
    Context apm;

    public InstallReferrerService() {
        super("InstallReferrerService");
    }

    public InstallReferrerService(String name) {
        super(name);
    }

    private void a(Context context, Intent intent) throws IOException {
        if (this.apl == null) {
            this.apl = new CampaignTrackingService();
        }
        this.apl.processIntent(context, intent);
    }

    @Override // android.app.IntentService
    protected void onHandleIntent(Intent intent) throws IOException {
        String stringExtra = intent.getStringExtra("referrer");
        Context applicationContext = this.apm != null ? this.apm : getApplicationContext();
        ay.d(applicationContext, stringExtra);
        a(applicationContext, intent);
    }
}
