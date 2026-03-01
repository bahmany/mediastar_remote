package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.internal.i;
import java.io.IOException;

/* loaded from: classes.dex */
public class j extends i {

    class a {
        private String kO;
        private boolean kP;

        public a(String str, boolean z) {
            this.kO = str;
            this.kP = z;
        }

        public String getId() {
            return this.kO;
        }

        public boolean isLimitAdTrackingEnabled() {
            return this.kP;
        }
    }

    protected j(Context context, m mVar, n nVar) {
        super(context, mVar, nVar);
    }

    public static j a(String str, Context context) {
        e eVar = new e();
        a(str, context, eVar);
        return new j(context, eVar, new p(239));
    }

    @Override // com.google.android.gms.internal.i, com.google.android.gms.internal.h
    protected void b(Context context) throws IllegalStateException {
        super.b(context);
        try {
            try {
                a aVarH = h(context);
                a(28, aVarH.isLimitAdTrackingEnabled() ? 1L : 0L);
                String id = aVarH.getId();
                if (id != null) {
                    a(26, 5L);
                    a(24, id);
                }
            } catch (GooglePlayServicesNotAvailableException e) {
                a(24, d(context));
            }
        } catch (i.a e2) {
        } catch (IOException e3) {
        }
    }

    a h(Context context) throws IllegalStateException, GooglePlayServicesNotAvailableException, IOException {
        String strA;
        int i = 0;
        try {
            AdvertisingIdClient.Info advertisingIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
            String id = advertisingIdInfo.getId();
            if (id == null || !id.matches("^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$")) {
                strA = id;
            } else {
                byte[] bArr = new byte[16];
                int i2 = 0;
                while (i < id.length()) {
                    if (i == 8 || i == 13 || i == 18 || i == 23) {
                        i++;
                    }
                    bArr[i2] = (byte) ((Character.digit(id.charAt(i), 16) << 4) + Character.digit(id.charAt(i + 1), 16));
                    i2++;
                    i += 2;
                }
                strA = this.ky.a(bArr, true);
            }
            return new a(strA, advertisingIdInfo.isLimitAdTrackingEnabled());
        } catch (GooglePlayServicesRepairableException e) {
            throw new IOException(e);
        } catch (SecurityException e2) {
            throw new IOException(e2);
        }
    }
}
