package com.google.android.gms.games.snapshot;

import android.os.Parcel;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.drive.Contents;
import java.io.IOException;

/* loaded from: classes.dex */
public final class SnapshotEntity implements SafeParcelable, Snapshot {
    public static final SnapshotEntityCreator CREATOR = new SnapshotEntityCreator();
    private final int BR;
    private final SnapshotMetadataEntity acW;
    private final SnapshotContents acX;

    SnapshotEntity(int versionCode, SnapshotMetadata metadata, SnapshotContents contents) {
        this.BR = versionCode;
        this.acW = new SnapshotMetadataEntity(metadata);
        this.acX = contents;
    }

    public SnapshotEntity(SnapshotMetadata metadata, SnapshotContents contents) {
        this(2, metadata, contents);
    }

    static boolean a(Snapshot snapshot, Object obj) {
        if (!(obj instanceof Snapshot)) {
            return false;
        }
        if (snapshot == obj) {
            return true;
        }
        Snapshot snapshot2 = (Snapshot) obj;
        return m.equal(snapshot2.getMetadata(), snapshot.getMetadata()) && m.equal(snapshot2.getSnapshotContents(), snapshot.getSnapshotContents());
    }

    static int b(Snapshot snapshot) {
        return m.hashCode(snapshot.getMetadata(), snapshot.getSnapshotContents());
    }

    static String c(Snapshot snapshot) {
        return m.h(snapshot).a("Metadata", snapshot.getMetadata()).a("HasContents", Boolean.valueOf(snapshot.getSnapshotContents() != null)).toString();
    }

    private boolean isClosed() {
        return this.acX.isClosed();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        return a(this, obj);
    }

    @Override // com.google.android.gms.common.data.Freezable
    public Snapshot freeze() {
        return this;
    }

    @Override // com.google.android.gms.games.snapshot.Snapshot
    public Contents getContents() {
        if (isClosed()) {
            return null;
        }
        return this.acX.getContents();
    }

    @Override // com.google.android.gms.games.snapshot.Snapshot
    public SnapshotMetadata getMetadata() {
        return this.acW;
    }

    @Override // com.google.android.gms.games.snapshot.Snapshot
    public SnapshotContents getSnapshotContents() {
        if (isClosed()) {
            return null;
        }
        return this.acX;
    }

    public int getVersionCode() {
        return this.BR;
    }

    public int hashCode() {
        return b(this);
    }

    @Override // com.google.android.gms.common.data.Freezable
    public boolean isDataValid() {
        return true;
    }

    @Override // com.google.android.gms.games.snapshot.Snapshot
    public boolean modifyBytes(int dstOffset, byte[] content, int srcOffset, int count) {
        return this.acX.modifyBytes(dstOffset, content, srcOffset, count);
    }

    @Override // com.google.android.gms.games.snapshot.Snapshot
    public byte[] readFully() {
        try {
            return this.acX.readFully();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        return c(this);
    }

    @Override // com.google.android.gms.games.snapshot.Snapshot
    public boolean writeBytes(byte[] content) {
        return this.acX.writeBytes(content);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        SnapshotEntityCreator.a(this, out, flags);
    }
}
