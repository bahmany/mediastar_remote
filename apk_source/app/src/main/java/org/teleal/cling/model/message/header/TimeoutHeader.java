package org.teleal.cling.model.message.header;

import android.support.v7.internal.widget.ActivityChooserView;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cybergarage.upnp.event.Subscription;

/* loaded from: classes.dex */
public class TimeoutHeader extends UpnpHeader<Integer> {
    public static final Integer INFINITE_VALUE = Integer.valueOf(ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
    public static final Pattern PATTERN = Pattern.compile("Second-(?:([0-9]+)|infinite)");

    public TimeoutHeader() {
        setValue(1800);
    }

    public TimeoutHeader(int timeoutSeconds) {
        setValue(Integer.valueOf(timeoutSeconds));
    }

    public TimeoutHeader(Integer timeoutSeconds) {
        setValue(timeoutSeconds);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String s) throws InvalidHeaderException {
        Matcher matcher = PATTERN.matcher(s);
        if (!matcher.matches()) {
            throw new InvalidHeaderException("Can't parse timeout seconds integer from: " + s);
        }
        if (matcher.group(1) != null) {
            setValue(Integer.valueOf(Integer.parseInt(matcher.group(1))));
        } else {
            setValue(INFINITE_VALUE);
        }
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public String getString() {
        return Subscription.TIMEOUT_HEADER + (getValue().equals(INFINITE_VALUE) ? Subscription.INFINITE_STRING : getValue());
    }
}
