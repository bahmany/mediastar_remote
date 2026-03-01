package mktvsmart.screen.socketthread;

import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import mktvsmart.screen.GMScreenGlobalInfo;
import mktvsmart.screen.GlobalConstantValue;
import mktvsmart.screen.GsMobileLoginInfo;
import mktvsmart.screen.message.process.MessageProcessor;
import org.apache.mina.proxy.handlers.socks.SocksProxyConstants;

/* loaded from: classes.dex */
public class UdpSocketReceiveBroadcastThread extends Thread {
    private static final int TIMEOUT_5S = 5000;
    private String TAG;
    private boolean interruptFlag;
    private MessageProcessor msgProc;
    private ArrayList<GsMobileLoginInfo> stbInfoList;
    private long timeMark;
    private DatagramSocket udpBroadcastSocket;
    private DatagramPacket udpPacket;

    public UdpSocketReceiveBroadcastThread() {
        super("UdpSocketReceiveBroadcastThread");
        this.TAG = UdpSocketReceiveBroadcastThread.class.getSimpleName();
        this.interruptFlag = false;
        this.stbInfoList = new ArrayList<>();
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() throws IOException {
        String stbHost;
        byte[] receiveBuffer;
        super.run();
        this.msgProc = MessageProcessor.obtain();
        try {
            this.udpBroadcastSocket = new DatagramSocket(GlobalConstantValue.G_MS_BROADCAST_PORT);
            this.udpBroadcastSocket.setSoTimeout(5000);
            byte[] buffer = new byte[2048];
            this.udpPacket = new DatagramPacket(buffer, buffer.length);
            Log.d(this.TAG, "Thread  BroadcastThread onStart");
            this.timeMark = SystemClock.uptimeMillis();
            while (!this.interruptFlag) {
                try {
                    this.udpBroadcastSocket.receive(this.udpPacket);
                    stbHost = this.udpPacket.getAddress().getHostAddress();
                    byte[] bArr = new byte[this.udpPacket.getLength()];
                    receiveBuffer = this.udpPacket.getData();
                } catch (SocketTimeoutException e) {
                    this.stbInfoList.clear();
                    Log.d(this.TAG, "SocketTimeoutException, no device found");
                    update_stb_info_to_login_list();
                    this.timeMark = SystemClock.uptimeMillis();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                if (this.udpPacket.getLength() == 108) {
                    scramble_stb_info_for_broadcast(receiveBuffer, this.udpPacket.getLength());
                    String stringMagicCode = new String(receiveBuffer, 0, 12);
                    if (stringMagicCode.equals(GlobalConstantValue.G_MS_BROADCAST_INFO_MAGIC_CODE)) {
                        GsMobileLoginInfo loginInfoTemp = new GsMobileLoginInfo(receiveBuffer);
                        loginInfoTemp.setLastFoundTime(SystemClock.uptimeMillis());
                        if (GMScreenGlobalInfo.check_is_apk_match_platform(loginInfoTemp.getPlatform_id())) {
                            boolean bFoundNew = true;
                            int index = 0;
                            while (true) {
                                if (index >= this.stbInfoList.size()) {
                                    break;
                                }
                                GsMobileLoginInfo longinInfoInList = this.stbInfoList.get(index);
                                if (!longinInfoInList.getStb_sn_disp().equals(loginInfoTemp.getStb_sn_disp())) {
                                    index++;
                                } else {
                                    longinInfoInList.setLastFoundTime(loginInfoTemp.getLastFoundTime());
                                    if (loginInfoTemp.getIs_current_stb_connected_full() == 1) {
                                        this.stbInfoList.remove(index);
                                        update_stb_info_to_login_list();
                                        Log.d(this.TAG, "stb is full remove it " + stbHost);
                                    } else {
                                        this.stbInfoList.set(index, loginInfoTemp);
                                        if (!longinInfoInList.getStb_ip_address_disp().equals(loginInfoTemp.getStb_ip_address_disp())) {
                                            update_stb_info_to_login_list();
                                        }
                                    }
                                    bFoundNew = false;
                                }
                            }
                            if (bFoundNew && loginInfoTemp.getIs_current_stb_connected_full() == 0) {
                                this.stbInfoList.add(loginInfoTemp);
                                update_stb_info_to_login_list();
                            }
                        }
                    } else {
                        Log.d(this.TAG, "Thread  BroadcastThread receive error data. magic code wrong!: " + stringMagicCode);
                    }
                    if (SystemClock.uptimeMillis() - this.timeMark >= 5000) {
                        boolean bDeviceRM = false;
                        int index2 = 0;
                        while (index2 < this.stbInfoList.size()) {
                            GsMobileLoginInfo longinInfoInList2 = this.stbInfoList.get(index2);
                            if (SystemClock.uptimeMillis() - longinInfoInList2.getLastFoundTime() > 5000) {
                                this.stbInfoList.remove(index2);
                                Log.d(this.TAG, "remove " + longinInfoInList2.getStb_ip_address_disp() + " because stb no response");
                                bDeviceRM = true;
                            } else {
                                index2++;
                            }
                        }
                        if (bDeviceRM) {
                            update_stb_info_to_login_list();
                        }
                        this.timeMark = SystemClock.uptimeMillis();
                    }
                }
            }
            Log.d(this.TAG, "run interrupt1");
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
    }

    @Override // java.lang.Thread
    public void interrupt() {
        Log.d(this.TAG, "recv interrupt2");
        this.interruptFlag = true;
        if (this.udpBroadcastSocket != null) {
            this.udpBroadcastSocket.disconnect();
            this.udpBroadcastSocket.close();
        }
        super.interrupt();
    }

    public static void scramble_stb_info_for_broadcast(byte[] send_buff, int buffLength) {
        for (int index = 0; index < buffLength / 2; index++) {
            byte temp = send_buff[(buffLength - 1) - index];
            send_buff[(buffLength - 1) - index] = send_buff[index];
            send_buff[index] = temp;
            send_buff[index] = (byte) (send_buff[index] ^ SocksProxyConstants.V4_REPLY_REQUEST_REJECTED_OR_FAILED);
            send_buff[(buffLength - 1) - index] = (byte) (send_buff[(buffLength - 1) - index] ^ SocksProxyConstants.V4_REPLY_REQUEST_REJECTED_OR_FAILED);
        }
        if (buffLength % 2 != 0) {
            send_buff[buffLength / 2] = (byte) (send_buff[buffLength / 2] ^ SocksProxyConstants.V4_REPLY_REQUEST_REJECTED_OR_FAILED);
        }
    }

    private void update_stb_info_to_login_list() {
        Message dataMessage = Message.obtain();
        dataMessage.what = GlobalConstantValue.GSCMD_NOTIFY_BROADCAST_LOGIN_INFO_UPDATED;
        dataMessage.obj = this.stbInfoList;
        this.msgProc.postMessage(dataMessage);
    }
}
