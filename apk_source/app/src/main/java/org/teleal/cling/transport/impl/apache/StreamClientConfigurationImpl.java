package org.teleal.cling.transport.impl.apache;

import org.teleal.cling.model.ServerClientTokens;
import org.teleal.cling.transport.spi.StreamClientConfiguration;

/* loaded from: classes.dex */
public class StreamClientConfigurationImpl implements StreamClientConfiguration {
    private int maxTotalConnections = 1024;
    private int connectionTimeoutSeconds = 5;
    private int dataReadTimeoutSeconds = 5;
    private String contentCharset = "UTF-8";

    public int getMaxTotalConnections() {
        return this.maxTotalConnections;
    }

    public void setMaxTotalConnections(int maxTotalConnections) {
        this.maxTotalConnections = maxTotalConnections;
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

    public String getContentCharset() {
        return this.contentCharset;
    }

    public void setContentCharset(String contentCharset) {
        this.contentCharset = contentCharset;
    }

    @Override // org.teleal.cling.transport.spi.StreamClientConfiguration
    public String getUserAgentValue(int majorVersion, int minorVersion) {
        return new ServerClientTokens(majorVersion, minorVersion).toString();
    }

    public int getSocketBufferSize() {
        return -1;
    }

    public boolean getStaleCheckingEnabled() {
        return true;
    }

    public int getRequestRetryCount() {
        return -1;
    }
}
