package org.teleal.cling.model;

import java.net.URI;
import java.net.URL;
import org.teleal.common.util.URIUtil;

/* loaded from: classes.dex */
public class Location {
    protected NetworkAddress networkAddress;
    protected URI path;

    public Location(NetworkAddress networkAddress, URI path) {
        this.networkAddress = networkAddress;
        this.path = path;
    }

    public NetworkAddress getNetworkAddress() {
        return this.networkAddress;
    }

    public URI getPath() {
        return this.path;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Location location = (Location) o;
        return this.networkAddress.equals(location.networkAddress) && this.path.equals(location.path);
    }

    public int hashCode() {
        int result = this.networkAddress.hashCode();
        return (result * 31) + this.path.hashCode();
    }

    public URL getURL() {
        return URIUtil.createAbsoluteURL(this.networkAddress.getAddress(), this.networkAddress.getPort(), this.path);
    }
}
