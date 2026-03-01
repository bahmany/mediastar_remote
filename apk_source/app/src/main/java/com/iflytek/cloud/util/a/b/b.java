package com.iflytek.cloud.util.a.b;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;

/* loaded from: classes.dex */
public class b extends com.iflytek.cloud.util.a.c.a {
    private static final String[] d = {"display_name", "_id"};
    private static final String[] e = {"display_name", "data1", "contact_id"};
    private static final String[] f = {"_id", "has_phone_number"};
    private static final String[] g = {"contact_id"};
    private static final String[] h = {"display_name"};
    private static final String[] i = {"data1", "data2", "display_name"};
    private static final String[] j = {"has_phone_number"};

    public b(Context context) {
        super(context);
        a(context);
    }

    @Override // com.iflytek.cloud.util.a.c.a
    public Uri a() {
        return ContactsContract.Contacts.CONTENT_URI;
    }

    @Override // com.iflytek.cloud.util.a.c.a
    protected String[] b() {
        return d;
    }

    @Override // com.iflytek.cloud.util.a.c.a
    protected String c() {
        return Integer.parseInt(Build.VERSION.SDK) >= 8 ? "sort_key" : "display_name";
    }
}
