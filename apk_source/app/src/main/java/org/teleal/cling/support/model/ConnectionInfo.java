package org.teleal.cling.support.model;

import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public class ConnectionInfo {
    protected final int avTransportID;
    protected final int connectionID;
    protected Status connectionStatus;
    protected final Direction direction;
    protected final int peerConnectionID;
    protected final ServiceReference peerConnectionManager;
    protected final ProtocolInfo protocolInfo;
    protected final int rcsID;

    public enum Status {
        OK,
        ContentFormatMismatch,
        InsufficientBandwidth,
        UnreliableChannel,
        Unknown;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static Status[] valuesCustom() {
            Status[] statusArrValuesCustom = values();
            int length = statusArrValuesCustom.length;
            Status[] statusArr = new Status[length];
            System.arraycopy(statusArrValuesCustom, 0, statusArr, 0, length);
            return statusArr;
        }
    }

    public enum Direction {
        Output,
        Input;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static Direction[] valuesCustom() {
            Direction[] directionArrValuesCustom = values();
            int length = directionArrValuesCustom.length;
            Direction[] directionArr = new Direction[length];
            System.arraycopy(directionArrValuesCustom, 0, directionArr, 0, length);
            return directionArr;
        }

        public Direction getOpposite() {
            return equals(Output) ? Input : Output;
        }
    }

    public ConnectionInfo() {
        this(0, 0, 0, null, null, -1, Direction.Input, Status.Unknown);
    }

    public ConnectionInfo(int connectionID, int rcsID, int avTransportID, ProtocolInfo protocolInfo, ServiceReference peerConnectionManager, int peerConnectionID, Direction direction, Status connectionStatus) {
        this.connectionStatus = Status.Unknown;
        this.connectionID = connectionID;
        this.rcsID = rcsID;
        this.avTransportID = avTransportID;
        this.protocolInfo = protocolInfo;
        this.peerConnectionManager = peerConnectionManager;
        this.peerConnectionID = peerConnectionID;
        this.direction = direction;
        this.connectionStatus = connectionStatus;
    }

    public int getConnectionID() {
        return this.connectionID;
    }

    public int getRcsID() {
        return this.rcsID;
    }

    public int getAvTransportID() {
        return this.avTransportID;
    }

    public ProtocolInfo getProtocolInfo() {
        return this.protocolInfo;
    }

    public ServiceReference getPeerConnectionManager() {
        return this.peerConnectionManager;
    }

    public int getPeerConnectionID() {
        return this.peerConnectionID;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public synchronized Status getConnectionStatus() {
        return this.connectionStatus;
    }

    public synchronized void setConnectionStatus(Status connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConnectionInfo that = (ConnectionInfo) o;
        if (this.avTransportID == that.avTransportID && this.connectionID == that.connectionID && this.peerConnectionID == that.peerConnectionID && this.rcsID == that.rcsID && this.connectionStatus == that.connectionStatus && this.direction == that.direction) {
            if (this.peerConnectionManager == null ? that.peerConnectionManager != null : !this.peerConnectionManager.equals(that.peerConnectionManager)) {
                return false;
            }
            if (this.protocolInfo != null) {
                if (this.protocolInfo.equals(that.protocolInfo)) {
                    return true;
                }
            } else if (that.protocolInfo == null) {
                return true;
            }
            return false;
        }
        return false;
    }

    public int hashCode() {
        int result = this.connectionID;
        return (((((((((((((result * 31) + this.rcsID) * 31) + this.avTransportID) * 31) + (this.protocolInfo != null ? this.protocolInfo.hashCode() : 0)) * 31) + (this.peerConnectionManager != null ? this.peerConnectionManager.hashCode() : 0)) * 31) + this.peerConnectionID) * 31) + this.direction.hashCode()) * 31) + this.connectionStatus.hashCode();
    }

    public String toString() {
        return "(" + getClass().getSimpleName() + ") ID: " + getConnectionID() + ", Status: " + getConnectionStatus();
    }
}
