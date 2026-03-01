package com.google.android.gms.tagmanager;

import android.content.Context;
import android.content.res.Resources;
import com.google.android.gms.internal.c;
import com.google.android.gms.internal.ok;
import com.google.android.gms.internal.pl;
import com.google.android.gms.internal.pm;
import com.google.android.gms.tagmanager.cr;
import com.google.android.gms.tagmanager.o;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONException;

/* loaded from: classes.dex */
class cq implements o.f {
    private final String anR;
    private bg<ok.a> aqi;
    private final ExecutorService aqp = Executors.newSingleThreadExecutor();
    private final Context mContext;

    cq(Context context, String str) {
        this.mContext = context;
        this.anR = str;
    }

    private cr.c a(ByteArrayOutputStream byteArrayOutputStream) {
        try {
            return ba.cD(byteArrayOutputStream.toString("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            bh.S("Failed to convert binary resource to string for JSON parsing; the file format is not UTF-8 format.");
            return null;
        } catch (JSONException e2) {
            bh.W("Failed to extract the container from the resource file. Resource is a UTF-8 encoded string but doesn't contain a JSON container");
            return null;
        }
    }

    private void d(ok.a aVar) throws IllegalArgumentException {
        if (aVar.gs == null && aVar.ash == null) {
            throw new IllegalArgumentException("Resource and SupplementedResource are NULL.");
        }
    }

    private cr.c k(byte[] bArr) {
        try {
            cr.c cVarB = cr.b(c.f.a(bArr));
            if (cVarB == null) {
                return cVarB;
            }
            bh.V("The container was successfully loaded from the resource (using binary file)");
            return cVarB;
        } catch (pl e) {
            bh.T("The resource file is corrupted. The container cannot be extracted from the binary file");
            return null;
        } catch (cr.g e2) {
            bh.W("The resource file is invalid. The container from the binary file is invalid");
            return null;
        }
    }

    @Override // com.google.android.gms.tagmanager.o.f
    public void a(bg<ok.a> bgVar) {
        this.aqi = bgVar;
    }

    @Override // com.google.android.gms.tagmanager.o.f
    public void b(final ok.a aVar) {
        this.aqp.execute(new Runnable() { // from class: com.google.android.gms.tagmanager.cq.2
            @Override // java.lang.Runnable
            public void run() throws IOException {
                cq.this.c(aVar);
            }
        });
    }

    boolean c(ok.a aVar) throws IOException {
        boolean z = false;
        File fileOQ = oQ();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileOQ);
            try {
                try {
                    fileOutputStream.write(pm.f(aVar));
                    z = true;
                } finally {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        bh.W("error closing stream for writing resource to disk");
                    }
                }
            } catch (IOException e2) {
                bh.W("Error writing resource to disk. Removing resource from disk.");
                fileOQ.delete();
                try {
                    fileOutputStream.close();
                } catch (IOException e3) {
                    bh.W("error closing stream for writing resource to disk");
                }
            }
        } catch (FileNotFoundException e4) {
            bh.T("Error opening resource file for writing");
        }
        return z;
    }

    @Override // com.google.android.gms.tagmanager.o.f
    public cr.c fe(int i) throws Resources.NotFoundException {
        try {
            InputStream inputStreamOpenRawResource = this.mContext.getResources().openRawResource(i);
            bh.V("Attempting to load a container from the resource ID " + i + " (" + this.mContext.getResources().getResourceName(i) + ")");
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                cr.b(inputStreamOpenRawResource, byteArrayOutputStream);
                cr.c cVarA = a(byteArrayOutputStream);
                if (cVarA != null) {
                    bh.V("The container was successfully loaded from the resource (using JSON file format)");
                } else {
                    cVarA = k(byteArrayOutputStream.toByteArray());
                }
                return cVarA;
            } catch (IOException e) {
                bh.W("Error reading the default container with resource ID " + i + " (" + this.mContext.getResources().getResourceName(i) + ")");
                return null;
            }
        } catch (Resources.NotFoundException e2) {
            bh.W("Failed to load the container. No default container resource found with the resource ID " + i);
            return null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:10:0x002e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    void oP() throws java.io.IOException {
        /*
            r3 = this;
            com.google.android.gms.tagmanager.bg<com.google.android.gms.internal.ok$a> r0 = r3.aqi
            if (r0 != 0) goto Lc
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "Callback must be set before execute"
            r0.<init>(r1)
            throw r0
        Lc:
            com.google.android.gms.tagmanager.bg<com.google.android.gms.internal.ok$a> r0 = r3.aqi
            r0.nZ()
            java.lang.String r0 = "Attempting to load resource from disk"
            com.google.android.gms.tagmanager.bh.V(r0)
            com.google.android.gms.tagmanager.ce r0 = com.google.android.gms.tagmanager.ce.oH()
            com.google.android.gms.tagmanager.ce$a r0 = r0.oI()
            com.google.android.gms.tagmanager.ce$a r1 = com.google.android.gms.tagmanager.ce.a.CONTAINER
            if (r0 == r1) goto L2e
            com.google.android.gms.tagmanager.ce r0 = com.google.android.gms.tagmanager.ce.oH()
            com.google.android.gms.tagmanager.ce$a r0 = r0.oI()
            com.google.android.gms.tagmanager.ce$a r1 = com.google.android.gms.tagmanager.ce.a.CONTAINER_DEBUG
            if (r0 != r1) goto L46
        L2e:
            java.lang.String r0 = r3.anR
            com.google.android.gms.tagmanager.ce r1 = com.google.android.gms.tagmanager.ce.oH()
            java.lang.String r1 = r1.getContainerId()
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L46
            com.google.android.gms.tagmanager.bg<com.google.android.gms.internal.ok$a> r0 = r3.aqi
            com.google.android.gms.tagmanager.bg$a r1 = com.google.android.gms.tagmanager.bg.a.NOT_AVAILABLE
            r0.a(r1)
        L45:
            return
        L46:
            java.io.FileInputStream r1 = new java.io.FileInputStream     // Catch: java.io.FileNotFoundException -> L70
            java.io.File r0 = r3.oQ()     // Catch: java.io.FileNotFoundException -> L70
            r1.<init>(r0)     // Catch: java.io.FileNotFoundException -> L70
            java.io.ByteArrayOutputStream r0 = new java.io.ByteArrayOutputStream     // Catch: java.io.IOException -> L85 java.lang.IllegalArgumentException -> L9d java.lang.Throwable -> Lb5
            r0.<init>()     // Catch: java.io.IOException -> L85 java.lang.IllegalArgumentException -> L9d java.lang.Throwable -> Lb5
            com.google.android.gms.tagmanager.cr.b(r1, r0)     // Catch: java.io.IOException -> L85 java.lang.IllegalArgumentException -> L9d java.lang.Throwable -> Lb5
            byte[] r0 = r0.toByteArray()     // Catch: java.io.IOException -> L85 java.lang.IllegalArgumentException -> L9d java.lang.Throwable -> Lb5
            com.google.android.gms.internal.ok$a r0 = com.google.android.gms.internal.ok.a.l(r0)     // Catch: java.io.IOException -> L85 java.lang.IllegalArgumentException -> L9d java.lang.Throwable -> Lb5
            r3.d(r0)     // Catch: java.io.IOException -> L85 java.lang.IllegalArgumentException -> L9d java.lang.Throwable -> Lb5
            com.google.android.gms.tagmanager.bg<com.google.android.gms.internal.ok$a> r2 = r3.aqi     // Catch: java.io.IOException -> L85 java.lang.IllegalArgumentException -> L9d java.lang.Throwable -> Lb5
            r2.l(r0)     // Catch: java.io.IOException -> L85 java.lang.IllegalArgumentException -> L9d java.lang.Throwable -> Lb5
            r1.close()     // Catch: java.io.IOException -> L7e
        L6a:
            java.lang.String r0 = "The Disk resource was successfully read."
            com.google.android.gms.tagmanager.bh.V(r0)
            goto L45
        L70:
            r0 = move-exception
            java.lang.String r0 = "Failed to find the resource in the disk"
            com.google.android.gms.tagmanager.bh.S(r0)
            com.google.android.gms.tagmanager.bg<com.google.android.gms.internal.ok$a> r0 = r3.aqi
            com.google.android.gms.tagmanager.bg$a r1 = com.google.android.gms.tagmanager.bg.a.NOT_AVAILABLE
            r0.a(r1)
            goto L45
        L7e:
            r0 = move-exception
            java.lang.String r0 = "Error closing stream for reading resource from disk"
            com.google.android.gms.tagmanager.bh.W(r0)
            goto L6a
        L85:
            r0 = move-exception
            com.google.android.gms.tagmanager.bg<com.google.android.gms.internal.ok$a> r0 = r3.aqi     // Catch: java.lang.Throwable -> Lb5
            com.google.android.gms.tagmanager.bg$a r2 = com.google.android.gms.tagmanager.bg.a.IO_ERROR     // Catch: java.lang.Throwable -> Lb5
            r0.a(r2)     // Catch: java.lang.Throwable -> Lb5
            java.lang.String r0 = "Failed to read the resource from disk"
            com.google.android.gms.tagmanager.bh.W(r0)     // Catch: java.lang.Throwable -> Lb5
            r1.close()     // Catch: java.io.IOException -> L96
            goto L6a
        L96:
            r0 = move-exception
            java.lang.String r0 = "Error closing stream for reading resource from disk"
            com.google.android.gms.tagmanager.bh.W(r0)
            goto L6a
        L9d:
            r0 = move-exception
            com.google.android.gms.tagmanager.bg<com.google.android.gms.internal.ok$a> r0 = r3.aqi     // Catch: java.lang.Throwable -> Lb5
            com.google.android.gms.tagmanager.bg$a r2 = com.google.android.gms.tagmanager.bg.a.IO_ERROR     // Catch: java.lang.Throwable -> Lb5
            r0.a(r2)     // Catch: java.lang.Throwable -> Lb5
            java.lang.String r0 = "Failed to read the resource from disk. The resource is inconsistent"
            com.google.android.gms.tagmanager.bh.W(r0)     // Catch: java.lang.Throwable -> Lb5
            r1.close()     // Catch: java.io.IOException -> Lae
            goto L6a
        Lae:
            r0 = move-exception
            java.lang.String r0 = "Error closing stream for reading resource from disk"
            com.google.android.gms.tagmanager.bh.W(r0)
            goto L6a
        Lb5:
            r0 = move-exception
            r1.close()     // Catch: java.io.IOException -> Lba
        Lb9:
            throw r0
        Lba:
            r1 = move-exception
            java.lang.String r1 = "Error closing stream for reading resource from disk"
            com.google.android.gms.tagmanager.bh.W(r1)
            goto Lb9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.tagmanager.cq.oP():void");
    }

    File oQ() {
        return new File(this.mContext.getDir("google_tagmanager", 0), "resource_" + this.anR);
    }

    @Override // com.google.android.gms.tagmanager.o.f
    public void oa() {
        this.aqp.execute(new Runnable() { // from class: com.google.android.gms.tagmanager.cq.1
            @Override // java.lang.Runnable
            public void run() throws IOException {
                cq.this.oP();
            }
        });
    }

    @Override // com.google.android.gms.common.api.Releasable
    public synchronized void release() {
        this.aqp.shutdown();
    }
}
