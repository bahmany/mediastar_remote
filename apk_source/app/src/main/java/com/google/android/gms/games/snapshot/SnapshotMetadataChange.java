package com.google.android.gms.games.snapshot;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import com.google.android.gms.common.data.a;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

/* loaded from: classes.dex */
public final class SnapshotMetadataChange implements SafeParcelable {
    public static final SnapshotMetadataChangeCreator CREATOR = new SnapshotMetadataChangeCreator();
    public static final SnapshotMetadataChange EMPTY_CHANGE = new SnapshotMetadataChange();
    private final int BR;
    private final String Tg;
    private final Long acY;
    private final Uri acZ;
    private a ada;

    public static final class Builder {
        private String Tg;
        private Uri acZ;
        private Long adb;
        private a adc;

        public SnapshotMetadataChange build() {
            return new SnapshotMetadataChange(this.Tg, this.adb, this.adc, this.acZ);
        }

        public Builder fromMetadata(SnapshotMetadata metadata) {
            this.Tg = metadata.getDescription();
            this.adb = Long.valueOf(metadata.getPlayedTime());
            if (this.adb.longValue() == -1) {
                this.adb = null;
            }
            this.acZ = metadata.getCoverImageUri();
            if (this.acZ != null) {
                this.adc = null;
            }
            return this;
        }

        public Builder setCoverImage(Bitmap coverImage) {
            this.adc = new a(coverImage);
            this.acZ = null;
            return this;
        }

        public Builder setDescription(String description) {
            this.Tg = description;
            return this;
        }

        public Builder setPlayedTimeMillis(long playedTimeMillis) {
            this.adb = Long.valueOf(playedTimeMillis);
            return this;
        }
    }

    SnapshotMetadataChange() {
        this(4, null, null, null, null);
    }

    SnapshotMetadataChange(int versionCode, String description, Long playedTimeMillis, a coverImage, Uri coverImageUri) {
        this.BR = versionCode;
        this.Tg = description;
        this.acY = playedTimeMillis;
        this.ada = coverImage;
        this.acZ = coverImageUri;
        if (this.ada != null) {
            n.a(this.acZ == null, "Cannot set both a URI and an image");
        } else if (this.acZ != null) {
            n.a(this.ada == null, "Cannot set both a URI and an image");
        }
    }

    SnapshotMetadataChange(String description, Long playedTimeMillis, a coverImage, Uri coverImageUri) {
        this(4, description, playedTimeMillis, coverImage, coverImageUri);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Bitmap getCoverImage() {
        if (this.ada == null) {
            return null;
        }
        return this.ada.gx();
    }

    public Uri getCoverImageUri() {
        return this.acZ;
    }

    public String getDescription() {
        return this.Tg;
    }

    public Long getPlayedTimeMillis() {
        return this.acY;
    }

    public int getVersionCode() {
        return this.BR;
    }

    public a lK() {
        return this.ada;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        SnapshotMetadataChangeCreator.a(this, out, flags);
    }
}
