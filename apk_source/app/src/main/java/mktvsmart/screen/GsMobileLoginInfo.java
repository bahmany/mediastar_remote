package mktvsmart.screen;

import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;

/* loaded from: classes.dex */
public class GsMobileLoginInfo {
    private static final int G_MS_BROADCAST_INFO_SN_LEN = 8;
    private static final int G_MS_MAX_CHIP_LEN = 8;
    private static final int G_MS_MAX_MODEL_STRING_LENGTH = 32;
    private static final int G_MS_RESERVED1_LEN = 3;
    private static final int G_MS_RESERVED3_LEN = 20;
    private int client_type;
    private int is_current_stb_connected_full;
    private int mConnectStatus;
    private int mIpLoginMark;
    private long mLastFoundTime;
    private int mSat2ipEnable;
    private int mSatEnable;
    private String magic_code;
    private String model_name;
    private int platform_id;
    private byte[] reserved_1;
    private byte[] reserved_3;
    private byte[] reserver_2;
    private int send_data_type;
    private byte[] stb_cpu_chip_id;
    private int stb_customer_id;
    private byte[] stb_flash_id;
    private int stb_ip_address;
    private String stb_ip_address_disp;
    private int stb_model_id;
    private byte[] stb_sn;
    private String stb_sn_disp;
    private int sw_sub_version;
    private int sw_version;
    private String upnpIp;
    private int upnpPort;

    public int getSend_data_type() {
        return this.send_data_type;
    }

    public void setSend_data_type(int send_data_type) {
        this.send_data_type = send_data_type;
    }

    public int getmSatEnable() {
        return this.mSatEnable;
    }

    public void setmSatEnable(int mSatEnable) {
        this.mSatEnable = mSatEnable;
    }

    public String getMagic_code() {
        return this.magic_code;
    }

    public void setMagic_code(String magic_code) {
        this.magic_code = magic_code;
    }

    public byte[] getStb_sn() {
        return this.stb_sn;
    }

    public void setStb_sn(byte[] stb_sn) {
        this.stb_sn = stb_sn;
    }

    public String getModel_name() {
        return this.model_name;
    }

    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    public byte[] getStb_cpu_chip_id() {
        return this.stb_cpu_chip_id;
    }

    public void setStb_cpu_chip_id(byte[] stb_cpu_chip_id) {
        this.stb_cpu_chip_id = stb_cpu_chip_id;
    }

    public byte[] getStb_flash_id() {
        return this.stb_flash_id;
    }

    public void setStb_flash_id(byte[] stb_flash_id) {
        this.stb_flash_id = stb_flash_id;
    }

    public int getStb_ip_address() {
        return this.stb_ip_address;
    }

    public void setStb_ip_address(int stb_ip_address) {
        this.stb_ip_address = stb_ip_address;
    }

    public String getStb_sn_disp() {
        return this.stb_sn_disp;
    }

    public void setStb_sn_disp(String stb_sn_disp) {
        this.stb_sn_disp = stb_sn_disp;
    }

    public String getStb_ip_address_disp() {
        return this.stb_ip_address_disp;
    }

    public void setStb_ip_address_disp(String stb_ip_address_disp) {
        this.stb_ip_address_disp = stb_ip_address_disp;
    }

    public int getPlatform_id() {
        return this.platform_id;
    }

    public void setPlatform_id(int platform_id) {
        this.platform_id = platform_id;
    }

    public int getSw_version() {
        return this.sw_version;
    }

    public void setSw_version(int sw_version) {
        this.sw_version = sw_version;
    }

    public int getStb_customer_id() {
        return this.stb_customer_id;
    }

    public void setStb_customer_id(int stb_customer_id) {
        this.stb_customer_id = stb_customer_id;
    }

    public int getStb_model_id() {
        return this.stb_model_id;
    }

    public void setStb_model_id(int stb_model_id) {
        this.stb_model_id = stb_model_id;
    }

    public byte[] getReserved_1() {
        return this.reserved_1;
    }

    public void setReserved_1(byte[] reserved_1) {
        this.reserved_1 = reserved_1;
    }

    public int getIs_current_stb_connected_full() {
        return this.is_current_stb_connected_full;
    }

    public void setIs_current_stb_connected_full(int is_current_stb_connected_by_mobile) {
        this.is_current_stb_connected_full = is_current_stb_connected_by_mobile;
    }

    public byte[] getReserved_3() {
        return this.reserved_3;
    }

    public void setReserved_3(byte[] reserved_3) {
        this.reserved_3 = reserved_3;
    }

    public int getmSat2ipEnable() {
        return this.mSat2ipEnable;
    }

    public void setmSat2ipEnable(int mSat2ipEnable) {
        this.mSat2ipEnable = mSat2ipEnable;
    }

    public int getmIpLoginMark() {
        return this.mIpLoginMark;
    }

    public void setmIpLoginMark(int mIpLoginMark) {
        this.mIpLoginMark = mIpLoginMark;
    }

    public GsMobileLoginInfo() {
        this.mLastFoundTime = 0L;
        this.upnpPort = GlobalConstantValue.G_MS_LOGIN_DEFAULT_PORT_NUM;
    }

