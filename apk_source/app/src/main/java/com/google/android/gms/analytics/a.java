package com.google.android.gms.analytics;

import android.content.Context;
import android.text.TextUtils;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/* loaded from: classes.dex */
class a implements l {
    private static a xA;
    private static Object xz = new Object();
    private Context mContext;
    private AdvertisingIdClient.Info xB;
    private long xC;
    private String xD;
    private boolean xE = false;
    private Object xF = new Object();

    a(Context context) {
        this.mContext = context.getApplicationContext();
    }

    private boolean a(AdvertisingIdClient.Info info, AdvertisingIdClient.Info info2) {
        String strDS;
        String id = info2 == null ? null : info2.getId();
        if (TextUtils.isEmpty(id)) {
            return true;
        }
        h.y(this.mContext);
        h hVarDR = h.dR();
        String value = hVarDR.getValue("&cid");
        synchronized (this.xF) {
            if (!this.xE) {
                this.xD = x(this.mContext);
                this.xE = true;
            } else if (TextUtils.isEmpty(this.xD)) {
                String id2 = info != null ? info.getId() : null;
                if (id2 == null) {
                    return ab(id + value);
                }
                this.xD = aa(id2 + value);
            }
            String strAa = aa(id + value);
            if (TextUtils.isEmpty(strAa)) {
                return false;
            }
            if (strAa.equals(this.xD)) {
                return true;
            }
            if (TextUtils.isEmpty(this.xD)) {
                strDS = value;
            } else {
                z.V("Resetting the client id because Advertising Id changed.");
                strDS = hVarDR.dS();
                z.V("New client Id: " + strDS);
            }
            return ab(id + strDS);
        }
    }

    static String aa(String str) throws NoSuchAlgorithmException {
        MessageDigest messageDigestAp = aj.ap("MD5");
        if (messageDigestAp == null) {
            return null;
        }
        return String.format(Locale.US, "%032X", new BigInteger(1, messageDigestAp.digest(str.getBytes())));
    }

    private boolean ab(String str) throws NoSuchAlgorithmException, IOException {
        try {
            String strAa = aa(str);
            z.V("Storing hashed adid.");
            FileOutputStream fileOutputStreamOpenFileOutput = this.mContext.openFileOutput("gaClientIdData", 0);
            fileOutputStreamOpenFileOutput.write(strAa.getBytes());
            fileOutputStreamOpenFileOutput.close();
            this.xD = strAa;
            return true;
        } catch (FileNotFoundException e) {
            z.T("Error creating hash file.");
            return false;
        } catch (IOException e2) {
            z.T("Error writing to hash file.");
            return false;
        }
    }

    public static l w(Context context) {
        if (xA == null) {
            synchronized (xz) {
                if (xA == null) {
                    xA = new a(context);
                }
            }
        }
        return xA;
    }

    static String x(Context context) throws IOException {
        String str = null;
        try {
            FileInputStream fileInputStreamOpenFileInput = context.openFileInput("gaClientIdData");
            byte[] bArr = new byte[128];
            int i = fileInputStreamOpenFileInput.read(bArr, 0, 128);
            if (fileInputStreamOpenFileInput.available() > 0) {
                z.W("Hash file seems corrupted, deleting it.");
                fileInputStreamOpenFileInput.close();
                context.deleteFile("gaClientIdData");
            } else if (i <= 0) {
                z.U("Hash file is empty.");
                fileInputStreamOpenFileInput.close();
            } else {
                String str2 = new String(bArr, 0, i);
                try {
                    fileInputStreamOpenFileInput.close();
                    str = str2;
                } catch (FileNotFoundException e) {
                    str = str2;
                } catch (IOException e2) {
                    str = str2;
                    z.W("Error reading Hash file, deleting it.");
                    context.deleteFile("gaClientIdData");
                    return str;
                }
            }
        } catch (FileNotFoundException e3) {
        } catch (IOException e4) {
        }
        return str;
    }

    AdvertisingIdClient.Info dH() {
        try {
            return AdvertisingIdClient.getAdvertisingIdInfo(this.mContext);
        } catch (GooglePlayServicesNotAvailableException e) {
            z.W("GooglePlayServicesNotAvailableException getting Ad Id Info");
            return null;
        } catch (GooglePlayServicesRepairableException e2) {
            z.W("GooglePlayServicesRepairableException getting Ad Id Info");
            return null;
        } catch (IOException e3) {
            z.W("IOException getting Ad Id Info");
            return null;
        } catch (IllegalStateException e4) {
            z.W("IllegalStateException getting Ad Id Info. If you would like to see Audience reports, please ensure that you have added '<meta-data android:name=\"com.google.android.gms.version\" android:value=\"@integer/google_play_services_version\" />' to your application manifest file. See http://goo.gl/naFqQk for details.");
            return null;
        } catch (Throwable th) {
            z.W("Unknown exception. Could not get the ad Id.");
            return null;
        }
    }

    @Override // com.google.android.gms.analytics.l
    public String getValue(String field) {
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (jCurrentTimeMillis - this.xC > 1000) {
            AdvertisingIdClient.Info infoDH = dH();
            if (a(this.xB, infoDH)) {
                this.xB = infoDH;
            } else {
                this.xB = new AdvertisingIdClient.Info("", false);
            }
            this.xC = jCurrentTimeMillis;
        }
        if (this.xB != null) {
            if ("&adid".equals(field)) {
                return this.xB.getId();
            }
            if ("&ate".equals(field)) {
                return this.xB.isLimitAdTrackingEnabled() ? "0" : "1";
            }
        }
        return null;
    }
}
