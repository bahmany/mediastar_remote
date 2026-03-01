package mktvsmart.screen.util;

import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import com.hisilicon.dlna.dmc.data.PlaylistSQLiteHelper;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServerUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class NetWorkUtils {
    public static String FLAG_HEAD = "@#!AA";

    public static boolean isInnerIP(String ipAddress) {
        long ipNum = getIpNum(ipAddress);
        long aBegin = getIpNum("10.0.0.0");
        long aEnd = getIpNum("10.255.255.255");
        long bBegin = getIpNum("172.16.0.0");
        long bEnd = getIpNum("172.31.255.255");
        long cBegin = getIpNum("192.168.0.0");
        long cEnd = getIpNum("192.168.255.255");
        return isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd) || ipAddress.equals(HttpServerUtil.LOOP);
    }

    private static long getIpNum(String ipAddress) {
        String[] ip = ipAddress.split("\\.");
        long a = Integer.parseInt(ip[0]);
        long b = Integer.parseInt(ip[1]);
        long c = Integer.parseInt(ip[2]);
        long d = Integer.parseInt(ip[3]);
        long ipNum = (256 * a * 256 * 256) + (256 * b * 256) + (256 * c) + d;
        return ipNum;
    }

    private static boolean isInner(long userIp, long begin, long end) {
        return userIp >= begin && userIp <= end;
    }

    public static final String int2Ip(int ip) {
        StringBuffer ipAddressBuff = new StringBuffer();
        ipAddressBuff.append(ip & 255);
        ipAddressBuff.append(".");
        ipAddressBuff.append((ip >> 8) & 255);
        ipAddressBuff.append(".");
        ipAddressBuff.append((ip >> 16) & 255);
        ipAddressBuff.append(".");
        ipAddressBuff.append((ip >> 24) & 255);
        return ipAddressBuff.toString();
    }

    public static int Ip2Int(String strIp) {
        String[] ss = strIp.split("\\.");
        if (ss.length != 4) {
            return 0;
        }
        byte[] bytes = new byte[ss.length];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(ss[i]);
        }
        return byte2Int(bytes);
    }

    private static int byte2Int(byte[] bytes) {
        int n = bytes[0] & 255;
        return n | ((bytes[1] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | ((bytes[2] << 16) & 16711680) | ((bytes[3] << 24) & ViewCompat.MEASURED_STATE_MASK);
    }

    public static boolean isValidIp(String ip) {
        String ip2 = ip.trim();
        if (!ip2.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            return false;
        }
        String[] s = ip2.split("\\.");
        if (Integer.parseInt(s[0]) >= 255 || Integer.parseInt(s[1]) > 255 || Integer.parseInt(s[2]) > 255 || Integer.parseInt(s[3]) > 255) {
            return false;
        }
        return true;
    }

    public static String getWanIpBySN(String sn) throws JSONException, IOException {
        Log.i("test", "test receiveServerBack = haha test7");
        if (sn == null || sn.length() <= 0) {
            return null;
        }
        String ipAddr = null;
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("14.29.35.7", 25788), 3000);
            String request = packRequestData(sn);
            Log.i("test", "test packRequestData(sn) " + request);
            sendRequest(socket, 1, request.getBytes());
            int ip = receiveServerBack(socket);
            ipAddr = int2Ip(ip);
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (UnknownHostException e) {
            Log.i("test", "test receiveServerBack = haha test8");
            e.printStackTrace();
        } catch (IOException e2) {
            Log.i("test", "test receiveServerBack = haha test9");
            e2.printStackTrace();
        }
        System.out.println("ipAddr = " + ipAddr);
        return ipAddr;
    }

    private static String packRequestData(String sn) throws JSONException {
        String data = "";
        try {
            JSONObject root = new JSONObject();
            root.put("id", sn);
            root.put("os", 0);
            root.put(PlaylistSQLiteHelper.COL_TYPE, 0);
            data = root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("data = " + data);
        return data;
    }

    private static void sendRequest(Socket socket, int cmd, byte[] data) throws IOException {
        int dataLen;
        if (data == null) {
            dataLen = 0;
        } else {
            try {
                dataLen = data.length;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        ByteBuffer bytebuffer = ByteBuffer.allocate(FLAG_HEAD.length() + 4 + 4 + dataLen).order(ByteOrder.LITTLE_ENDIAN);
        bytebuffer.put(FLAG_HEAD.getBytes());
        bytebuffer.putInt(cmd);
        bytebuffer.putInt(dataLen);
        if (data != null) {
            bytebuffer.put(data);
        }
        Log.i("test", "test sendRequest = " + bytebuffer);
        OutputStream output = socket.getOutputStream();
        output.write(bytebuffer.array());
        output.flush();
        System.out.println("send = " + new String(bytebuffer.array()));
        System.out.println(" send len = " + bytebuffer.array().length);
    }

    private static int receiveServerBack(Socket socket) throws IOException {
        int ipaddr = 0;
        try {
            socket.setSoTimeout(3000);
            byte[] buffer = new byte[256];
            Log.i("test", "test receiveServerBack = " + buffer);
            int readLen = socket.getInputStream().read(buffer);
            Log.i("test", "test receiveServerBack = " + buffer);
            System.out.println("receiveServerBack = " + buffer);
            ByteBuffer bytebuffer = ByteBuffer.allocate(readLen).order(ByteOrder.LITTLE_ENDIAN);
            bytebuffer.put(buffer, 0, readLen);
            bytebuffer.rewind();
            byte[] flag = new byte[FLAG_HEAD.length()];
            bytebuffer.get(flag);
            if (new String(flag).equals(FLAG_HEAD)) {
                int cmd = bytebuffer.getInt();
                int result = bytebuffer.getInt();
                int dataLen = bytebuffer.getInt();
                if (cmd == 1 && result == 1) {
                    ipaddr = bytebuffer.getInt();
                }
                System.out.println("dataLen = " + dataLen + " cmd = " + cmd + " result = " + result + " ip = " + ipaddr);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return ipaddr;
    }
}
