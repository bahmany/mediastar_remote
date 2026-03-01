package com.google.android.gms.drive.query;

import com.google.android.gms.drive.metadata.SortableMetadataField;
import com.google.android.gms.internal.kd;
import com.google.android.gms.internal.kf;
import java.util.Date;

/* loaded from: classes.dex */
public class SortableField {
    public static final SortableMetadataField<String> TITLE = kd.Qe;
    public static final SortableMetadataField<Date> CREATED_DATE = kf.Ql;
    public static final SortableMetadataField<Date> MODIFIED_DATE = kf.Qn;
    public static final SortableMetadataField<Date> MODIFIED_BY_ME_DATE = kf.Qo;
    public static final SortableMetadataField<Date> LAST_VIEWED_BY_ME = kf.Qm;
    public static final SortableMetadataField<Date> SHARED_WITH_ME_DATE = kf.Qp;
    public static final SortableMetadataField<Long> QUOTA_USED = kd.Qb;
}
