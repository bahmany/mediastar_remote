package org.teleal.cling.model.meta;

import org.teleal.cling.model.types.UDN;

/* loaded from: classes.dex */
public class DeviceIdentity {
    private final Integer maxAgeSeconds;
    private final UDN udn;

    public DeviceIdentity(UDN udn, DeviceIdentity template) {
        this.udn = udn;
        this.maxAgeSeconds = template.getMaxAgeSeconds();
    }

    public DeviceIdentity(UDN udn) {
        this.udn = udn;
        this.maxAgeSeconds = 1800;
    }

    public DeviceIdentity(UDN udn, Integer maxAgeSeconds) {
        this.udn = udn;
        this.maxAgeSeconds = maxAgeSeconds;
    }

    public UDN getUdn() {
        return this.udn;
    }

    public Integer getMaxAgeSeconds() {
        return this.maxAgeSeconds;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeviceIdentity that = (DeviceIdentity) o;
        return this.udn.equals(that.udn);
    }

    public int hashCode() {
        return this.udn.hashCode();
    }

    public String toString() {
        return "(" + getClass().getSimpleName() + ") UDN: " + getUdn();
    }
}
