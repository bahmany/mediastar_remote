package com.hisilicon.multiscreen.protocol.remote;

import com.hisilicon.multiscreen.protocol.HiDeviceInfo;
import com.hisilicon.multiscreen.protocol.ServiceInfo;
import com.hisilicon.multiscreen.protocol.message.KeyInfo;
import com.hisilicon.multiscreen.protocol.message.KeyboardRequest;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import com.hisilicon.multiscreen.protocol.utils.UDPClient;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

/* loaded from: classes.dex */
public class RemoteKeyboard {
    private HiDeviceInfo mDevice = null;
    private KeyboardRequest mRequest = null;
    private DatagramSocket mSocket = null;

    public RemoteKeyboard(HiDeviceInfo deviceInfo) {
        if (deviceInfo == null) {
            LogTool.e("device info is null in remote keyboard.");
        } else {
            resetDevice(deviceInfo);
        }
    }

    protected void destroy() {
        if (this.mSocket != null) {
            this.mSocket.close();
            this.mSocket = null;
        }
    }

    protected void resetDevice(HiDeviceInfo deviceInfo) {
        this.mDevice = deviceInfo;
        if (this.mRequest == null) {
            this.mRequest = new KeyboardRequest(11, 0);
        }
        if (this.mSocket == null) {
            try {
                this.mSocket = new DatagramSocket();
                return;
            } catch (SocketException e) {
                LogTool.e(e.getMessage());
                return;
            }
        }
        if (this.mSocket.isClosed()) {
            try {
                this.mSocket = new DatagramSocket();
            } catch (SocketException e2) {
                LogTool.e(e2.getMessage());
            }
        }
    }

    public void sendDownOrUpKeyCode(int keycode, short event_type) throws IOException {
        if (event_type == 1 || event_type == 0) {
            sendToVirtualDriver(keycode, event_type);
        }
    }

    public void sendDownAndUpKeyCode(int keycode) throws IOException {
        int key_value;
        boolean shift_press = false;
        switch (keycode) {
            case 78:
                key_value = 13;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_ASK /* 120 */:
                key_value = 53;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_Q /* 216 */:
                key_value = 16;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_E /* 218 */:
                key_value = 18;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_R /* 219 */:
                key_value = 19;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_T /* 220 */:
                key_value = 20;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_Y /* 221 */:
                key_value = 21;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_U /* 222 */:
                key_value = 22;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_I /* 223 */:
                key_value = 23;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_O /* 224 */:
                key_value = 24;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_P /* 225 */:
                key_value = 25;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_A /* 230 */:
                key_value = 30;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_S /* 231 */:
                key_value = 31;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_D /* 232 */:
                key_value = 32;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_F /* 233 */:
                key_value = 33;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_G /* 234 */:
                key_value = 34;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_H /* 235 */:
                key_value = 35;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_J /* 236 */:
                key_value = 36;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_K /* 237 */:
                key_value = 37;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_L /* 238 */:
                key_value = 38;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_Z /* 244 */:
                key_value = 44;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_X /* 245 */:
                key_value = 45;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_C /* 246 */:
                key_value = 46;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_V /* 247 */:
                key_value = 47;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_B /* 248 */:
                key_value = 48;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_N /* 249 */:
                key_value = 49;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_M /* 250 */:
                key_value = 50;
                shift_press = true;
                break;
            case 302:
                key_value = 2;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_AT /* 303 */:
                key_value = 3;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_WELL /* 304 */:
                key_value = 4;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_DOLLAR /* 305 */:
                key_value = 5;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_PERCENT /* 306 */:
                key_value = 6;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_AND /* 307 */:
                key_value = 7;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_ANDD /* 308 */:
                key_value = 8;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_STAR /* 309 */:
                key_value = 9;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_LEFTBRACKET /* 310 */:
                key_value = 10;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_RIGHTBRACKET /* 311 */:
                key_value = 11;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_UNDERLINE /* 312 */:
                key_value = 12;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_W /* 317 */:
                key_value = 17;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_LEFT_BIG_BRACKET /* 326 */:
                key_value = 26;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_RIGHT_BIG_BRACKET /* 327 */:
                key_value = 27;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_COLON /* 339 */:
                key_value = 39;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_DOUBLE_QUOTATION /* 340 */:
                key_value = 40;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_REVERSE /* 341 */:
                key_value = 41;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_OR /* 343 */:
                key_value = 43;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_LESS_THAN /* 351 */:
                key_value = 51;
                shift_press = true;
                break;
            case KeyInfo.KEYCODE_BIG_THAN /* 352 */:
                key_value = 52;
                shift_press = true;
                break;
            default:
                key_value = keycode;
                break;
        }
        if (shift_press) {
            sendToVirtualDriver(42, (short) 1);
            sendToVirtualDriver(key_value, (short) 1);
            sendToVirtualDriver(key_value, (short) 0);
            sendToVirtualDriver(42, (short) 0);
            return;
        }
        sendToVirtualDriver(key_value, (short) 1);
        sendToVirtualDriver(key_value, (short) 0);
    }

    private void sendToVirtualDriver(int keycode, short updownState) throws IOException {
        if (this.mDevice == null) {
            LogTool.e("mDevice is null");
            return;
        }
        String ip = this.mDevice.getDeviceIP();
        ServiceInfo service = this.mDevice.getService("HI_UPNP_VAR_VinpuServerURI");
        if (ip != null && service != null) {
            int port = service.getServicePort();
            this.mRequest.setData(keycode, updownState);
            UDPClient.send(this.mSocket, ip, port, this.mRequest);
        }
    }
}
