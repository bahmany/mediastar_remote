package com.iflytek.cloud.util.a.c;

import android.content.Context;
import android.net.Uri;
import android.provider.CallLog;

/* loaded from: classes.dex */
public abstract class a {
    protected static final String[] a = {"number", "name", "date"};
    protected static String[] c;
    protected Context b;

    public a(Context context) {
        this.b = null;
        this.b = context;
    }

    public abstract Uri a();

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0082  */
    /* JADX WARN: Type inference failed for: r1v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r1v1 */
    /* JADX WARN: Type inference failed for: r1v3, types: [android.database.Cursor] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.util.HashMap<java.lang.String, java.lang.String> a(int r9) throws java.lang.Throwable {
        /*
            r8 = this;
            r6 = 0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "date DESC limit "
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.StringBuilder r0 = r0.append(r9)
            java.lang.String r5 = r0.toString()
            java.util.HashMap r7 = new java.util.HashMap
            r7.<init>()
            java.lang.String r3 = "0==0) GROUP BY (number"
            android.content.Context r0 = r8.b     // Catch: java.lang.Throwable -> La3 java.lang.Exception -> La6
            android.content.ContentResolver r0 = r0.getContentResolver()     // Catch: java.lang.Throwable -> La3 java.lang.Exception -> La6
            android.net.Uri r1 = android.provider.CallLog.Calls.CONTENT_URI     // Catch: java.lang.Throwable -> La3 java.lang.Exception -> La6
            java.lang.String[] r2 = com.iflytek.cloud.util.a.c.a.a     // Catch: java.lang.Throwable -> La3 java.lang.Exception -> La6
            r4 = 0
            android.database.Cursor r1 = r0.query(r1, r2, r3, r4, r5)     // Catch: java.lang.Throwable -> La3 java.lang.Exception -> La6
            if (r1 != 0) goto L39
            java.lang.String r0 = "iFly_ContactManager"
            java.lang.String r2 = "queryCallLog ----------------cursor is null"
            com.iflytek.cloud.a.f.a.a.a(r0, r2)     // Catch: java.lang.Exception -> L47 java.lang.Throwable -> L7f
        L33:
            if (r1 == 0) goto L38
            r1.close()
        L38:
            return r7
        L39:
            int r0 = r1.getCount()     // Catch: java.lang.Exception -> L47 java.lang.Throwable -> L7f
            if (r0 != 0) goto L51
            java.lang.String r0 = "iFly_ContactManager"
            java.lang.String r2 = "queryCallLog ----------------cursor getCount == 0"
            com.iflytek.cloud.a.f.a.a.a(r0, r2)     // Catch: java.lang.Exception -> L47 java.lang.Throwable -> L7f
            goto L33
        L47:
            r0 = move-exception
        L48:
            r0.printStackTrace()     // Catch: java.lang.Throwable -> L7f
            if (r1 == 0) goto L38
            r1.close()
            goto L38
        L51:
            boolean r0 = r1.moveToNext()     // Catch: java.lang.Exception -> L47 java.lang.Throwable -> L7f
            if (r0 == 0) goto L86
            java.lang.String r0 = "number"
            int r0 = r1.getColumnIndex(r0)     // Catch: java.lang.Exception -> L47 java.lang.Throwable -> L7f
            java.lang.String r2 = r1.getString(r0)     // Catch: java.lang.Exception -> L47 java.lang.Throwable -> L7f
            java.lang.String r0 = "name"
            int r0 = r1.getColumnIndex(r0)     // Catch: java.lang.Exception -> L47 java.lang.Throwable -> L7f
            java.lang.String r0 = r1.getString(r0)     // Catch: java.lang.Exception -> L47 java.lang.Throwable -> L7f
            java.lang.String r3 = "date"
            int r3 = r1.getColumnIndex(r3)     // Catch: java.lang.Exception -> L47 java.lang.Throwable -> L7f
            java.lang.String r3 = r1.getString(r3)     // Catch: java.lang.Exception -> L47 java.lang.Throwable -> L7f
            if (r0 != 0) goto L7b
            java.lang.String r0 = com.iflytek.cloud.util.a.e.a(r2)     // Catch: java.lang.Exception -> L47 java.lang.Throwable -> L7f
        L7b:
            r7.put(r3, r0)     // Catch: java.lang.Exception -> L47 java.lang.Throwable -> L7f
            goto L51
        L7f:
            r0 = move-exception
        L80:
            if (r1 == 0) goto L85
            r1.close()
        L85:
            throw r0
        L86:
            java.lang.String r0 = "iFly_ContactManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> L47 java.lang.Throwable -> L7f
            r2.<init>()     // Catch: java.lang.Exception -> L47 java.lang.Throwable -> L7f
            java.lang.String r3 = "queryCallLog ----------------cursor getCount =="
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Exception -> L47 java.lang.Throwable -> L7f
            int r3 = r1.getCount()     // Catch: java.lang.Exception -> L47 java.lang.Throwable -> L7f
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Exception -> L47 java.lang.Throwable -> L7f
            java.lang.String r2 = r2.toString()     // Catch: java.lang.Exception -> L47 java.lang.Throwable -> L7f
            com.iflytek.cloud.a.f.a.a.a(r0, r2)     // Catch: java.lang.Exception -> L47 java.lang.Throwable -> L7f
            goto L33
        La3:
            r0 = move-exception
            r1 = r6
            goto L80
        La6:
            r0 = move-exception
            r1 = r6
            goto L48
        */
        throw new UnsupportedOperationException("Method not decompiled: com.iflytek.cloud.util.a.c.a.a(int):java.util.HashMap");
    }

    protected void a(Context context) {
        c = new String[100];
        c[0] = "其他";
        c[1] = "住宅";
        c[2] = "手机";
        c[3] = "工作";
        c[4] = "工作传真";
        c[5] = "住宅传真";
        c[6] = "寻呼机";
        c[7] = "其他";
        c[9] = "SIM卡";
        for (int i = 10; i < c.length; i++) {
            c[i] = "自定义电话";
        }
    }

    protected abstract String[] b();

    protected abstract String c();

    /* JADX WARN: Removed duplicated region for block: B:25:0x006a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.util.HashMap<java.lang.String, java.lang.String> d() throws java.lang.Throwable {
        /*
            r8 = this;
            r6 = 0
            java.lang.String[] r2 = r8.b()
            java.util.HashMap r7 = new java.util.HashMap
            r7.<init>()
            android.content.Context r0 = r8.b     // Catch: java.lang.Throwable -> L8b java.lang.Exception -> L8e
            android.content.ContentResolver r0 = r0.getContentResolver()     // Catch: java.lang.Throwable -> L8b java.lang.Exception -> L8e
            android.net.Uri r1 = r8.a()     // Catch: java.lang.Throwable -> L8b java.lang.Exception -> L8e
            r3 = 0
            r4 = 0
            java.lang.String r5 = r8.c()     // Catch: java.lang.Throwable -> L8b java.lang.Exception -> L8e
            android.database.Cursor r1 = r0.query(r1, r2, r3, r4, r5)     // Catch: java.lang.Throwable -> L8b java.lang.Exception -> L8e
            if (r1 != 0) goto L2d
            java.lang.String r0 = "iFly_ContactManager"
            java.lang.String r2 = "queryContacts------cursor is null"
            com.iflytek.cloud.a.f.a.a.a(r0, r2)     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L67
        L27:
            if (r1 == 0) goto L2c
            r1.close()
        L2c:
            return r7
        L2d:
            int r0 = r1.getCount()     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L67
            if (r0 != 0) goto L45
            java.lang.String r0 = "iFly_ContactManager"
            java.lang.String r2 = "queryContacts------cursor getCount == 0"
            com.iflytek.cloud.a.f.a.a.a(r0, r2)     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L67
            goto L27
        L3b:
            r0 = move-exception
        L3c:
            r0.printStackTrace()     // Catch: java.lang.Throwable -> L67
            if (r1 == 0) goto L2c
            r1.close()
            goto L2c
        L45:
            boolean r0 = r1.moveToNext()     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L67
            if (r0 == 0) goto L6e
            r0 = 0
            r0 = r2[r0]     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L67
            int r0 = r1.getColumnIndex(r0)     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L67
            java.lang.String r0 = r1.getString(r0)     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L67
            r3 = 1
            r3 = r2[r3]     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L67
            int r3 = r1.getColumnIndex(r3)     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L67
            java.lang.String r3 = r1.getString(r3)     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L67
            if (r0 == 0) goto L45
            r7.put(r3, r0)     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L67
            goto L45
        L67:
            r0 = move-exception
        L68:
            if (r1 == 0) goto L6d
            r1.close()
        L6d:
            throw r0
        L6e:
            java.lang.String r0 = "iFly_ContactManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L67
            r2.<init>()     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L67
            java.lang.String r3 = "queryContacts_20------count = "
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L67
            int r3 = r1.getCount()     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L67
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L67
            java.lang.String r2 = r2.toString()     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L67
            com.iflytek.cloud.a.f.a.a.a(r0, r2)     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L67
            goto L27
        L8b:
            r0 = move-exception
            r1 = r6
            goto L68
        L8e:
            r0 = move-exception
            r1 = r6
            goto L3c
        */
        throw new UnsupportedOperationException("Method not decompiled: com.iflytek.cloud.util.a.c.a.d():java.util.HashMap");
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x008e A[Catch: Exception -> 0x0061, all -> 0x0096, TRY_ENTER, TRY_LEAVE, TryCatch #5 {Exception -> 0x0061, all -> 0x0096, blocks: (B:5:0x001c, B:7:0x0022, B:9:0x0028, B:11:0x0050, B:19:0x006c, B:22:0x008e), top: B:36:0x001c }] */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0099  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.util.List<com.iflytek.cloud.util.a.a.a> e() throws java.lang.Throwable {
        /*
            r10 = this;
            r6 = 0
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            android.content.Context r0 = r10.b     // Catch: java.lang.Throwable -> L9d java.lang.Exception -> La3
            android.content.ContentResolver r0 = r0.getContentResolver()     // Catch: java.lang.Throwable -> L9d java.lang.Exception -> La3
            java.lang.String r1 = "content://icc/adn"
            android.net.Uri r1 = android.net.Uri.parse(r1)     // Catch: java.lang.Throwable -> L9d java.lang.Exception -> La3
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            android.database.Cursor r7 = r0.query(r1, r2, r3, r4, r5)     // Catch: java.lang.Throwable -> L9d java.lang.Exception -> La3
            if (r7 == 0) goto L8e
            int r0 = r7.getCount()     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L96
            if (r0 <= 0) goto L8e
        L22:
            boolean r0 = r7.moveToNext()     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L96
            if (r0 == 0) goto L6c
            java.lang.String r0 = "name"
            int r0 = r7.getColumnIndex(r0)     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L96
            java.lang.String r2 = r7.getString(r0)     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L96
            java.lang.String r0 = "_id"
            int r0 = r7.getColumnIndex(r0)     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L96
            java.lang.String r1 = r7.getString(r0)     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L96
            java.lang.String r0 = "number"
            int r0 = r7.getColumnIndex(r0)     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L96
            java.lang.String r0 = r7.getString(r0)     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L96
            java.lang.String r0 = com.iflytek.cloud.util.a.e.a(r0)     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L96
            java.lang.String r4 = com.iflytek.cloud.a.f.d.a(r0)     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L96
            if (r2 == 0) goto L22
            com.iflytek.cloud.util.a.a.a r0 = new com.iflytek.cloud.util.a.a.a     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L96
            r3 = 0
            r5 = 0
            java.lang.String[] r6 = com.iflytek.cloud.util.a.c.a.c     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L96
            r9 = 9
            r6 = r6[r9]     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L96
            r0.<init>(r1, r2, r3, r4, r5, r6)     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L96
            r8.add(r0)     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L96
            goto L22
        L61:
            r0 = move-exception
            r1 = r7
        L63:
            r0.printStackTrace()     // Catch: java.lang.Throwable -> La0
            if (r1 == 0) goto L6b
            r1.close()
        L6b:
            return r8
        L6c:
            java.lang.String r0 = "iFly_ContactManager"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L96
            r1.<init>()     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L96
            java.lang.String r2 = "querySIM-------count = "
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L96
            int r2 = r7.getCount()     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L96
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L96
            java.lang.String r1 = r1.toString()     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L96
            com.iflytek.cloud.a.f.a.a.a(r0, r1)     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L96
        L88:
            if (r7 == 0) goto L6b
            r7.close()
            goto L6b
        L8e:
            java.lang.String r0 = "iFly_ContactManager"
            java.lang.String r1 = "querySIM-------cursor getCount = 0 or cursor is null"
            com.iflytek.cloud.a.f.a.a.a(r0, r1)     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L96
            goto L88
        L96:
            r0 = move-exception
        L97:
            if (r7 == 0) goto L9c
            r7.close()
        L9c:
            throw r0
        L9d:
            r0 = move-exception
            r7 = r6
            goto L97
        La0:
            r0 = move-exception
            r7 = r1
            goto L97
        La3:
            r0 = move-exception
            r1 = r6
            goto L63
        */
        throw new UnsupportedOperationException("Method not decompiled: com.iflytek.cloud.util.a.c.a.e():java.util.List");
    }

    public Uri f() {
        return CallLog.Calls.CONTENT_URI;
    }
}
