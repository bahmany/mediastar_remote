package mktvsmart.screen.socketthread;

import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import com.jcraft.jzlib.GsZilb;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import mktvsmart.screen.CreateSocket;
import mktvsmart.screen.GMScreenGlobalInfo;
import mktvsmart.screen.GlobalConstantValue;
import mktvsmart.screen.GsSendSocket;
import mktvsmart.screen.exception.AppDebug;
import mktvsmart.screen.message.process.MessageProcessor;

/* loaded from: classes.dex */
public class SocketReceiveThread extends Thread {
    private static final String TAG = SocketReceiveThread.class.getSimpleName();
    private final int SOCKET_KEEP_ALIVE_TIMEOUT;
    private boolean enableRecvUsefulData;
    private InputStream inStream;
    private boolean interruptFlag;
    private MessageProcessor msgProc;
    private int totalDataCount;

    public SocketReceiveThread(InputStream is) {
        super("SocketReceiveThread");
        this.interruptFlag = false;
        this.totalDataCount = 0;
        this.SOCKET_KEEP_ALIVE_TIMEOUT = 30000;
        this.inStream = is;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() throws IOException {
        byte[] buffer = new byte[2048];
        int dataLen = 0;
        int dataType = 0;
        int msgResponseState = 0;
        boolean needSendKeepAliveMsg = true;
        Socket tcpSocket = null;
        this.msgProc = MessageProcessor.obtain();
        CreateSocket cSocket = new CreateSocket("", 0);
        try {
            tcpSocket = cSocket.GetSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.enableRecvUsefulData = false;
        long recDataTimeMark = SystemClock.uptimeMillis();
        while (!this.interruptFlag) {
            try {
                if (!this.enableRecvUsefulData) {
                    try {
                        if (this.inStream.read(buffer, 0, 16) != -1) {
                            String dataHeader = new String(new StringBuilder().append((char) buffer[0]).append((char) buffer[1]).append((char) buffer[2]).append((char) buffer[3]).toString());
                            if (dataHeader.equals(GlobalConstantValue.G_MSCREEN_CONTROL_DATA_HEADER_STR)) {
                                dataLen = ((buffer[7] << 24) & ViewCompat.MEASURED_STATE_MASK) | ((buffer[6] << 16) & 16711680) | ((buffer[5] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (buffer[4] & 255);
                                dataType = ((buffer[11] << 24) & ViewCompat.MEASURED_STATE_MASK) | ((buffer[10] << 16) & 16711680) | ((buffer[9] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (buffer[8] & 255);
                                msgResponseState = ((buffer[15] << 24) & ViewCompat.MEASURED_STATE_MASK) | ((buffer[14] << 16) & 16711680) | ((buffer[13] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (buffer[12] & 255);
                                this.enableRecvUsefulData = true;
                                Log.d(TAG, "control data Type = " + dataType);
                                needSendKeepAliveMsg = true;
                            }
                        } else {
                            this.msgProc.postEmptyMessage(GlobalConstantValue.GSCMD_NOTIFY_SOCKET_CLOSED);
                            this.interruptFlag = true;
                            AppDebug.writeLog("App return login menu, because receive data is empty.");
                        }
                    } catch (SocketException e2) {
                        this.msgProc.postEmptyMessage(GlobalConstantValue.GSCMD_NOTIFY_SOCKET_CLOSED);
                        this.interruptFlag = true;
                        AppDebug.writeLog("App return login menu, SocketException :\n" + e2.getMessage());
                    } catch (SocketTimeoutException e3) {
                        if (needSendKeepAliveMsg) {
                            recDataTimeMark = SystemClock.uptimeMillis();
                            needSendKeepAliveMsg = false;
                            boolean bsendOk = GsSendSocket.sendOnlyCommandSocketToStb(tcpSocket, 26);
                            Log.d(TAG, "send heart run " + (bsendOk ? "ok" : "fail"));
                        } else if (SystemClock.uptimeMillis() - recDataTimeMark > 30000) {
                            this.msgProc.postEmptyMessage(GlobalConstantValue.GSCMD_NOTIFY_SOCKET_CLOSED);
                            this.interruptFlag = true;
                            Log.d(TAG, "send heartrun over 30 seconds, nothing reveive");
                            AppDebug.writeLog("App return login menu, kepp alive msg timeout beyond 3 times");
                        }
                    }
                } else {
                    byte[] response_buffer = new byte[dataLen + 8];
                    this.totalDataCount = 0;
                    int dataLenLeft = dataLen;
                    while (this.totalDataCount < dataLen) {
                        try {
                            int recvDataCount = this.inStream.read(buffer, 0, Math.min(dataLenLeft, 2048));
                            if (recvDataCount == -1) {
                                break;
                            }
                            System.arraycopy(buffer, 0, response_buffer, this.totalDataCount, recvDataCount);
                            this.totalDataCount += recvDataCount;
                            dataLenLeft -= recvDataCount;
                        } catch (SocketTimeoutException e4) {
                            System.out.println("gmscreen SocketTimeoutException=totalDataCount=" + this.totalDataCount);
                            Message dataMessage = Message.obtain();
                            dataMessage.arg1 = 0;
                            dataMessage.arg2 = -1;
                            dataMessage.what = dataType;
                            this.msgProc.postMessage(dataMessage);
                            e4.printStackTrace();
                        }
                    }
                    if (this.totalDataCount == dataLen) {
                        if (dataType <= 2000 || dataType >= 2999) {
                            recDataTimeMark = SystemClock.uptimeMillis();
                            needSendKeepAliveMsg = true;
                        }
                        if (dataType == 2015) {
                            GMScreenGlobalInfo.getCurStbInfo().setClient_type(0);
                        }
                        Message dataMessage2 = Message.obtain();
                        dataMessage2.arg1 = 0;
                        dataMessage2.arg2 = msgResponseState;
                        byte[] unCompressBuffer = null;
                        if (dataLen != 0) {
                            unCompressBuffer = GsZilb.UnCompress(response_buffer);
                            dataMessage2.arg1 = unCompressBuffer.length;
                        }
                        dataMessage2.what = dataType;
                        Bundle data = new Bundle();
                        data.putByteArray("ReceivedData", unCompressBuffer);
                        dataMessage2.setData(data);
                        System.out.println("dataMessage.what==" + dataMessage2.what + "==msgResponseState==" + msgResponseState);
                        this.msgProc.postMessage(dataMessage2);
                    }
                    this.enableRecvUsefulData = false;
                }
            } catch (IOException e5) {
                e5.printStackTrace();
            }
        }
        System.out.println("run interrupt");
    }

    @Override // java.lang.Thread
    public void interrupt() {
        this.interruptFlag = true;
        super.interrupt();
    }
}
