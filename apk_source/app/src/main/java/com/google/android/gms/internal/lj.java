package com.google.android.gms.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.fitness.service.FitnessSensorServiceRequest;
import com.google.android.gms.internal.km;
import com.google.android.gms.internal.ks;

/* loaded from: classes.dex */
public interface lj extends IInterface {

    public static abstract class a extends Binder implements lj {
        public a() {
            attachInterface(this, "com.google.android.gms.fitness.internal.service.IFitnessSensorService");
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            switch (i) {
                case 1:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.service.IFitnessSensorService");
                    a(parcel.readInt() != 0 ? lf.CREATOR.createFromParcel(parcel) : null, km.a.aq(parcel.readStrongBinder()));
                    return true;
                case 2:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.service.IFitnessSensorService");
                    a(parcel.readInt() != 0 ? FitnessSensorServiceRequest.CREATOR.createFromParcel(parcel) : null, ks.a.aw(parcel.readStrongBinder()));
                    return true;
                case 3:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.service.IFitnessSensorService");
                    a(parcel.readInt() != 0 ? lh.CREATOR.createFromParcel(parcel) : null, ks.a.aw(parcel.readStrongBinder()));
                    return true;
                case 1598968902:
                    parcel2.writeString("com.google.android.gms.fitness.internal.service.IFitnessSensorService");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    void a(FitnessSensorServiceRequest fitnessSensorServiceRequest, ks ksVar) throws RemoteException;

    void a(lf lfVar, km kmVar) throws RemoteException;

    void a(lh lhVar, ks ksVar) throws RemoteException;
}
