package com.google.android.gms.drive;

import com.google.android.gms.common.data.DataBuffer;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.drive.internal.l;
import com.google.android.gms.drive.metadata.MetadataField;
import com.google.android.gms.drive.metadata.internal.MetadataBundle;
import com.google.android.gms.internal.kd;

/* loaded from: classes.dex */
public final class MetadataBuffer extends DataBuffer<Metadata> {
    private final String Ni;
    private a Nj;

    private static class a extends Metadata {
        private final DataHolder IC;
        private final int JR;
        private final int Nk;

        public a(DataHolder dataHolder, int i) {
            this.IC = dataHolder;
            this.Nk = i;
            this.JR = dataHolder.ar(i);
        }

        @Override // com.google.android.gms.drive.Metadata
        protected <T> T a(MetadataField<T> metadataField) {
            return metadataField.a(this.IC, this.Nk, this.JR);
        }

        @Override // com.google.android.gms.common.data.Freezable
        /* renamed from: hR, reason: merged with bridge method [inline-methods] */
        public Metadata freeze() {
            MetadataBundle metadataBundleIo = MetadataBundle.io();
            for (MetadataField<?> metadataField : com.google.android.gms.drive.metadata.internal.e.in()) {
                if (!(metadataField instanceof com.google.android.gms.drive.metadata.b) && metadataField != kd.Qd) {
                    metadataField.a(this.IC, metadataBundleIo, this.Nk, this.JR);
                }
            }
            return new l(metadataBundleIo);
        }

        @Override // com.google.android.gms.common.data.Freezable
        public boolean isDataValid() {
            return !this.IC.isClosed();
        }
    }

    public MetadataBuffer(DataHolder dataHolder, String nextPageToken) {
        super(dataHolder);
        this.Ni = nextPageToken;
        dataHolder.gz().setClassLoader(MetadataBuffer.class.getClassLoader());
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.google.android.gms.common.data.DataBuffer
    public Metadata get(int row) {
        a aVar = this.Nj;
        if (aVar != null && aVar.Nk == row) {
            return aVar;
        }
        a aVar2 = new a(this.IC, row);
        this.Nj = aVar2;
        return aVar2;
    }

    public String getNextPageToken() {
        return this.Ni;
    }
}
