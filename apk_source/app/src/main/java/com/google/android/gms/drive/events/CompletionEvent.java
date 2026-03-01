package com.google.android.gms.drive.events;

import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.RemoteException;
import android.text.TextUtils;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.internal.ae;
import com.google.android.gms.drive.internal.v;
import com.google.android.gms.drive.metadata.internal.MetadataBundle;
import com.google.android.gms.internal.jy;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public final class CompletionEvent implements SafeParcelable, ResourceEvent {
    public static final Parcelable.Creator<CompletionEvent> CREATOR = new b();
    public static final int STATUS_CONFLICT = 2;
    public static final int STATUS_FAILURE = 1;
    public static final int STATUS_SUCCESS = 0;
    final int BR;
    final String Dd;
    final int Fa;
    final DriveId MO;
    final ParcelFileDescriptor NF;
    final ParcelFileDescriptor NG;
    final MetadataBundle NH;
    final ArrayList<String> NI;
    final IBinder NJ;
    private boolean NK = false;
    private boolean NL = false;
    private boolean NM = false;

    CompletionEvent(int versionCode, DriveId driveId, String accountName, ParcelFileDescriptor baseParcelFileDescriptor, ParcelFileDescriptor modifiedParcelFileDescriptor, MetadataBundle modifiedMetadataBundle, ArrayList<String> trackingTags, int status, IBinder releaseCallback) {
        this.BR = versionCode;
        this.MO = driveId;
        this.Dd = accountName;
        this.NF = baseParcelFileDescriptor;
        this.NG = modifiedParcelFileDescriptor;
        this.NH = modifiedMetadataBundle;
        this.NI = trackingTags;
        this.Fa = status;
        this.NJ = releaseCallback;
    }

    private void L(boolean z) throws IOException {
        hU();
        this.NM = true;
        jy.a(this.NF);
        jy.a(this.NG);
        if (this.NJ == null) {
            v.q("CompletionEvent", "No callback on " + (z ? "snooze" : "dismiss"));
            return;
        }
        try {
            ae.a.X(this.NJ).L(z);
        } catch (RemoteException e) {
            v.q("CompletionEvent", "RemoteException on " + (z ? "snooze" : "dismiss") + ": " + e);
        }
    }

    private void hU() {
        if (this.NM) {
            throw new IllegalStateException("Event has already been dismissed or snoozed.");
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void dismiss() throws IOException {
        L(false);
    }

    public String getAccountName() {
        hU();
        return this.Dd;
    }

    public InputStream getBaseContentsInputStream() {
        hU();
        if (this.NF == null) {
            return null;
        }
        if (this.NK) {
            throw new IllegalStateException("getBaseInputStream() can only be called once per CompletionEvent instance.");
        }
        this.NK = true;
        return new FileInputStream(this.NF.getFileDescriptor());
    }

    @Override // com.google.android.gms.drive.events.ResourceEvent
    public DriveId getDriveId() {
        hU();
        return this.MO;
    }

    public InputStream getModifiedContentsInputStream() {
        hU();
        if (this.NG == null) {
            return null;
        }
        if (this.NL) {
            throw new IllegalStateException("getModifiedInputStream() can only be called once per CompletionEvent instance.");
        }
        this.NL = true;
        return new FileInputStream(this.NG.getFileDescriptor());
    }

    public MetadataChangeSet getModifiedMetadataChangeSet() {
        hU();
        if (this.NH != null) {
            return new MetadataChangeSet(this.NH);
        }
        return null;
    }

    public int getStatus() {
        hU();
        return this.Fa;
    }

    public List<String> getTrackingTags() {
        hU();
        return new ArrayList(this.NI);
    }

    @Override // com.google.android.gms.drive.events.DriveEvent
    public int getType() {
        return 2;
    }

    public void snooze() throws IOException {
        L(true);
    }

    public String toString() {
        return String.format(Locale.US, "CompletionEvent [id=%s, status=%s, trackingTag=%s]", this.MO, Integer.valueOf(this.Fa), this.NI == null ? "<null>" : "'" + TextUtils.join("','", this.NI) + "'");
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        b.a(this, dest, flags);
    }
}
