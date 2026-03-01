package com.google.android.gms.games.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import mktvsmart.screen.GlobalConstantValue;

/* loaded from: classes.dex */
public interface IRoomServiceCallbacks extends IInterface {

    public static abstract class Stub extends Binder implements IRoomServiceCallbacks {

        private static class Proxy implements IRoomServiceCallbacks {
            private IBinder lb;

            Proxy(IBinder remote) {
                this.lb = remote;
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void a(ParcelFileDescriptor parcelFileDescriptor, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    if (parcelFileDescriptor != null) {
                        parcelObtain.writeInt(1);
                        parcelFileDescriptor.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i);
                    this.lb.transact(1024, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void a(ConnectionInfo connectionInfo) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    if (connectionInfo != null) {
                        parcelObtain.writeInt(1);
                        connectionInfo.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_EVENT_TIMER_EDIT, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void a(String str, byte[] bArr, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    parcelObtain.writeString(str);
                    parcelObtain.writeByteArray(bArr);
                    parcelObtain.writeInt(i);
                    this.lb.transact(1002, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void a(String str, String[] strArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    parcelObtain.writeString(str);
                    parcelObtain.writeStringArray(strArr);
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_PLAYING_CHANNEL_PASSWORD_CHECK, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void aD(IBinder iBinder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    parcelObtain.writeStrongBinder(iBinder);
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_EVENT_TIMER_ADD, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.lb;
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void b(String str, String[] strArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    parcelObtain.writeString(str);
                    parcelObtain.writeStringArray(strArr);
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_SAT2IP_CHANNEL_PLAY, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void bM(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    parcelObtain.writeString(str);
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_CHANNEL_LOCK, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void bN(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    parcelObtain.writeString(str);
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_CHANNEL_FAV_MARK, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void bO(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    parcelObtain.writeString(str);
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_CHANNEL_MOVE, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void bP(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    parcelObtain.writeString(str);
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_CHANNEL_LIST_SORT, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void bQ(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    parcelObtain.writeString(str);
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_CHANNEL_LIST_TYPE_CHANGED, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void bR(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    parcelObtain.writeString(str);
                    this.lb.transact(1019, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void c(int i, int i2, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeString(str);
                    this.lb.transact(1001, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void c(String str, byte[] bArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    parcelObtain.writeString(str);
                    parcelObtain.writeByteArray(bArr);
                    this.lb.transact(1018, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void c(String str, String[] strArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    parcelObtain.writeString(str);
                    parcelObtain.writeStringArray(strArr);
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_CHANNEL_LIST_UPDATE, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void d(String str, String[] strArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    parcelObtain.writeString(str);
                    parcelObtain.writeStringArray(strArr);
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_FAV_CHANNEL_DELETE, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void dF(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    parcelObtain.writeInt(i);
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_EVENT_TIMER_DELETE, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void e(String str, String[] strArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    parcelObtain.writeString(str);
                    parcelObtain.writeStringArray(strArr);
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_SAT2IP_PLAY_STOP, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void f(String str, String[] strArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    parcelObtain.writeString(str);
                    parcelObtain.writeStringArray(strArr);
                    this.lb.transact(1013, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void g(String str, String[] strArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    parcelObtain.writeString(str);
                    parcelObtain.writeStringArray(strArr);
                    this.lb.transact(1017, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void i(String str, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.lb.transact(1026, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void kH() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    this.lb.transact(1016, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void kI() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    this.lb.transact(GlobalConstantValue.GMS_MSG_DO_REPEATE_EVENT_TIMER_SAVE, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void onP2PConnected(String participantId) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    parcelObtain.writeString(participantId);
                    this.lb.transact(1014, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void onP2PDisconnected(String participantId) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    parcelObtain.writeString(participantId);
                    this.lb.transact(1015, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.google.android.gms.games.internal.IRoomServiceCallbacks
            public void v(String str, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    this.lb.transact(1025, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, "com.google.android.gms.games.internal.IRoomServiceCallbacks");
        }

        public static IRoomServiceCallbacks aE(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IRoomServiceCallbacks)) ? new Proxy(iBinder) : (IRoomServiceCallbacks) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            switch (i) {
                case 1001:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    c(parcel.readInt(), parcel.readInt(), parcel.readString());
                    return true;
                case 1002:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    a(parcel.readString(), parcel.createByteArray(), parcel.readInt());
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_CHANNEL_LOCK /* 1003 */:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    bM(parcel.readString());
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_CHANNEL_FAV_MARK /* 1004 */:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    bN(parcel.readString());
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_CHANNEL_MOVE /* 1005 */:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    bO(parcel.readString());
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_CHANNEL_LIST_SORT /* 1006 */:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    bP(parcel.readString());
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_CHANNEL_LIST_TYPE_CHANGED /* 1007 */:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    bQ(parcel.readString());
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_PLAYING_CHANNEL_PASSWORD_CHECK /* 1008 */:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    a(parcel.readString(), parcel.createStringArray());
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_SAT2IP_CHANNEL_PLAY /* 1009 */:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    b(parcel.readString(), parcel.createStringArray());
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_CHANNEL_LIST_UPDATE /* 1010 */:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    c(parcel.readString(), parcel.createStringArray());
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_FAV_CHANNEL_DELETE /* 1011 */:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    d(parcel.readString(), parcel.createStringArray());
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_SAT2IP_PLAY_STOP /* 1012 */:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    e(parcel.readString(), parcel.createStringArray());
                    return true;
                case 1013:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    f(parcel.readString(), parcel.createStringArray());
                    return true;
                case 1014:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    onP2PConnected(parcel.readString());
                    return true;
                case 1015:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    onP2PDisconnected(parcel.readString());
                    return true;
                case 1016:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    kH();
                    return true;
                case 1017:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    g(parcel.readString(), parcel.createStringArray());
                    return true;
                case 1018:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    c(parcel.readString(), parcel.createByteArray());
                    return true;
                case 1019:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    bR(parcel.readString());
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_EVENT_TIMER_DELETE /* 1020 */:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    dF(parcel.readInt());
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_EVENT_TIMER_ADD /* 1021 */:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    aD(parcel.readStrongBinder());
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_EVENT_TIMER_EDIT /* 1022 */:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    a(parcel.readInt() != 0 ? ConnectionInfo.CREATOR.createFromParcel(parcel) : null);
                    return true;
                case GlobalConstantValue.GMS_MSG_DO_REPEATE_EVENT_TIMER_SAVE /* 1023 */:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    kI();
                    return true;
                case 1024:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    a(parcel.readInt() != 0 ? (ParcelFileDescriptor) ParcelFileDescriptor.CREATOR.createFromParcel(parcel) : null, parcel.readInt());
                    return true;
                case 1025:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    v(parcel.readString(), parcel.readInt());
                    return true;
                case 1026:
                    parcel.enforceInterface("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    i(parcel.readString(), parcel.readInt() != 0);
                    return true;
                case 1598968902:
                    parcel2.writeString("com.google.android.gms.games.internal.IRoomServiceCallbacks");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    void a(ParcelFileDescriptor parcelFileDescriptor, int i) throws RemoteException;

    void a(ConnectionInfo connectionInfo) throws RemoteException;

    void a(String str, byte[] bArr, int i) throws RemoteException;

    void a(String str, String[] strArr) throws RemoteException;

    void aD(IBinder iBinder) throws RemoteException;

    void b(String str, String[] strArr) throws RemoteException;

    void bM(String str) throws RemoteException;

    void bN(String str) throws RemoteException;

    void bO(String str) throws RemoteException;

    void bP(String str) throws RemoteException;

    void bQ(String str) throws RemoteException;

    void bR(String str) throws RemoteException;

    void c(int i, int i2, String str) throws RemoteException;

    void c(String str, byte[] bArr) throws RemoteException;

    void c(String str, String[] strArr) throws RemoteException;

    void d(String str, String[] strArr) throws RemoteException;

    void dF(int i) throws RemoteException;

    void e(String str, String[] strArr) throws RemoteException;

    void f(String str, String[] strArr) throws RemoteException;

    void g(String str, String[] strArr) throws RemoteException;

    void i(String str, boolean z) throws RemoteException;

    void kH() throws RemoteException;

    void kI() throws RemoteException;

    void onP2PConnected(String str) throws RemoteException;

    void onP2PDisconnected(String str) throws RemoteException;

    void v(String str, int i) throws RemoteException;
}
