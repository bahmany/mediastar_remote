package com.google.android.gms.internal;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.drive.DriveId;
import java.util.Arrays;

/* loaded from: classes.dex */
public class kg extends com.google.android.gms.drive.metadata.internal.j<DriveId> {
    public static final kg Qq = new kg();

    private kg() {
        super("driveId", Arrays.asList("sqlId", "resourceId"), Arrays.asList("dbInstanceId"), 4100000);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.drive.metadata.a
    /* renamed from: m, reason: merged with bridge method [inline-methods] */
    public DriveId c(DataHolder dataHolder, int i, int i2) {
        long j = dataHolder.gz().getLong("dbInstanceId");
        String strC = dataHolder.c("resourceId", i, i2);
        if (strC != null && strC.startsWith("generated-android-")) {
            strC = null;
        }
        return new DriveId(strC, Long.valueOf(dataHolder.a("sqlId", i, i2)).longValue(), j);
    }
}
