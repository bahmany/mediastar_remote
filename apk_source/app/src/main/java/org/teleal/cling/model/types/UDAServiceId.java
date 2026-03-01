package org.teleal.cling.model.types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class UDAServiceId extends ServiceId {
    public static final String BROKEN_DEFAULT_NAMESPACE = "schemas-upnp-org";
    public static final String DEFAULT_NAMESPACE = "upnp-org";
    public static final Pattern PATTERN = Pattern.compile("urn:upnp-org:serviceId:([a-zA-Z_0-9\\-:\\.]{1,64})");
    public static final Pattern BROKEN_PATTERN = Pattern.compile("urn:schemas-upnp-org:service:([a-zA-Z_0-9\\-:\\.]{1,64})");

    public UDAServiceId(String id) {
        super(DEFAULT_NAMESPACE, id);
    }

    /* renamed from: valueOf, reason: collision with other method in class */
    public static UDAServiceId m9valueOf(String s) throws InvalidValueException {
        Matcher matcher = PATTERN.matcher(s);
        if (matcher.matches()) {
            return new UDAServiceId(matcher.group(1));
        }
        Matcher matcher2 = BROKEN_PATTERN.matcher(s);
        if (matcher2.matches()) {
            return new UDAServiceId(matcher2.group(1));
        }
        throw new InvalidValueException("Can't parse UDA service ID string (upnp-org/id): " + s);
    }
}
