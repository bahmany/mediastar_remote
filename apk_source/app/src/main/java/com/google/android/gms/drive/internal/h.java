package com.google.android.gms.drive.internal;

import android.content.IntentSender;
import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;

/* loaded from: classes.dex */
public class h {
    private String No;
    private DriveId Nq;
    protected MetadataChangeSet Oa;
    private Integer Ob;
    private final int Oc;

    public h(int i) {
        this.Oc = i;
    }

    public void a(DriveId driveId) {
        this.Nq = (DriveId) com.google.android.gms.common.internal.n.i(driveId);
    }

    public void a(MetadataChangeSet metadataChangeSet) {
        this.Oa = (MetadataChangeSet) com.google.android.gms.common.internal.n.i(metadataChangeSet);
    }

    public void bi(String str) {
        this.No = (String) com.google.android.gms.common.internal.n.i(str);
    }

    public void bk(int i) {
        this.Ob = Integer.valueOf(i);
    }

    public IntentSender build(GoogleApiClient apiClient) {
        com.google.android.gms.common.internal.n.b(this.Oa, "Must provide initial metadata to CreateFileActivityBuilder.");
        com.google.android.gms.common.internal.n.a(apiClient.isConnected(), "Client must be connected");
        q qVar = (q) apiClient.a(Drive.CU);
        this.Oa.hS().setContext(qVar.getContext());
        try {
            return qVar.hY().a(new CreateFileIntentSenderRequest(this.Oa.hS(), this.Ob == null ? -1 : this.Ob.intValue(), this.No, this.Nq, this.Oc));
        } catch (RemoteException e) {
            throw new RuntimeException("Unable to connect Drive Play Service", e);
        }
    }
}
