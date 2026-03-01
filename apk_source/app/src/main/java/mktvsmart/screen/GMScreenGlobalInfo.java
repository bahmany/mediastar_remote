package mktvsmart.screen;

import com.google.android.gms.games.GamesStatusCodes;
import com.hisilicon.multiscreen.protocol.message.KeyInfo;
import java.util.ArrayList;
import java.util.List;
import mktvsmart.screen.dataconvert.model.DataConvertFavorModel;
import mktvsmart.screen.dataconvert.parser.ParserFactory;

/* loaded from: classes.dex */
public class GMScreenGlobalInfo {
    public static final String DEFAULT_BUILT_IN_PLAYER = "com.mktech.player";
    public static final String DEFAULT_EXTERNAL_PLAYER = "com.mxtech.videoplayer.ad";
    public static final int GET_KEY_WAY_NEW = 2;
    public static final int GET_KEY_WAY_OLD = 1;
    public static final int GMS_CLIENT_TYPE_MASTER = 0;
    public static final int GMS_CLIENT_TYPE_SLAVE = 1;
    public static final int GS_PLATFORM_ALI_3329 = 3;
    public static final int GS_PLATFORM_ALI_3510E = 11;
    public static final int GS_PLATFORM_ALI_3511 = 8;
    public static final int GS_PLATFORM_ALI_3516 = 9;
    public static final int GS_PLATFORM_ALI_3526 = 14;
    public static final int GS_PLATFORM_ALI_3601 = 6;
    public static final int GS_PLATFORM_ALI_3606 = 5;
    public static final int GS_PLATFORM_ALI_3616 = 7;
    public static final int GS_PLATFORM_ALI_3618 = 10;
    public static final int GS_PLATFORM_ALI_3821 = 12;
    public static final int GS_PLATFORM_BCM = 2;
    public static final int GS_PLATFORM_GPRS_6500 = 46;
    public static final int GS_PLATFORM_GX3200 = 40;
    public static final int GS_PLATFORM_GX6601 = 41;
    public static final int GS_PLATFORM_GX6601E = 42;
    public static final int GS_PLATFORM_GX6622 = 44;
    public static final int GS_PLATFORM_G_CARD = 100;
    public static final int GS_PLATFORM_HAISI = 1;
    public static final int GS_PLATFORM_HD7101 = 0;
    public static final int GS_PLATFORM_HISI3712 = 72;
    public static final int GS_PLATFORM_HISI3719 = 71;
    public static final int GS_PLATFORM_HISI3796 = 74;
    public static final int GS_PLATFORM_INVALID = 9999;
    public static final int GS_PLATFORM_MSD7819 = 89;
    public static final int GS_PLATFORM_MSD7821 = 88;
    public static final int GS_PLATFORM_MSTAR = 4;
    public static final int GS_PLATFORM_NT78326 = 25;
    public static final int GS_PLATFORM_SUNPLUS_1502 = 21;
    public static final int GS_PLATFORM_SUNPLUS_1512 = 20;
    public static final int GS_PLATFORM_TCS131 = 31;
    public static final int GS_PLATFORM_TCS188 = 32;
    public static final int GS_PLATFORM_TRIDENT_8471 = 30;
    public static final int GS_SAT_ENABLE = 1;
    public static final int GS_SAT_UNENABLE = 0;
    public static final int NO_PLAY = 0;
    public static final int PVR_MENU_ENABLE = 1;
    public static final int PVR_PLAY = 1;
    public static final int SAT2IP_DISABLE = 2;
    public static final int SAT2IP_ENABLE = 1;
    public static final int SAT2IP_PLAY = 2;
    public static final int SOCKET_TIME_OUT_EXCEPTION = -1;
    private static int mSatIndexSelected = 100;
    private static int mMaxPasswordNum = 4;
    private static boolean mSendEmailFinished = true;
    private static int mMaxProgramNumPerRequest = 100;
    private static int mMaxDebugDataLenthPerRequest = 131072;
    private static int mMaxProgramNumber = 6100;
    private static int mPvr2ipServerSupport = 0;
    private static int mIsSdsOpen = 0;
    private static int mWaitDialogTimeOut = 30000;
    public static int playType = 0;
    public static ArrayList<String> favType = new ArrayList<String>() { // from class: mktvsmart.screen.GMScreenGlobalInfo.1
        private static final long serialVersionUID = 4807314058258696673L;

        {
            add("News");
            add("Movies");
            add("Music");
            add("Sports");
            add("Education");
            add("Weather");
            add("Children");
            add("Culture");
        }
    };
    public static List<DataConvertFavorModel> favGroups = new ArrayList();
    private static GsMobileLoginInfo curStbInfo = null;

