package com.hisilicon.multiscreen.protocol.remote;

import com.hisilicon.multiscreen.protocol.HiDeviceInfo;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/* loaded from: classes.dex */
public class RemoteSensor {
    private static final int BUFFER_SIZE = 18;
    private static final int FLOAT_LENGTH = 4;
    private static final short SENSOR_MESSAGE_LENGTH = 18;
    public static final int SENSOR_TYPE_ACCELEROMETER = 1;
    public static final int SENSOR_TYPE_GYROSCOPE = 4;
    public static final int SENSOR_TYPE_MAGNETIC_FIELD = 2;
    public static final int SENSOR_TYPE_ORIENTATION = 3;
    public static final int SENSOR_TYPE_TEMPERATURE = 7;
    private ByteBuffer mByteBuffer;
    private byte[] mSendMsg;
    private HiDeviceInfo mDevice = null;
    private DatagramSocket mSocket = null;
    private DatagramPacket dataPacket = null;
    private InetAddress mAddress = null;
    private int mPort = 11021;
    private ByteBuffer mTransByteBuffer = ByteBuffer.allocate(4);
    private byte[] mFloatBytes = new byte[4];

    public RemoteSensor(HiDeviceInfo deviceInfo) {
        this.mByteBuffer = null;
        this.mSendMsg = null;
        this.mByteBuffer = ByteBuffer.allocate(18);
        this.mSendMsg = new byte[18];
        if (deviceInfo == null) {
            LogTool.e("device info is null in remote sensor.");
        } else {
            resetDevice(deviceInfo);
        }
    }

    public void destroy() {
        if (this.mSocket != null) {
            this.mSocket.close();
            this.mSocket = null;
        }
    }

    protected void resetDevice(HiDeviceInfo deviceInfo) {
        this.mDevice = deviceInfo;
        if (this.mDevice.getService("HI_UPNP_VAR_GsensorServerURI") == null) {
            LogTool.e("gsensor service port is not obtained");
        } else {
            this.mPort = this.mDevice.getService("HI_UPNP_VAR_GsensorServerURI").getServicePort();
        }
        try {
            this.mAddress = InetAddress.getByName(this.mDevice.getDeviceIP());
        } catch (UnknownHostException e) {
            LogTool.e(e.getMessage());
        }
        if (this.dataPacket == null) {
            byte[] msgByte = new byte[18];
            this.dataPacket = new DatagramPacket(msgByte, 0, 18, this.mAddress, this.mPort);
        } else {
            this.dataPacket.setAddress(this.mAddress);
            this.dataPacket.setPort(this.mPort);
        }
        if (this.mSocket == null) {
            try {
                this.mSocket = new DatagramSocket();
                return;
            } catch (SocketException e2) {
                LogTool.e(e2.getMessage());
                return;
            }
        }
        if (this.mSocket.isClosed()) {
            try {
                this.mSocket = new DatagramSocket();
            } catch (SocketException e3) {
                LogTool.e(e3.getMessage());
            }
        }
    }

    public void sendSensorEvent(int sensorType, float sendX, float sendY, float sendZ) throws IOException {
        if (this.mDevice != null && this.mAddress != null) {
            this.mSendMsg = getBytes(sensorType, sendX, sendY, sendZ);
            this.dataPacket.setData(this.mSendMsg, 0, this.mSendMsg.length);
            try {
                if (this.mSocket != null) {
                    this.mSocket.send(this.dataPacket);
                } else {
                    LogTool.d("mSocket is null.");
                    this.mSocket = new DatagramSocket();
                }
            } catch (IOException e) {
                LogTool.e(e.getMessage());
            }
        }
    }

    private byte[] getBytes(int eventType, float pointX, float pointY, float pointZ) {
        if (this.mByteBuffer != null && this.mSendMsg != null) {
            this.mByteBuffer.putShort((short) 0);
            this.mByteBuffer.putShort(Short.reverseBytes((short) eventType));
            this.mByteBuffer.putShort((short) 0);
            this.mByteBuffer.put(floatToByteL(pointX));
            this.mByteBuffer.put(floatToByteL(pointY));
            this.mByteBuffer.put(floatToByteL(pointZ));
            this.mByteBuffer.rewind();
            this.mByteBuffer.get(this.mSendMsg);
            this.mByteBuffer.clear();
        }
        return this.mSendMsg;
    }

    private byte[] getDataInByteOrder(int eventType, float pointX, float pointY, float pointZ) {
        int index = 0;
        byte[] msgByte = new byte[18];
        byte[] bArr = new byte[4];
        for (int i = 0; i < 2; i++) {
            msgByte[index] = 0;
            index++;
        }
        byte[] temp = intToByteL(eventType);
        for (int i2 = 0; i2 < 2; i2++) {
            msgByte[index] = temp[i2];
            index++;
        }
        for (int i3 = 0; i3 < 2; i3++) {
            msgByte[index] = 0;
            index++;
        }
        byte[] temp2 = floatToByteL(pointX);
        for (int i4 = 0; i4 < 4; i4++) {
            msgByte[index] = temp2[i4];
            index++;
        }
        byte[] temp3 = floatToByteL(pointY);
        for (int i5 = 0; i5 < 4; i5++) {
            msgByte[index] = temp3[i5];
            index++;
        }
        byte[] temp4 = floatToByteL(pointZ);
        for (int i6 = 0; i6 < 4; i6++) {
            msgByte[index] = temp4[i6];
            index++;
        }
        return msgByte;
    }

    private byte[] intToByteL(int intValue) {
        byte[] result = {(byte) (intValue & 255), (byte) ((65280 & intValue) >> 8), (byte) ((16711680 & intValue) >> 16), (byte) (((-16777216) & intValue) >> 24)};
        return result;
    }

    private byte[] floatToByteL(float inputVal) {
        FloatBuffer floatBuffer = this.mTransByteBuffer.asFloatBuffer();
        floatBuffer.put(inputVal);
        this.mTransByteBuffer.rewind();
        this.mTransByteBuffer.get(this.mFloatBytes);
        byte[] bArr = this.mFloatBytes;
        bArr[0] = (byte) (bArr[0] ^ this.mFloatBytes[3]);
        byte[] bArr2 = this.mFloatBytes;
        bArr2[3] = (byte) (bArr2[3] ^ this.mFloatBytes[0]);
        byte[] bArr3 = this.mFloatBytes;
        bArr3[0] = (byte) (bArr3[0] ^ this.mFloatBytes[3]);
        byte[] bArr4 = this.mFloatBytes;
        bArr4[1] = (byte) (bArr4[1] ^ this.mFloatBytes[2]);
        byte[] bArr5 = this.mFloatBytes;
        bArr5[2] = (byte) (bArr5[2] ^ this.mFloatBytes[1]);
        byte[] bArr6 = this.mFloatBytes;
        bArr6[1] = (byte) (bArr6[1] ^ this.mFloatBytes[2]);
        this.mTransByteBuffer.clear();
        return this.mFloatBytes;
    }
}
