package org.teleal.cling.transport.impl;

import org.teleal.cling.model.ServerClientTokens;
import org.teleal.cling.transport.spi.StreamClientConfiguration;

/* loaded from: classes.dex */
public class StreamClientConfigurationImpl implements StreamClientConfiguration {
    private boolean usePersistentConnections = false;
    private int connectionTimeoutSeconds = 5;
    private int dataReadTimeoutSeconds = 5;

    public boolean isUsePersistentConnections() {
        return this.usePersistentConnections;
    }

    public void setUsePersistentConnections(boolean usePersistentConnections) {
        this.usePersistentConnections = usePersistentConnections;
    }

    public int getConnectionTimeoutSeconds() {
        return this.connectionTimeoutSeconds;
    }

    public void setConnectionTimeoutSeconds(int connectionTimeoutSeconds) {
        this.connectionTimeoutSeconds = connectionTimeoutSeconds;
    }

    public int getDataReadTimeoutSeconds() {
        return this.dataReadTimeoutSeconds;
    }

    public void setDataReadTimeoutSeconds(int dataReadTimeoutSeconds) {
        this.dataReadTimeoutSeconds = dataReadTimeoutSeconds;
    }

    @Override // org.teleal.cling.transport.spi.StreamClientConfiguration
    public String getUserAgentValue(int majorVersion, int minorVersion) {
        return new ServerClientTokens(majorVersion, minorVersion).toString();
    }
}
