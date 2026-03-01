package org.apache.mina.transport.socket;

/* loaded from: classes.dex */
public class DefaultDatagramSessionConfig extends AbstractDatagramSessionConfig {
    private static boolean DEFAULT_BROADCAST = false;
    private static boolean DEFAULT_REUSE_ADDRESS = false;
    private static int DEFAULT_RECEIVE_BUFFER_SIZE = -1;
    private static int DEFAULT_SEND_BUFFER_SIZE = -1;
    private static int DEFAULT_TRAFFIC_CLASS = 0;
    private boolean broadcast = DEFAULT_BROADCAST;
    private boolean reuseAddress = DEFAULT_REUSE_ADDRESS;
    private int receiveBufferSize = DEFAULT_RECEIVE_BUFFER_SIZE;
    private int sendBufferSize = DEFAULT_SEND_BUFFER_SIZE;
    private int trafficClass = DEFAULT_TRAFFIC_CLASS;

    @Override // org.apache.mina.transport.socket.DatagramSessionConfig
    public boolean isBroadcast() {
        return this.broadcast;
    }

    @Override // org.apache.mina.transport.socket.DatagramSessionConfig
    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }

    @Override // org.apache.mina.transport.socket.DatagramSessionConfig
    public boolean isReuseAddress() {
        return this.reuseAddress;
    }

    @Override // org.apache.mina.transport.socket.DatagramSessionConfig
    public void setReuseAddress(boolean reuseAddress) {
        this.reuseAddress = reuseAddress;
    }

    @Override // org.apache.mina.transport.socket.DatagramSessionConfig
    public int getReceiveBufferSize() {
        return this.receiveBufferSize;
    }

    @Override // org.apache.mina.transport.socket.DatagramSessionConfig
    public void setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
    }

    @Override // org.apache.mina.transport.socket.DatagramSessionConfig
    public int getSendBufferSize() {
        return this.sendBufferSize;
    }

    @Override // org.apache.mina.transport.socket.DatagramSessionConfig
    public void setSendBufferSize(int sendBufferSize) {
        this.sendBufferSize = sendBufferSize;
    }

    @Override // org.apache.mina.transport.socket.DatagramSessionConfig
    public int getTrafficClass() {
        return this.trafficClass;
    }

    @Override // org.apache.mina.transport.socket.DatagramSessionConfig
    public void setTrafficClass(int trafficClass) {
        this.trafficClass = trafficClass;
    }

    @Override // org.apache.mina.transport.socket.AbstractDatagramSessionConfig
    protected boolean isBroadcastChanged() {
        return this.broadcast != DEFAULT_BROADCAST;
    }

    @Override // org.apache.mina.transport.socket.AbstractDatagramSessionConfig
    protected boolean isReceiveBufferSizeChanged() {
        return this.receiveBufferSize != DEFAULT_RECEIVE_BUFFER_SIZE;
    }

    @Override // org.apache.mina.transport.socket.AbstractDatagramSessionConfig
    protected boolean isReuseAddressChanged() {
        return this.reuseAddress != DEFAULT_REUSE_ADDRESS;
    }

    @Override // org.apache.mina.transport.socket.AbstractDatagramSessionConfig
    protected boolean isSendBufferSizeChanged() {
        return this.sendBufferSize != DEFAULT_SEND_BUFFER_SIZE;
    }

    @Override // org.apache.mina.transport.socket.AbstractDatagramSessionConfig
    protected boolean isTrafficClassChanged() {
        return this.trafficClass != DEFAULT_TRAFFIC_CLASS;
    }
}