    public GsMobileLoginInfo(byte[] transportMsg) {
        this.mLastFoundTime = 0L;
        this.stb_sn = new byte[8];
        this.stb_cpu_chip_id = new byte[8];
        this.stb_flash_id = new byte[8];
        this.reserved_1 = new byte[3];
        this.reserved_3 = new byte[20];
        this.magic_code = new String(transportMsg, 0, 12);
        System.arraycopy(transportMsg, 12, this.stb_sn, 0, 8);
        this.stb_sn_disp = SerialNumberToDisp(this.stb_sn);
        int bufferIndex = 12 + 8;
        int model_name_lenth = 0;
        while (model_name_lenth < 32 && transportMsg[model_name_lenth + 20] != 0) {
            model_name_lenth++;
        }
        this.model_name = new String(transportMsg, bufferIndex, model_name_lenth);
        int bufferIndex2 = bufferIndex + 32;
        System.arraycopy(transportMsg, bufferIndex2, this.stb_cpu_chip_id, 0, 8);
        int bufferIndex3 = bufferIndex2 + 8;
        System.arraycopy(transportMsg, bufferIndex3, this.stb_flash_id, 0, 8);
        int bufferIndex4 = bufferIndex3 + 8;
        this.stb_ip_address_disp = String.valueOf(transportMsg[71] & 255) + "." + (transportMsg[70] & 255) + "." + (transportMsg[69] & 255) + "." + (transportMsg[bufferIndex4] & 255);
        int bufferIndex5 = bufferIndex4 + 4;
        int bufferIndex6 = bufferIndex5 + 1;
        this.platform_id = transportMsg[bufferIndex5];
        int bufferIndex7 = bufferIndex6 + 1;
        int i = (transportMsg[bufferIndex6] & 255) << 8;
        int bufferIndex8 = bufferIndex7 + 1;
        this.sw_version = i | (transportMsg[bufferIndex7] & 255);
        int bufferIndex9 = bufferIndex8 + 1;
        this.stb_customer_id = transportMsg[bufferIndex8] & 255;
        int bufferIndex10 = bufferIndex9 + 1;
        this.stb_model_id = transportMsg[bufferIndex9];
        System.arraycopy(transportMsg, bufferIndex10, this.reserved_1, 0, 3);
        int bufferIndex11 = bufferIndex10 + 3;
        this.sw_sub_version = (((transportMsg[83] & 255) << 24) & ViewCompat.MEASURED_STATE_MASK) | (((transportMsg[82] & 255) << 16) & 16711680) | (((transportMsg[81] & 255) << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (transportMsg[bufferIndex11] & 255);
        int bufferIndex12 = bufferIndex11 + 4;
        this.is_current_stb_connected_full = transportMsg[bufferIndex12] & 1;
        this.client_type = (transportMsg[bufferIndex12] & 2) >> 1;
        this.mSatEnable = (transportMsg[bufferIndex12] & 4) >> 2;
        this.mSat2ipEnable = (transportMsg[bufferIndex12] & 24) >> 3;
        this.send_data_type = (transportMsg[bufferIndex12] & 64) >> 6;
        int bufferIndex13 = bufferIndex12 + 4;
        System.arraycopy(transportMsg, bufferIndex13, this.reserved_3, 0, 20);
        int i2 = bufferIndex13 + 20;
    }

    private String SerialNumberToDisp(byte[] pcSNNumber) {
        if (pcSNNumber == null) {
            return "";
        }
        int iDate = ((pcSNNumber[2] & 255) | (((pcSNNumber[1] & 255) << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (((pcSNNumber[0] & 255) << 16) & 16711680)) & ViewCompat.MEASURED_SIZE_MASK;
        int iSerialNumber = ((pcSNNumber[5] & 255) | (((pcSNNumber[4] & 255) << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (((pcSNNumber[3] & 255) << 16) & 16711680)) & ViewCompat.MEASURED_SIZE_MASK;
        String SerialNumberDisp = String.format("%06d%06d", Integer.valueOf(iDate), Integer.valueOf(iSerialNumber));
        return SerialNumberDisp;
    }

    public int getSw_sub_version() {
        return this.sw_sub_version;
    }

    public void setSw_sub_version(int sw_sub_version) {
        this.sw_sub_version = sw_sub_version;
    }

    public int getmConnectStatus() {
        return this.mConnectStatus;
    }

    public void setmConnectStatus(int mConnectStatus) {
        this.mConnectStatus = mConnectStatus;
    }

    public int getClient_type() {
        return this.client_type;
    }

    public void setClient_type(int client_type) {
        this.client_type = client_type;
    }

    public int getUpnpPort() {
        return this.upnpPort;
    }

    public void setUpnpPort(int upnpPort) {
        this.upnpPort = upnpPort;
    }

    public String getUpnpIp() {
        return this.upnpIp;
    }

    public void setUpnpIp(String upnpIp) {
        this.upnpIp = upnpIp;
    }

    public long getLastFoundTime() {
        return this.mLastFoundTime;
    }

    public void setLastFoundTime(long mLastFoundTime) {
        this.mLastFoundTime = mLastFoundTime;
    }
}
