package com.google.android.gms.internal;

import android.util.Base64;

/* loaded from: classes.dex */
class e implements m {
    e() {
    }

    @Override // com.google.android.gms.internal.m
    public String a(byte[] bArr, boolean z) {
        return Base64.encodeToString(bArr, z ? 11 : 2);
    }

    @Override // com.google.android.gms.internal.m
    public byte[] a(String str, boolean z) throws IllegalArgumentException {
        return Base64.decode(str, z ? 11 : 2);
    }
}
