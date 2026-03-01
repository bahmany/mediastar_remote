package com.google.android.gms.cast;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.images.WebImage;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.internal.ik;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class CastDevice implements SafeParcelable {
    public static final Parcelable.Creator<CastDevice> CREATOR = new b();
    private final int BR;
    private String ER;
    String ES;
    private Inet4Address ET;
    private String EU;
    private String EV;
    private String EW;
    private int EX;
    private List<WebImage> EY;
    private int EZ;
    private int Fa;

    private CastDevice() {
        this(3, null, null, null, null, null, -1, new ArrayList(), 0, -1);
    }

    CastDevice(int versionCode, String deviceId, String hostAddress, String friendlyName, String modelName, String deviceVersion, int servicePort, List<WebImage> icons, int capabilities, int status) throws UnknownHostException {
        this.BR = versionCode;
        this.ER = deviceId;
        this.ES = hostAddress;
        if (this.ES != null) {
            try {
                InetAddress byName = InetAddress.getByName(this.ES);
                if (byName instanceof Inet4Address) {
                    this.ET = (Inet4Address) byName;
                }
            } catch (UnknownHostException e) {
                this.ET = null;
            }
        }
        this.EU = friendlyName;
        this.EV = modelName;
        this.EW = deviceVersion;
        this.EX = servicePort;
        this.EY = icons;
        this.EZ = capabilities;
        this.Fa = status;
    }

    public static CastDevice getFromBundle(Bundle extras) {
        if (extras == null) {
            return null;
        }
        extras.setClassLoader(CastDevice.class.getClassLoader());
        return (CastDevice) extras.getParcelable("com.google.android.gms.cast.EXTRA_CAST_DEVICE");
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CastDevice)) {
            return false;
        }
        CastDevice castDevice = (CastDevice) obj;
        return getDeviceId() == null ? castDevice.getDeviceId() == null : ik.a(this.ER, castDevice.ER) && ik.a(this.ET, castDevice.ET) && ik.a(this.EV, castDevice.EV) && ik.a(this.EU, castDevice.EU) && ik.a(this.EW, castDevice.EW) && this.EX == castDevice.EX && ik.a(this.EY, castDevice.EY) && this.EZ == castDevice.EZ && this.Fa == castDevice.Fa;
    }

    public int getCapabilities() {
        return this.EZ;
    }

    public String getDeviceId() {
        return this.ER;
    }

    public String getDeviceVersion() {
        return this.EW;
    }

    public String getFriendlyName() {
        return this.EU;
    }

    /* JADX WARN: Removed duplicated region for block: B:75:0x0072  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.google.android.gms.common.images.WebImage getIcon(int r10, int r11) {
        /*
            r9 = this;
            r7 = 0
            r1 = 0
            java.util.List<com.google.android.gms.common.images.WebImage> r0 = r9.EY
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto Lb
        La:
            return r1
        Lb:
            if (r10 <= 0) goto Lf
            if (r11 > 0) goto L19
        Lf:
            java.util.List<com.google.android.gms.common.images.WebImage> r0 = r9.EY
            java.lang.Object r0 = r0.get(r7)
            com.google.android.gms.common.images.WebImage r0 = (com.google.android.gms.common.images.WebImage) r0
            r1 = r0
            goto La
        L19:
            java.util.List<com.google.android.gms.common.images.WebImage> r0 = r9.EY
            java.util.Iterator r3 = r0.iterator()
            r2 = r1
        L20:
            boolean r0 = r3.hasNext()
            if (r0 == 0) goto L60
            java.lang.Object r0 = r3.next()
            com.google.android.gms.common.images.WebImage r0 = (com.google.android.gms.common.images.WebImage) r0
            int r4 = r0.getWidth()
            int r5 = r0.getHeight()
            if (r4 < r10) goto L4c
            if (r5 < r11) goto L4c
            if (r2 == 0) goto L46
            int r6 = r2.getWidth()
            if (r6 <= r4) goto L72
            int r4 = r2.getHeight()
            if (r4 <= r5) goto L72
        L46:
            r8 = r1
            r1 = r0
            r0 = r8
        L49:
            r2 = r1
            r1 = r0
            goto L20
        L4c:
            if (r4 >= r10) goto L72
            if (r5 >= r11) goto L72
            if (r1 == 0) goto L5e
            int r6 = r1.getWidth()
            if (r6 >= r4) goto L72
            int r4 = r1.getHeight()
            if (r4 >= r5) goto L72
        L5e:
            r1 = r2
            goto L49
        L60:
            if (r2 == 0) goto L64
        L62:
            r1 = r2
            goto La
        L64:
            if (r1 == 0) goto L68
            r2 = r1
            goto L62
        L68:
            java.util.List<com.google.android.gms.common.images.WebImage> r0 = r9.EY
            java.lang.Object r0 = r0.get(r7)
            com.google.android.gms.common.images.WebImage r0 = (com.google.android.gms.common.images.WebImage) r0
            r2 = r0
            goto L62
        L72:
            r0 = r1
            r1 = r2
            goto L49
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.cast.CastDevice.getIcon(int, int):com.google.android.gms.common.images.WebImage");
    }

    public List<WebImage> getIcons() {
        return Collections.unmodifiableList(this.EY);
    }

    public Inet4Address getIpAddress() {
        return this.ET;
    }

    public String getModelName() {
        return this.EV;
    }

    public int getServicePort() {
        return this.EX;
    }

    public int getStatus() {
        return this.Fa;
    }

    int getVersionCode() {
        return this.BR;
    }

    public boolean hasIcons() {
        return !this.EY.isEmpty();
    }

    public int hashCode() {
        if (this.ER == null) {
            return 0;
        }
        return this.ER.hashCode();
    }

    public boolean isSameDevice(CastDevice castDevice) {
        if (castDevice == null) {
            return false;
        }
        return getDeviceId() == null ? castDevice.getDeviceId() == null : ik.a(getDeviceId(), castDevice.getDeviceId());
    }

    public void putInBundle(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        bundle.putParcelable("com.google.android.gms.cast.EXTRA_CAST_DEVICE", this);
    }

    public String toString() {
        return String.format("\"%s\" (%s)", this.EU, this.ER);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        b.a(this, out, flags);
    }
}
