package com.iflytek.speech.aidl;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.iflytek.speech.SynthesizeToUrlListener;
import com.iflytek.speech.SynthesizerListener;

/* loaded from: classes.dex */
public interface ISpeechSynthesizer extends IInterface {

    public static abstract class Stub extends Binder implements ISpeechSynthesizer {
        private static final String DESCRIPTOR = "com.iflytek.speech.aidl.ISpeechSynthesizer";
        static final int TRANSACTION_getLocalSpeakerList = 7;
        static final int TRANSACTION_isSpeaking = 6;
        static final int TRANSACTION_pauseSpeaking = 3;
        static final int TRANSACTION_resumeSpeaking = 4;
        static final int TRANSACTION_startSpeaking = 2;
        static final int TRANSACTION_stopSpeaking = 5;
        static final int TRANSACTION_synthesizeToUrl = 1;

        private static class Proxy implements ISpeechSynthesizer {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.iflytek.speech.aidl.ISpeechSynthesizer
            public String getLocalSpeakerList() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.iflytek.speech.aidl.ISpeechSynthesizer
            public boolean isSpeaking() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.iflytek.speech.aidl.ISpeechSynthesizer
            public int pauseSpeaking(SynthesizerListener synthesizerListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(synthesizerListener != null ? synthesizerListener.asBinder() : null);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.iflytek.speech.aidl.ISpeechSynthesizer
            public int resumeSpeaking(SynthesizerListener synthesizerListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(synthesizerListener != null ? synthesizerListener.asBinder() : null);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.iflytek.speech.aidl.ISpeechSynthesizer
            public int startSpeaking(Intent intent, SynthesizerListener synthesizerListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        parcelObtain.writeInt(1);
                        intent.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(synthesizerListener != null ? synthesizerListener.asBinder() : null);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.iflytek.speech.aidl.ISpeechSynthesizer
            public int stopSpeaking(SynthesizerListener synthesizerListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(synthesizerListener != null ? synthesizerListener.asBinder() : null);
                    this.mRemote.transact(5, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.iflytek.speech.aidl.ISpeechSynthesizer
            public int synthesizeToUrl(Intent intent, SynthesizeToUrlListener synthesizeToUrlListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        parcelObtain.writeInt(1);
                        intent.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(synthesizeToUrlListener != null ? synthesizeToUrlListener.asBinder() : null);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISpeechSynthesizer asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ISpeechSynthesizer)) ? new Proxy(iBinder) : (ISpeechSynthesizer) iInterfaceQueryLocalInterface;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            switch (i) {
                case 1:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iSynthesizeToUrl = synthesizeToUrl(parcel.readInt() != 0 ? (Intent) Intent.CREATOR.createFromParcel(parcel) : null, SynthesizeToUrlListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(iSynthesizeToUrl);
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iStartSpeaking = startSpeaking(parcel.readInt() != 0 ? (Intent) Intent.CREATOR.createFromParcel(parcel) : null, SynthesizerListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(iStartSpeaking);
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iPauseSpeaking = pauseSpeaking(SynthesizerListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(iPauseSpeaking);
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iResumeSpeaking = resumeSpeaking(SynthesizerListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(iResumeSpeaking);
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iStopSpeaking = stopSpeaking(SynthesizerListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(iStopSpeaking);
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsSpeaking = isSpeaking();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsSpeaking ? 1 : 0);
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    String localSpeakerList = getLocalSpeakerList();
                    parcel2.writeNoException();
                    parcel2.writeString(localSpeakerList);
                    return true;
                case 1598968902:
                    parcel2.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    String getLocalSpeakerList() throws RemoteException;

    boolean isSpeaking() throws RemoteException;

    int pauseSpeaking(SynthesizerListener synthesizerListener) throws RemoteException;

    int resumeSpeaking(SynthesizerListener synthesizerListener) throws RemoteException;

    int startSpeaking(Intent intent, SynthesizerListener synthesizerListener) throws RemoteException;

    int stopSpeaking(SynthesizerListener synthesizerListener) throws RemoteException;

    int synthesizeToUrl(Intent intent, SynthesizeToUrlListener synthesizeToUrlListener) throws RemoteException;
}
