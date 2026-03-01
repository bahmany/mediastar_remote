package org.teleal.cling.model.types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.teleal.cling.model.Constants;

/* loaded from: classes.dex */
public class ServiceType {
    public static final Pattern PATTERN = Pattern.compile("urn:([a-zA-Z0-9\\-\\.]+):service:([a-zA-Z_0-9\\-]{1,64}):([0-9]+).*");
    private String namespace;
    private String type;
    private int version;

    public ServiceType(String namespace, String type) {
        this(namespace, type, 1);
    }

    public ServiceType(String namespace, String type, int version) {
        this.version = 1;
        if (namespace != null && !namespace.matches(Constants.REGEX_NAMESPACE)) {
            throw new IllegalArgumentException("Service type namespace contains illegal characters");
        }
        this.namespace = namespace;
        if (type != null && !type.matches(Constants.REGEX_TYPE)) {
            throw new IllegalArgumentException("Service type suffix too long (64) or contains illegal characters");
        }
        this.type = type;
        this.version = version;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getType() {
        return this.type;
    }

    public int getVersion() {
        return this.version;
    }

    public static ServiceType valueOf(String s) throws InvalidValueException {
        ServiceType serviceType = null;
        String s2 = s.replaceAll("\\s", "");
        try {
            serviceType = UDAServiceType.m10valueOf(s2);
        } catch (Exception e) {
        }
        if (serviceType == null) {
            Matcher matcher = PATTERN.matcher(s2);
            if (matcher.matches()) {
                return new ServiceType(matcher.group(1), matcher.group(2), Integer.valueOf(matcher.group(3)).intValue());
            }
            throw new InvalidValueException("Can't parse service type string (namespace/type/version): " + s2);
        }
        return serviceType;
    }

    public boolean implementsVersion(ServiceType that) {
        return that != null && this.namespace.equals(that.namespace) && this.type.equals(that.type) && this.version >= that.version;
    }

    public String toFriendlyString() {
        return String.valueOf(getNamespace()) + ":" + getType() + ":" + getVersion();
    }

    public String toString() {
        return "urn:" + getNamespace() + ":service:" + getType() + ":" + getVersion();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof ServiceType)) {
            return false;
        }
        ServiceType that = (ServiceType) o;
        return this.version == that.version && this.namespace.equals(that.namespace) && this.type.equals(that.type);
    }

    public int hashCode() {
        int result = this.namespace.hashCode();
        return (((result * 31) + this.type.hashCode()) * 31) + this.version;
    }
}
