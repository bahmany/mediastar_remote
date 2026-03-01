package org.teleal.cling.model.types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.teleal.cling.model.Constants;

/* loaded from: classes.dex */
public class ServiceId {
    public static final Pattern PATTERN = Pattern.compile("urn:([a-zA-Z0-9\\-\\.]+):serviceId:([a-zA-Z_0-9\\-:\\.]{1,64})");
    private String id;
    private String namespace;

    public ServiceId(String namespace, String id) {
        if (namespace != null && !namespace.matches(Constants.REGEX_NAMESPACE)) {
            throw new IllegalArgumentException("Service ID namespace contains illegal characters");
        }
        this.namespace = namespace;
        if (id != null && !id.matches(Constants.REGEX_ID)) {
            throw new IllegalArgumentException("Service ID suffix too long (64) or contains illegal characters");
        }
        this.id = id;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getId() {
        return this.id;
    }

    public static ServiceId valueOf(String s) throws InvalidValueException {
        ServiceId serviceId = null;
        try {
            serviceId = UDAServiceId.m9valueOf(s);
        } catch (Exception e) {
        }
        if (serviceId == null) {
            Matcher matcher = PATTERN.matcher(s);
            if (matcher.matches()) {
                return new ServiceId(matcher.group(1), matcher.group(2));
            }
            throw new InvalidValueException("Can't parse Service ID string (namespace/id): " + s);
        }
        return serviceId;
    }

    public String toString() {
        return "urn:" + getNamespace() + ":serviceId:" + getId();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof ServiceId)) {
            return false;
        }
        ServiceId serviceId = (ServiceId) o;
        return this.id.equals(serviceId.id) && this.namespace.equals(serviceId.namespace);
    }

    public int hashCode() {
        int result = this.namespace.hashCode();
        return (result * 31) + this.id.hashCode();
    }
}
