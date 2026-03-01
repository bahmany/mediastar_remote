package com.google.android.gms.games.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.games.internal.IRoomServiceCallbacks;
import mktvsmart.screen.GlobalConstantValue;

/* loaded from: classes.dex */
public interface IRoomService extends IInterface {

    public static abstract class Stub extends Binder implements IRoomService {

        private static class Proxy implements IRoomService {
            private IBinder lb;

            @Override // com.google.android.gms.games.internal.IRoomService
            public void Q(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomService");
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_PLAYING_CHANNEL_PASSWORD_CHECK, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomService
            public void a(IBinder iBinder, IRoomServiceCallbacks iRoomServiceCallbacks) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomService");
                    parcelObtain.writeStrongBinder(iBinder);
                    parcelObtain.writeStrongBinder(iRoomServiceCallbacks != null ? iRoomServiceCallbacks.asBinder() : null);
                    this.lb.transact(1001, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomService
            public void a(DataHolder dataHolder, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomService");
                    if (dataHolder != null) {
                        parcelObtain.writeInt(1);
                        dataHolder.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_CHANNEL_LIST_SORT, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomService
            public void a(byte[] bArr, String str, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomService");
                    parcelObtain.writeByteArray(bArr);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_SAT2IP_CHANNEL_PLAY, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomService
            public void a(byte[] bArr, String[] strArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomService");
                    parcelObtain.writeByteArray(bArr);
                    parcelObtain.writeStringArray(strArr);
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_CHANNEL_LIST_UPDATE, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.lb;
            }

            @Override // com.google.android.gms.games.internal.IRoomService
            public void bK(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomService");
                    parcelObtain.writeString(str);
                    this.lb.transact(1013, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomService
            public void bL(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomService");
                    parcelObtain.writeString(str);
                    this.lb.transact(1014, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomService
            public void c(String str, String str2, String str3) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomService");
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    parcelObtain.writeString(str3);
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_CHANNEL_FAV_MARK, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomService
            public void kD() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomService");
                    this.lb.transact(1002, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomService
            public void kE() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomService");
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_CHANNEL_LOCK, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomService
            public void kF() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomService");
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_CHANNEL_MOVE, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomService
            public void kG() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomService");
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_CHANNEL_LIST_TYPE_CHANGED, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomService
            public void t(String str, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomService");
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_FAV_CHANNEL_DELETE, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomService
            public void u(String str, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomService");
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_SAT2IP_PLAY_STOP, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, "com.google.android.gms.games.internal.IRoomService");
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1001:
                    data.enforceInterface("com.google.android.gms.games.internal.IRoomService");
                    a(data.readStrongBinder(), IRoomServiceCallbacks.Stub.aE(data.readStrongBinder()));
                    return true;
                case 1002:
                    data.enforceInterface("com.google.android.gms.games.internal.IRoomService");
                    kD();
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_CHANNEL_LOCK /* 1003 */:
                    data.enforceInterface("com.google.android.gms.games.internal.IRoomService");
                    kE();
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_CHANNEL_FAV_MARK /* 1004 */:
                    data.enforceInterface("com.google.android.gms.games.internal.IRoomService");
                    c(data.readString(), data.readString(), data.readString());
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_CHANNEL_MOVE /* 1005 */:
                    data.enforceInterface("com.google.android.gms.games.internal.IRoomService");
                    kF();
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_CHANNEL_LIST_SORT /* 1006 */:
                    data.enforceInterface("com.google.android.gms.games.internal.IRoomService");
                    a(data.readInt() != 0 ? DataHolder.CREATOR.createFromParcel(data) : null, data.readInt() != 0);
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_CHANNEL_LIST_TYPE_CHANGED /* 1007 */:
                    data.enforceInterface("com.google.android.gms.games.internal.IRoomService");
                    kG();
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_PLAYING_CHANNEL_PASSWORD_CHECK /* 1008 */:
                    data.enforceInterface("com.google.android.gms.games.internal.IRoomService");
                    Q(data.readInt() != 0);
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_SAT2IP_CHANNEL_PLAY /* 1009 */:
                    data.enforceInterface("com.google.android.gms.games.internal.IRoomService");
                    a(data.createByteArray(), data.readString(), data.readInt());
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_CHANNEL_LIST_UPDATE /* 1010 */:
                    data.enforceInterface("com.google.android.gms.games.internal.IRoomService");
                    a(data.createByteArray(), data.createStringArray());
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_FAV_CHANNEL_DELETE /* 1011 */:
                    data.enforceInterface("com.google.android.gms.games.internal.IRoomService");
                    t(data.readString(), data.readInt());
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_SAT2IP_PLAY_STOP /* 1012 */:
                    data.enforceInterface("com.google.android.gms.games.internal.IRoomService");
                    u(data.readString(), data.readInt());
                    return true;
                case 1013:
                    data.enforceInterface("com.google.android.gms.games.internal.IRoomService");
                    bK(data.readString());
                    return true;
                case 1014:
                    data.enforceInterface("com.google.android.gms.games.internal.IRoomService");
                    bL(data.readString());
                    return true;
                case 1598968902:
                    reply.writeString("com.google.android.gms.games.internal.IRoomService");
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    void Q(boolean z) throws RemoteException;

    void a(IBinder iBinder, IRoomServiceCallbacks iRoomServiceCallbacks) throws RemoteException;

    void a(DataHolder dataHolder, boolean z) throws RemoteException;

    void a(byte[] bArr, String str, int i) throws RemoteException;

    void a(byte[] bArr, String[] strArr) throws RemoteException;

    void bK(String str) throws RemoteException;

    void bL(String str) throws RemoteException;

    void c(String str, String str2, String str3) throws RemoteException;

    void kD() throws RemoteException;

    void kE() throws RemoteException;

    void kF() throws RemoteException;

    void kG() throws RemoteException;

    void t(String str, int i) throws RemoteException;

    void u(String str, int i) throws RemoteException;
}
