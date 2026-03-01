package com.google.android.gms.drive.query.internal;

import com.google.android.gms.drive.metadata.MetadataField;
import java.util.List;

/* loaded from: classes.dex */
public interface f<F> {
    <T> F b(com.google.android.gms.drive.metadata.b<T> bVar, T t);

    <T> F b(Operator operator, MetadataField<T> metadataField, T t);

    F b(Operator operator, List<F> list);

    F d(MetadataField<?> metadataField);

    <T> F d(MetadataField<T> metadataField, T t);

    F is();

    F j(F f);
}
