package com.google.android.gms.analytics;

import android.content.Context;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/* loaded from: classes.dex */
class h implements l {
    private static h xQ;
    private static final Object xz = new Object();
    private final Context mContext;
    private String xR;
    private boolean xS = false;
    private final Object xT = new Object();

    protected h(Context context) {
        this.mContext = context;
        dV();
    }

    private boolean ad(String str) throws IOException {
        try {
            z.V("Storing clientId.");
            FileOutputStream fileOutputStreamOpenFileOutput = this.mContext.openFileOutput("gaClientId", 0);
            fileOutputStreamOpenFileOutput.write(str.getBytes());
            fileOutputStreamOpenFileOutput.close();
            return true;
        } catch (FileNotFoundException e) {
            z.T("Error creating clientId file.");
            return false;
        } catch (IOException e2) {
            z.T("Error writing to clientId file.");
            return false;
        }
    }

    public static h dR() {
        h hVar;
        synchronized (xz) {
            hVar = xQ;
        }
        return hVar;
    }

    private String dT() {
        if (!this.xS) {
            synchronized (this.xT) {
                if (!this.xS) {
                    z.V("Waiting for clientId to load");
                    do {
                        try {
                            this.xT.wait();
                        } catch (InterruptedException e) {
                            z.T("Exception while waiting for clientId: " + e);
                        }
                    } while (!this.xS);
                }
            }
        }
        z.V("Loaded clientId");
        return this.xR;
    }

    private void dV() {
        new Thread("client_id_fetcher") { // from class: com.google.android.gms.analytics.h.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                synchronized (h.this.xT) {
                    h.this.xR = h.this.dW();
                    h.this.xS = true;
                    h.this.xT.notifyAll();
                }
            }
        }.start();
    }

    public static void y(Context context) {
        synchronized (xz) {
            if (xQ == null) {
                xQ = new h(context);
            }
        }
    }

    public boolean ac(String str) {
        return "&cid".equals(str);
    }

    String dS() {
        String str;
        synchronized (this.xT) {
            this.xR = dU();
            str = this.xR;
        }
        return str;
    }

    protected String dU() {
        String lowerCase = UUID.randomUUID().toString().toLowerCase();
        try {
            return !ad(lowerCase) ? "0" : lowerCase;
        } catch (Exception e) {
            return null;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:27:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:7:0x002b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    java.lang.String dW() throws java.io.IOException {
        /*
            r6 = this;
            r0 = 0
            android.content.Context r1 = r6.mContext     // Catch: java.io.FileNotFoundException -> L42 java.io.IOException -> L54
            java.lang.String r2 = "gaClientId"
            java.io.FileInputStream r2 = r1.openFileInput(r2)     // Catch: java.io.FileNotFoundException -> L42 java.io.IOException -> L54
            r1 = 128(0x80, float:1.8E-43)
            byte[] r3 = new byte[r1]     // Catch: java.io.FileNotFoundException -> L42 java.io.IOException -> L54
            r1 = 0
            r4 = 128(0x80, float:1.8E-43)
            int r4 = r2.read(r3, r1, r4)     // Catch: java.io.FileNotFoundException -> L42 java.io.IOException -> L54
            int r1 = r2.available()     // Catch: java.io.FileNotFoundException -> L42 java.io.IOException -> L54
            if (r1 <= 0) goto L30
            java.lang.String r1 = "clientId file seems corrupted, deleting it."
            com.google.android.gms.analytics.z.T(r1)     // Catch: java.io.FileNotFoundException -> L42 java.io.IOException -> L54
            r2.close()     // Catch: java.io.FileNotFoundException -> L42 java.io.IOException -> L54
            android.content.Context r1 = r6.mContext     // Catch: java.io.FileNotFoundException -> L42 java.io.IOException -> L54
            java.lang.String r2 = "gaClientId"
            r1.deleteFile(r2)     // Catch: java.io.FileNotFoundException -> L42 java.io.IOException -> L54
        L29:
            if (r0 != 0) goto L2f
            java.lang.String r0 = r6.dU()
        L2f:
            return r0
        L30:
            if (r4 > 0) goto L44
            java.lang.String r1 = "clientId file seems empty, deleting it."
            com.google.android.gms.analytics.z.T(r1)     // Catch: java.io.FileNotFoundException -> L42 java.io.IOException -> L54
            r2.close()     // Catch: java.io.FileNotFoundException -> L42 java.io.IOException -> L54
            android.content.Context r1 = r6.mContext     // Catch: java.io.FileNotFoundException -> L42 java.io.IOException -> L54
            java.lang.String r2 = "gaClientId"
            r1.deleteFile(r2)     // Catch: java.io.FileNotFoundException -> L42 java.io.IOException -> L54
            goto L29
        L42:
            r1 = move-exception
            goto L29
        L44:
            java.lang.String r1 = new java.lang.String     // Catch: java.io.FileNotFoundException -> L42 java.io.IOException -> L54
            r5 = 0
            r1.<init>(r3, r5, r4)     // Catch: java.io.FileNotFoundException -> L42 java.io.IOException -> L54
            r2.close()     // Catch: java.io.IOException -> L62 java.io.FileNotFoundException -> L65
            java.lang.String r0 = "Loaded client id from disk."
            com.google.android.gms.analytics.z.V(r0)     // Catch: java.io.IOException -> L62 java.io.FileNotFoundException -> L65
            r0 = r1
            goto L29
        L54:
            r1 = move-exception
        L55:
            java.lang.String r1 = "Error reading clientId file, deleting it."
            com.google.android.gms.analytics.z.T(r1)
            android.content.Context r1 = r6.mContext
            java.lang.String r2 = "gaClientId"
            r1.deleteFile(r2)
            goto L29
        L62:
            r0 = move-exception
            r0 = r1
            goto L55
        L65:
            r0 = move-exception
            r0 = r1
            goto L29
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.analytics.h.dW():java.lang.String");
    }

    @Override // com.google.android.gms.analytics.l
    public String getValue(String field) {
        if ("&cid".equals(field)) {
            return dT();
        }
        return null;
    }
}
