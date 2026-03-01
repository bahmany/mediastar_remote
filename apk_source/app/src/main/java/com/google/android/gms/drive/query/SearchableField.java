package com.google.android.gms.drive.query;

import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.metadata.SearchableCollectionMetadataField;
import com.google.android.gms.drive.metadata.SearchableMetadataField;
import com.google.android.gms.drive.metadata.SearchableOrderedMetadataField;
import com.google.android.gms.drive.metadata.internal.AppVisibleCustomProperties;
import com.google.android.gms.internal.kd;
import com.google.android.gms.internal.kf;
import java.util.Date;

/* loaded from: classes.dex */
public class SearchableField {
    public static final SearchableMetadataField<String> TITLE = kd.Qe;
    public static final SearchableMetadataField<String> MIME_TYPE = kd.PV;
    public static final SearchableMetadataField<Boolean> TRASHED = kd.Qf;
    public static final SearchableCollectionMetadataField<DriveId> PARENTS = kd.Qa;
    public static final SearchableOrderedMetadataField<Date> Qy = kf.Qp;
    public static final SearchableMetadataField<Boolean> STARRED = kd.Qc;
    public static final SearchableOrderedMetadataField<Date> MODIFIED_DATE = kf.Qn;
    public static final SearchableOrderedMetadataField<Date> LAST_VIEWED_BY_ME = kf.Qm;
    public static final SearchableMetadataField<Boolean> IS_PINNED = kd.PQ;
    public static final SearchableMetadataField<AppVisibleCustomProperties> Qz = kd.PG;
}
