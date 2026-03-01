package com.google.android.gms.drive.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.Status;

/* loaded from: classes.dex */
public class bb extends c {
    private final BaseImplementation.b<Status> De;

    public bb(BaseImplementation.b<Status> bVar) {
        this.De = bVar;
    }

    @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
    public void o(Status status) throws RemoteException {
        this.De.b(status);
    }

    @Override // com.google.android.gms.drive.internal.c, com.google.android.gms.drive.internal.ac
    public void onSuccess() throws RemoteException {
        this.De.b(Status.Jo);
    }
}
