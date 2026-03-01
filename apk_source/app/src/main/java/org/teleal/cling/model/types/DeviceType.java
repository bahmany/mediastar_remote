package org.teleal.cling.model.types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.teleal.cling.model.Constants;

/* loaded from: classes.dex */
public class DeviceType {
    public static final Pattern PATTERN = Pattern.compile("urn:([a-zA-Z0-9\\-\\.]+):device:([a-zA-Z_0-9\\-]{1,64}):([0-9]+).*");
    private String namespace;
    private String type;
    private int version;

    public DeviceType(String namespace, String type) {
        this(namespace, type, 1);
    }

    public DeviceType(String namespace, String type, int version) {
        this.version = 1;
        if (namespace != null && !namespace.matches(Constants.REGEX_NAMESPACE)) {
            throw new IllegalArgumentException("Device type namespace contains illegal characters");
        }
        this.namespace = namespace;
        if (type != null && !type.matches(Constants.REGEX_TYPE)) {
            throw new IllegalArgumentException("Device type suffix too long (64) or contains illegal characters");
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

    public static DeviceType valueOf(String s) throws InvalidValueException {
        DeviceType deviceType = null;
        String s2 = s.replaceAll("\\s", "");
        try {
            deviceType = UDADeviceType.m8valueOf(s2);
        } catch (Exception e) {
        }
        if (deviceType == null) {
            Matcher matcher = PATTERN.matcher(s2);
            if (matcher.matches()) {
                return new DeviceType(matcher.group(1), matcher.group(2), Integer.valueOf(matcher.group(3)).intValue());
            }
            throw new InvalidValueException("Can't parse device type string (namespace/type/version): " + s2);
        }
        return deviceType;
    }

    public boolean implementsVersion(DeviceType that) {
        return this.namespace.equals(that.namespace) && this.type.equals(that.type) && this.version >= that.version;
    }

    public String getDisplayString() {
        return getType();
    }

    public String toString() {
        return "urn:" + getNamespace() + ":device:" + getType() + ":" + getVersion();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof DeviceType)) {
            return false;
        }
        DeviceType that = (DeviceType) o;
        return this.version == that.version && this.namespace.equals(that.namespace) && this.type.equals(that.type);
    }

    public int hashCode() {
        int result = this.namespace.hashCode();
        return (((result * 31) + this.type.hashCode()) * 31) + this.version;
    }
}
