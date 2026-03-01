package org.teleal.cling.model.profile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.teleal.cling.model.meta.DeviceDetails;

/* loaded from: classes.dex */
public class HeaderDeviceDetailsProvider implements DeviceDetailsProvider {
    private final DeviceDetails defaultDeviceDetails;
    private final Map<Key, DeviceDetails> headerDetails;

    public static class Key {
        final String headerName;
        final Pattern pattern;
        final String valuePattern;

        public Key(String headerName, String valuePattern) {
            this.headerName = headerName;
            this.valuePattern = valuePattern;
            this.pattern = Pattern.compile(valuePattern, 2);
        }

        public String getHeaderName() {
            return this.headerName;
        }

        public String getValuePattern() {
            return this.valuePattern;
        }

        public boolean isValuePatternMatch(String value) {
            return this.pattern.matcher(value).matches();
        }
    }

    public HeaderDeviceDetailsProvider(DeviceDetails defaultDeviceDetails) {
        this(defaultDeviceDetails, null);
    }

    public HeaderDeviceDetailsProvider(DeviceDetails defaultDeviceDetails, Map<Key, DeviceDetails> headerDetails) {
        this.defaultDeviceDetails = defaultDeviceDetails;
        this.headerDetails = headerDetails == null ? new HashMap<>() : headerDetails;
    }

    public DeviceDetails getDefaultDeviceDetails() {
        return this.defaultDeviceDetails;
    }

    public Map<Key, DeviceDetails> getHeaderDetails() {
        return this.headerDetails;
    }

    @Override // org.teleal.cling.model.profile.DeviceDetailsProvider
    public DeviceDetails provide(ControlPointInfo info) {
        if (info == null || info.getHeaders().isEmpty()) {
            return getDefaultDeviceDetails();
        }
        for (Key key : getHeaderDetails().keySet()) {
            List<String> headerValues = info.getHeaders().get(key.getHeaderName());
            if (headerValues != null) {
                for (String headerValue : headerValues) {
                    if (key.isValuePatternMatch(headerValue)) {
                        return getHeaderDetails().get(key);
                    }
                }
            }
        }
        return getDefaultDeviceDetails();
    }
}
