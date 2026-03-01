package com.google.android.gms.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.c;
import com.google.android.gms.dynamic.d;
import com.google.android.gms.internal.oq;
import com.google.android.gms.internal.or;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;

/* loaded from: classes.dex */
public interface ot extends IInterface {

    public static abstract class a extends Binder implements ot {

        /* renamed from: com.google.android.gms.internal.ot$a$a, reason: collision with other inner class name */
        private static class C0089a implements ot {
            private IBinder lb;

            C0089a(IBinder iBinder) {
                this.lb = iBinder;
            }

            @Override // com.google.android.gms.internal.ot
            public oq a(com.google.android.gms.dynamic.d dVar, com.google.android.gms.dynamic.c cVar, WalletFragmentOptions walletFragmentOptions, or orVar) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.google.android.gms.wallet.internal.IWalletDynamiteCreator");
                    parcelObtain.writeStrongBinder(dVar != null ? dVar.asBinder() : null);
                    parcelObtain.writeStrongBinder(cVar != null ? cVar.asBinder() : null);
                    if (walletFragmentOptions != null) {
                        parcelObtain.writeInt(1);
                        walletFragmentOptions.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(orVar != null ? orVar.asBinder() : null);
                    this.lb.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return oq.a.bJ(parcelObtain2.readStrongBinder());
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.lb;
            }
        }

        public static ot bM(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.wallet.internal.IWalletDynamiteCreator");
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ot)) ? new C0089a(iBinder) : (ot) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface("com.google.android.gms.wallet.internal.IWalletDynamiteCreator");
                    oq oqVarA = a(d.a.am(data.readStrongBinder()), c.a.al(data.readStrongBinder()), data.readInt() != 0 ? WalletFragmentOptions.CREATOR.createFromParcel(data) : null, or.a.bK(data.readStrongBinder()));
                    reply.writeNoException();
                    reply.writeStrongBinder(oqVarA != null ? oqVarA.asBinder() : null);
                    return true;
                case 1598968902:
                    reply.writeString("com.google.android.gms.wallet.internal.IWalletDynamiteCreator");
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    oq a(com.google.android.gms.dynamic.d dVar, com.google.android.gms.dynamic.c cVar, WalletFragmentOptions walletFragmentOptions, or orVar) throws RemoteException;
}
