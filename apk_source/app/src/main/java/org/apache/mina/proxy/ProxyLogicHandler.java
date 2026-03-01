package org.apache.mina.proxy;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.proxy.session.ProxyIoSession;

/* loaded from: classes.dex */
public interface ProxyLogicHandler {
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [org.apache.mina.proxy.ProxyAuthException] */
    void doHandshake(IoFilter.NextFilter nextFilter);

    void enqueueWriteRequest(IoFilter.NextFilter nextFilter, WriteRequest writeRequest);

    ProxyIoSession getProxyIoSession();

    boolean isHandshakeComplete();

    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [org.apache.mina.proxy.ProxyAuthException] */
    void messageReceived(IoFilter.NextFilter nextFilter, IoBuffer ioBuffer);
}
