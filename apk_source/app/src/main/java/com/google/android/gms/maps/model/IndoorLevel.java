package com.google.android.gms.maps.model;

import android.os.RemoteException;

/* loaded from: classes.dex */
public final class IndoorLevel {
    private final com.google.android.gms.maps.model.internal.e ajM;

    public IndoorLevel(com.google.android.gms.maps.model.internal.e delegate) {
        this.ajM = (com.google.android.gms.maps.model.internal.e) com.google.android.gms.common.internal.n.i(delegate);
    }

    public void activate() {
        try {
            this.ajM.activate();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public boolean equals(Object other) {
        if (!(other instanceof IndoorLevel)) {
            return false;
        }
        try {
            return this.ajM.a(((IndoorLevel) other).ajM);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public String getName() {
        try {
            return this.ajM.getName();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public String getShortName() {
        try {
            return this.ajM.getShortName();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public int hashCode() {
        try {
            return this.ajM.hashCodeRemote();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }
}
