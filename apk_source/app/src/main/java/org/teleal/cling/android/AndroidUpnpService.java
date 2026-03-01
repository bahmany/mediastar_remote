package org.teleal.cling.android;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceConfiguration;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.registry.Registry;

/* loaded from: classes.dex */
public interface AndroidUpnpService {
    UpnpService get();

    UpnpServiceConfiguration getConfiguration();

    ControlPoint getControlPoint();

    Registry getRegistry();

    boolean isInitialized();

    void refreshAfterConfig();
}
