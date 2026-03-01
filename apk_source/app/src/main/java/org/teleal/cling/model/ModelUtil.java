package org.teleal.cling.model;

import com.hisilicon.multiscreen.protocol.ClientInfo;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

/* loaded from: classes.dex */
public class ModelUtil {
    public static final boolean ANDROID_EMULATOR;
    public static final boolean ANDROID_RUNTIME;

    /* JADX WARN: Removed duplicated region for block: B:11:0x004b  */
    static {
        /*
            r1 = 0
            java.lang.Thread r4 = java.lang.Thread.currentThread()     // Catch: java.lang.Exception -> L53
            java.lang.ClassLoader r4 = r4.getContextClassLoader()     // Catch: java.lang.Exception -> L53
            java.lang.String r5 = "android.os.Build"
            java.lang.Class r0 = r4.loadClass(r5)     // Catch: java.lang.Exception -> L53
            java.lang.String r4 = "ID"
            java.lang.reflect.Field r4 = r0.getField(r4)     // Catch: java.lang.Exception -> L53
            r5 = 0
            java.lang.Object r4 = r4.get(r5)     // Catch: java.lang.Exception -> L53
            if (r4 == 0) goto L4f
            r1 = 1
        L1d:
            org.teleal.cling.model.ModelUtil.ANDROID_RUNTIME = r1
            r2 = 0
            java.lang.Thread r4 = java.lang.Thread.currentThread()     // Catch: java.lang.Exception -> L51
            java.lang.ClassLoader r4 = r4.getContextClassLoader()     // Catch: java.lang.Exception -> L51
            java.lang.String r5 = "android.os.Build"
            java.lang.Class r0 = r4.loadClass(r5)     // Catch: java.lang.Exception -> L51
            java.lang.String r4 = "PRODUCT"
            java.lang.reflect.Field r4 = r0.getField(r4)     // Catch: java.lang.Exception -> L51
            r5 = 0
            java.lang.Object r3 = r4.get(r5)     // Catch: java.lang.Exception -> L51
            java.lang.String r3 = (java.lang.String) r3     // Catch: java.lang.Exception -> L51
            java.lang.String r4 = "google_sdk"
            boolean r4 = r4.equals(r3)     // Catch: java.lang.Exception -> L51
            if (r4 != 0) goto L4b
            java.lang.String r4 = "sdk"
            boolean r4 = r4.equals(r3)     // Catch: java.lang.Exception -> L51
            if (r4 == 0) goto L4c
        L4b:
            r2 = 1
        L4c:
            org.teleal.cling.model.ModelUtil.ANDROID_EMULATOR = r2
            return
        L4f:
            r1 = 0
            goto L1d
        L51:
            r4 = move-exception
            goto L4c
        L53:
            r4 = move-exception
            goto L1d
        */
        throw new UnsupportedOperationException("Method not decompiled: org.teleal.cling.model.ModelUtil.<clinit>():void");
    }

    public static boolean isStringConvertibleType(Set<Class> stringConvertibleTypes, Class clazz) {
        if (clazz.isEnum()) {
            return true;
        }
        for (Class toStringOutputType : stringConvertibleTypes) {
            if (toStringOutputType.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidUDAName(String name) {
        return ANDROID_RUNTIME ? (name == null || name.length() == 0) ? false : true : (name == null || name.length() == 0 || name.toLowerCase().startsWith("xml") || !name.matches(Constants.REGEX_UDA_NAME)) ? false : true;
    }

    public static InetAddress getInetAddressByName(String name) {
        try {
            return InetAddress.getByName(name);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String toCommaSeparatedList(Object[] o) {
        return toCommaSeparatedList(o, true, false);
    }

    public static String toCommaSeparatedList(Object[] o, boolean escapeCommas, boolean escapeDoubleQuotes) {
        if (o == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object obj : o) {
            String objString = obj.toString().replaceAll("\\\\", "\\\\\\\\");
            if (escapeCommas) {
                objString = objString.replaceAll(ClientInfo.SEPARATOR_BETWEEN_VARS, "\\\\,");
            }
            if (escapeDoubleQuotes) {
                objString = objString.replaceAll("\"", "\\\"");
            }
            sb.append(objString).append(ClientInfo.SEPARATOR_BETWEEN_VARS);
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String[] fromCommaSeparatedList(String s) {
        return fromCommaSeparatedList(s, true);
    }

    public static String[] fromCommaSeparatedList(String s, boolean unescapeCommas) {
        if (s == null || s.length() == 0) {
            return null;
        }
        if (unescapeCommas) {
            s = s.replaceAll("\\\\,", "XXX1122334455XXX");
        }
        String[] split = s.split(ClientInfo.SEPARATOR_BETWEEN_VARS);
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].replaceAll("XXX1122334455XXX", ClientInfo.SEPARATOR_BETWEEN_VARS);
            split[i] = split[i].replaceAll("\\\\\\\\", "\\\\");
        }
        return split;
    }

    public static String toTimeString(long seconds) {
        long hour = seconds / 3600;
        long remainder = seconds % 3600;
        long minute = remainder / 60;
        long second = remainder % 60;
        return String.format("%02d", Long.valueOf(hour)) + ":" + String.format("%02d", Long.valueOf(minute)) + ":" + String.format("%02d", Long.valueOf(second));
    }

    public static long fromTimeString(String s) {
        if (s == null) {
            return 0L;
        }
        String[] split = s.split(":");
        if (split.length == 1) {
            return 0L;
        }
        if (split.length == 2) {
            try {
                long minite = Integer.parseInt(split[0]);
                long second = (int) Float.parseFloat(split[1]);
                return (60 * minite) + second;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (split.length >= 3) {
            try {
                long hour = Integer.parseInt(split[0]);
                long minite2 = Integer.parseInt(split[1]);
                long second2 = (int) Float.parseFloat(split[2]);
                return (3600 * hour) + (60 * minite2) + second2;
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
        return 0L;
    }

    public static String commaToNewline(String s) {
        StringBuilder sb = new StringBuilder();
        String[] split = s.split(ClientInfo.SEPARATOR_BETWEEN_VARS);
        for (String splitString : split) {
            sb.append(splitString).append(ClientInfo.SEPARATOR_BETWEEN_VARS).append("\n");
        }
        if (sb.length() > 2) {
            sb.deleteCharAt(sb.length() - 2);
        }
        return sb.toString();
    }

    public static String getLocalHostName(boolean includeDomain) {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            return (includeDomain || hostname.indexOf(".") == -1) ? hostname : hostname.substring(0, hostname.indexOf("."));
        } catch (Exception e) {
            return "UNKNOWN HOST";
        }
    }

    public static byte[] getFirstNetworkInterfaceHardwareAddress() throws SocketException {
        try {
            Enumeration<NetworkInterface> interfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            Iterator it = Collections.list(interfaceEnumeration).iterator();
            while (it.hasNext()) {
                NetworkInterface iface = (NetworkInterface) it.next();
                if (!iface.isLoopback() && iface.isUp() && iface.getHardwareAddress() != null) {
                    return iface.getHardwareAddress();
                }
            }
            throw new RuntimeException("Could not discover first network interface hardware address");
        } catch (Exception e) {
            throw new RuntimeException("Could not discover first network interface hardware address");
        }
    }
}
