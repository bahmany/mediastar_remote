package com.google.android.gms.drive;

import android.content.IntentSender;
import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.drive.internal.OpenFileIntentSenderRequest;
import com.google.android.gms.drive.internal.q;

/* loaded from: classes.dex */
public class OpenFileActivityBuilder {
    public static final String EXTRA_RESPONSE_DRIVE_ID = "response_drive_id";
    private String No;
    private String[] Np;
    private DriveId Nq;

    public IntentSender build(GoogleApiClient apiClient) {
        n.a(apiClient.isConnected(), "Client must be connected");
        if (this.Np == null) {
            this.Np = new String[0];
        }
        try {
            return ((q) apiClient.a(Drive.CU)).hY().a(new OpenFileIntentSenderRequest(this.No, this.Np, this.Nq));
        } catch (RemoteException e) {
            throw new RuntimeException("Unable to connect Drive Play Service", e);
        }
    }

    public OpenFileActivityBuilder setActivityStartFolder(DriveId folder) {
        this.Nq = (DriveId) n.i(folder);
        return this;
    }

    public OpenFileActivityBuilder setActivityTitle(String title) {
        this.No = (String) n.i(title);
        return this;
    }

    public OpenFileActivityBuilder setMimeType(String[] mimeTypes) {
        n.b(mimeTypes != null, "mimeTypes may not be null");
        this.Np = mimeTypes;
        return this;
    }
}
