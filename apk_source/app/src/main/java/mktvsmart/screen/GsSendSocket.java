package mktvsmart.screen;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;

/* loaded from: classes.dex */
public class GsSendSocket {
    private static final int MAX_DATA_LENGTH_BIT = 7;
    private static final String SOCKET_HEADER_END_FLAG = "End";
    private static final String SOCKET_HEADER_START_FLAG = "Start";

    private static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        for (int i = 0; i < count; i++) {
            bs[i] = src[i + begin];
        }
        return bs;
    }

    public static synchronized boolean sendSocketToStb(byte[] buffer, Socket tcpSocket, int offset, int dataLength, int commandType) {
        boolean z;
        byte[] newBytes = subBytes(buffer, offset, dataLength);
        String str = new String(newBytes);
        String dataLenStr = new StringBuilder().append(dataLength).toString();
        int needAddZeroNum = 7 - dataLenStr.length();
        String strDataLen = "";
        for (int i = 0; i < needAddZeroNum; i++) {
            strDataLen = String.valueOf(strDataLen) + "0";
        }
        byte[] newBuffer = (SOCKET_HEADER_START_FLAG + strDataLen + dataLength + SOCKET_HEADER_END_FLAG + str).getBytes();
        try {
            OutputStream out = tcpSocket.getOutputStream();
            out.write(newBuffer, 0, newBuffer.length);
            out.flush();
            z = true;
        } catch (Exception e) {
            e.printStackTrace();
            z = false;
        }
        return z;
    }

    public static boolean sendOnlyCommandSocketToStb(Socket tcpSocket, int commandType) throws UnsupportedEncodingException {
        byte[] dataCommand = null;
        DataParser parser = ParserFactory.getParser();
        try {
            dataCommand = parser.serialize(null, commandType).getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sendSocketToStb(dataCommand, tcpSocket, 0, dataCommand.length, commandType);
    }
}
