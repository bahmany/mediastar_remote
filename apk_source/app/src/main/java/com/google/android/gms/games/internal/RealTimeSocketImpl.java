package com.google.android.gms.games.internal;

import android.net.LocalSocket;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import com.google.android.gms.games.multiplayer.realtime.RealTimeSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
final class RealTimeSocketImpl implements RealTimeSocket {
    private ParcelFileDescriptor Kx;
    private final LocalSocket XT;
    private final String Xg;

    RealTimeSocketImpl(LocalSocket localSocket, String participantId) {
        this.XT = localSocket;
        this.Xg = participantId;
    }

    @Override // com.google.android.gms.games.multiplayer.realtime.RealTimeSocket
    public void close() throws IOException {
        this.XT.close();
    }

    @Override // com.google.android.gms.games.multiplayer.realtime.RealTimeSocket
    public InputStream getInputStream() throws IOException {
        return this.XT.getInputStream();
    }

    @Override // com.google.android.gms.games.multiplayer.realtime.RealTimeSocket
    public OutputStream getOutputStream() throws IOException {
        return this.XT.getOutputStream();
    }

    @Override // com.google.android.gms.games.multiplayer.realtime.RealTimeSocket
    public ParcelFileDescriptor getParcelFileDescriptor() throws IOException {
        if (this.Kx == null && !isClosed()) {
            Parcel parcelObtain = Parcel.obtain();
            parcelObtain.writeFileDescriptor(this.XT.getFileDescriptor());
            parcelObtain.setDataPosition(0);
            this.Kx = parcelObtain.readFileDescriptor();
        }
        return this.Kx;
    }

    @Override // com.google.android.gms.games.multiplayer.realtime.RealTimeSocket
    public boolean isClosed() {
        return (this.XT.isConnected() || this.XT.isBound()) ? false : true;
    }
}
