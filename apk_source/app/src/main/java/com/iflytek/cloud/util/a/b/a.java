package com.iflytek.cloud.util.a.b;

import android.content.Context;
import android.net.Uri;
import android.provider.Contacts;
import com.hisilicon.dlna.dmc.data.PlaylistSQLiteHelper;

/* loaded from: classes.dex */
public class a extends com.iflytek.cloud.util.a.c.a {
    private static final String[] d = {"_id", "name"};
    private static final String[] e = {"name", "number", "_id"};
    private static final String[] f = {"person"};
    private static final String[] g = {"display_name"};
    private static final String[] h = {"number", PlaylistSQLiteHelper.COL_TYPE, "name"};
    private static final String[] i = {"_id", "name", "number", PlaylistSQLiteHelper.COL_TYPE};
    private static final String[] j = {"number"};

    public a(Context context) {
        super(context);
        a(context);
    }

    @Override // com.iflytek.cloud.util.a.c.a
    public Uri a() {
        return Contacts.People.CONTENT_URI;
    }

    @Override // com.iflytek.cloud.util.a.c.a
    protected String[] b() {
        return d;
    }

    @Override // com.iflytek.cloud.util.a.c.a
    protected String c() {
        return "name";
    }
}
