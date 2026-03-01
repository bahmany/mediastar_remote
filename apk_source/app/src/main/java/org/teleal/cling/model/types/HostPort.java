package org.teleal.cling.model.types;

/* loaded from: classes.dex */
public class HostPort {
    private String host;
    private int port;

    public HostPort() {
    }

    public HostPort(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HostPort hostPort = (HostPort) o;
        return this.port == hostPort.port && this.host.equals(hostPort.host);
    }

    public int hashCode() {
        int result = this.host.hashCode();
        return (result * 31) + this.port;
    }

    public String toString() {
        return String.valueOf(this.host) + ":" + this.port;
    }
}
