package mktvsmart.screen;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class EditLoginHistoryFile {
    private static final String IP_INFO = "IPInfo";
    private static final String IS_IP_LOGIN = "IsIPLogin";
    private static final String MODEL_INFO = "ModelInfo";
    private static final String PLATFORM_ID = "PlatformId";
    private static final String SAT_ENABLE = "SatEnable";
    private static final String SN_INFO = "SnInfo";
    private static final String STB_CUSTOMER_ID = "CustomerId";
    private static final String STB_MODEL_ID = "StbModelId";
    private static final String SW_SUB_VERSION = "SwSubVersion";
    private static final String SW_VERSION = "SwVersion";
    private static final String UPNP_PORT = "UpnpPort";
    private Context mContext;
    private SharedPreferences.Editor mEditor;
    private int mHistoryNum;
    private SharedPreferences mLoginHistoryFile;
    private GsMobileLoginInfo stbModel;

    public EditLoginHistoryFile(Context context) {
        this.mContext = context;
    }

    public EditLoginHistoryFile() {
    }

    public void readFileItem(int index) {
        this.stbModel = new GsMobileLoginInfo();
        this.stbModel.setModel_name(this.mLoginHistoryFile.getString(MODEL_INFO + index, ""));
        this.stbModel.setStb_sn_disp(this.mLoginHistoryFile.getString(SN_INFO + index, ""));
        this.stbModel.setStb_ip_address_disp(this.mLoginHistoryFile.getString(IP_INFO + index, ""));
        this.stbModel.setmIpLoginMark(this.mLoginHistoryFile.getInt(IS_IP_LOGIN + index, 0));
        this.stbModel.setPlatform_id(this.mLoginHistoryFile.getInt(PLATFORM_ID + index, 0));
        this.stbModel.setSw_version(this.mLoginHistoryFile.getInt(SW_VERSION + index, 0));
        this.stbModel.setStb_customer_id(this.mLoginHistoryFile.getInt(STB_CUSTOMER_ID + index, 0));
        this.stbModel.setStb_model_id(this.mLoginHistoryFile.getInt(STB_MODEL_ID + index, 0));
        this.stbModel.setSw_sub_version(this.mLoginHistoryFile.getInt(SW_SUB_VERSION + index, 0));
        this.stbModel.setmSatEnable(this.mLoginHistoryFile.getInt(SAT_ENABLE + index, 0));
        this.stbModel.setUpnpPort(this.mLoginHistoryFile.getInt(UPNP_PORT + index, 0));
    }

    public int getListFromFile(ArrayList<GsMobileLoginInfo> mHistoryStbInfoList) {
        this.mLoginHistoryFile = this.mContext.getSharedPreferences("history_list_file", 0);
        this.mHistoryNum = this.mLoginHistoryFile.getInt("pointer_num", 0);
        if (this.mHistoryNum > 0) {
            for (int i = 0; i < this.mHistoryNum; i++) {
                readFileItem(i);
                mHistoryStbInfoList.add(this.stbModel);
            }
        }
        return this.mHistoryNum;
    }

    public void getIpLoginHistoryList(ArrayList<GsMobileLoginInfo> mIpLoginInfoList) {
        this.mLoginHistoryFile = this.mContext.getSharedPreferences("history_list_file", 0);
        this.mHistoryNum = this.mLoginHistoryFile.getInt("pointer_num", 0);
        if (this.mHistoryNum > 0) {
            for (int i = 0; i < this.mHistoryNum; i++) {
                readFileItem(i);
                if (this.stbModel.getmIpLoginMark() == 1 && !ipContains(mIpLoginInfoList, this.stbModel).booleanValue()) {
                    mIpLoginInfoList.add(this.stbModel);
                }
            }
        }
    }

    private Boolean ipContains(ArrayList<GsMobileLoginInfo> mIpLoginInfoList, GsMobileLoginInfo addStb) {
        for (int tempIndex = 0; tempIndex < mIpLoginInfoList.size(); tempIndex++) {
            if (addStb.getStb_ip_address_disp().equals(mIpLoginInfoList.get(tempIndex).getStb_ip_address_disp())) {
                return true;
            }
        }
        return false;
    }

    public void putListToFile(GsMobileLoginInfo mLoginTemp, ArrayList<GsMobileLoginInfo> mHistoryStbInfoList) {
        this.mLoginHistoryFile = this.mContext.getSharedPreferences("history_list_file", 0);
        this.mHistoryNum = this.mLoginHistoryFile.getInt("pointer_num", 0);
        this.mEditor = this.mLoginHistoryFile.edit();
        boolean isExist = false;
        if (mLoginTemp != null) {
            int i = 0;
            while (true) {
                if (i >= this.mHistoryNum) {
                    break;
                }
                if (!mLoginTemp.getStb_sn_disp().equals(mHistoryStbInfoList.get(i).getStb_sn_disp())) {
                    i++;
                } else {
                    if (mHistoryStbInfoList.get(i).getmIpLoginMark() == 1) {
                        mLoginTemp.setmIpLoginMark(mHistoryStbInfoList.get(i).getmIpLoginMark());
                    }
                    mHistoryStbInfoList.remove(i);
                    mHistoryStbInfoList.add(0, mLoginTemp);
                    isExist = true;
                }
            }
            if (!isExist) {
                if (this.mHistoryNum >= 10) {
                    mHistoryStbInfoList.remove(9);
                }
                mHistoryStbInfoList.add(0, mLoginTemp);
            }
            this.mEditor.putInt("pointer_num", mHistoryStbInfoList.size());
            for (int i2 = 0; i2 < mHistoryStbInfoList.size(); i2++) {
                this.mEditor.putString(MODEL_INFO + i2, mHistoryStbInfoList.get(i2).getModel_name());
                this.mEditor.putString(SN_INFO + i2, mHistoryStbInfoList.get(i2).getStb_sn_disp());
                this.mEditor.putString(IP_INFO + i2, mHistoryStbInfoList.get(i2).getStb_ip_address_disp());
                this.mEditor.putInt(IS_IP_LOGIN + i2, mHistoryStbInfoList.get(i2).getmIpLoginMark());
                this.mEditor.putInt(PLATFORM_ID + i2, mHistoryStbInfoList.get(i2).getPlatform_id());
                this.mEditor.putInt(SW_VERSION + i2, mHistoryStbInfoList.get(i2).getSw_version());
                this.mEditor.putInt(STB_CUSTOMER_ID + i2, mHistoryStbInfoList.get(i2).getStb_customer_id());
                this.mEditor.putInt(STB_MODEL_ID + i2, mHistoryStbInfoList.get(i2).getStb_model_id());
                this.mEditor.putInt(SW_SUB_VERSION + i2, mHistoryStbInfoList.get(i2).getSw_sub_version());
                this.mEditor.putInt(SAT_ENABLE + i2, mHistoryStbInfoList.get(i2).getmSatEnable());
                this.mEditor.putInt(UPNP_PORT + i2, mHistoryStbInfoList.get(i2).getUpnpPort());
                this.mEditor.commit();
            }
        }
    }
}
