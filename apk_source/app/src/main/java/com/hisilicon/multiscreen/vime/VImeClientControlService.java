package com.hisilicon.multiscreen.vime;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;
import com.hisilicon.multiscreen.mybox.MultiScreenControlService;
import com.hisilicon.multiscreen.mybox.MultiSettingActivity;
import com.hisilicon.multiscreen.protocol.HiDeviceInfo;
import com.hisilicon.multiscreen.protocol.message.Action;
import com.hisilicon.multiscreen.protocol.message.VImeTextInfo;
import com.hisilicon.multiscreen.protocol.remote.IVImeActivityHandler;
import com.hisilicon.multiscreen.protocol.remote.VImeClientController;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import com.hisilicon.multiscreen.protocol.utils.MultiScreenIntentAction;
import java.io.IOException;

/* loaded from: classes.dex */
public class VImeClientControlService extends Service {
    private static final int FIFTH_ARGUMENT_INDEX = 4;
    private static final int FIRST_ARGUMENT_INDEX = 0;
    private static final int FOURTH_ARGUMENT_INDEX = 3;
    private static final int SECOND_ARGUMENT_INDEX = 1;
    private static final int THIRD_ARGUMENT_INDEX = 2;
    private static VImeClientControlService mVImeControlServiceInstance = null;
    private VImeClientController mVImeCenter = null;
    private boolean mIsActivityOnTop = false;
    private Handler mHandler = null;
    private IVImeActivityHandler mActivityHandler = new IVImeActivityHandler() { // from class: com.hisilicon.multiscreen.vime.VImeClientControlService.1
        @Override // com.hisilicon.multiscreen.protocol.remote.IVImeActivityHandler
        public void openVimeSwitch() {
        }

        @Override // com.hisilicon.multiscreen.protocol.remote.IVImeActivityHandler
        public void closeVimeSwitch() {
            VImeClientControlService.this.showVImeDisableToast();
        }

        @Override // com.hisilicon.multiscreen.protocol.remote.IVImeActivityHandler
        public void callInput(Action action) {
            try {
                Intent intent = new Intent(VImeClientControlService.getInstance(), Class.forName("smarttv.multiscreen.hisientry.ContentInputActivity"));
                intent.setFlags(268435456);
                Bundle bundle = new Bundle();
                bundle.putInt(VImeTextInfo.INPUT_TYPE, ((Integer) action.getArgument(0).getArgumentValue(0).getVaule()).intValue());
                bundle.putInt(VImeTextInfo.INPUT_OPTION, ((Integer) action.getArgument(0).getArgumentValue(1).getVaule()).intValue());
                bundle.putString("text", (String) action.getArgument(0).getArgumentValue(2).getVaule());
                bundle.putInt(VImeTextInfo.SRC_START_SELECTION, ((Integer) action.getArgument(0).getArgumentValue(3).getVaule()).intValue());
                bundle.putInt(VImeTextInfo.SRC_END_SELECTION, ((Integer) action.getArgument(0).getArgumentValue(4).getVaule()).intValue());
                intent.putExtras(bundle);
                VImeClientControlService.this.startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override // com.hisilicon.multiscreen.protocol.remote.IVImeActivityHandler
        public void endInputBySelf() {
            Intent intent = new Intent();
            intent.setAction(MultiScreenIntentAction.END_INPUT_BY_PHONE);
            VImeClientControlService.this.sendBroadcast(intent);
        }

        @Override // com.hisilicon.multiscreen.protocol.remote.IVImeActivityHandler
        public void endInputByServer() {
            Intent intent = new Intent(MultiScreenIntentAction.END_INPUT_BY_STB);
            VImeClientControlService.this.sendBroadcast(intent);
            LogTool.d("end input by STB.");
        }

        @Override // com.hisilicon.multiscreen.protocol.remote.IVImeActivityHandler
        public boolean isInputActivityOnTop() {
            return VImeClientControlService.this.mIsActivityOnTop;
        }
    };

    public static VImeClientControlService getInstance() {
        if (mVImeControlServiceInstance == null) {
            LogTool.e("VImeControlServiceInstance is null");
        }
        return mVImeControlServiceInstance;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flg, int startid) {
        LogTool.v("VImeClientControl service onStartCommand.");
        super.onStartCommand(intent, flg, startid);
        mVImeControlServiceInstance = this;
        this.mHandler = new Handler();
        this.mActivityHandler.endInputByServer();
        this.mIsActivityOnTop = false;
        reset();
        return 2;
    }

    @Override // android.app.Service
    public void onDestroy() throws InterruptedException, IOException {
        super.onDestroy();
        LogTool.v("VImeClientControl service onDestroy.");
        if (this.mVImeCenter.isVImeEnable()) {
            LogTool.v("VIme will be disable.");
            disableVIme();
        }
        this.mVImeCenter.destroy();
    }

    public boolean reset() {
        boolean isSwitchOpen = readVIMEStatusPreference();
        boolean isOK = initVimeServer(MultiScreenControlService.getInstance().getHiDevice(), isSwitchOpen);
        return isOK;
    }

    public boolean enableVIme() {
        return this.mVImeCenter.enableVIme();
    }

    public boolean disableVIme() {
        return this.mVImeCenter.disableVIme();
    }

    public boolean isInputStatusOnClient() {
        return this.mVImeCenter.isInputStatusOnClient();
    }

    public void setInputActivityTopStatus(boolean isNewOnTop) {
        this.mIsActivityOnTop = isNewOnTop;
    }

    public boolean readVIMEStatusPreference() {
        SharedPreferences prefrence = getSharedPreferences(MultiSettingActivity.SETTING_STATUS_FILE_NAME, 0);
        boolean isOpened = prefrence.getBoolean(MultiSettingActivity.VIME_STATUS_KEY, true);
        return isOpened;
    }

    private void writeVIMEStatusPreference(boolean isOpened) {
        SharedPreferences.Editor editor = getSharedPreferences(MultiSettingActivity.SETTING_STATUS_FILE_NAME, 0).edit();
        editor.putBoolean(MultiSettingActivity.VIME_STATUS_KEY, isOpened);
        editor.commit();
    }

    private boolean initVimeServer(HiDeviceInfo device, boolean isSwitchOpened) {
        this.mVImeCenter = VImeClientController.getInstance(device);
        this.mVImeCenter.setVimeActivityHandler(this.mActivityHandler);
        if (isSwitchOpened) {
            LogTool.d("Send enable VIME");
            boolean isEnableSuccess = enableVIme();
            if (isEnableSuccess) {
                LogTool.d("Enable VIME successful.");
                return isEnableSuccess;
            }
            LogTool.e("Enable VIME failed.");
            Intent intentSwitchVime = new Intent(MultiScreenIntentAction.SWITCH_VIME_SETTING);
            sendBroadcast(intentSwitchVime);
            return isEnableSuccess;
        }
        LogTool.d("Switch closed");
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showVImeDisableToast() {
        this.mHandler.post(new ToastRunnable(this, null));
    }

    private class ToastRunnable implements Runnable {
        private ToastRunnable() {
        }

        /* synthetic */ ToastRunnable(VImeClientControlService vImeClientControlService, ToastRunnable toastRunnable) {
            this();
        }

        @Override // java.lang.Runnable
        public void run() {
            Toast toast = Toast.makeText(VImeClientControlService.this.getApplicationContext(), "Vime Close", 0);
            toast.setGravity(48, 0, 0);
            toast.show();
        }
    }
}
