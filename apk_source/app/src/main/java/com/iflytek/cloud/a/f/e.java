package com.iflytek.cloud.a.f;

import android.content.Context;
import android.text.TextUtils;
import java.io.File;
import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public class e {
    private static String a = "";

    public static String a(Context context) {
        if (!TextUtils.isEmpty(a)) {
            return a;
        }
        String absolutePath = context.getFilesDir().getAbsolutePath();
        if (!absolutePath.endsWith(ServiceReference.DELIMITER)) {
            absolutePath = absolutePath + ServiceReference.DELIMITER;
        }
        String str = absolutePath + "msclib/";
        File file = new File(str);
        if (!file.exists()) {
            file.mkdirs();
        }
        a = str;
        return a;
    }

    public static void a(String str) {
        File file = new File(str);
        if (file.exists()) {
            file.delete();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:39:0x003f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static void a(java.util.concurrent.ConcurrentLinkedQueue<byte[]> r4, java.lang.String r5) throws java.lang.Throwable {
        /*
            r2 = 0
            b(r5)     // Catch: java.lang.Throwable -> L3b java.lang.Exception -> L4a
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch: java.lang.Throwable -> L3b java.lang.Exception -> L4a
            r1.<init>(r5)     // Catch: java.lang.Throwable -> L3b java.lang.Exception -> L4a
            java.util.Iterator r3 = r4.iterator()     // Catch: java.lang.Exception -> L1d java.lang.Throwable -> L48
        Ld:
            boolean r0 = r3.hasNext()     // Catch: java.lang.Exception -> L1d java.lang.Throwable -> L48
            if (r0 == 0) goto L27
            java.lang.Object r0 = r3.next()     // Catch: java.lang.Exception -> L1d java.lang.Throwable -> L48
            byte[] r0 = (byte[]) r0     // Catch: java.lang.Exception -> L1d java.lang.Throwable -> L48
            r1.write(r0)     // Catch: java.lang.Exception -> L1d java.lang.Throwable -> L48
            goto Ld
        L1d:
            r0 = move-exception
        L1e:
            r0.printStackTrace()     // Catch: java.lang.Throwable -> L48
            if (r1 == 0) goto L26
            r1.close()     // Catch: java.lang.Exception -> L36
        L26:
            return
        L27:
            r1.close()     // Catch: java.lang.Exception -> L1d java.lang.Throwable -> L48
            r0 = 0
            if (r2 == 0) goto L26
            r0.close()     // Catch: java.lang.Exception -> L31
            goto L26
        L31:
            r0 = move-exception
            r0.printStackTrace()
            goto L26
        L36:
            r0 = move-exception
            r0.printStackTrace()
            goto L26
        L3b:
            r0 = move-exception
            r1 = r2
        L3d:
            if (r1 == 0) goto L42
            r1.close()     // Catch: java.lang.Exception -> L43
        L42:
            throw r0
        L43:
            r1 = move-exception
            r1.printStackTrace()
            goto L42
        L48:
            r0 = move-exception
            goto L3d
        L4a:
            r0 = move-exception
            r1 = r2
            goto L1e
        */
        throw new UnsupportedOperationException("Method not decompiled: com.iflytek.cloud.a.f.e.a(java.util.concurrent.ConcurrentLinkedQueue, java.lang.String):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:50:0x0053 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static boolean a(android.os.MemoryFile r9, long r10, java.lang.String r12) throws java.lang.Throwable {
        /*
            r6 = 1024(0x400, double:5.06E-321)
            r0 = 0
            if (r9 == 0) goto Lb
            boolean r1 = android.text.TextUtils.isEmpty(r12)
            if (r1 == 0) goto Lc
        Lb:
            return r0
        Lc:
            r3 = 0
            a(r12)     // Catch: java.lang.Exception -> L42 java.lang.Throwable -> L4f
            b(r12)     // Catch: java.lang.Exception -> L42 java.lang.Throwable -> L4f
            java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch: java.lang.Exception -> L42 java.lang.Throwable -> L4f
            r2.<init>(r12)     // Catch: java.lang.Exception -> L42 java.lang.Throwable -> L4f
            r1 = 1024(0x400, float:1.435E-42)
            byte[] r3 = new byte[r1]     // Catch: java.lang.Throwable -> L59 java.lang.Exception -> L5b
            r1 = r0
        L1d:
            long r4 = (long) r1     // Catch: java.lang.Throwable -> L59 java.lang.Exception -> L5b
            int r4 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r4 >= 0) goto L39
            long r4 = (long) r1     // Catch: java.lang.Throwable -> L59 java.lang.Exception -> L5b
            long r4 = r10 - r4
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 <= 0) goto L35
            r4 = r6
        L2a:
            int r4 = (int) r4     // Catch: java.lang.Throwable -> L59 java.lang.Exception -> L5b
            r5 = 0
            r9.readBytes(r3, r1, r5, r4)     // Catch: java.lang.Throwable -> L59 java.lang.Exception -> L5b
            r5 = 0
            r2.write(r3, r5, r4)     // Catch: java.lang.Throwable -> L59 java.lang.Exception -> L5b
            int r1 = r1 + r4
            goto L1d
        L35:
            long r4 = (long) r1
            long r4 = r10 - r4
            goto L2a
        L39:
            r0 = 1
            if (r2 == 0) goto Lb
            r2.close()     // Catch: java.lang.Exception -> L40
            goto Lb
        L40:
            r1 = move-exception
            goto Lb
        L42:
            r1 = move-exception
            r2 = r3
        L44:
            r1.printStackTrace()     // Catch: java.lang.Throwable -> L59
            if (r2 == 0) goto Lb
            r2.close()     // Catch: java.lang.Exception -> L4d
            goto Lb
        L4d:
            r1 = move-exception
            goto Lb
        L4f:
            r0 = move-exception
            r2 = r3
        L51:
            if (r2 == 0) goto L56
            r2.close()     // Catch: java.lang.Exception -> L57
        L56:
            throw r0
        L57:
            r1 = move-exception
            goto L56
        L59:
            r0 = move-exception
            goto L51
        L5b:
            r1 = move-exception
            goto L44
        */
        throw new UnsupportedOperationException("Method not decompiled: com.iflytek.cloud.a.f.e.a(android.os.MemoryFile, long, java.lang.String):boolean");
    }

    public static void b(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        File file = new File(str);
        if (!str.endsWith(ServiceReference.DELIMITER)) {
            file = file.getParentFile();
        }
        if (file.exists()) {
            return;
        }
        file.mkdirs();
    }
}
