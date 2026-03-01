package org.cybergarage.upnp;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import org.cybergarage.http.HTTPRequest;
import org.cybergarage.http.HTTPRequestListener;
import org.cybergarage.http.HTTPServerList;
import org.cybergarage.net.HostInterface;
import org.cybergarage.upnp.control.RenewSubscriber;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.cybergarage.upnp.device.Disposer;
import org.cybergarage.upnp.device.NotifyListener;
import org.cybergarage.upnp.device.SearchResponseListener;
import org.cybergarage.upnp.device.USN;
import org.cybergarage.upnp.event.EventListener;
import org.cybergarage.upnp.event.NotifyRequest;
import org.cybergarage.upnp.event.Property;
import org.cybergarage.upnp.event.PropertyList;
import org.cybergarage.upnp.event.SubscriptionRequest;
import org.cybergarage.upnp.event.SubscriptionResponse;
import org.cybergarage.upnp.ssdp.SSDPNotifySocketList;
import org.cybergarage.upnp.ssdp.SSDPPacket;
import org.cybergarage.upnp.ssdp.SSDPSearchRequest;
import org.cybergarage.upnp.ssdp.SSDPSearchResponseSocketList;
import org.cybergarage.util.Debug;
import org.cybergarage.util.ListenerList;
import org.cybergarage.util.Mutex;
import org.cybergarage.xml.Node;
import org.cybergarage.xml.NodeList;
import org.cybergarage.xml.Parser;
import org.cybergarage.xml.ParserException;

/* loaded from: classes.dex */
public class ControlPoint implements HTTPRequestListener {
    private static final int DEFAULT_EVENTSUB_PORT = 8058;
    private static final String DEFAULT_EVENTSUB_URI = "/evetSub";
    private static final int DEFAULT_EXPIRED_DEVICE_MONITORING_INTERVAL = 60;
    private static final int DEFAULT_SSDP_PORT = 8008;
    private NodeList devNodeList;
    ListenerList deviceChangeListenerList;
    private Disposer deviceDisposer;
    private ListenerList deviceNotifyListenerList;
    private ListenerList deviceSearchResponseListenerList;
    private ListenerList eventListenerList;
    private String eventSubURI;
    private long expiredDeviceMonitoringInterval;
    private int httpPort;
    private HTTPServerList httpServerList;
    private Mutex mutex;
    private boolean nmprMode;
    private RenewSubscriber renewSubscriber;
    private int searchMx;
    private SSDPNotifySocketList ssdpNotifySocketList;
    private int ssdpPort;
    private SSDPSearchResponseSocketList ssdpSearchResponseSocketList;
    private Object userData;

    private SSDPNotifySocketList getSSDPNotifySocketList() {
        return this.ssdpNotifySocketList;
    }

    private SSDPSearchResponseSocketList getSSDPSearchResponseSocketList() {
        return this.ssdpSearchResponseSocketList;
    }

    static {
        UPnP.initialize();
    }

    public ControlPoint(int ssdpPort, int httpPort, InetAddress[] binds) {
        this.mutex = new Mutex();
        this.ssdpPort = 0;
        this.httpPort = 0;
        this.devNodeList = new NodeList();
        this.deviceNotifyListenerList = new ListenerList();
        this.deviceSearchResponseListenerList = new ListenerList();
        this.deviceChangeListenerList = new ListenerList();
        this.searchMx = 3;
        this.httpServerList = new HTTPServerList();
        this.eventListenerList = new ListenerList();
        this.eventSubURI = DEFAULT_EVENTSUB_URI;
        this.userData = null;
        this.ssdpNotifySocketList = new SSDPNotifySocketList(binds);
        this.ssdpSearchResponseSocketList = new SSDPSearchResponseSocketList(binds);
        setSSDPPort(ssdpPort);
        setHTTPPort(httpPort);
        setDeviceDisposer(null);
        setExpiredDeviceMonitoringInterval(60L);
        setRenewSubscriber(null);
        setNMPRMode(false);
        setRenewSubscriber(null);
    }

    public ControlPoint(int ssdpPort, int httpPort) {
        this(ssdpPort, httpPort, null);
    }

    public ControlPoint() {
        this(DEFAULT_SSDP_PORT, DEFAULT_EVENTSUB_PORT);
    }

    public void finalize() {
        stop();
    }

    public void lock() {
        this.mutex.lock();
    }

    public void unlock() {
        this.mutex.unlock();
    }

    public int getSSDPPort() {
        return this.ssdpPort;
    }

    public void setSSDPPort(int port) {
        this.ssdpPort = port;
    }

