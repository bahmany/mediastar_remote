package com.hisilicon.multiscreen.mybox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.hisilicon.multiscreen.controller.IAccessListener;
import com.hisilicon.multiscreen.mybox.MultiScreenControlService;
import com.hisilicon.multiscreen.protocol.message.KeyInfo;
import com.hisilicon.multiscreen.protocol.remote.RemoteControlCenter;
import com.hisilicon.multiscreen.protocol.remote.RemoteKeyboard;
import com.hisilicon.multiscreen.protocol.remote.RemoteSpeech;
import com.hisilicon.multiscreen.protocol.remote.RemoteTouch;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import com.hisilicon.multiscreen.protocol.utils.MultiScreenIntentAction;
import com.hisilicon.multiscreen.protocol.utils.ServiceUtil;
import com.hisilicon.multiscreen.vime.VImeClientControlService;
import java.io.IOException;
import java.lang.ref.SoftReference;

/* loaded from: classes.dex */
public abstract class BaseActivity extends Activity implements RemotePool {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$hisilicon$multiscreen$mybox$MultiScreenControlService$ClientState = null;
    protected static final int MAX_LOOP_COUNT = 100;
    protected static final long TIME_VIBRATE = 20;
    public static Vibrator mVibrator;
    private static boolean vib_status;
    public static RemoteKeyboard mKeyboard = null;
    public static RemoteTouch mRemoteTouch = null;
    public static RemoteSpeech mRemoteSpeech = null;
    public int DEFAULT_REMOTE_UI_STATUS = 1;
    protected int mRemoteUiStatus = this.DEFAULT_REMOTE_UI_STATUS;
    public MultiScreenControlService mMultiScreenControlService = null;
    protected RemoteControlCenter mRemoteControlCenter = null;
    protected ProgressDialog mProgressDialog = null;
    protected long mPressTime = 0;
    protected long mPressTime2 = 0;
    protected int mLoopCount = 0;
    protected int mLoopCount2 = 0;
    protected IAccessListener mAccessListener = null;
    public AccessEventHandler mAccessEventHandler = null;

