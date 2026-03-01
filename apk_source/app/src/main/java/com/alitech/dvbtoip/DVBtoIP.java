package com.alitech.dvbtoip;

import java.util.ArrayList;

/* loaded from: classes.dex */
public class DVBtoIP {

    public static class ChannelInfo {
        public String desc;
        public String duration;
        public String id;
        public boolean isEncrypted;
        public boolean isRadio;
        public String startTime;
    }

    public static class ProgramInfo {
        public String desc;
        public String id;
    }

    public static class ServerInfo {
        public String desc;
        public String id;
        public String ip;
        public String location;
        public String port;
    }

    private static native void __destroyResourceForPlayer();

    private static native int __initResourceForPlayer(int i, String str, int i2, int i3);

    public static native ArrayList<ChannelInfo> getChannelList(String str, boolean z);

    public static native String getChannelURL(String str);

    public static native void getChannelUserKey(String str);

    public static native ArrayList<ServerInfo> getServerList(boolean z);

    public static native void setSeed(int i);

    public static boolean initResourceForPlayer(int rtpPort, String pipeFilePath, int playType, int getKeyWay) {
        return __initResourceForPlayer(rtpPort, pipeFilePath, playType, getKeyWay) == 0;
    }

    public static void destroyResourceForPlayer() {
        __destroyResourceForPlayer();
    }

    static {
        try {
            System.loadLibrary("dvbtoip");
        } catch (UnsatisfiedLinkError ule) {
            System.err.println("WARNING: Could not load library!");
            ule.printStackTrace();
        }
    }
}