    public int getHTTPPort() {
        return this.httpPort;
    }

    public void setHTTPPort(int port) {
        this.httpPort = port;
    }

    public void setNMPRMode(boolean flag) {
        this.nmprMode = flag;
    }

    public boolean isNMPRMode() {
        return this.nmprMode;
    }

    private void addDevice(Node rootNode) {
        this.devNodeList.lock();
        this.devNodeList.add(rootNode);
        this.devNodeList.unlock();
    }

    private synchronized void addDevice(SSDPPacket ssdpPacket) {
        if (ssdpPacket.isHiMultiScreenDevice()) {
            String locationURL = ssdpPacket.getLocation();
            String RemoteAddress = ssdpPacket.getRemoteAddress();
            if (!locationURL.contains(RemoteAddress)) {
                Debug.message("Wrong download URL: locationURL=" + locationURL + ", RemoteAddress=" + RemoteAddress);
            }
            String usn = ssdpPacket.getUSN();
            String udn = USN.getUDN(usn);
            Device dev = getDevice(udn);
            if (dev != null) {
                dev.setSSDPPacket(ssdpPacket);
                performRefreshDeviceListener(dev);
            } else {
                String location = ssdpPacket.getLocation();
                try {
                    URL locationUrl = new URL(location);
                    Parser parser = UPnP.getXMLParser();
                    Node rootNode = parser.parse(locationUrl);
                    Device rootDev = getDevice(rootNode);
                    if (rootDev != null && rootDev.isDeviceType("urn:schemas-upnp-org:device:HiMultiScreenServerDevice:1")) {
                        rootDev.setSSDPPacket(ssdpPacket);
                        addDevice(rootNode);
                        performAddDeviceListener(rootDev);
                    }
                } catch (MalformedURLException me) {
                    Debug.warning(ssdpPacket.toString());
                    Debug.warning(me);
                } catch (ParserException pe) {
                    Debug.warning(ssdpPacket.toString());
                    Debug.warning(pe);
                }
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:6:0x000f A[Catch: all -> 0x0052, TRY_LEAVE, TryCatch #1 {, blocks: (B:4:0x0003, B:9:0x0016, B:11:0x0029, B:13:0x0031, B:15:0x0043, B:23:0x0055, B:18:0x004b, B:26:0x0060, B:6:0x000f), top: B:28:0x0003, inners: #3 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private synchronized void addDevice(java.lang.String r13, java.lang.String r14, java.lang.String r15) {
        /*
            r12 = this;
            monitor-enter(r12)
            if (r13 == 0) goto Lf
            java.lang.String r10 = ""
            java.lang.String r11 = r13.trim()     // Catch: java.lang.Throwable -> L52
            boolean r10 = r10.equals(r11)     // Catch: java.lang.Throwable -> L52
            if (r10 == 0) goto L16
        Lf:
            java.lang.String r10 = "LocationURL is error."
            com.hisilicon.multiscreen.protocol.utils.LogTool.d(r10)     // Catch: java.lang.Throwable -> L52
        L14:
            monitor-exit(r12)
            return
        L16:
            java.net.URL r1 = new java.net.URL     // Catch: java.net.MalformedURLException -> L4a java.lang.Throwable -> L52 org.cybergarage.xml.ParserException -> L5f
            r1.<init>(r13)     // Catch: java.net.MalformedURLException -> L4a java.lang.Throwable -> L52 org.cybergarage.xml.ParserException -> L5f
            org.cybergarage.xml.Parser r3 = org.cybergarage.upnp.UPnP.getXMLParser()     // Catch: java.net.MalformedURLException -> L4a java.lang.Throwable -> L52 org.cybergarage.xml.ParserException -> L5f
            org.cybergarage.xml.Node r6 = r3.parse(r1)     // Catch: java.net.MalformedURLException -> L4a java.lang.Throwable -> L52 org.cybergarage.xml.ParserException -> L5f
            org.cybergarage.upnp.Device r5 = r12.getDevice(r6)     // Catch: java.net.MalformedURLException -> L4a java.lang.Throwable -> L52 org.cybergarage.xml.ParserException -> L5f
            if (r5 == 0) goto L14
            java.lang.String r10 = "urn:schemas-upnp-org:device:HiMultiScreenServerDevice:1"
            boolean r10 = r5.isDeviceType(r10)     // Catch: java.net.MalformedURLException -> L4a java.lang.Throwable -> L52 org.cybergarage.xml.ParserException -> L5f
            if (r10 == 0) goto L14
            org.cybergarage.upnp.ssdp.SSDPPacket r7 = r12.getDefaultSsdp(r13, r14, r15)     // Catch: java.net.MalformedURLException -> L4a java.lang.Throwable -> L52 org.cybergarage.xml.ParserException -> L5f
            java.lang.String r9 = r7.getUSN()     // Catch: java.net.MalformedURLException -> L4a java.lang.Throwable -> L52 org.cybergarage.xml.ParserException -> L5f
            java.lang.String r8 = org.cybergarage.upnp.device.USN.getUDN(r9)     // Catch: java.net.MalformedURLException -> L4a java.lang.Throwable -> L52 org.cybergarage.xml.ParserException -> L5f
            org.cybergarage.upnp.Device r0 = r12.getDevice(r8)     // Catch: java.net.MalformedURLException -> L4a java.lang.Throwable -> L52 org.cybergarage.xml.ParserException -> L5f
            if (r0 == 0) goto L55
            r0.setSSDPPacket(r7)     // Catch: java.net.MalformedURLException -> L4a java.lang.Throwable -> L52 org.cybergarage.xml.ParserException -> L5f
            r12.performRefreshDeviceListener(r0)     // Catch: java.net.MalformedURLException -> L4a java.lang.Throwable -> L52 org.cybergarage.xml.ParserException -> L5f
            goto L14
        L4a:
            r2 = move-exception
            org.cybergarage.util.Debug.warning(r13)     // Catch: java.lang.Throwable -> L52
            org.cybergarage.util.Debug.warning(r2)     // Catch: java.lang.Throwable -> L52
            goto L14
        L52:
            r10 = move-exception
            monitor-exit(r12)
            throw r10
        L55:
            r5.setSSDPPacket(r7)     // Catch: java.net.MalformedURLException -> L4a java.lang.Throwable -> L52 org.cybergarage.xml.ParserException -> L5f
            r12.addDevice(r6)     // Catch: java.net.MalformedURLException -> L4a java.lang.Throwable -> L52 org.cybergarage.xml.ParserException -> L5f
            r12.performAddDeviceListener(r5)     // Catch: java.net.MalformedURLException -> L4a java.lang.Throwable -> L52 org.cybergarage.xml.ParserException -> L5f
            goto L14
        L5f:
            r4 = move-exception
            org.cybergarage.util.Debug.warning(r13)     // Catch: java.lang.Throwable -> L52
            org.cybergarage.util.Debug.warning(r4)     // Catch: java.lang.Throwable -> L52
            goto L14
        */
        throw new UnsupportedOperationException("Method not decompiled: org.cybergarage.upnp.ControlPoint.addDevice(java.lang.String, java.lang.String, java.lang.String):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:6:0x000f A[Catch: all -> 0x0059, TRY_LEAVE, TryCatch #0 {, blocks: (B:4:0x0003, B:9:0x0016, B:11:0x002d, B:13:0x0035, B:21:0x005c, B:23:0x0074, B:30:0x00a1, B:32:0x00c5, B:33:0x00d4, B:25:0x007c, B:16:0x004d, B:28:0x0083, B:6:0x000f), top: B:35:0x0003, inners: #1, #2 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private synchronized void addDevice(java.lang.String r18, java.lang.String r19) {
        /*
            r17 = this;
            monitor-enter(r17)
            if (r18 == 0) goto Lf
            java.lang.String r15 = ""
            java.lang.String r16 = r18.trim()     // Catch: java.lang.Throwable -> L59
            boolean r15 = r15.equals(r16)     // Catch: java.lang.Throwable -> L59
            if (r15 == 0) goto L16
        Lf:
            java.lang.String r15 = "LocationURL is error."
            com.hisilicon.multiscreen.protocol.utils.LogTool.d(r15)     // Catch: java.lang.Throwable -> L59
        L14:
            monitor-exit(r17)
            return
        L16:
            java.net.URL r4 = new java.net.URL     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            r0 = r18
            r4.<init>(r0)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            org.cybergarage.xml.Parser r6 = org.cybergarage.upnp.UPnP.getXMLParser()     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            org.cybergarage.xml.Node r9 = r6.parse(r4)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            r0 = r17
            org.cybergarage.upnp.Device r8 = r0.getDevice(r9)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            if (r8 == 0) goto L14
            java.lang.String r15 = "urn:schemas-upnp-org:device:HiMultiScreenServerDevice:1"
            boolean r15 = r8.isDeviceType(r15)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            if (r15 != 0) goto L5c
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            java.lang.String r16 = java.lang.String.valueOf(r18)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            r15.<init>(r16)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            java.lang.String r16 = " is not multiscreen device."
            java.lang.StringBuilder r15 = r15.append(r16)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            java.lang.String r15 = r15.toString()     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            com.hisilicon.multiscreen.protocol.utils.LogTool.d(r15)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            goto L14
        L4c:
            r5 = move-exception
            org.cybergarage.util.Debug.warning(r18)     // Catch: java.lang.Throwable -> L59
            org.cybergarage.util.Debug.warning(r5)     // Catch: java.lang.Throwable -> L59
            java.lang.String r15 = "MalformedURLException me"
            com.hisilicon.multiscreen.protocol.utils.LogTool.d(r15)     // Catch: java.lang.Throwable -> L59
            goto L14
        L59:
            r15 = move-exception
            monitor-exit(r17)
            throw r15
        L5c:
            java.lang.String r11 = r8.getUDN()     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            java.lang.String r16 = "strUDN = "
            r15.<init>(r16)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            java.lang.StringBuilder r15 = r15.append(r11)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            java.lang.String r15 = r15.toString()     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            com.hisilicon.multiscreen.protocol.utils.LogTool.d(r15)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            if (r11 == 0) goto L7c
            java.lang.String r15 = "uuid:"
            boolean r15 = r11.startsWith(r15)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            if (r15 != 0) goto La1
        L7c:
            java.lang.String r15 = "strUDN is null or not startwith uuid:"
            com.hisilicon.multiscreen.protocol.utils.LogTool.d(r15)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            goto L14
        L82:
            r7 = move-exception
            org.cybergarage.util.Debug.warning(r18)     // Catch: java.lang.Throwable -> L59
            org.cybergarage.util.Debug.warning(r7)     // Catch: java.lang.Throwable -> L59
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L59
            java.lang.String r16 = "ParserException pe "
            r15.<init>(r16)     // Catch: java.lang.Throwable -> L59
            java.lang.String r16 = r7.getMessage()     // Catch: java.lang.Throwable -> L59
            java.lang.StringBuilder r15 = r15.append(r16)     // Catch: java.lang.Throwable -> L59
            java.lang.String r15 = r15.toString()     // Catch: java.lang.Throwable -> L59
            com.hisilicon.multiscreen.protocol.utils.LogTool.e(r15)     // Catch: java.lang.Throwable -> L59
            goto L14
        La1:
            java.lang.String r15 = "uuid:"
            int r15 = r15.length()     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            java.lang.String r14 = r11.substring(r15)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            r0 = r17
            r1 = r18
            r2 = r19
            org.cybergarage.upnp.ssdp.SSDPPacket r10 = r0.getDefaultSsdp(r1, r14, r2)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            java.lang.String r13 = r10.getUSN()     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            java.lang.String r12 = org.cybergarage.upnp.device.USN.getUDN(r13)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            r0 = r17
            org.cybergarage.upnp.Device r3 = r0.getDevice(r12)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            if (r3 == 0) goto Ld4
            r3.setSSDPPacket(r10)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            r0 = r17
            r0.performRefreshDeviceListener(r3)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            java.lang.String r15 = "device has exist, so just refresh device list"
            com.hisilicon.multiscreen.protocol.utils.LogTool.d(r15)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            goto L14
        Ld4:
            r8.setSSDPPacket(r10)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            r0 = r17
            r0.addDevice(r9)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            r0 = r17
            r0.performAddDeviceListener(r8)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            java.lang.String r15 = "add device manual successful"
            com.hisilicon.multiscreen.protocol.utils.LogTool.d(r15)     // Catch: java.net.MalformedURLException -> L4c java.lang.Throwable -> L59 org.cybergarage.xml.ParserException -> L82
            goto L14
        */
        throw new UnsupportedOperationException("Method not decompiled: org.cybergarage.upnp.ControlPoint.addDevice(java.lang.String, java.lang.String):void");
    }

    private Device getDevice(Node rootNode) {
        if (rootNode == null) {
            return null;
        }
        rootNode.lock();
        Node devNode = rootNode.getNode(Device.ELEM_NAME);
        if (devNode == null) {
            rootNode.unlock();
            return null;
        }
        rootNode.unlock();
        return new Device(rootNode, devNode);
    }

    public DeviceList getDeviceList() {
        DeviceList devList = new DeviceList();
        this.devNodeList.lock();
        int nRoots = this.devNodeList.size();
        for (int n = 0; n < nRoots; n++) {
            Node rootNode = this.devNodeList.getNode(n);
            Device dev = getDevice(rootNode);
            if (dev != null) {
                devList.add(dev);
            }
        }
        this.devNodeList.unlock();
        return devList;
    }

    public Device getDevice(String name) {
        int nRoots = this.devNodeList.size();
        for (int n = 0; n < nRoots; n++) {
            Node rootNode = this.devNodeList.getNode(n);
            Device dev = getDevice(rootNode);
            if (dev != null) {
                if (dev.isDevice(name)) {
                    return dev;
                }
                Device cdev = dev.getDevice(name);
                if (cdev != null) {
                    return cdev;
                }
            }
        }
        return null;
    }

    public boolean hasDevice(String name) {
        return getDevice(name) != null;
    }

    private void removeDevice(Node rootNode) {
        Device dev = getDevice(rootNode);
        if (dev != null && dev.isRootDevice()) {
            performRemoveDeviceListener(dev);
        }
        this.devNodeList.remove(rootNode);
    }

    protected void removeDevice(Device dev) {
        if (dev != null) {
            removeDevice(dev.getRootNode());
        }
    }

    protected void removeDevice(String name) {
        this.devNodeList.lock();
        Device dev = getDevice(name);
        removeDevice(dev);
        this.devNodeList.unlock();
    }

    private void removeDevice(SSDPPacket packet) {
        if (packet.isByeBye()) {
            String usn = packet.getUSN();
            String udn = USN.getUDN(usn);
            lock();
            removeDevice(udn);
            unlock();
        }
    }

    public void removeExpiredDevices() {
        DeviceList devList = getDeviceList();
        int devCnt = devList.size();
        Device[] dev = new Device[devCnt];
        for (int n = 0; n < devCnt; n++) {
            dev[n] = devList.getDevice(n);
        }
        for (int n2 = 0; n2 < devCnt; n2++) {
            if (dev[n2].isExpired()) {
                Debug.message("Expired device = " + dev[n2].getFriendlyName());
                removeDevice(dev[n2]);
            }
        }
    }

    public void setExpiredDeviceMonitoringInterval(long interval) {
        this.expiredDeviceMonitoringInterval = interval;
    }

    public long getExpiredDeviceMonitoringInterval() {
        return this.expiredDeviceMonitoringInterval;
    }

    public void setDeviceDisposer(Disposer disposer) {
        this.deviceDisposer = disposer;
    }

    public Disposer getDeviceDisposer() {
        return this.deviceDisposer;
    }

    public void addNotifyListener(NotifyListener listener) {
        this.deviceNotifyListenerList.add(listener);
    }

    public void removeNotifyListener(NotifyListener listener) {
        this.deviceNotifyListenerList.remove(listener);
    }

    public void performNotifyListener(SSDPPacket ssdpPacket) {
        int listenerSize = this.deviceNotifyListenerList.size();
        for (int n = 0; n < listenerSize; n++) {
            NotifyListener listener = (NotifyListener) this.deviceNotifyListenerList.get(n);
            try {
                listener.deviceNotifyReceived(ssdpPacket);
            } catch (Exception e) {
                Debug.warning("NotifyListener returned an error:", e);
            }
        }
    }

    public void addSearchResponseListener(SearchResponseListener listener) {
        this.deviceSearchResponseListenerList.add(listener);
    }

    public void removeSearchResponseListener(SearchResponseListener listener) {
        this.deviceSearchResponseListenerList.remove(listener);
    }

    public void performSearchResponseListener(SSDPPacket ssdpPacket) {
        int listenerSize = this.deviceSearchResponseListenerList.size();
        for (int n = 0; n < listenerSize; n++) {
            SearchResponseListener listener = (SearchResponseListener) this.deviceSearchResponseListenerList.get(n);
            try {
                listener.deviceSearchResponseReceived(ssdpPacket);
            } catch (Exception e) {
                Debug.warning("SearchResponseListener returned an error:", e);
            }
        }
    }

    public void addDeviceChangeListener(DeviceChangeListener listener) {
        this.deviceChangeListenerList.add(listener);
    }

    public void removeDeviceChangeListener(DeviceChangeListener listener) {
        this.deviceChangeListenerList.remove(listener);
    }

    public void performAddDeviceListener(Device dev) {
        int listenerSize = this.deviceChangeListenerList.size();
        for (int n = 0; n < listenerSize; n++) {
            DeviceChangeListener listener = (DeviceChangeListener) this.deviceChangeListenerList.get(n);
            listener.deviceAdded(dev);
        }
    }

    public void performRemoveDeviceListener(Device dev) {
        int listenerSize = this.deviceChangeListenerList.size();
        for (int n = 0; n < listenerSize; n++) {
            DeviceChangeListener listener = (DeviceChangeListener) this.deviceChangeListenerList.get(n);
            listener.deviceRemoved(dev);
        }
    }

    public void performRefreshDeviceListener(Device dev) {
        int listenerSize = this.deviceChangeListenerList.size();
        for (int n = 0; n < listenerSize; n++) {
            DeviceChangeListener listener = (DeviceChangeListener) this.deviceChangeListenerList.get(n);
            listener.deviceRefreshed(dev);
        }
    }

    public void notifyReceived(SSDPPacket packet) {
        if (packet.isHiMultiScreenDevice()) {
            if (packet.isAlive()) {
                addDevice(packet);
            } else if (packet.isByeBye()) {
                removeDevice(packet);
            }
            performNotifyListener(packet);
        }
    }

    public void searchResponseReceived(SSDPPacket packet) {
        if (packet.isHiMultiScreenDevice()) {
            addDevice(packet);
            performSearchResponseListener(packet);
        }
    }

    public int getSearchMx() {
        return this.searchMx;
    }

    public void setSearchMx(int mx) {
        this.searchMx = mx;
    }

    public void search(String target, int mx) {
        SSDPSearchRequest msReq = new SSDPSearchRequest(target, mx);
        SSDPSearchResponseSocketList ssdpSearchResponseSocketList = getSSDPSearchResponseSocketList();
        ssdpSearchResponseSocketList.post(msReq);
    }

    public void search(String addr, String target, int mx) {
        SSDPSearchRequest msReq = new SSDPSearchRequest(target, mx);
        SSDPSearchResponseSocketList ssdpSearchResponseSocketList = getSSDPSearchResponseSocketList();
        ssdpSearchResponseSocketList.post(addr, msReq);
    }

    public void search(String target) {
        search(target, 3);
    }

    public void search() {
        search("upnp:rootdevice", 3);
    }

    public void searchByAddress(String addr) {
        search(addr, "upnp:rootdevice", 3);
    }

    public void searchByUrl(String locationURL, String uuid, String localAddress) {
        addDevice(locationURL, uuid, localAddress);
    }

    public void searchByUrl(String locationURL, String localAddress) {
        addDevice(locationURL, localAddress);
    }

    private HTTPServerList getHTTPServerList() {
        return this.httpServerList;
    }

    @Override // org.cybergarage.http.HTTPRequestListener
    public void httpRequestRecieved(HTTPRequest httpReq) {
        if (Debug.isOn()) {
            httpReq.print();
        }
        if (httpReq.isNotifyRequest()) {
            NotifyRequest notifyReq = new NotifyRequest(httpReq);
            String uuid = notifyReq.getSID();
            long seq = notifyReq.getSEQ();
            PropertyList props = notifyReq.getPropertyList();
            int propCnt = props.size();
            for (int n = 0; n < propCnt; n++) {
                Property prop = props.getProperty(n);
                String varName = prop.getName();
                String varValue = prop.getValue();
                performEventListener(uuid, seq, varName, varValue);
            }
            httpReq.returnOK();
            return;
        }
        httpReq.returnBadRequest();
    }

    public void addEventListener(EventListener listener) {
        this.eventListenerList.add(listener);
    }

    public void removeEventListener(EventListener listener) {
        this.eventListenerList.remove(listener);
    }

    public void performEventListener(String uuid, long seq, String name, String value) {
        int listenerSize = this.eventListenerList.size();
        for (int n = 0; n < listenerSize; n++) {
            EventListener listener = (EventListener) this.eventListenerList.get(n);
            listener.eventNotifyReceived(uuid, seq, name, value);
        }
    }

    public String getEventSubURI() {
        return this.eventSubURI;
    }

    public void setEventSubURI(String url) {
        this.eventSubURI = url;
    }

    private String getEventSubCallbackURL(String host) {
        return HostInterface.getHostURL(host, getHTTPPort(), getEventSubURI());
    }

    public boolean subscribe(Service service, long timeout) {
        if (service.isSubscribed()) {
            String sid = service.getSID();
            return subscribe(service, sid, timeout);
        }
        Device rootDev = service.getRootDevice();
        if (rootDev == null) {
            return false;
        }
        String ifAddress = rootDev.getInterfaceAddress();
        SubscriptionRequest subReq = new SubscriptionRequest();
        subReq.setSubscribeRequest(service, getEventSubCallbackURL(ifAddress), timeout);
        SubscriptionResponse subRes = subReq.post();
        if (subRes.isSuccessful()) {
            service.setSID(subRes.getSID());
            service.setTimeout(subRes.getTimeout());
            return true;
        }
        service.clearSID();
        return false;
    }

    public boolean subscribe(Service service) {
        return subscribe(service, -1L);
    }

    public boolean subscribe(Service service, String uuid, long timeout) {
        SubscriptionRequest subReq = new SubscriptionRequest();
        subReq.setRenewRequest(service, uuid, timeout);
        if (Debug.isOn()) {
            subReq.print();
        }
        SubscriptionResponse subRes = subReq.post();
        if (Debug.isOn()) {
            subRes.print();
        }
        if (subRes.isSuccessful()) {
            service.setSID(subRes.getSID());
            service.setTimeout(subRes.getTimeout());
            return true;
        }
        service.clearSID();
        return false;
    }

    public boolean subscribe(Service service, String uuid) {
        return subscribe(service, uuid, -1L);
    }

    public boolean isSubscribed(Service service) {
        if (service == null) {
            return false;
        }
        return service.isSubscribed();
    }

    public boolean unsubscribe(Service service) {
        SubscriptionRequest subReq = new SubscriptionRequest();
        subReq.setUnsubscribeRequest(service);
        SubscriptionResponse subRes = subReq.post();
        if (!subRes.isSuccessful()) {
            return false;
        }
        service.clearSID();
        return true;
    }

    public void unsubscribe(Device device) {
        ServiceList serviceList = device.getServiceList();
        int serviceCnt = serviceList.size();
        for (int n = 0; n < serviceCnt; n++) {
            Service service = serviceList.getService(n);
            if (service.hasSID()) {
                unsubscribe(service);
            }
        }
        DeviceList childDevList = device.getDeviceList();
        int childDevCnt = childDevList.size();
        for (int n2 = 0; n2 < childDevCnt; n2++) {
            Device cdev = childDevList.getDevice(n2);
            unsubscribe(cdev);
        }
    }

    public void unsubscribe() {
        DeviceList devList = getDeviceList();
        int devCnt = devList.size();
        for (int n = 0; n < devCnt; n++) {
            Device dev = devList.getDevice(n);
            unsubscribe(dev);
        }
    }

    public Service getSubscriberService(String uuid) {
        DeviceList devList = getDeviceList();
        int devCnt = devList.size();
        for (int n = 0; n < devCnt; n++) {
            Device dev = devList.getDevice(n);
            Service service = dev.getSubscriberService(uuid);
            if (service != null) {
                return service;
            }
        }
        return null;
    }

    public void renewSubscriberService(Device dev, long timeout) {
        ServiceList serviceList = dev.getServiceList();
        int serviceCnt = serviceList.size();
        for (int n = 0; n < serviceCnt; n++) {
            Service service = serviceList.getService(n);
            if (service.isSubscribed()) {
                String sid = service.getSID();
                boolean isRenewed = subscribe(service, sid, timeout);
                if (!isRenewed) {
                    subscribe(service, timeout);
                }
            }
        }
        DeviceList cdevList = dev.getDeviceList();
        int cdevCnt = cdevList.size();
        for (int n2 = 0; n2 < cdevCnt; n2++) {
            Device cdev = cdevList.getDevice(n2);
            renewSubscriberService(cdev, timeout);
        }
    }

    public void renewSubscriberService(long timeout) {
        DeviceList devList = getDeviceList();
        int devCnt = devList.size();
        for (int n = 0; n < devCnt; n++) {
            Device dev = devList.getDevice(n);
            renewSubscriberService(dev, timeout);
        }
    }

    public void renewSubscriberService() {
        renewSubscriberService(-1L);
    }

    public void setRenewSubscriber(RenewSubscriber sub) {
        this.renewSubscriber = sub;
    }

    public RenewSubscriber getRenewSubscriber() {
        return this.renewSubscriber;
    }

    public boolean start(String target, int mx) {
        stop();
        int retryCnt = 0;
        int bindPort = getHTTPPort();
        HTTPServerList httpServerList = getHTTPServerList();
        while (!httpServerList.open(bindPort)) {
            retryCnt++;
            if (100 < retryCnt) {
                return false;
            }
            setHTTPPort(bindPort + 1);
            bindPort = getHTTPPort();
        }
        httpServerList.addRequestListener(this);
        httpServerList.start();
        SSDPNotifySocketList ssdpNotifySocketList = getSSDPNotifySocketList();
        if (!ssdpNotifySocketList.open()) {
            return false;
        }
        ssdpNotifySocketList.setControlPoint(this);
        ssdpNotifySocketList.start();
        int ssdpPort = getSSDPPort();
        int retryCnt2 = 0;
        SSDPSearchResponseSocketList ssdpSearchResponseSocketList = getSSDPSearchResponseSocketList();
        while (!ssdpSearchResponseSocketList.open(ssdpPort)) {
            retryCnt2++;
            if (100 < retryCnt2) {
                return false;
            }
            setSSDPPort(ssdpPort + 1);
            ssdpPort = getSSDPPort();
        }
        ssdpSearchResponseSocketList.setControlPoint(this);
        ssdpSearchResponseSocketList.start();
        search(target, mx);
        Disposer disposer = new Disposer(this);
        setDeviceDisposer(disposer);
        disposer.start();
        if (isNMPRMode()) {
            RenewSubscriber renewSub = new RenewSubscriber(this);
            setRenewSubscriber(renewSub);
            renewSub.start();
        }
        return true;
    }

    public boolean start(String target) {
        return start(target, 3);
    }

    public boolean start() {
        return start("upnp:rootdevice", 3);
    }

    public boolean stop() {
        unsubscribe();
        SSDPNotifySocketList ssdpNotifySocketList = getSSDPNotifySocketList();
        ssdpNotifySocketList.stop();
        ssdpNotifySocketList.close();
        ssdpNotifySocketList.clear();
        SSDPSearchResponseSocketList ssdpSearchResponseSocketList = getSSDPSearchResponseSocketList();
        ssdpSearchResponseSocketList.stop();
        ssdpSearchResponseSocketList.close();
        ssdpSearchResponseSocketList.clear();
        HTTPServerList httpServerList = getHTTPServerList();
        httpServerList.stop();
        httpServerList.close();
        httpServerList.clear();
        Disposer disposer = getDeviceDisposer();
        if (disposer != null) {
            disposer.stop();
            setDeviceDisposer(null);
        }
        RenewSubscriber renewSub = getRenewSubscriber();
        if (renewSub != null) {
            renewSub.stop();
            setRenewSubscriber(null);
            return true;
        }
        return true;
    }

    public void setUserData(Object data) {
        this.userData = data;
    }

    public Object getUserData() {
        return this.userData;
    }

    public void print() {
        DeviceList devList = getDeviceList();
        int devCnt = devList.size();
        Debug.message("Device Num = " + devCnt);
        for (int n = 0; n < devCnt; n++) {
            Device dev = devList.getDevice(n);
            Debug.message("[" + n + "] " + dev.getFriendlyName() + ", " + dev.getLeaseTime() + ", " + dev.getElapsedTime());
        }
    }

    private SSDPPacket getDefaultSsdp(String locationURL, String uuid, String localAddress) {
        StringBuffer ssdpContent = new StringBuffer("NOTIFY * HTTP/1.1\r\n");
        ssdpContent.append("HOST: 239.255.255.250:1900\r\n");
        ssdpContent.append("CACHE-CONTROL: max-age=1800\r\n");
        ssdpContent.append("LOCATION: ");
        ssdpContent.append(locationURL);
        ssdpContent.append("\r\n");
        ssdpContent.append("OPT: \"http://schemas.upnp.org/upnp/1/0/\"; ns=01\r\n");
        ssdpContent.append("01-NLS: c663375c-1dd1-11b2-b12f-c5eca95ebebe\r\n");
        ssdpContent.append("NT: urn:schemas-upnp-org:device:HiMultiScreenServerDevice:1\r\n");
        ssdpContent.append("NTS: ssdp:alive\r\n");
        ssdpContent.append("SERVER: Linux/3.4.67_s40 UPnP/1.0 Portable SDK for UPnP devices/1.6.12\r\n");
        ssdpContent.append("X-User-Agent: redsonic\r\n");
        ssdpContent.append("USN: uuid:");
        ssdpContent.append(uuid);
        ssdpContent.append("::urn:schemas-upnp-org:device:HiMultiScreenServerDevice:1\r\n");
        ssdpContent.append("\r\n");
        String packetData = ssdpContent.toString();
        byte[] ssdvRecvBuf = new byte[1024];
        SSDPPacket ssdpPacket = new SSDPPacket(ssdvRecvBuf, ssdvRecvBuf.length);
        ssdpPacket.setLocalAddress(localAddress);
        ssdpPacket.getDatagramPacket().setData(packetData.getBytes());
        ssdpPacket.setTimeStamp(System.currentTimeMillis());
        return ssdpPacket;
    }
}
