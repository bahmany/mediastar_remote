package com.google.android.gms.drive.metadata.internal;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.drive.UserMetadata;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/* loaded from: classes.dex */
public class m extends j<UserMetadata> {
    public m(String str, int i) {
        super(str, bm(str), Collections.emptyList(), i);
    }

    private String bl(String str) {
        return r(getName(), str);
    }

    private static Collection<String> bm(String str) {
        return Arrays.asList(r(str, "permissionId"), r(str, "displayName"), r(str, "picture"), r(str, "isAuthenticatedUser"), r(str, "emailAddress"));
    }

    private static String r(String str, String str2) {
        return str + "." + str2;
    }

    @Override // com.google.android.gms.drive.metadata.a
    protected boolean b(DataHolder dataHolder, int i, int i2) {
        return !dataHolder.h(bl("permissionId"), i, i2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.drive.metadata.a
    /* renamed from: j, reason: merged with bridge method [inline-methods] */
    public UserMetadata c(DataHolder dataHolder, int i, int i2) {
        String strC = dataHolder.c(bl("permissionId"), i, i2);
        if (strC == null) {
            return null;
        }
        String strC2 = dataHolder.c(bl("displayName"), i, i2);
        String strC3 = dataHolder.c(bl("picture"), i, i2);
        Boolean boolValueOf = Boolean.valueOf(dataHolder.d(bl("isAuthenticatedUser"), i, i2));
        return new UserMetadata(strC, strC2, strC3, boolValueOf.booleanValue(), dataHolder.c(bl("emailAddress"), i, i2));
    }
}
