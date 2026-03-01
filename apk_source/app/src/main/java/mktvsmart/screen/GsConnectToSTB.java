package mktvsmart.screen;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.dataconvert.parser.XmlParser;
import mktvsmart.screen.socketthread.UdpSocketReceiveBroadcastThread;
import mktvsmart.screen.util.AndroidDeviceUtil;
import org.cybergarage.upnp.device.ST;

/* loaded from: classes.dex */
public class GsConnectToSTB {
    private static final String UPNP_HANDSHARK_MSG_MOBILE = "HANDSHAKE_MOBILE";
    private static final String UPNP_HANDSHARK_MSG_MOBILE_LIST = "HANDSHAKE_MOBILE_LIST";

    public static GsMobileLoginInfo upnpConnectToServer(String Address, int Port, int connectType) throws InterruptedException, IOException {
        CreateSocket cSocket;
        Socket tcpSocket;
        InputStream in;
        String handSharkReceveString;
        CreateSocket cSocket2 = null;
        GsMobileLoginInfo loginInfoTemp = new GsMobileLoginInfo();
        loginInfoTemp.setmConnectStatus(-1);
        boolean is_create_socket_success = false;
        try {
            cSocket = new CreateSocket(Address, Port);
            try {
                tcpSocket = cSocket.GetSocket();
                tcpSocket.setSoTimeout(4000);
                in = tcpSocket.getInputStream();
                byte[] handSharkRceveByte = new byte[108];
                OutputStream out = tcpSocket.getOutputStream();
                byte[] handSharkSendData = UPNP_HANDSHARK_MSG_MOBILE.getBytes();
                out.write(handSharkSendData, 0, handSharkSendData.length);
                Thread.sleep(300L);
                in.read(handSharkRceveByte, 0, 108);
                handSharkReceveString = new String(handSharkRceveByte, "UTF-8");
            } catch (ConnectException e) {
                cSocket2 = cSocket;
            } catch (SocketException e2) {
                cSocket2 = cSocket;
            } catch (SocketTimeoutException e3) {
                cSocket2 = cSocket;
            } catch (IOException e4) {
                cSocket2 = cSocket;
            } catch (IllegalArgumentException e5) {
                cSocket2 = cSocket;
            } catch (Exception e6) {
                cSocket2 = cSocket;
            }
        } catch (IllegalArgumentException e7) {
        } catch (ConnectException e8) {
        } catch (SocketException e9) {
        } catch (SocketTimeoutException e10) {
        } catch (IOException e11) {
        } catch (Exception e12) {
        }
        if (!handSharkReceveString.trim().equals(UPNP_HANDSHARK_MSG_MOBILE)) {
            loginInfoTemp.setmConnectStatus(-8);
            cSocket.DestroySocket();
            return loginInfoTemp;
        }
        Thread.sleep(300L);
        ArrayList<Map<String, String>> mLogInfos = new ArrayList<>();
        Map<String, String> logInfo = new HashMap<>();
        logInfo.put("data", Build.MODEL);
        logInfo.put(ST.UUID_DEVICE, AndroidDeviceUtil.getDeviceUUID());
        mLogInfos.add(logInfo);
        XmlParser xmlParser = new XmlParser();
        byte[] sendData = xmlParser.serialize(mLogInfos, GlobalConstantValue.GMS_MSG_REQUEST_LOGIN_INFO).getBytes();
        GsSendSocket.sendSocketToStb(sendData, tcpSocket, 0, sendData.length, GlobalConstantValue.GMS_MSG_REQUEST_LOGIN_INFO);
        byte[] receiveBuffer = new byte[108];
        is_create_socket_success = true;
        int bytesNum = in.read(receiveBuffer, 0, 108);
        System.out.println("Connect type:" + connectType);
        System.out.println("connect server data bytes Num: " + bytesNum);
        boolean isStbValid = false;
        int isStbConnectFull = 0;
        if (bytesNum == 108) {
            UdpSocketReceiveBroadcastThread.scramble_stb_info_for_broadcast(receiveBuffer, bytesNum);
            System.out.println("receiveBuffer =  " + new String(receiveBuffer));
            String stringMagicCode = new String(receiveBuffer, 0, 12);
            if (stringMagicCode.equals(GlobalConstantValue.G_MS_BROADCAST_INFO_MAGIC_CODE)) {
                GsMobileLoginInfo loginInfoTemp2 = new GsMobileLoginInfo(receiveBuffer);
                try {
                    if (GMScreenGlobalInfo.check_is_apk_match_platform(loginInfoTemp2.getPlatform_id())) {
                        isStbValid = true;
                        if (connectType == 1) {
                            loginInfoTemp2.setmIpLoginMark(1);
                        }
                        loginInfoTemp2.setmConnectStatus(bytesNum);
                        GMScreenGlobalInfo.getCurStbInfo().setClient_type(loginInfoTemp2.getClient_type());
                        isStbConnectFull = loginInfoTemp2.getIs_current_stb_connected_full();
                        System.out.println("connect server getClient_type: " + loginInfoTemp2.getClient_type());
                        loginInfoTemp = loginInfoTemp2;
                    } else {
                        loginInfoTemp = loginInfoTemp2;
                    }
                } catch (ConnectException e13) {
                    loginInfoTemp = loginInfoTemp2;
                    cSocket2 = cSocket;
                    loginInfoTemp.setmConnectStatus(-3);
                    cSocket2.DestroySocket();
                    return loginInfoTemp;
                } catch (SocketException e14) {
                    loginInfoTemp = loginInfoTemp2;
                    cSocket2 = cSocket;
                    loginInfoTemp.setmConnectStatus(-4);
                    cSocket2.DestroySocket();
                    return loginInfoTemp;
                } catch (SocketTimeoutException e15) {
                    loginInfoTemp = loginInfoTemp2;
                    cSocket2 = cSocket;
                    if (is_create_socket_success) {
                        loginInfoTemp.setmConnectStatus(-10);
                    } else {
                        loginInfoTemp.setmConnectStatus(-2);
                    }
                    cSocket2.DestroySocket();
                    return loginInfoTemp;
                } catch (IOException e16) {
                    loginInfoTemp = loginInfoTemp2;
                    cSocket2 = cSocket;
                    cSocket2.DestroySocket();
                    return loginInfoTemp;
                } catch (IllegalArgumentException e17) {
                    loginInfoTemp = loginInfoTemp2;
                    cSocket2 = cSocket;
                    loginInfoTemp.setmConnectStatus(-5);
                    cSocket2.DestroySocket();
                    return loginInfoTemp;
                } catch (Exception e18) {
                    loginInfoTemp = loginInfoTemp2;
                    cSocket2 = cSocket;
                    cSocket2.DestroySocket();
                    return loginInfoTemp;
                }
            } else {
                System.out.println("stringMagicCode =  " + stringMagicCode);
            }
        }
        if (!isStbValid) {
            loginInfoTemp.setmConnectStatus(-10);
            cSocket.DestroySocket();
        } else if (isStbConnectFull == 1) {
            loginInfoTemp.setmConnectStatus(-7);
            cSocket.DestroySocket();
        } else {
            ParserFactory.setDataType(loginInfoTemp.getSend_data_type());
        }
        return loginInfoTemp;
    }

