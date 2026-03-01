package com.google.android.gms.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.dex */
public interface bs extends IInterface {

    public static abstract class a extends Binder implements bs {
        public a() {
            attachInterface(this, "com.google.android.gms.ads.internal.formats.client.INativeContentAd");
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface("com.google.android.gms.ads.internal.formats.client.INativeContentAd");
                    i(data.readInt());
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface("com.google.android.gms.ads.internal.formats.client.INativeContentAd");
                    as();
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface("com.google.android.gms.ads.internal.formats.client.INativeContentAd");
                    String strBt = bt();
                    reply.writeNoException();
                    reply.writeString(strBt);
                    return true;
                case 4:
                    data.enforceInterface("com.google.android.gms.ads.internal.formats.client.INativeContentAd");
                    com.google.android.gms.dynamic.d dVarBu = bu();
                    reply.writeNoException();
                    reply.writeStrongBinder(dVarBu != null ? dVarBu.asBinder() : null);
                    return true;
                case 5:
                    data.enforceInterface("com.google.android.gms.ads.internal.formats.client.INativeContentAd");
                    String body = getBody();
                    reply.writeNoException();
                    reply.writeString(body);
                    return true;
                case 6:
                    data.enforceInterface("com.google.android.gms.ads.internal.formats.client.INativeContentAd");
                    com.google.android.gms.dynamic.d dVarBA = bA();
                    reply.writeNoException();
                    reply.writeStrongBinder(dVarBA != null ? dVarBA.asBinder() : null);
                    return true;
                case 7:
                    data.enforceInterface("com.google.android.gms.ads.internal.formats.client.INativeContentAd");
                    String strBw = bw();
                    reply.writeNoException();
                    reply.writeString(strBw);
                    return true;
                case 8:
                    data.enforceInterface("com.google.android.gms.ads.internal.formats.client.INativeContentAd");
                    String strBB = bB();
                    reply.writeNoException();
                    reply.writeString(strBB);
                    return true;
                case 1598968902:
                    reply.writeString("com.google.android.gms.ads.internal.formats.client.INativeContentAd");
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    void as() throws RemoteException;

    com.google.android.gms.dynamic.d bA() throws RemoteException;

    String bB() throws RemoteException;

    String bt() throws RemoteException;

    com.google.android.gms.dynamic.d bu() throws RemoteException;

    String bw() throws RemoteException;

    String getBody() throws RemoteException;

    void i(int i) throws RemoteException;
}
