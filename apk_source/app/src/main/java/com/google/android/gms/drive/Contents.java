package com.google.android.gms.drive;

import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@Deprecated
/* loaded from: classes.dex */
public class Contents implements SafeParcelable {
    public static final Parcelable.Creator<Contents> CREATOR = new a();
    final int BR;
    final ParcelFileDescriptor Kx;
    final int MN;
    final DriveId MO;
    final boolean MP;
    final int uQ;
    private boolean mClosed = false;
    private boolean MQ = false;
    private boolean MR = false;

    Contents(int versionCode, ParcelFileDescriptor parcelFileDescriptor, int requestId, int mode, DriveId driveId, boolean validForConflictDetection) {
        this.BR = versionCode;
        this.Kx = parcelFileDescriptor;
        this.uQ = requestId;
        this.MN = mode;
        this.MO = driveId;
        this.MP = validForConflictDetection;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public DriveId getDriveId() {
        return this.MO;
    }

    public InputStream getInputStream() {
        if (this.mClosed) {
            throw new IllegalStateException("Contents have been closed, cannot access the input stream.");
        }
        if (this.MN != 268435456) {
            throw new IllegalStateException("getInputStream() can only be used with contents opened with MODE_READ_ONLY.");
        }
        if (this.MQ) {
            throw new IllegalStateException("getInputStream() can only be called once per Contents instance.");
        }
        this.MQ = true;
        return new FileInputStream(this.Kx.getFileDescriptor());
    }

    public int getMode() {
        return this.MN;
    }

    public OutputStream getOutputStream() {
        if (this.mClosed) {
            throw new IllegalStateException("Contents have been closed, cannot access the output stream.");
        }
        if (this.MN != 536870912) {
            throw new IllegalStateException("getOutputStream() can only be used with contents opened with MODE_WRITE_ONLY.");
        }
        if (this.MR) {
            throw new IllegalStateException("getOutputStream() can only be called once per Contents instance.");
        }
        this.MR = true;
        return new FileOutputStream(this.Kx.getFileDescriptor());
    }

    public ParcelFileDescriptor getParcelFileDescriptor() {
        if (this.mClosed) {
            throw new IllegalStateException("Contents have been closed, cannot access the output stream.");
        }
        return this.Kx;
    }

    public int getRequestId() {
        return this.uQ;
    }

    public void hJ() {
        this.mClosed = true;
    }

    public boolean hK() {
        return this.mClosed;
    }

    public boolean hL() {
        return this.MP;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        a.a(this, dest, flags);
    }
}
