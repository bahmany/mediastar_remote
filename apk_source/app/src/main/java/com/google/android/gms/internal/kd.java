package com.google.android.gms.internal;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.metadata.MetadataField;
import com.google.android.gms.drive.metadata.SearchableCollectionMetadataField;
import com.google.android.gms.drive.metadata.SearchableMetadataField;
import com.google.android.gms.drive.metadata.SortableMetadataField;
import com.google.android.gms.drive.metadata.internal.AppVisibleCustomProperties;
import com.google.android.gms.plus.PlusShare;
import java.util.Collection;
import java.util.Collections;

/* loaded from: classes.dex */
public class kd {
    public static final MetadataField<DriveId> PE = kg.Qq;
    public static final MetadataField<String> PF = new com.google.android.gms.drive.metadata.internal.l("alternateLink", 4300000);
    public static final a PG = new a(5000000);
    public static final MetadataField<String> PH = new com.google.android.gms.drive.metadata.internal.l(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_DESCRIPTION, 4300000);
    public static final MetadataField<String> PI = new com.google.android.gms.drive.metadata.internal.l("embedLink", 4300000);
    public static final MetadataField<String> PJ = new com.google.android.gms.drive.metadata.internal.l("fileExtension", 4300000);
    public static final MetadataField<Long> PK = new com.google.android.gms.drive.metadata.internal.g("fileSize", 4300000);
    public static final MetadataField<Boolean> PL = new com.google.android.gms.drive.metadata.internal.b("hasThumbnail", 4300000);
    public static final MetadataField<String> PM = new com.google.android.gms.drive.metadata.internal.l("indexableText", 4300000);
    public static final MetadataField<Boolean> PN = new com.google.android.gms.drive.metadata.internal.b("isAppData", 4300000);
    public static final MetadataField<Boolean> PO = new com.google.android.gms.drive.metadata.internal.b("isCopyable", 4300000);
    public static final MetadataField<Boolean> PP = new com.google.android.gms.drive.metadata.internal.b("isEditable", 4100000);
    public static final b PQ = new b("isPinned", 4100000);
    public static final MetadataField<Boolean> PR = new com.google.android.gms.drive.metadata.internal.b("isRestricted", 4300000);
    public static final MetadataField<Boolean> PS = new com.google.android.gms.drive.metadata.internal.b("isShared", 4300000);
    public static final MetadataField<Boolean> PT = new com.google.android.gms.drive.metadata.internal.b("isTrashable", 4400000);
    public static final MetadataField<Boolean> PU = new com.google.android.gms.drive.metadata.internal.b("isViewed", 4300000);
    public static final c PV = new c("mimeType", 4100000);
    public static final MetadataField<String> PW = new com.google.android.gms.drive.metadata.internal.l("originalFilename", 4300000);
    public static final com.google.android.gms.drive.metadata.b<String> PX = new com.google.android.gms.drive.metadata.internal.k("ownerNames", 4300000);
    public static final com.google.android.gms.drive.metadata.internal.m PY = new com.google.android.gms.drive.metadata.internal.m("lastModifyingUser", 6000000);
    public static final com.google.android.gms.drive.metadata.internal.m PZ = new com.google.android.gms.drive.metadata.internal.m("sharingUser", 6000000);
    public static final d Qa = new d("parents", 4100000);
    public static final e Qb = new e("quotaBytesUsed", 4300000);
    public static final f Qc = new f("starred", 4100000);
    public static final MetadataField<com.google.android.gms.common.data.a> Qd = new com.google.android.gms.drive.metadata.internal.j<com.google.android.gms.common.data.a>("thumbnail", Collections.emptySet(), Collections.emptySet(), 4400000) { // from class: com.google.android.gms.internal.kd.1
        AnonymousClass1(String str, Collection collection, Collection collection2, int i) {
            super(str, collection, collection2, i);
        }

        @Override // com.google.android.gms.drive.metadata.a
        /* renamed from: k */
        public com.google.android.gms.common.data.a c(DataHolder dataHolder, int i, int i2) {
            throw new IllegalStateException("Thumbnail field is write only");
        }
    };
    public static final g Qe = new g("title", 4100000);
    public static final h Qf = new h("trashed", 4100000);
    public static final MetadataField<String> Qg = new com.google.android.gms.drive.metadata.internal.l("webContentLink", 4300000);
    public static final MetadataField<String> Qh = new com.google.android.gms.drive.metadata.internal.l("webViewLink", 4300000);
    public static final MetadataField<String> Qi = new com.google.android.gms.drive.metadata.internal.l("uniqueIdentifier", 5000000);
    public static final com.google.android.gms.drive.metadata.internal.b Qj = new com.google.android.gms.drive.metadata.internal.b("writersCanShare", 6000000);
    public static final MetadataField<String> Qk = new com.google.android.gms.drive.metadata.internal.l("role", 6000000);

    /* renamed from: com.google.android.gms.internal.kd$1 */
    static class AnonymousClass1 extends com.google.android.gms.drive.metadata.internal.j<com.google.android.gms.common.data.a> {
        AnonymousClass1(String str, Collection collection, Collection collection2, int i) {
            super(str, collection, collection2, i);
        }

        @Override // com.google.android.gms.drive.metadata.a
        /* renamed from: k */
        public com.google.android.gms.common.data.a c(DataHolder dataHolder, int i, int i2) {
            throw new IllegalStateException("Thumbnail field is write only");
        }
    }

    public static class a extends ke implements SearchableMetadataField<AppVisibleCustomProperties> {
        public a(int i) {
            super(i);
        }
    }

    public static class b extends com.google.android.gms.drive.metadata.internal.b implements SearchableMetadataField<Boolean> {
        public b(String str, int i) {
            super(str, i);
        }
    }

    public static class c extends com.google.android.gms.drive.metadata.internal.l implements SearchableMetadataField<String> {
        public c(String str, int i) {
            super(str, i);
        }
    }

    public static class d extends com.google.android.gms.drive.metadata.internal.i<DriveId> implements SearchableCollectionMetadataField<DriveId> {
        public d(String str, int i) {
            super(str, i);
        }
    }

    public static class e extends com.google.android.gms.drive.metadata.internal.g implements SortableMetadataField<Long> {
        public e(String str, int i) {
            super(str, i);
        }
    }

    public static class f extends com.google.android.gms.drive.metadata.internal.b implements SearchableMetadataField<Boolean> {
        public f(String str, int i) {
            super(str, i);
        }
    }

    public static class g extends com.google.android.gms.drive.metadata.internal.l implements SearchableMetadataField<String>, SortableMetadataField<String> {
        public g(String str, int i) {
            super(str, i);
        }
    }

    public static class h extends com.google.android.gms.drive.metadata.internal.b implements SearchableMetadataField<Boolean> {
        public h(String str, int i) {
            super(str, i);
        }

        @Override // com.google.android.gms.drive.metadata.internal.b, com.google.android.gms.drive.metadata.a
        /* renamed from: e */
        public Boolean c(DataHolder dataHolder, int i, int i2) {
            return Boolean.valueOf(dataHolder.b(getName(), i, i2) != 0);
        }
    }
}
