package mktvsmart.screen.gchat.client;

import android.util.Log;
import java.net.InetSocketAddress;
import mktvsmart.screen.json.serialize.JsonSerialize;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/* loaded from: classes.dex */
public class ChatConnector {
    private static final int MAX_CONNECT_TIME = 5;
    private static final String TAG = ChatClientAsync.class.getSimpleName();
    private ConnectFuture mConnectFuture;
    private IoConnector mConnector;
    private DemuxingProtocolCodecFactory mFactory = new DemuxingProtocolCodecFactory();
    private IoSession mSession;
    private InetSocketAddress mSocketAddress;

    public ChatConnector(InetSocketAddress mSocketAddress, IoHandlerAdapter ioHandlerAdapter) {
        this.mSocketAddress = mSocketAddress;
        this.mFactory.addMessageEncoder(TransmissionPackage.class, new PackageEncoder());
        this.mFactory.addMessageDecoder(new PackageDecoder());
        this.mConnector = new NioSocketConnector();
        this.mConnector.getFilterChain().addLast("logger", new LoggingFilter());
        this.mConnector.getFilterChain().addLast("codec", new ProtocolCodecFilter(this.mFactory));
        this.mConnector.setHandler(ioHandlerAdapter);
    }

    public void addReconnectFilter(String name, IoFilter filter) {
        this.mConnector.getFilterChain().addFirst(name, filter);
    }

    public boolean connect() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            try {
                Log.d(TAG, "connect Host: " + this.mSocketAddress.getHostName() + "  Port: " + this.mSocketAddress.getPort());
                this.mConnectFuture = this.mConnector.connect(this.mSocketAddress);
                this.mConnectFuture.awaitUninterruptibly();
                this.mSession = this.mConnectFuture.getSession();
                Log.d(TAG, "connect success !");
                return true;
            } catch (Exception e) {
                Log.d(TAG, "connect failed !");
                try {
                    Thread.sleep(3000L);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    public void send(JsonSerialize message) {
        if (this.mSession != null && this.mSession.isConnected()) {
            this.mSession.write(message.serialize());
        }
    }

    public InetSocketAddress getmSocketAddress() {
        return this.mSocketAddress;
    }

    public void setmSocketAddress(InetSocketAddress mSocketAddress) {
        this.mSocketAddress = mSocketAddress;
    }

    public IoSession getmSession() {
        return this.mSession;
    }

    public void setmSession(IoSession mSession) {
        this.mSession = mSession;
    }

    public IoConnector getmConnector() {
        return this.mConnector;
    }

    public void setmConnector(IoConnector mConnector) {
        this.mConnector = mConnector;
    }

    public ConnectFuture getmConnectFuture() {
        return this.mConnectFuture;
    }

    public void setmConnectFuture(ConnectFuture mConnectFuture) {
        this.mConnectFuture = mConnectFuture;
    }

    public DemuxingProtocolCodecFactory getmFactory() {
        return this.mFactory;
    }

    public void setmFactory(DemuxingProtocolCodecFactory mFactory) {
        this.mFactory = mFactory;
    }
}
