package com.google.android.gms.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.dex */
public interface br extends IInterface {

    public static abstract class a extends Binder implements br {
        public a() {
            attachInterface(this, "com.google.android.gms.ads.internal.formats.client.INativeAppInstallAd");
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface("com.google.android.gms.ads.internal.formats.client.INativeAppInstallAd");
                    i(data.readInt());
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface("com.google.android.gms.ads.internal.formats.client.INativeAppInstallAd");
                    as();
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface("com.google.android.gms.ads.internal.formats.client.INativeAppInstallAd");
                    String strBt = bt();
                    reply.writeNoException();
                    reply.writeString(strBt);
                    return true;
                case 4:
                    data.enforceInterface("com.google.android.gms.ads.internal.formats.client.INativeAppInstallAd");
                    com.google.android.gms.dynamic.d dVarBu = bu();
                    reply.writeNoException();
                    reply.writeStrongBinder(dVarBu != null ? dVarBu.asBinder() : null);
                    return true;
                case 5:
                    data.enforceInterface("com.google.android.gms.ads.internal.formats.client.INativeAppInstallAd");
                    String body = getBody();
                    reply.writeNoException();
                    reply.writeString(body);
                    return true;
                case 6:
                    data.enforceInterface("com.google.android.gms.ads.internal.formats.client.INativeAppInstallAd");
                    com.google.android.gms.dynamic.d dVarBv = bv();
                    reply.writeNoException();
                    reply.writeStrongBinder(dVarBv != null ? dVarBv.asBinder() : null);
                    return true;
                case 7:
                    data.enforceInterface("com.google.android.gms.ads.internal.formats.client.INativeAppInstallAd");
                    String strBw = bw();
                    reply.writeNoException();
                    reply.writeString(strBw);
                    return true;
                case 8:
                    data.enforceInterface("com.google.android.gms.ads.internal.formats.client.INativeAppInstallAd");
                    double dBx = bx();
                    reply.writeNoException();
                    reply.writeDouble(dBx);
                    return true;
                case 9:
                    data.enforceInterface("com.google.android.gms.ads.internal.formats.client.INativeAppInstallAd");
                    String strBy = by();
                    reply.writeNoException();
                    reply.writeString(strBy);
                    return true;
                case 10:
                    data.enforceInterface("com.google.android.gms.ads.internal.formats.client.INativeAppInstallAd");
                    String strBz = bz();
                    reply.writeNoException();
                    reply.writeString(strBz);
                    return true;
                case 1598968902:
                    reply.writeString("com.google.android.gms.ads.internal.formats.client.INativeAppInstallAd");
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    void as() throws RemoteException;

    String bt() throws RemoteException;

    com.google.android.gms.dynamic.d bu() throws RemoteException;

    com.google.android.gms.dynamic.d bv() throws RemoteException;

    String bw() throws RemoteException;

    double bx() throws RemoteException;

    String by() throws RemoteException;

    String bz() throws RemoteException;

    String getBody() throws RemoteException;

    void i(int i) throws RemoteException;
}