    public static String getDefaultPlayer() {
        switch (getCurStbInfo().getPlatform_id()) {
            case 20:
            case 21:
            case 25:
            case 30:
            case 32:
            case 41:
            case 42:
            case 44:
            case 71:
            case 72:
            case 74:
                return DEFAULT_BUILT_IN_PLAYER;
            default:
                return DEFAULT_EXTERNAL_PLAYER;
        }
    }

    public static int getmMaxProgramNumber() {
        switch (getCurStbInfo().getPlatform_id()) {
            case 20:
            case 21:
            case 25:
            case 40:
            case 41:
            case 42:
            case 44:
                return GamesStatusCodes.STATUS_MILESTONE_CLAIMED_PREVIOUSLY;
            default:
                return mMaxProgramNumber;
        }
    }

    public static int getmPvr2ipServerSupport() {
        return mPvr2ipServerSupport;
    }

    public static void setmPvr2ipServerSupport(int mPvr2ipServerSupport2) {
        mPvr2ipServerSupport = mPvr2ipServerSupport2;
    }

    public static int getmWaitDialogTimeOut() {
        return mWaitDialogTimeOut;
    }

    public static int getmMaxPasswordNum() {
        return mMaxPasswordNum;
    }

    public static void setmMaxPasswordNum(int mMaxPasswordNum2) {
        mMaxPasswordNum = mMaxPasswordNum2;
    }

    public static int getmSatIndexSelected() {
        return mSatIndexSelected;
    }

    public static void setmSatIndexSelected(int mSatIndexSelected2) {
        mSatIndexSelected = mSatIndexSelected2;
    }

    public static boolean check_is_apk_match_platform(int stb_platform_id) {
        switch (stb_platform_id) {
            case 8:
            case 9:
            case 12:
            case 14:
            case 20:
            case 21:
            case 25:
            case 30:
            case 31:
            case 32:
            case 40:
            case 41:
            case 42:
            case 44:
            case 71:
            case 72:
            case 74:
                return true;
            default:
                return false;
        }
    }

    public static GsMobileLoginInfo getCurStbInfo() {
        if (curStbInfo == null) {
            curStbInfo = new GsMobileLoginInfo();
        }
        return curStbInfo;
    }

    public static boolean isClientTypeSlave() {
        return getCurStbInfo().getClient_type() == 1;
    }

    public static void setmCurStbInfo(GsMobileLoginInfo curStbInfo2) {
        curStbInfo = curStbInfo2;
        ParserFactory.setDataType(curStbInfo2.getSend_data_type());
    }

    public static int getCurStbPlatform() {
        return getCurStbInfo().getPlatform_id();
    }

    public static boolean ismSendEmailFinished() {
        return mSendEmailFinished;
    }

    public static void setmSendEmailFinished(boolean mSendEmailFinished2) {
        mSendEmailFinished = mSendEmailFinished2;
    }

    public static int getIndexOfAllSat() {
        switch (getCurStbInfo().getPlatform_id()) {
            case 20:
            case 21:
                return 239;
            case 25:
                return KeyInfo.KEYCODE_ASK;
            case 30:
            case 31:
            case 32:
            case 71:
            case 72:
            case 74:
                return 0;
            default:
                return 100;
        }
    }

    public static int getMaxProgramNumPerRequest() {
        getCurStbInfo().getPlatform_id();
        mMaxProgramNumPerRequest = 100;
        return mMaxProgramNumPerRequest;
    }

    public static int getMaxDebugDataLenthPerRequest() {
        getCurStbInfo().getPlatform_id();
        mMaxDebugDataLenthPerRequest = 131072;
        return mMaxDebugDataLenthPerRequest;
    }

    public static int isSdsOpen() {
        return mIsSdsOpen;
    }

    public static void setSdsOpen(int isSdsOpen) {
        mIsSdsOpen = isSdsOpen;
    }

    public static boolean isChatSupport() {
        switch (getCurStbInfo().getPlatform_id()) {
            case 8:
            case 9:
            case 12:
            case 14:
                if (getCurStbInfo().getSw_sub_version() >= 14804) {
                }
                break;
        }
        return false;
    }

    public static int getKeyWay() {
        switch (getCurStbInfo().getPlatform_id()) {
            case 14:
                return 2;
            default:
                return 1;
        }
    }
}
