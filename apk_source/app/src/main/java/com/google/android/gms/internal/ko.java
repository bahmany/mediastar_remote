package com.google.android.gms.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.DataInsertRequest;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.DataTypeCreateRequest;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.request.StartBleScanRequest;
import com.google.android.gms.fitness.request.UnclaimBleDeviceRequest;
import com.google.android.gms.internal.kl;
import com.google.android.gms.internal.km;
import com.google.android.gms.internal.kn;
import com.google.android.gms.internal.kp;
import com.google.android.gms.internal.kq;
import com.google.android.gms.internal.kr;
import com.google.android.gms.internal.ks;
import com.google.android.gms.internal.le;

/* loaded from: classes.dex */
public interface ko extends IInterface {

    public static abstract class a extends Binder implements ko {

        /* renamed from: com.google.android.gms.internal.ko$a$a */
        private static class C0069a implements ko {
            private IBinder lb;

            C0069a(IBinder iBinder) {
                this.lb = iBinder;
            }

            @Override // com.google.android.gms.internal.ko
            public void a(DataDeleteRequest dataDeleteRequest, ks ksVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    if (dataDeleteRequest != null) {
                        parcelObtain.writeInt(1);
                        dataDeleteRequest.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(ksVar != null ? ksVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(19, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.internal.ko
            public void a(DataInsertRequest dataInsertRequest, ks ksVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    if (dataInsertRequest != null) {
                        parcelObtain.writeInt(1);
                        dataInsertRequest.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(ksVar != null ? ksVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(7, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.internal.ko
            public void a(DataReadRequest dataReadRequest, kl klVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    if (dataReadRequest != null) {
                        parcelObtain.writeInt(1);
                        dataReadRequest.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(klVar != null ? klVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(8, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.internal.ko
            public void a(DataSourcesRequest dataSourcesRequest, km kmVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    if (dataSourcesRequest != null) {
                        parcelObtain.writeInt(1);
                        dataSourcesRequest.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(kmVar != null ? kmVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(1, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.internal.ko
            public void a(DataTypeCreateRequest dataTypeCreateRequest, kn knVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    if (dataTypeCreateRequest != null) {
                        parcelObtain.writeInt(1);
                        dataTypeCreateRequest.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(knVar != null ? knVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(13, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.internal.ko
            public void a(SessionInsertRequest sessionInsertRequest, ks ksVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    if (sessionInsertRequest != null) {
                        parcelObtain.writeInt(1);
                        sessionInsertRequest.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(ksVar != null ? ksVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(9, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.internal.ko
            public void a(SessionReadRequest sessionReadRequest, kq kqVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    if (sessionReadRequest != null) {
                        parcelObtain.writeInt(1);
                        sessionReadRequest.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(kqVar != null ? kqVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(10, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.internal.ko
            public void a(StartBleScanRequest startBleScanRequest, ks ksVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    if (startBleScanRequest != null) {
                        parcelObtain.writeInt(1);
                        startBleScanRequest.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(ksVar != null ? ksVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(15, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.internal.ko
            public void a(UnclaimBleDeviceRequest unclaimBleDeviceRequest, ks ksVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    if (unclaimBleDeviceRequest != null) {
                        parcelObtain.writeInt(1);
                        unclaimBleDeviceRequest.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(ksVar != null ? ksVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(18, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.internal.ko
            public void a(com.google.android.gms.fitness.request.ac acVar, ks ksVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    if (acVar != null) {
                        parcelObtain.writeInt(1);
                        acVar.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(ksVar != null ? ksVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(16, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.internal.ko
            public void a(com.google.android.gms.fitness.request.ae aeVar, ks ksVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    if (aeVar != null) {
                        parcelObtain.writeInt(1);
                        aeVar.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(ksVar != null ? ksVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(4, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.internal.ko
            public void a(com.google.android.gms.fitness.request.ah ahVar, ks ksVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    if (ahVar != null) {
                        parcelObtain.writeInt(1);
                        ahVar.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(ksVar != null ? ksVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(5, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.internal.ko
            public void a(com.google.android.gms.fitness.request.b bVar, ks ksVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    if (bVar != null) {
                        parcelObtain.writeInt(1);
                        bVar.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(ksVar != null ? ksVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(17, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.internal.ko
            public void a(com.google.android.gms.fitness.request.i iVar, kn knVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    if (iVar != null) {
                        parcelObtain.writeInt(1);
                        iVar.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(knVar != null ? knVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(14, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.internal.ko
            public void a(com.google.android.gms.fitness.request.l lVar, kp kpVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    if (lVar != null) {
                        parcelObtain.writeInt(1);
                        lVar.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(kpVar != null ? kpVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(6, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.internal.ko
            public void a(com.google.android.gms.fitness.request.n nVar, ks ksVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    if (nVar != null) {
                        parcelObtain.writeInt(1);
                        nVar.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(ksVar != null ? ksVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(2, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.internal.ko
            public void a(com.google.android.gms.fitness.request.p pVar, ks ksVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    if (pVar != null) {
                        parcelObtain.writeInt(1);
                        pVar.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(ksVar != null ? ksVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(3, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.internal.ko
            public void a(com.google.android.gms.fitness.request.t tVar, ks ksVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    if (tVar != null) {
                        parcelObtain.writeInt(1);
                        tVar.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(ksVar != null ? ksVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(20, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.internal.ko
            public void a(com.google.android.gms.fitness.request.v vVar, ks ksVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    if (vVar != null) {
                        parcelObtain.writeInt(1);
                        vVar.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(ksVar != null ? ksVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(11, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.internal.ko
            public void a(com.google.android.gms.fitness.request.x xVar, kr krVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    if (xVar != null) {
                        parcelObtain.writeInt(1);
                        xVar.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(krVar != null ? krVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(12, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.internal.ko
            public void a(com.google.android.gms.fitness.request.z zVar, ks ksVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    if (zVar != null) {
                        parcelObtain.writeInt(1);
                        zVar.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(ksVar != null ? ksVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(21, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.internal.ko
            public void a(ks ksVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    parcelObtain.writeStrongBinder(ksVar != null ? ksVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(22, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.internal.ko
            public void a(le leVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    parcelObtain.writeStrongBinder(leVar != null ? leVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(24, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.lb;
            }

            @Override // com.google.android.gms.internal.ko
            public void b(ks ksVar, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    parcelObtain.writeStrongBinder(ksVar != null ? ksVar.asBinder() : null);
                    parcelObtain.writeString(str);
                    this.lb.transact(23, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public static ko as(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ko)) ? new C0069a(iBinder) : (ko) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            switch (i) {
                case 1:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    a(parcel.readInt() != 0 ? DataSourcesRequest.CREATOR.createFromParcel(parcel) : null, km.a.aq(parcel.readStrongBinder()), parcel.readString());
                    return true;
                case 2:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    a(parcel.readInt() != 0 ? com.google.android.gms.fitness.request.n.CREATOR.createFromParcel(parcel) : null, ks.a.aw(parcel.readStrongBinder()), parcel.readString());
                    return true;
                case 3:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    a(parcel.readInt() != 0 ? com.google.android.gms.fitness.request.p.CREATOR.createFromParcel(parcel) : null, ks.a.aw(parcel.readStrongBinder()), parcel.readString());
                    return true;
                case 4:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    a(parcel.readInt() != 0 ? com.google.android.gms.fitness.request.ae.CREATOR.createFromParcel(parcel) : null, ks.a.aw(parcel.readStrongBinder()), parcel.readString());
                    return true;
                case 5:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    a(parcel.readInt() != 0 ? com.google.android.gms.fitness.request.ah.CREATOR.createFromParcel(parcel) : null, ks.a.aw(parcel.readStrongBinder()), parcel.readString());
                    return true;
                case 6:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    a(parcel.readInt() != 0 ? com.google.android.gms.fitness.request.l.CREATOR.createFromParcel(parcel) : null, kp.a.at(parcel.readStrongBinder()), parcel.readString());
                    return true;
                case 7:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    a(parcel.readInt() != 0 ? DataInsertRequest.CREATOR.createFromParcel(parcel) : null, ks.a.aw(parcel.readStrongBinder()), parcel.readString());
                    return true;
                case 8:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    a(parcel.readInt() != 0 ? DataReadRequest.CREATOR.createFromParcel(parcel) : null, kl.a.ap(parcel.readStrongBinder()), parcel.readString());
                    return true;
                case 9:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    a(parcel.readInt() != 0 ? SessionInsertRequest.CREATOR.createFromParcel(parcel) : null, ks.a.aw(parcel.readStrongBinder()), parcel.readString());
                    return true;
                case 10:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    a(parcel.readInt() != 0 ? SessionReadRequest.CREATOR.createFromParcel(parcel) : null, kq.a.au(parcel.readStrongBinder()), parcel.readString());
                    return true;
                case 11:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    a(parcel.readInt() != 0 ? com.google.android.gms.fitness.request.v.CREATOR.createFromParcel(parcel) : null, ks.a.aw(parcel.readStrongBinder()), parcel.readString());
                    return true;
                case 12:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    a(parcel.readInt() != 0 ? com.google.android.gms.fitness.request.x.CREATOR.createFromParcel(parcel) : null, kr.a.av(parcel.readStrongBinder()), parcel.readString());
                    return true;
                case 13:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    a(parcel.readInt() != 0 ? DataTypeCreateRequest.CREATOR.createFromParcel(parcel) : null, kn.a.ar(parcel.readStrongBinder()), parcel.readString());
                    return true;
                case 14:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    a(parcel.readInt() != 0 ? com.google.android.gms.fitness.request.i.CREATOR.createFromParcel(parcel) : null, kn.a.ar(parcel.readStrongBinder()), parcel.readString());
                    return true;
                case 15:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    a(parcel.readInt() != 0 ? StartBleScanRequest.CREATOR.createFromParcel(parcel) : null, ks.a.aw(parcel.readStrongBinder()), parcel.readString());
                    return true;
                case 16:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    a(parcel.readInt() != 0 ? com.google.android.gms.fitness.request.ac.CREATOR.createFromParcel(parcel) : null, ks.a.aw(parcel.readStrongBinder()), parcel.readString());
                    return true;
                case 17:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    a(parcel.readInt() != 0 ? com.google.android.gms.fitness.request.b.CREATOR.createFromParcel(parcel) : null, ks.a.aw(parcel.readStrongBinder()), parcel.readString());
                    return true;
                case 18:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    a(parcel.readInt() != 0 ? UnclaimBleDeviceRequest.CREATOR.createFromParcel(parcel) : null, ks.a.aw(parcel.readStrongBinder()), parcel.readString());
                    return true;
                case 19:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    a(parcel.readInt() != 0 ? DataDeleteRequest.CREATOR.createFromParcel(parcel) : null, ks.a.aw(parcel.readStrongBinder()), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case 20:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    a(parcel.readInt() != 0 ? com.google.android.gms.fitness.request.t.CREATOR.createFromParcel(parcel) : null, ks.a.aw(parcel.readStrongBinder()), parcel.readString());
                    return true;
                case 21:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    a(parcel.readInt() != 0 ? com.google.android.gms.fitness.request.z.CREATOR.createFromParcel(parcel) : null, ks.a.aw(parcel.readStrongBinder()), parcel.readString());
                    return true;
                case 22:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    a(ks.a.aw(parcel.readStrongBinder()), parcel.readString());
                    return true;
                case 23:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    b(ks.a.aw(parcel.readStrongBinder()), parcel.readString());
                    return true;
                case 24:
                    parcel.enforceInterface("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    a(le.a.ax(parcel.readStrongBinder()), parcel.readString());
                    return true;
                case 1598968902:
                    parcel2.writeString("com.google.android.gms.fitness.internal.IGoogleFitnessService");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    void a(DataDeleteRequest dataDeleteRequest, ks ksVar, String str) throws RemoteException;

    void a(DataInsertRequest dataInsertRequest, ks ksVar, String str) throws RemoteException;

    void a(DataReadRequest dataReadRequest, kl klVar, String str) throws RemoteException;

    void a(DataSourcesRequest dataSourcesRequest, km kmVar, String str) throws RemoteException;

    void a(DataTypeCreateRequest dataTypeCreateRequest, kn knVar, String str) throws RemoteException;

    void a(SessionInsertRequest sessionInsertRequest, ks ksVar, String str) throws RemoteException;

    void a(SessionReadRequest sessionReadRequest, kq kqVar, String str) throws RemoteException;

    void a(StartBleScanRequest startBleScanRequest, ks ksVar, String str) throws RemoteException;

    void a(UnclaimBleDeviceRequest unclaimBleDeviceRequest, ks ksVar, String str) throws RemoteException;

    void a(com.google.android.gms.fitness.request.ac acVar, ks ksVar, String str) throws RemoteException;

    void a(com.google.android.gms.fitness.request.ae aeVar, ks ksVar, String str) throws RemoteException;

    void a(com.google.android.gms.fitness.request.ah ahVar, ks ksVar, String str) throws RemoteException;

    void a(com.google.android.gms.fitness.request.b bVar, ks ksVar, String str) throws RemoteException;

    void a(com.google.android.gms.fitness.request.i iVar, kn knVar, String str) throws RemoteException;

    void a(com.google.android.gms.fitness.request.l lVar, kp kpVar, String str) throws RemoteException;

    void a(com.google.android.gms.fitness.request.n nVar, ks ksVar, String str) throws RemoteException;

    void a(com.google.android.gms.fitness.request.p pVar, ks ksVar, String str) throws RemoteException;

    void a(com.google.android.gms.fitness.request.t tVar, ks ksVar, String str) throws RemoteException;

    void a(com.google.android.gms.fitness.request.v vVar, ks ksVar, String str) throws RemoteException;

    void a(com.google.android.gms.fitness.request.x xVar, kr krVar, String str) throws RemoteException;

    void a(com.google.android.gms.fitness.request.z zVar, ks ksVar, String str) throws RemoteException;

    void a(ks ksVar, String str) throws RemoteException;

    void a(le leVar, String str) throws RemoteException;

    void b(ks ksVar, String str) throws RemoteException;
}
