package org.teleal.cling.support.model;

import android.graphics.drawable.Drawable;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.types.UDN;

/* loaded from: classes.dex */
public class DeviceItem {
    private Device device;
    private Drawable icon;
    private String[] label;
    private UDN udn;

    public DeviceItem(Device device) {
        this.udn = device.getIdentity().getUdn();
        this.device = device;
    }

    public DeviceItem(Device device, String... label) {
        this.udn = device.getIdentity().getUdn();
        this.device = device;
        this.label = label;
    }

    public UDN getUdn() {
        return this.udn;
    }

    public Device getDevice() {
        return this.device;
    }

    public String[] getLabel() {
        return this.label;
    }

    public void setLabel(String[] label) {
        this.label = label;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeviceItem that = (DeviceItem) o;
        return this.udn.equals(that.udn);
    }

    public int hashCode() {
        return this.udn.hashCode();
    }

    public String toString() {
        String display;
        if (this.device.getDetails().getFriendlyName() != null) {
            display = this.device.getDetails().getFriendlyName();
        } else {
            display = this.device.getDisplayString();
        }
        return this.device.isFullyHydrated() ? display : String.valueOf(display) + " *";
    }
}
