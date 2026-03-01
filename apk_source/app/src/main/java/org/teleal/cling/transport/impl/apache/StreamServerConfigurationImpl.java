package org.teleal.cling.transport.impl.apache;

import org.teleal.cling.transport.spi.StreamServerConfiguration;

/* loaded from: classes.dex */
public class StreamServerConfigurationImpl implements StreamServerConfiguration {
    private int bufferSizeKilobytes;
    private int dataWaitTimeoutSeconds;
    private int listenPort;
    private boolean staleConnectionCheck;
    private int tcpConnectionBacklog;
    private boolean tcpNoDelay;

    public StreamServerConfigurationImpl() {
        this.listenPort = 0;
        this.dataWaitTimeoutSeconds = 5;
        this.bufferSizeKilobytes = 8;
        this.staleConnectionCheck = true;
        this.tcpNoDelay = true;
        this.tcpConnectionBacklog = 0;
    }

    public StreamServerConfigurationImpl(int listenPort) {
        this.listenPort = 0;
        this.dataWaitTimeoutSeconds = 5;
        this.bufferSizeKilobytes = 8;
        this.staleConnectionCheck = true;
        this.tcpNoDelay = true;
        this.tcpConnectionBacklog = 0;
        this.listenPort = listenPort;
    }

    @Override // org.teleal.cling.transport.spi.StreamServerConfiguration
    public int getListenPort() {
        return this.listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    public int getDataWaitTimeoutSeconds() {
        return this.dataWaitTimeoutSeconds;
    }

    public void setDataWaitTimeoutSeconds(int dataWaitTimeoutSeconds) {
        this.dataWaitTimeoutSeconds = dataWaitTimeoutSeconds;
    }

    public int getBufferSizeKilobytes() {
        return this.bufferSizeKilobytes;
    }

    public void setBufferSizeKilobytes(int bufferSizeKilobytes) {
        this.bufferSizeKilobytes = bufferSizeKilobytes;
    }

    public boolean isStaleConnectionCheck() {
        return this.staleConnectionCheck;
    }

    public void setStaleConnectionCheck(boolean staleConnectionCheck) {
        this.staleConnectionCheck = staleConnectionCheck;
    }

    public boolean isTcpNoDelay() {
        return this.tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public int getTcpConnectionBacklog() {
        return this.tcpConnectionBacklog;
    }

    public void setTcpConnectionBacklog(int tcpConnectionBacklog) {
        this.tcpConnectionBacklog = tcpConnectionBacklog;
    }
}
