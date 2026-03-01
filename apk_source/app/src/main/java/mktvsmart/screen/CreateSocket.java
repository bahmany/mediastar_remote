package mktvsmart.screen;

import java.net.InetSocketAddress;
import java.net.Socket;

/* loaded from: classes.dex */
public class CreateSocket {
    private static Socket socketHandle = null;
    private String Address;
    private int Port;

    public CreateSocket(String NetAddress, int NetPort) {
        this.Address = NetAddress;
        this.Port = NetPort;
    }

    public synchronized Socket GetSocket() throws Exception {
        if (socketHandle == null) {
            socketHandle = new Socket();
            socketHandle.connect(new InetSocketAddress(this.Address, this.Port), 3000);
        }
        return socketHandle;
    }

    public synchronized void DestroySocket() {
        if (socketHandle != null) {
            try {
                socketHandle.shutdownInput();
                socketHandle.shutdownOutput();
                socketHandle.close();
            } catch (Exception e) {
            }
            socketHandle = null;
        }
    }
}
