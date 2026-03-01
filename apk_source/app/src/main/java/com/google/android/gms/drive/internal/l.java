package com.google.android.gms.drive.internal;

import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.metadata.MetadataField;
import com.google.android.gms.drive.metadata.internal.MetadataBundle;

/* loaded from: classes.dex */
public final class l extends Metadata {
    private final MetadataBundle Oj;

    public l(MetadataBundle metadataBundle) {
        this.Oj = metadataBundle;
    }

    @Override // com.google.android.gms.drive.Metadata
    protected <T> T a(MetadataField<T> metadataField) {
        return (T) this.Oj.a(metadataField);
    }

    @Override // com.google.android.gms.common.data.Freezable
    /* renamed from: hR, reason: merged with bridge method [inline-methods] */
    public Metadata freeze() {
        return new l(MetadataBundle.a(this.Oj));
    }

    @Override // com.google.android.gms.common.data.Freezable
    public boolean isDataValid() {
        return this.Oj != null;
    }

    public String toString() {
        return "Metadata [mImpl=" + this.Oj + "]";
    }
}
