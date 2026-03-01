package org.teleal.cling.model;

import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.UDN;

/* loaded from: classes.dex */
public class ServiceReference {
    public static final String DELIMITER = "/";
    private final ServiceId serviceId;
    private final UDN udn;

    public ServiceReference(String s) {
        String[] split = s.split(DELIMITER);
        if (split.length == 2) {
            this.udn = UDN.valueOf(split[0]);
            this.serviceId = ServiceId.valueOf(split[1]);
        } else {
            this.udn = null;
            this.serviceId = null;
        }
    }

    public ServiceReference(UDN udn, ServiceId serviceId) {
        this.udn = udn;
        this.serviceId = serviceId;
    }

    public UDN getUdn() {
        return this.udn;
    }

    public ServiceId getServiceId() {
        return this.serviceId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServiceReference that = (ServiceReference) o;
        return this.serviceId.equals(that.serviceId) && this.udn.equals(that.udn);
    }

    public int hashCode() {
        int result = this.udn.hashCode();
        return (result * 31) + this.serviceId.hashCode();
    }

    public String toString() {
        return (this.udn == null || this.serviceId == null) ? "" : String.valueOf(this.udn.toString()) + DELIMITER + this.serviceId.toString();
    }
}