    public static GsMobileLoginInfo upnpGetDeviceList(String Address, int Port, int connectType) throws InterruptedException, IOException {
        CreateSocket cSocket = null;
        GsMobileLoginInfo loginInfoTemp = new GsMobileLoginInfo();
        loginInfoTemp.setmConnectStatus(-1);
        try {
            CreateSocket cSocket2 = new CreateSocket(Address, Port);
            try {
                cSocket2.DestroySocket();
                Socket tcpSocket = cSocket2.GetSocket();
                tcpSocket.setSoTimeout(4000);
                InputStream in = tcpSocket.getInputStream();
                OutputStream out = tcpSocket.getOutputStream();
                byte[] handSharkSendData = UPNP_HANDSHARK_MSG_MOBILE_LIST.getBytes();
                out.write(handSharkSendData, 0, handSharkSendData.length);
                Thread.sleep(300L);
                byte[] receiveBuffer = new byte[108];
                int bytesNum = in.read(receiveBuffer, 0, 108);
                System.out.println("Connect type:" + connectType);
                System.out.println("connect server data bytes Num: " + bytesNum);
                boolean isStbValid = false;
                int isStbConnectFull = 0;
                if (bytesNum == 108) {
                    UdpSocketReceiveBroadcastThread.scramble_stb_info_for_broadcast(receiveBuffer, bytesNum);
                    System.out.println("receiveBuffer =  " + new String(receiveBuffer));
                    String stringMagicCode = new String(receiveBuffer, 0, 12);
                    if (stringMagicCode.equals(GlobalConstantValue.G_MS_BROADCAST_INFO_MAGIC_CODE)) {
                        GsMobileLoginInfo loginInfoTemp2 = new GsMobileLoginInfo(receiveBuffer);
                        try {
                            if (GMScreenGlobalInfo.check_is_apk_match_platform(loginInfoTemp2.getPlatform_id())) {
                                isStbValid = true;
                                if (connectType == 1) {
                                    loginInfoTemp2.setmIpLoginMark(1);
                                }
                                loginInfoTemp2.setmConnectStatus(bytesNum);
                                GMScreenGlobalInfo.getCurStbInfo().setClient_type(loginInfoTemp2.getClient_type());
                                isStbConnectFull = loginInfoTemp2.getIs_current_stb_connected_full();
                                System.out.println("connect server getClient_type: " + loginInfoTemp2.getClient_type());
                                loginInfoTemp = loginInfoTemp2;
                            } else {
                                loginInfoTemp = loginInfoTemp2;
                            }
                        } catch (IllegalArgumentException e) {
                            loginInfoTemp = loginInfoTemp2;
                            cSocket = cSocket2;
                            loginInfoTemp.setmConnectStatus(-5);
                            cSocket.DestroySocket();
                            cSocket.DestroySocket();
                            return loginInfoTemp;
                        } catch (ConnectException e2) {
                            loginInfoTemp = loginInfoTemp2;
                            cSocket = cSocket2;
                            loginInfoTemp.setmConnectStatus(-3);
                            cSocket.DestroySocket();
                            cSocket.DestroySocket();
                            return loginInfoTemp;
                        } catch (SocketException e3) {
                            loginInfoTemp = loginInfoTemp2;
                            cSocket = cSocket2;
                            loginInfoTemp.setmConnectStatus(-4);
                            cSocket.DestroySocket();
                            cSocket.DestroySocket();
                            return loginInfoTemp;
                        } catch (SocketTimeoutException e4) {
                            loginInfoTemp = loginInfoTemp2;
                            cSocket = cSocket2;
                            loginInfoTemp.setmConnectStatus(-2);
                            cSocket.DestroySocket();
                            cSocket.DestroySocket();
                            return loginInfoTemp;
                        } catch (IOException e5) {
                            loginInfoTemp = loginInfoTemp2;
                            cSocket = cSocket2;
                            cSocket.DestroySocket();
                            cSocket.DestroySocket();
                            return loginInfoTemp;
                        } catch (Exception e6) {
                            loginInfoTemp = loginInfoTemp2;
                            cSocket = cSocket2;
                            cSocket.DestroySocket();
                            cSocket.DestroySocket();
                            return loginInfoTemp;
                        }
                    } else {
                        System.out.println("stringMagicCode =  " + stringMagicCode);
                    }
                }
                if (!isStbValid) {
                    loginInfoTemp.setmConnectStatus(-10);
                    cSocket2.DestroySocket();
                    cSocket = cSocket2;
                } else if (isStbConnectFull == 1) {
                    loginInfoTemp.setmConnectStatus(-7);
                    cSocket2.DestroySocket();
                    cSocket = cSocket2;
                } else {
                    cSocket2.DestroySocket();
                    cSocket = cSocket2;
                }
            } catch (IllegalArgumentException e7) {
                cSocket = cSocket2;
            } catch (ConnectException e8) {
                cSocket = cSocket2;
            } catch (SocketException e9) {
                cSocket = cSocket2;
            } catch (SocketTimeoutException e10) {
                cSocket = cSocket2;
            } catch (IOException e11) {
                cSocket = cSocket2;
            } catch (Exception e12) {
                cSocket = cSocket2;
            }
        } catch (IllegalArgumentException e13) {
        } catch (ConnectException e14) {
        } catch (SocketException e15) {
        } catch (SocketTimeoutException e16) {
        } catch (IOException e17) {
        } catch (Exception e18) {
        }
        cSocket.DestroySocket();
        return loginInfoTemp;
    }

