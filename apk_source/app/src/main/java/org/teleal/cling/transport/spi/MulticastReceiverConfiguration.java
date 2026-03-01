package org.teleal.cling.transport.spi;

import java.net.InetAddress;

/* loaded from: classes.dex */
public interface MulticastReceiverConfiguration {
    InetAddress getGroup();

    int getMaxDatagramBytes();

    int getPort();
}