    static /* synthetic */ int[] $SWITCH_TABLE$com$hisilicon$multiscreen$mybox$MultiScreenControlService$ClientState() {
        int[] iArr = $SWITCH_TABLE$com$hisilicon$multiscreen$mybox$MultiScreenControlService$ClientState;
        if (iArr == null) {
            iArr = new int[MultiScreenControlService.ClientState.valuesCustom().length];
            try {
                iArr[MultiScreenControlService.ClientState.DEINIT.ordinal()] = 2;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[MultiScreenControlService.ClientState.INIT.ordinal()] = 1;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[MultiScreenControlService.ClientState.NETWORK_LOST.ordinal()] = 4;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[MultiScreenControlService.ClientState.REAVED.ordinal()] = 5;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[MultiScreenControlService.ClientState.RUNNING.ordinal()] = 3;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[MultiScreenControlService.ClientState.STB_LEAVE.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[MultiScreenControlService.ClientState.STB_SUSPEND.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            $SWITCH_TABLE$com$hisilicon$multiscreen$mybox$MultiScreenControlService$ClientState = iArr;
        }
        return iArr;
    }

    private static class AccessEventHandler extends Handler {
        SoftReference<BaseActivity> mActivityReference;

        AccessEventHandler(BaseActivity activity) {
            this.mActivityReference = null;
            this.mActivityReference = new SoftReference<>(activity);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) throws InterruptedException {
            BaseActivity activity = this.mActivityReference.get();
            super.handleMessage(msg);
            switch (msg.what) {
                case 10:
                    LogTool.e("keep alive packet loss!");
                    Toast.makeText(activity, "keep alive packet loss!", 0).show();
                    break;
                case 20:
                    IAccessListener.Caller caller = (IAccessListener.Caller) msg.obj;
                    activity.dealNetfailedStatus(caller);
                    break;
                case 30:
                    IAccessListener.Caller caller2 = (IAccessListener.Caller) msg.obj;
                    activity.dealAccessByeForReave(caller2);
                    break;
                case 40:
                    IAccessListener.Caller caller3 = (IAccessListener.Caller) msg.obj;
                    activity.dealSTBLeave(caller3);
                    break;
                case 80:
                    IAccessListener.Caller caller4 = (IAccessListener.Caller) msg.obj;
                    activity.dealSTBSuppend(caller4);
                    break;
            }
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        LogTool.v("On Create.");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(128);
        setStrictMode();
        initData();
        initView();
    }

    @Override // android.app.Activity
    protected void onRestart() {
        LogTool.v("on restart.");
        super.onRestart();
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        LogTool.d("onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    @Override // android.app.Activity
    protected void onResume() {
        LogTool.v("on resume.");
        super.onResume();
    }

    @Override // android.app.Activity
    protected void onPause() {
        LogTool.v("on pause.");
        super.onPause();
        dismissProgressDialog();
    }

    @Override // android.app.Activity
    protected void onStop() {
        LogTool.v("on stop.");
        super.onStop();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        LogTool.v("on destroy.");
        super.onDestroy();
        if (mVibrator != null) {
            mVibrator.cancel();
        }
    }

    protected void clearAccessListener() {
        this.mMultiScreenControlService.setAllAccessListener(null);
    }

    protected void dealNetfailedStatus(IAccessListener.Caller caller) throws InterruptedException {
        this.mMultiScreenControlService.stopPing(caller, MultiScreenControlService.ClientState.NETWORK_LOST);
        stopVIME();
        destroyRemoteControl();
        removeInvalidDevice();
    }

    protected void dealAccessByeForReave(IAccessListener.Caller caller) throws InterruptedException {
        deInitNetworkChecker(caller, MultiScreenControlService.ClientState.REAVED);
        stopVIME();
        destroyRemoteControl();
        clearCurrentDevice();
    }

    protected void dealSTBLeave(IAccessListener.Caller caller) throws InterruptedException {
        deInitNetworkChecker(caller, MultiScreenControlService.ClientState.STB_LEAVE);
        stopVIME();
        destroyRemoteControl();
        removeInvalidDevice();
    }

    protected void dealSTBSuppend(IAccessListener.Caller caller) throws InterruptedException {
        deInitNetworkChecker(caller, MultiScreenControlService.ClientState.STB_SUSPEND);
        stopVIME();
        destroyRemoteControl();
        clearCurrentDevice();
    }

    protected void gotoRemoteTouch() {
        LogTool.d("");
    }

    protected void gotoAirMouse() {
        LogTool.d("");
    }

    protected void gotoRemoteKey() {
        LogTool.d("");
    }

    protected void gotoGamePage() {
        LogTool.d("");
    }

    protected void gotoMirror() {
        LogTool.d("");
    }

    protected void gotoMirrorSensor() {
        LogTool.d("");
    }

    protected void gotoDeviceDiscovery() {
        LogTool.d("");
    }

    protected void destroyRemoteControl() {
    }

    protected void deInitNetworkChecker(IAccessListener.Caller caller, MultiScreenControlService.ClientState clientState) throws InterruptedException {
        this.mMultiScreenControlService.stopPing(caller, clientState);
    }

    private void removeInvalidDevice() {
        this.mMultiScreenControlService.getControlPoint().removeCannotAccessDevice(this.mMultiScreenControlService.getControlPoint().getCurrentDevice());
        clearCurrentDevice();
    }

    protected void clearCurrentDevice() {
        this.mMultiScreenControlService.getControlPoint().setCurrentDevice(null);
    }

    protected void unbindDrawables(View view) {
        if (view != null) {
            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
            }
            if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    unbindDrawables(((ViewGroup) view).getChildAt(i));
                }
                ((ViewGroup) view).removeAllViews();
            }
        }
    }

    protected void showProgressDialog(int titleId, int msgId) {
        if (this.mProgressDialog == null) {
            this.mProgressDialog = new ProgressDialog(this);
        }
        this.mProgressDialog.setTitle(titleId);
        this.mProgressDialog.setMessage(getString(msgId));
        this.mProgressDialog.setCancelable(false);
        this.mProgressDialog.show();
    }

    protected void dismissProgressDialog() {
        if (this.mProgressDialog != null) {
            this.mProgressDialog.dismiss();
            this.mProgressDialog = null;
        }
    }

    protected void registerBroadcastReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        registerReceiver(receiver, filter);
    }

    protected void unregisterBroadcastReceiver(BroadcastReceiver receiver) {
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            LogTool.d("the receiver was already unregistered or was not registered.");
        }
    }

    protected void resetAccessListener() {
        if (this.mAccessListener == null) {
            this.mAccessListener = new IAccessListener() { // from class: com.hisilicon.multiscreen.mybox.BaseActivity.1
                @Override // com.hisilicon.multiscreen.controller.IAccessListener
                public void dealNetWorkNotWellEvent() {
                    BaseActivity.this.mAccessEventHandler.sendEmptyMessage(10);
                }

                @Override // com.hisilicon.multiscreen.controller.IAccessListener
                public void dealNetWorkLostEvent(IAccessListener.Caller caller) {
                    LogTool.d("Keep alive fail.");
                    BaseActivity.this.sendAccessStatusMessage(caller, 20);
                }

                @Override // com.hisilicon.multiscreen.controller.IAccessListener
                public void dealReaveEvent(IAccessListener.Caller caller) {
                    LogTool.d("Be reaved.");
                    BaseActivity.this.sendAccessStatusMessage(caller, 30);
                }

                @Override // com.hisilicon.multiscreen.controller.IAccessListener
                public void dealSTBLeaveEvent(IAccessListener.Caller caller) {
                    LogTool.d("STB leave.");
                    BaseActivity.this.sendAccessStatusMessage(caller, 40);
                }

                @Override // com.hisilicon.multiscreen.controller.IAccessListener
                public void dealSTBSuspendEvent(IAccessListener.Caller caller) {
                    LogTool.d("STB suspend.");
                    BaseActivity.this.sendAccessStatusMessage(caller, 80);
                }
            };
        }
        this.mMultiScreenControlService.setAllAccessListener(this.mAccessListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendAccessStatusMessage(IAccessListener.Caller caller, int what) {
        Message msg = this.mAccessEventHandler.obtainMessage();
        msg.obj = caller;
        msg.what = what;
        msg.sendToTarget();
    }

    protected static void toVibrate() {
        if (isVibrate()) {
            mVibrator.vibrate(TIME_VIBRATE);
        }
    }

    protected static boolean isVibrate() {
        return vib_status;
    }

    private void initView() {
        requestWindowFeature(1);
        vib_status = readStatusPreference(MultiSettingActivity.VIBRATOR_STATUS_KEY);
        mVibrator = (Vibrator) getApplication().getSystemService(MultiSettingActivity.VIBRATOR_STATUS_KEY);
    }

    private boolean readStatusPreference(String statusKey) {
        SharedPreferences prefrence = getSharedPreferences(MultiSettingActivity.SETTING_STATUS_FILE_NAME, 0);
        return prefrence.getBoolean(statusKey, true);
    }

    @SuppressLint({"NewApi"})
    private void setStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
    }

    private void checkState() {
        switch ($SWITCH_TABLE$com$hisilicon$multiscreen$mybox$MultiScreenControlService$ClientState()[this.mMultiScreenControlService.getState().ordinal()]) {
            case 3:
                LogTool.d("check state RUNNING.");
                resetAccessListener();
                break;
            case 4:
                LogTool.d("check state NETWORK_LOST.");
                sendAccessStatusMessage(IAccessListener.Caller.KeepAlive, 20);
                break;
            case 5:
                LogTool.d("check state REAVED.");
                sendAccessStatusMessage(IAccessListener.Caller.AccessPing, 30);
                break;
            case 6:
                LogTool.d("check state STB_LEAVE.");
                sendAccessStatusMessage(IAccessListener.Caller.AccessPing, 40);
                break;
            case 7:
                LogTool.d("check state STB_SUSPEND.");
                sendAccessStatusMessage(IAccessListener.Caller.AccessPing, 80);
                break;
            default:
                LogTool.e("check state error: " + this.mMultiScreenControlService.getState().toString());
                sendAccessStatusMessage(IAccessListener.Caller.Others, 20);
                break;
        }
    }

    protected void initData() {
        ServiceUtil.checkMultiScreenControlService(this);
        this.mMultiScreenControlService = MultiScreenControlService.getInstance();
        this.mRemoteControlCenter = this.mMultiScreenControlService.getRemoteControlCenter();
        this.mAccessEventHandler = new AccessEventHandler(this);
    }

    protected void stopVIME() {
        LogTool.d("stop vime.");
        sendBroadcast(new Intent(MultiScreenIntentAction.END_INPUT_BY_STB));
        stopService(new Intent(this, (Class<?>) VImeClientControlService.class));
    }

    public static void doKeyboard(int result) throws IOException {
        toVibrate();
        if (mKeyboard != null) {
            switch (result) {
                case 2000:
                    mKeyboard.sendDownAndUpKeyCode(102);
                    break;
                case 2001:
                    mKeyboard.sendDownAndUpKeyCode(KeyInfo.KEYCODE_MENU);
                    break;
                case 2002:
                    mKeyboard.sendDownAndUpKeyCode(KeyInfo.KEYCODE_MUTE);
                    break;
                case 2004:
                    mKeyboard.sendDownAndUpKeyCode(28);
                    break;
                case 2005:
                    mKeyboard.sendDownAndUpKeyCode(103);
                    break;
                case 2006:
                    mKeyboard.sendDownAndUpKeyCode(105);
                    break;
                case 2007:
                    mKeyboard.sendDownAndUpKeyCode(106);
                    break;
                case 2008:
                    mKeyboard.sendDownAndUpKeyCode(108);
                    break;
                case 2009:
                    mKeyboard.sendDownAndUpKeyCode(2);
                    break;
                case 2010:
                    mKeyboard.sendDownAndUpKeyCode(3);
                    break;
                case 2011:
                    mKeyboard.sendDownAndUpKeyCode(4);
                    break;
                case 2012:
                    mKeyboard.sendDownAndUpKeyCode(5);
                    break;
                case 2013:
                    mKeyboard.sendDownAndUpKeyCode(6);
                    break;
                case 2014:
                    mKeyboard.sendDownAndUpKeyCode(7);
                    break;
                case 2015:
                    mKeyboard.sendDownAndUpKeyCode(8);
                    break;
                case 2016:
                    mKeyboard.sendDownAndUpKeyCode(9);
                    break;
                case 2017:
                    mKeyboard.sendDownAndUpKeyCode(10);
                    break;
                case 2018:
                    mKeyboard.sendDownAndUpKeyCode(57);
                    break;
                case 2019:
                    mKeyboard.sendDownAndUpKeyCode(11);
                    break;
                case 2020:
                    mKeyboard.sendDownAndUpKeyCode(14);
                    break;
                case 2021:
                    mKeyboard.sendDownAndUpKeyCode(158);
                    break;
                case 2022:
                    mKeyboard.sendDownAndUpKeyCode(59);
                    break;
                case 2023:
                    mKeyboard.sendDownAndUpKeyCode(60);
                    break;
                case 2024:
                    mKeyboard.sendDownAndUpKeyCode(61);
                    break;
                case 2025:
                    mKeyboard.sendDownAndUpKeyCode(62);
                    break;
                case 2026:
                    mKeyboard.sendDownAndUpKeyCode(KeyInfo.KEYCODE_VOLUME_UP);
                    break;
                case 2027:
                    mKeyboard.sendDownAndUpKeyCode(KeyInfo.KEYCODE_VOLUME_DOWN);
                    break;
            }
        }
    }

    public void sendLongPress(int key_value, short event_type) throws IOException {
        if (mKeyboard != null) {
            mKeyboard.sendDownOrUpKeyCode(key_value, event_type);
        }
    }
}