    public static GsMobileLoginInfo connecttoserver(String Address, int Port, int connectType) throws InterruptedException, IOException {
        CreateSocket cSocket = null;
        GsMobileLoginInfo loginInfoTemp = new GsMobileLoginInfo();
        loginInfoTemp.setmConnectStatus(-1);
        try {
            CreateSocket cSocket2 = new CreateSocket(Address, Port);
            try {
                Socket tcpSocket = cSocket2.GetSocket();
                tcpSocket.setSoTimeout(4000);
                InputStream in = tcpSocket.getInputStream();
                ArrayList<Map<String, String>> mLogInfos = new ArrayList<>();
                Map<String, String> logInfo = new HashMap<>();
                logInfo.put("data", Build.MODEL);
                logInfo.put(ST.UUID_DEVICE, AndroidDeviceUtil.getDeviceUUID());
                mLogInfos.add(logInfo);
                XmlParser xmlParser = new XmlParser();
                byte[] sendData = xmlParser.serialize(mLogInfos, GlobalConstantValue.GMS_MSG_REQUEST_LOGIN_INFO).getBytes();
                GsSendSocket.sendSocketToStb(sendData, tcpSocket, 0, sendData.length, GlobalConstantValue.GMS_MSG_REQUEST_LOGIN_INFO);
                System.out.println("sendBuffer =  " + new String(sendData));
                Thread.sleep(300L);
                byte[] receiveBuffer = new byte[108];
                int bytesNum = in.read(receiveBuffer, 0, 108);
                System.out.println("Connect type:" + connectType);
                System.out.println("connect server data bytes Num: " + bytesNum);
                boolean isStbValid = false;
                int isStbConnectFull = 0;
                if (bytesNum == 108) {
                    UdpSocketReceiveBroadcastThread.scramble_stb_info_for_broadcast(receiveBuffer, bytesNum);
                    System.out.println("receiveBuffer =  " + new String(receiveBuffer));
                    String stringMagicCode = new String(receiveBuffer, 0, 12);
                    if (stringMagicCode.equals(GlobalConstantValue.G_MS_BROADCAST_INFO_MAGIC_CODE)) {
                        GsMobileLoginInfo loginInfoTemp2 = new GsMobileLoginInfo(receiveBuffer);
                        try {
                            if (GMScreenGlobalInfo.check_is_apk_match_platform(loginInfoTemp2.getPlatform_id())) {
                                isStbValid = true;
                                if (connectType == 1) {
                                    loginInfoTemp2.setmIpLoginMark(1);
                                }
                                loginInfoTemp2.setmConnectStatus(bytesNum);
                                GMScreenGlobalInfo.getCurStbInfo().setClient_type(loginInfoTemp2.getClient_type());
                                isStbConnectFull = loginInfoTemp2.getIs_current_stb_connected_full();
                                System.out.println("connect server getClient_type: " + loginInfoTemp2.getClient_type());
                                loginInfoTemp = loginInfoTemp2;
                            } else {
                                loginInfoTemp = loginInfoTemp2;
                            }
                        } catch (ConnectException e) {
                            loginInfoTemp = loginInfoTemp2;
                            cSocket = cSocket2;
                            loginInfoTemp.setmConnectStatus(-3);
                            cSocket.DestroySocket();
                            return loginInfoTemp;
                        } catch (SocketTimeoutException e2) {
                            loginInfoTemp = loginInfoTemp2;
                            cSocket = cSocket2;
                            loginInfoTemp.setmConnectStatus(-2);
                            cSocket.DestroySocket();
                            return loginInfoTemp;
                        } catch (IOException e3) {
                            loginInfoTemp = loginInfoTemp2;
                            cSocket = cSocket2;
                            cSocket.DestroySocket();
                            return loginInfoTemp;
                        } catch (IllegalArgumentException e4) {
                            loginInfoTemp = loginInfoTemp2;
                            cSocket = cSocket2;
                            loginInfoTemp.setmConnectStatus(-5);
                            cSocket.DestroySocket();
                            return loginInfoTemp;
                        } catch (SocketException e5) {
                            loginInfoTemp = loginInfoTemp2;
                            cSocket = cSocket2;
                            loginInfoTemp.setmConnectStatus(-4);
                            cSocket.DestroySocket();
                            return loginInfoTemp;
                        } catch (Exception e6) {
                            loginInfoTemp = loginInfoTemp2;
                            cSocket = cSocket2;
                            cSocket.DestroySocket();
                            return loginInfoTemp;
                        }
                    } else {
                        System.out.println("stringMagicCode =  " + stringMagicCode);
                    }
                }
                if (!isStbValid) {
                    loginInfoTemp.setmConnectStatus(-10);
                    cSocket2.DestroySocket();
                } else if (isStbConnectFull == 1) {
                    loginInfoTemp.setmConnectStatus(-7);
                    cSocket2.DestroySocket();
                } else {
                    ParserFactory.setDataType(loginInfoTemp.getSend_data_type());
                }
            } catch (ConnectException e7) {
                cSocket = cSocket2;
            } catch (SocketTimeoutException e8) {
                cSocket = cSocket2;
            } catch (IOException e9) {
                cSocket = cSocket2;
            } catch (IllegalArgumentException e10) {
                cSocket = cSocket2;
            } catch (SocketException e11) {
                cSocket = cSocket2;
            } catch (Exception e12) {
                cSocket = cSocket2;
            }
        } catch (SocketException e13) {
        } catch (IOException e14) {
        } catch (IllegalArgumentException e15) {
        } catch (ConnectException e16) {
        } catch (SocketTimeoutException e17) {
        } catch (Exception e18) {
        }
        return loginInfoTemp;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static void makeTextForConnectError(Context context, int errorType) {
        int resId;
        switch (errorType) {
            case GlobalConstantValue.CONNECT_STB_ERROR_DATA_TRANSMISSION_FAIL /* -10 */:
                resId = R.string.stb_connect_data_transmission_fail;
                Toast.makeText(context, resId, 0).show();
                break;
            case GlobalConstantValue.CONNECT_STB_ERROR_SERVER_IP_NON_EXIST /* -9 */:
                resId = R.string.server_ip_non_exist;
                Toast.makeText(context, resId, 0).show();
                break;
            case GlobalConstantValue.CONNECT_STB_ERROR_HAND_SHARK_ERROR /* -8 */:
                resId = R.string.ConnectUnkonwnError;
                Toast.makeText(context, resId, 0).show();
                break;
            case GlobalConstantValue.CONNECT_STB_ERROR_STB_IS_FULL /* -7 */:
                resId = R.string.str_stb_is_full;
                Toast.makeText(context, resId, 0).show();
                break;
            case -5:
                resId = R.string.stb_connect_data_transmission_fail;
                Toast.makeText(context, resId, 0).show();
                break;
            case -4:
                resId = R.string.IpNotVaild;
                Toast.makeText(context, resId, 0).show();
                break;
            case -3:
                resId = R.string.NetworkNotReachable;
                Toast.makeText(context, resId, 0).show();
                break;
            case -2:
                resId = R.string.ServerNotResponse;
                Toast.makeText(context, resId, 0).show();
                break;
            case -1:
                resId = R.string.ConnectUnkonwnError;
                Toast.makeText(context, resId, 0).show();
                break;
        }
    }
}
