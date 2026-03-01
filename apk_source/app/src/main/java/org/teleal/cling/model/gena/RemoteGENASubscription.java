package org.teleal.cling.model.gena;

import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.teleal.cling.model.Location;
import org.teleal.cling.model.Namespace;
import org.teleal.cling.model.NetworkAddress;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.RemoteService;

/* loaded from: classes.dex */
public abstract class RemoteGENASubscription extends GENASubscription<RemoteService> {
    protected PropertyChangeSupport propertyChangeSupport;

    public abstract void ended(CancelReason cancelReason, UpnpResponse upnpResponse);

    public abstract void eventsMissed(int i);

    public abstract void failed(UpnpResponse upnpResponse);

    protected RemoteGENASubscription(RemoteService service) {
        super(service);
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    protected RemoteGENASubscription(RemoteService service, int requestedDurationSeconds) {
        super(service, requestedDurationSeconds);
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public synchronized URL getEventSubscriptionURL() {
        return getService().getDevice().normalizeURI(getService().getEventSubscriptionURI());
    }

    public synchronized List<URL> getEventCallbackURLs(List<NetworkAddress> activeStreamServers, Namespace namespace) {
        List<URL> callbackURLs;
        callbackURLs = new ArrayList<>();
        for (NetworkAddress activeStreamServer : activeStreamServers) {
            callbackURLs.add(new Location(activeStreamServer, namespace.getEventCallbackPath(getService())).getURL());
        }
        return callbackURLs;
    }

    public synchronized void establish() {
        established();
    }

    public synchronized void fail(UpnpResponse responseStatus) {
        failed(responseStatus);
    }

    public synchronized void end(CancelReason reason, UpnpResponse response) {
        ended(reason, response);
    }

    /* JADX WARN: Removed duplicated region for block: B:56:0x007a A[Catch: all -> 0x0077, LOOP:0: B:49:0x006d->B:56:0x007a, LOOP_END, TRY_ENTER, TRY_LEAVE, TryCatch #0 {, blocks: (B:34:0x0003, B:36:0x0007, B:38:0x0021, B:40:0x002d, B:43:0x0036, B:45:0x004c, B:47:0x0064, B:48:0x0067, B:49:0x006d, B:51:0x0073, B:56:0x007a), top: B:58:0x0003 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public synchronized void receive(org.teleal.cling.model.types.UnsignedIntegerFourBytes r11, java.util.Collection<org.teleal.cling.model.state.StateVariableValue> r12) {
        /*
            r10 = this;
            r8 = 1
            monitor-enter(r10)
            org.teleal.cling.model.types.UnsignedIntegerFourBytes r4 = r10.currentSequence     // Catch: java.lang.Throwable -> L77
            if (r4 == 0) goto L67
            org.teleal.cling.model.types.UnsignedIntegerFourBytes r4 = r10.currentSequence     // Catch: java.lang.Throwable -> L77
            java.lang.Long r4 = r4.getValue()     // Catch: java.lang.Throwable -> L77
            org.teleal.cling.model.types.UnsignedIntegerFourBytes r5 = r10.currentSequence     // Catch: java.lang.Throwable -> L77
            org.teleal.cling.model.types.UnsignedVariableInteger$Bits r5 = r5.getBits()     // Catch: java.lang.Throwable -> L77
            long r6 = r5.getMaxValue()     // Catch: java.lang.Throwable -> L77
            java.lang.Long r5 = java.lang.Long.valueOf(r6)     // Catch: java.lang.Throwable -> L77
            boolean r4 = r4.equals(r5)     // Catch: java.lang.Throwable -> L77
            if (r4 == 0) goto L36
            java.lang.Long r4 = r11.getValue()     // Catch: java.lang.Throwable -> L77
            long r4 = r4.longValue()     // Catch: java.lang.Throwable -> L77
            int r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r4 != 0) goto L36
            java.io.PrintStream r4 = java.lang.System.err     // Catch: java.lang.Throwable -> L77
            java.lang.String r5 = "TODO: HANDLE ROLLOVER"
            r4.println(r5)     // Catch: java.lang.Throwable -> L77
        L34:
            monitor-exit(r10)
            return
        L36:
            org.teleal.cling.model.types.UnsignedIntegerFourBytes r4 = r10.currentSequence     // Catch: java.lang.Throwable -> L77
            java.lang.Long r4 = r4.getValue()     // Catch: java.lang.Throwable -> L77
            long r4 = r4.longValue()     // Catch: java.lang.Throwable -> L77
            java.lang.Long r6 = r11.getValue()     // Catch: java.lang.Throwable -> L77
            long r6 = r6.longValue()     // Catch: java.lang.Throwable -> L77
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 >= 0) goto L34
            org.teleal.cling.model.types.UnsignedIntegerFourBytes r4 = r10.currentSequence     // Catch: java.lang.Throwable -> L77
            java.lang.Long r4 = r4.getValue()     // Catch: java.lang.Throwable -> L77
            long r4 = r4.longValue()     // Catch: java.lang.Throwable -> L77
            long r2 = r4 + r8
            java.lang.Long r4 = r11.getValue()     // Catch: java.lang.Throwable -> L77
            long r4 = r4.longValue()     // Catch: java.lang.Throwable -> L77
            long r4 = r4 - r2
            int r0 = (int) r4     // Catch: java.lang.Throwable -> L77
            if (r0 == 0) goto L67
            r10.eventsMissed(r0)     // Catch: java.lang.Throwable -> L77
        L67:
            r10.currentSequence = r11     // Catch: java.lang.Throwable -> L77
            java.util.Iterator r4 = r12.iterator()     // Catch: java.lang.Throwable -> L77
        L6d:
            boolean r5 = r4.hasNext()     // Catch: java.lang.Throwable -> L77
            if (r5 != 0) goto L7a
            r10.eventReceived()     // Catch: java.lang.Throwable -> L77
            goto L34
        L77:
            r4 = move-exception
            monitor-exit(r10)
            throw r4
        L7a:
            java.lang.Object r1 = r4.next()     // Catch: java.lang.Throwable -> L77
            org.teleal.cling.model.state.StateVariableValue r1 = (org.teleal.cling.model.state.StateVariableValue) r1     // Catch: java.lang.Throwable -> L77
            java.util.Map<java.lang.String, org.teleal.cling.model.state.StateVariableValue<S extends org.teleal.cling.model.meta.Service>> r5 = r10.currentValues     // Catch: java.lang.Throwable -> L77
            org.teleal.cling.model.meta.StateVariable r6 = r1.getStateVariable()     // Catch: java.lang.Throwable -> L77
            java.lang.String r6 = r6.getName()     // Catch: java.lang.Throwable -> L77
            r5.put(r6, r1)     // Catch: java.lang.Throwable -> L77
            goto L6d
        */
        throw new UnsupportedOperationException("Method not decompiled: org.teleal.cling.model.gena.RemoteGENASubscription.receive(org.teleal.cling.model.types.UnsignedIntegerFourBytes, java.util.Collection):void");
    }

    @Override // org.teleal.cling.model.gena.GENASubscription
    public String toString() {
        return "(SID: " + getSubscriptionId() + ") " + getService();
    }
}
