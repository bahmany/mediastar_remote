package com.google.android.gms.auth.api;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.auth.api.IGoogleAuthApiCallbacks;

/* loaded from: classes.dex */
public interface IGoogleAuthService extends IInterface {

    public static abstract class Stub extends Binder implements IGoogleAuthService {

        private static class a implements IGoogleAuthService {
            private IBinder lb;

            a(IBinder iBinder) {
                this.lb = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.lb;
            }

            @Override // com.google.android.gms.auth.api.IGoogleAuthService
            public void sendConnection(IGoogleAuthApiCallbacks callbacks, GoogleAuthApiRequest request) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(GoogleAuthApiClientImpl.SERVICE_DESCRIPTOR);
                    parcelObtain.writeStrongBinder(callbacks != null ? callbacks.asBinder() : null);
                    if (request != null) {
                        parcelObtain.writeInt(1);
                        request.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    this.lb.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, GoogleAuthApiClientImpl.SERVICE_DESCRIPTOR);
        }

        public static IGoogleAuthService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = obj.queryLocalInterface(GoogleAuthApiClientImpl.SERVICE_DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IGoogleAuthService)) ? new a(obj) : (IGoogleAuthService) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface(GoogleAuthApiClientImpl.SERVICE_DESCRIPTOR);
                    sendConnection(IGoogleAuthApiCallbacks.Stub.asInterface(data.readStrongBinder()), data.readInt() != 0 ? GoogleAuthApiRequest.CREATOR.createFromParcel(data) : null);
                    reply.writeNoException();
                    return true;
                case 1598968902:
                    reply.writeString(GoogleAuthApiClientImpl.SERVICE_DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    void sendConnection(IGoogleAuthApiCallbacks iGoogleAuthApiCallbacks, GoogleAuthApiRequest googleAuthApiRequest) throws RemoteException;
}
