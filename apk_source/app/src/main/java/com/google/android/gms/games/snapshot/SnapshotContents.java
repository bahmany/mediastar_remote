package com.google.android.gms.games.snapshot;

import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.drive.Contents;
import com.google.android.gms.games.internal.GamesLog;
import com.google.android.gms.internal.jy;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/* loaded from: classes.dex */
public final class SnapshotContents implements SafeParcelable {
    private final int BR;
    private Contents Op;
    private static final Object acV = new Object();
    public static final SnapshotContentsCreator CREATOR = new SnapshotContentsCreator();

    SnapshotContents(int versionCode, Contents contents) {
        this.BR = versionCode;
        this.Op = contents;
    }

    public SnapshotContents(Contents contents) {
        this(1, contents);
    }

    private boolean a(int i, byte[] bArr, int i2, int i3, boolean z) {
        n.a(!isClosed(), "Must provide a previously opened SnapshotContents");
        synchronized (acV) {
            FileOutputStream fileOutputStream = new FileOutputStream(this.Op.getParcelFileDescriptor().getFileDescriptor());
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            try {
                FileChannel channel = fileOutputStream.getChannel();
                channel.position(i);
                bufferedOutputStream.write(bArr, i2, i3);
                if (z) {
                    channel.truncate(bArr.length);
                }
                bufferedOutputStream.flush();
            } catch (IOException e) {
                GamesLog.a("SnapshotContents", "Failed to write snapshot data", e);
                return false;
            }
        }
        return true;
    }

    public void close() {
        this.Op.hJ();
        this.Op = null;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Contents getContents() {
        return this.Op;
    }

    public ParcelFileDescriptor getParcelFileDescriptor() {
        n.a(!isClosed(), "Cannot mutate closed contents!");
        return this.Op.getParcelFileDescriptor();
    }

    public int getVersionCode() {
        return this.BR;
    }

    public boolean isClosed() {
        return this.Op == null;
    }

    public boolean modifyBytes(int dstOffset, byte[] content, int srcOffset, int count) {
        return a(dstOffset, content, srcOffset, content.length, false);
    }

    public byte[] readFully() throws IOException {
        byte[] bArrA;
        n.a(isClosed() ? false : true, "Must provide a previously opened Snapshot");
        synchronized (acV) {
            FileInputStream fileInputStream = new FileInputStream(this.Op.getParcelFileDescriptor().getFileDescriptor());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            try {
                fileInputStream.getChannel().position(0L);
                bArrA = jy.a(bufferedInputStream, false);
                fileInputStream.getChannel().position(0L);
            } catch (IOException e) {
                GamesLog.b("SnapshotContents", "Failed to read snapshot data", e);
                throw e;
            }
        }
        return bArrA;
    }

    public boolean writeBytes(byte[] content) {
        return a(0, content, 0, content.length, true);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        SnapshotContentsCreator.a(this, out, flags);
    }
}
