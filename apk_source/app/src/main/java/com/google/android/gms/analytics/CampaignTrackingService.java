package com.google.android.gms.analytics;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import java.io.FileOutputStream;
import java.io.IOException;

/* loaded from: classes.dex */
public class CampaignTrackingService extends IntentService {
    public CampaignTrackingService() {
        super("CampaignIntentService");
    }

    public CampaignTrackingService(String name) {
        super(name);
    }

    @Override // android.app.IntentService
    public void onHandleIntent(Intent intent) throws IOException {
        processIntent(this, intent);
    }

    public void processIntent(Context context, Intent intent) throws IOException {
        String stringExtra = intent.getStringExtra("referrer");
        try {
            FileOutputStream fileOutputStreamOpenFileOutput = context.openFileOutput("gaInstallData", 0);
            fileOutputStreamOpenFileOutput.write(stringExtra.getBytes());
            fileOutputStreamOpenFileOutput.close();
            z.V("Stored campaign information.");
        } catch (IOException e) {
            z.T("Error storing install campaign.");
        }
    }
}
