package com.google.android.gms.drive.query.internal;

import com.google.android.gms.drive.metadata.MetadataField;
import com.google.android.gms.drive.metadata.internal.MetadataBundle;
import java.util.Set;

/* loaded from: classes.dex */
class e {
    static MetadataField<?> b(MetadataBundle metadataBundle) {
        Set<MetadataField<?>> setIp = metadataBundle.ip();
        if (setIp.size() != 1) {
            throw new IllegalArgumentException("bundle should have exactly 1 populated field");
        }
        return setIp.iterator().next();
    }
}
