package mktvsmart.screen.hisientry;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Message;
import android.os.StrictMode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.hisilicon.multiscreen.controller.IAccessListener;
import com.hisilicon.multiscreen.gsensor.SensorService;
import com.hisilicon.multiscreen.mirror.MirrorView;
import com.hisilicon.multiscreen.mybox.MultiScreenControlService;
import com.hisilicon.multiscreen.mybox.MultiSettingActivity;
import com.hisilicon.multiscreen.protocol.message.KeyInfo;
import com.hisilicon.multiscreen.protocol.message.TouchRequest;
import com.hisilicon.multiscreen.protocol.remote.RemoteControlCenter;
import com.hisilicon.multiscreen.protocol.remote.RemoteKeyboard;
import com.hisilicon.multiscreen.protocol.remote.RemoteTouch;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import com.hisilicon.multiscreen.protocol.utils.MultiScreenIntentAction;
import com.hisilicon.multiscreen.protocol.utils.ServiceUtil;
import com.hisilicon.multiscreen.protocol.utils.WeakHandler;
import com.hisilicon.multiscreen.vime.VImeClientControlService;
import java.io.IOException;
import mktvsmart.screen.R;

/* loaded from: classes.dex */
public class MirrorPageView extends RelativeLayout implements View.OnClickListener {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$hisilicon$multiscreen$mybox$MultiScreenControlService$ClientState = null;
    private static final boolean FINGER_PRESSED = true;
    private static final boolean FINGER_UP = false;
    private static final int MAX_STB_TOUCH_HEIGHT = 719;
    private static final int MAX_STB_TOUCH_WIDTH = 1279;
    public static RemoteControlCenter mRemoteControlCenter = null;
    public static RemoteKeyboard mRemoteKeyboard = null;
    private static boolean sLoaded;
    private boolean bFullScreen;
    private boolean bModuleInit;
    private int countMove;
    private int currentX;
    private int currentY;
    private DisplayMetrics dm;
    private AccessEventHandler mAccessEventHandler;
    private IAccessListener mAccessListener;
    private int mCloseWidth;
    private Context mContext;
    private Button mControl_back;
    public Button mControl_close;
    private RelativeLayout mControl_layout;
    private Button mControl_more;
    private Button mControl_pop_game;
    private Button mControl_pop_home;
    private Button mControl_pop_menu;
    private Button mControl_pop_vol;
    private OnFullScreenListener mFullScreenListner;
    private int mLayoutWidth;
    public MirrorView mMirrorView;
    private MultiScreenMorePop mMorePop;
    private MultiScreenControlService mMultiScreenControlService;
    private OrientationDetector mOrientationDetector;
    private RemoteTouch mRemoteTouch;
    private TouchRequest mTouchInfo;
    private View.OnTouchListener mTouchListener;
    private View parent;
    private boolean remote_game_flag;
    private int screenHeight;
    private int screenWidth;
    private boolean vime_status;

    public interface OnFullScreenListener {
        void onFullScreenRequest(boolean z);
    }

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

    public MirrorPageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mMultiScreenControlService = null;
        this.mMirrorView = null;
        this.mLayoutWidth = 0;
        this.mCloseWidth = 0;
        this.remote_game_flag = false;
        this.dm = null;
        this.screenWidth = 0;
        this.screenHeight = 0;
        this.mRemoteTouch = null;
        this.mTouchInfo = new TouchRequest();
        this.countMove = 0;
        this.mAccessListener = null;
        this.mOrientationDetector = null;
        this.mAccessEventHandler = null;
        this.mContext = null;
        this.bFullScreen = false;
        this.bModuleInit = false;
        this.mTouchListener = new View.OnTouchListener() { // from class: mktvsmart.screen.hisientry.MirrorPageView.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) throws IOException {
                int id = v.getId();
                if (id != R.id.MirrorView) {
                    if (id != R.id.control_layout) {
                        return true;
                    }
                    int action = event.getAction();
                    if (action == 0) {
                        MirrorPageView.this.currentX = (int) event.getRawX();
                        MirrorPageView.this.currentY = (int) event.getRawY();
                        int right = MirrorPageView.this.mControl_layout.getRight();
                        int left = right - MirrorPageView.this.mControl_close.getWidth();
                        return MirrorPageView.this.currentX >= left && MirrorPageView.this.currentX <= right;
                    }
                    if (action == 2) {
                        int x2 = (int) event.getRawX();
                        int y2 = MirrorPageView.this.currentY;
                        MirrorPageView.this.moveViewBy(MirrorPageView.this.mControl_layout, x2 - MirrorPageView.this.currentX, y2 - MirrorPageView.this.currentY);
                        MirrorPageView.this.currentX = x2;
                        MirrorPageView.this.currentY = y2;
                        return true;
                    }
                    if (action == 1) {
                    }
                    return true;
                }
                MirrorPageView.this.handleMotionEvent(event);
                return true;
            }
        };
        loadMirrorLibs();
        setStrictMode();
        initData();
        initView();
        syncInfo();
    }

    public MirrorPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMultiScreenControlService = null;
        this.mMirrorView = null;
        this.mLayoutWidth = 0;
        this.mCloseWidth = 0;
        this.remote_game_flag = false;
        this.dm = null;
        this.screenWidth = 0;
        this.screenHeight = 0;
        this.mRemoteTouch = null;
        this.mTouchInfo = new TouchRequest();
        this.countMove = 0;
        this.mAccessListener = null;
        this.mOrientationDetector = null;
        this.mAccessEventHandler = null;
        this.mContext = null;
        this.bFullScreen = false;
        this.bModuleInit = false;
        this.mTouchListener = new View.OnTouchListener() { // from class: mktvsmart.screen.hisientry.MirrorPageView.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) throws IOException {
                int id = v.getId();
                if (id != R.id.MirrorView) {
                    if (id != R.id.control_layout) {
                        return true;
                    }
                    int action = event.getAction();
                    if (action == 0) {
                        MirrorPageView.this.currentX = (int) event.getRawX();
                        MirrorPageView.this.currentY = (int) event.getRawY();
                        int right = MirrorPageView.this.mControl_layout.getRight();
                        int left = right - MirrorPageView.this.mControl_close.getWidth();
                        return MirrorPageView.this.currentX >= left && MirrorPageView.this.currentX <= right;
                    }
                    if (action == 2) {
                        int x2 = (int) event.getRawX();
                        int y2 = MirrorPageView.this.currentY;
                        MirrorPageView.this.moveViewBy(MirrorPageView.this.mControl_layout, x2 - MirrorPageView.this.currentX, y2 - MirrorPageView.this.currentY);
                        MirrorPageView.this.currentX = x2;
                        MirrorPageView.this.currentY = y2;
                        return true;
                    }
                    if (action == 1) {
                    }
                    return true;
                }
                MirrorPageView.this.handleMotionEvent(event);
                return true;
            }
        };
        loadMirrorLibs();
        setStrictMode();
        initData();
        initView();
        syncInfo();
    }

    public MirrorPageView(Context context) {
        super(context);
        this.mMultiScreenControlService = null;
        this.mMirrorView = null;
        this.mLayoutWidth = 0;
        this.mCloseWidth = 0;
        this.remote_game_flag = false;
        this.dm = null;
        this.screenWidth = 0;
        this.screenHeight = 0;
        this.mRemoteTouch = null;
        this.mTouchInfo = new TouchRequest();
        this.countMove = 0;
        this.mAccessListener = null;
        this.mOrientationDetector = null;
        this.mAccessEventHandler = null;
        this.mContext = null;
        this.bFullScreen = false;
        this.bModuleInit = false;
        this.mTouchListener = new View.OnTouchListener() { // from class: mktvsmart.screen.hisientry.MirrorPageView.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) throws IOException {
                int id = v.getId();
                if (id != R.id.MirrorView) {
                    if (id != R.id.control_layout) {
                        return true;
                    }
                    int action = event.getAction();
                    if (action == 0) {
                        MirrorPageView.this.currentX = (int) event.getRawX();
                        MirrorPageView.this.currentY = (int) event.getRawY();
                        int right = MirrorPageView.this.mControl_layout.getRight();
                        int left = right - MirrorPageView.this.mControl_close.getWidth();
                        return MirrorPageView.this.currentX >= left && MirrorPageView.this.currentX <= right;
                    }
                    if (action == 2) {
                        int x2 = (int) event.getRawX();
                        int y2 = MirrorPageView.this.currentY;
                        MirrorPageView.this.moveViewBy(MirrorPageView.this.mControl_layout, x2 - MirrorPageView.this.currentX, y2 - MirrorPageView.this.currentY);
                        MirrorPageView.this.currentX = x2;
                        MirrorPageView.this.currentY = y2;
                        return true;
                    }
                    if (action == 1) {
                    }
                    return true;
                }
                MirrorPageView.this.handleMotionEvent(event);
                return true;
            }
        };
        loadMirrorLibs();
        setStrictMode();
        initData();
        initView();
        syncInfo();
    }

    private static boolean loadMirrorLibs() {
        if (HisiLibLoader.isLoaded()) {
            return true;
        }
        boolean err = false;
        try {
            String osVersion = Build.VERSION.SDK;
            if (Integer.valueOf(osVersion).intValue() < 14) {
                LogTool.d("load libmirror23_jni.so");
                System.loadLibrary("mirror23_jni");
            } else if (Integer.valueOf(osVersion).intValue() < 19) {
                LogTool.d("load libmirror40_jni.so");
                System.loadLibrary("mirror40_jni");
            } else if (Integer.valueOf(osVersion).intValue() < 21) {
                LogTool.d("load libmirror44_jni.so");
                System.loadLibrary("mirror44_jni");
            } else {
                LogTool.d("load libmirror50_jni.so");
                System.loadLibrary("mirror50_jni");
            }
        } catch (UnsatisfiedLinkError e) {
            err = true;
            LogTool.e("Error when Loading our lib: " + e.getMessage());
        }
        if (!err) {
            sLoaded = true;
        }
        return sLoaded;
    }

    @SuppressLint({"NewApi"})
    private void setStrictMode() {
        LogTool.d("setStrictMode");
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
    }

    private void initData() {
        LogTool.d("initData");
        ServiceUtil.checkMultiScreenControlService(getContext());
        this.mMultiScreenControlService = MultiScreenControlService.getInstance();
    }

    private void initView() {
        LogTool.d("initView");
        this.mContext = getContext();
        LayoutInflater.from(getContext()).inflate(R.layout.stb_mirror_layout, (ViewGroup) this, true);
        this.vime_status = readStatusPreference(MultiSettingActivity.VIME_STATUS_KEY, true);
        this.dm = getResources().getDisplayMetrics();
        this.screenWidth = this.dm.widthPixels;
        this.screenHeight = this.dm.heightPixels;
        this.mControl_close = (Button) findViewById(R.id.control_close);
        this.mControl_close.setOnClickListener(this);
        this.mControl_layout = (RelativeLayout) findViewById(R.id.control_layout);
        this.mControl_layout.setOnTouchListener(this.mTouchListener);
        this.mControl_back = (Button) findViewById(R.id.control_back);
        this.mControl_back.setOnClickListener(this);
        this.mControl_more = (Button) findViewById(R.id.control_more);
        this.mControl_more.setOnClickListener(this);
        this.mMorePop = new MultiScreenMorePop((Activity) this.mContext, this);
        this.mMirrorView = (MirrorView) findViewById(R.id.MirrorView);
    }

    private void syncInfo() {
        MultiScreenControlService.ClientState state = this.mMultiScreenControlService.getState();
        Log.d("View", "ClientState " + state);
        switch ($SWITCH_TABLE$com$hisilicon$multiscreen$mybox$MultiScreenControlService$ClientState()[state.ordinal()]) {
            case 1:
                if (this.mMultiScreenControlService.canSyncInfo()) {
                    this.mMultiScreenControlService.startPing();
                    initModules();
                    break;
                } else {
                    LogTool.e("sync STB info failed!");
                    this.mMultiScreenControlService.setState(MultiScreenControlService.ClientState.NETWORK_LOST);
                    break;
                }
            case 2:
            default:
                LogTool.e("The client state is " + state.toString() + " when activity on create.");
                break;
            case 3:
                LogTool.d("Resume MultiScreenActivity from HOME.");
                initModules();
                break;
        }
    }

    private void initModules() {
        LogTool.d("initModules");
        initRemoteCenter();
        initMirror();
        initTouch();
        initVIME();
        this.bModuleInit = true;
    }

    private void initMirror() {
        LogTool.d("initMirror");
        if (!initMirrorServer()) {
            LogTool.e("Fail to set mirror parameter.");
        }
        loadMirrorLibs();
    }

    private boolean initMirrorServer() {
        return this.mMultiScreenControlService.setDefaultMirrorParameter(2);
    }

    private void initTouch() {
        LogTool.d("initTouch");
        this.mRemoteTouch = mRemoteControlCenter.getRemoteTouch();
        if (this.mMirrorView != null) {
            this.mMirrorView.setOnTouchListener(this.mTouchListener);
        }
    }

    private void initRemoteCenter() {
        LogTool.d("initRemoteCenter");
        mRemoteControlCenter = MultiScreenControlService.getInstance().getRemoteControlCenter();
        mRemoteKeyboard = mRemoteControlCenter.getRemoteKeyboard();
    }

    private void initVIME() {
        LogTool.d("initVIME");
        if (this.vime_status) {
            this.mContext.startService(new Intent(this.mContext, (Class<?>) VImeClientControlService.class));
        } else {
            this.mContext.stopService(new Intent(this.mContext, (Class<?>) VImeClientControlService.class));
        }
    }

    private boolean readStatusPreference(String statusKey, boolean defValue) {
        SharedPreferences prefrence = this.mContext.getSharedPreferences(MultiSettingActivity.SETTING_STATUS_FILE_NAME, 0);
        return prefrence.getBoolean(statusKey, defValue);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initMirrorLayout();
    }

    public void resume() {
        this.mMultiScreenControlService.setTopActivity(MultiScreenControlService.TopActivity.mirror);
        enableOrientationDetector();
        resetAccessListener();
        checkState();
    }

    public void pause() {
        disableOrientationDetector();
        stopMirror();
        stopGsensor();
    }

    private void enableOrientationDetector() {
        if (this.mOrientationDetector != null) {
            this.mOrientationDetector.enable();
        }
    }

    private void disableOrientationDetector() {
        if (this.mOrientationDetector != null) {
            this.mOrientationDetector.disable();
        }
    }

    private void resetAccessListener() {
        if (this.mAccessEventHandler == null) {
            this.mAccessEventHandler = new AccessEventHandler(this);
        }
        if (this.mAccessListener == null) {
            this.mAccessListener = new IAccessListener() { // from class: mktvsmart.screen.hisientry.MirrorPageView.2
                @Override // com.hisilicon.multiscreen.controller.IAccessListener
                public void dealNetWorkNotWellEvent() {
                    MirrorPageView.this.mAccessEventHandler.sendEmptyMessage(10);
                }

                @Override // com.hisilicon.multiscreen.controller.IAccessListener
                public void dealNetWorkLostEvent(IAccessListener.Caller caller) {
                    LogTool.d("keep alive fail.");
                    MirrorPageView.this.sendAccessStatusMessage(caller, 20);
                }

                @Override // com.hisilicon.multiscreen.controller.IAccessListener
                public void dealReaveEvent(IAccessListener.Caller caller) {
                    LogTool.d("be reaved.");
                    MirrorPageView.this.sendAccessStatusMessage(caller, 30);
                }

                @Override // com.hisilicon.multiscreen.controller.IAccessListener
                public void dealSTBLeaveEvent(IAccessListener.Caller caller) {
                    LogTool.d("STB leave.");
                    MirrorPageView.this.sendAccessStatusMessage(caller, 40);
                }

                @Override // com.hisilicon.multiscreen.controller.IAccessListener
                public void dealSTBSuspendEvent(IAccessListener.Caller caller) {
                    LogTool.d("STB suspend.");
                    MirrorPageView.this.sendAccessStatusMessage(caller, 80);
                }
            };
        }
        this.mMultiScreenControlService.setAllAccessListener(this.mAccessListener);
    }

    private void checkState() {
        switch ($SWITCH_TABLE$com$hisilicon$multiscreen$mybox$MultiScreenControlService$ClientState()[this.mMultiScreenControlService.getState().ordinal()]) {
            case 3:
                LogTool.d("check state RUNNING.");
                startMirror();
                handleIntent();
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

    /* JADX INFO: Access modifiers changed from: private */
    public void sendAccessStatusMessage(IAccessListener.Caller caller, int what) {
        Message msg = this.mAccessEventHandler.obtainMessage();
        msg.obj = caller;
        msg.what = what;
        msg.sendToTarget();
    }

    private boolean startMirror() {
        LogTool.d("Start mirror.");
        boolean isOK = this.mMultiScreenControlService.startMirror(2);
        if (!isOK) {
            LogTool.e("Start mirror failed.");
        }
        return isOK;
    }

    private boolean stopMirror() {
        LogTool.d("Stop mirror.");
        boolean isOK = false;
        if ((this.mMultiScreenControlService.isRunning() || this.mMultiScreenControlService.getState() == MultiScreenControlService.ClientState.NETWORK_LOST) && !(isOK = this.mMultiScreenControlService.stopMirror(1))) {
            LogTool.w("Fail to stop mirror.");
        }
        return isOK;
    }

    private void handleIntent() {
        LogTool.d("");
        if (1 == 2) {
            this.remote_game_flag = true;
            this.mControl_pop_game.setBackgroundResource(R.drawable.image_control_game_focus);
            startGsensor();
        } else {
            this.remote_game_flag = false;
            this.mControl_pop_game.setBackgroundResource(R.drawable.image_control_game);
            stopGsensor();
        }
    }

    private void startGsensor() {
        LogTool.d("Start gsensor service.");
        this.mContext.startService(new Intent(this.mContext, (Class<?>) SensorService.class));
    }

    private void stopGsensor() {
        LogTool.v("Stop gsensor service.");
        this.mContext.stopService(new Intent(this.mContext, (Class<?>) SensorService.class));
    }

    public void doFinishWork() {
        unbindDrawables(this);
        stopGsensor();
        deInitVIMEService();
        this.mMultiScreenControlService.setAllAccessListener(null);
        this.mMirrorView = null;
        this.mContext = null;
    }

    private void unbindDrawables(View view) {
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

    private void deInitVIMEService() {
        LogTool.v("stopService VImeClientControlService.");
        this.mContext.sendBroadcast(new Intent(MultiScreenIntentAction.END_INPUT_BY_STB));
        if (this.vime_status) {
            this.mContext.stopService(new Intent(this.mContext, (Class<?>) VImeClientControlService.class));
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) throws IOException {
        if (this.bModuleInit) {
            if (this.mMorePop != null) {
                this.mMorePop.dismiss();
            }
            int id = view.getId();
            if (id == R.id.control_back) {
                mRemoteKeyboard.sendDownAndUpKeyCode(158);
                return;
            }
            if (id == R.id.control_more) {
                this.mMorePop.showAtLocation(findViewById(R.id.control_more), 1, 0, 0);
                if (this.remote_game_flag) {
                    this.mControl_pop_game.setBackgroundResource(R.drawable.image_control_game_focus);
                    return;
                } else {
                    this.mControl_pop_game.setBackgroundResource(R.drawable.image_control_game);
                    return;
                }
            }
            if (id == R.id.control_close) {
                exitImageControl();
                return;
            }
            if (id == R.id.control_pop_home) {
                mRemoteKeyboard.sendDownAndUpKeyCode(102);
                return;
            }
            if (id == R.id.control_pop_menu) {
                mRemoteKeyboard.sendDownAndUpKeyCode(KeyInfo.KEYCODE_MENU);
                return;
            }
            if (id == R.id.control_pop_vol) {
                mRemoteKeyboard.sendDownAndUpKeyCode(KeyInfo.KEYCODE_VOLUME_UP);
                mRemoteKeyboard.sendDownAndUpKeyCode(KeyInfo.KEYCODE_VOLUME_DOWN);
            } else if (id == R.id.control_pop_game) {
                this.remote_game_flag = this.remote_game_flag ? false : true;
                if (this.remote_game_flag) {
                    this.mControl_pop_game.setBackgroundResource(R.drawable.image_control_game_focus);
                    startGsensor();
                } else {
                    this.mControl_pop_game.setBackgroundResource(R.drawable.image_control_game);
                    stopGsensor();
                }
            }
        }
    }

    private void exitImageControl() {
        if (this.mFullScreenListner != null) {
            this.bFullScreen = !this.bFullScreen;
            this.mFullScreenListner.onFullScreenRequest(this.bFullScreen);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMotionEvent(MotionEvent event) throws IOException {
        int index = event.getActionIndex();
        if (index > 1) {
            LogTool.d("Action index is not 0 or 1, so we will not handle it.");
        }
        int pointCount = event.getPointerCount() == 1 ? 1 : 2;
        int x0 = getMappingX((int) event.getX(0));
        int y0 = getMappingY((int) event.getY(0));
        int x1 = pointCount == 1 ? 0 : getMappingX((int) event.getX(1));
        int y1 = pointCount == 1 ? 0 : getMappingY((int) event.getY(1));
        int id0 = event.getPointerId(0) == 0 ? 0 : 1;
        if (pointCount > 1) {
            id0 = event.getPointerId(0) <= event.getPointerId(1) ? 0 : 1;
        }
        int id1 = id0 == 0 ? 1 : 0;
        switch (event.getActionMasked()) {
            case 0:
            case 5:
                if (pointCount == 1) {
                    this.mTouchInfo.setFingerInfo(id0, x0, y0, true);
                    this.mTouchInfo.getFingerInfo(id1).setPress(false);
                } else {
                    this.mTouchInfo.setFingerInfo(id0, x0, y0, true);
                    this.mTouchInfo.setFingerInfo(id1, x1, y1, true);
                }
                this.mRemoteTouch.sendMultiTouchEvent(this.mTouchInfo);
                break;
            case 1:
            case 3:
            case 6:
                if (pointCount == 1) {
                    this.mTouchInfo.setFingerInfo(id0, x0, y0, false);
                    this.mTouchInfo.setFingerInfo(id1, x1, y1, false);
                } else if (index == 0) {
                    this.mTouchInfo.setFingerInfo(id0, x0, y0, false);
                    this.mTouchInfo.setFingerInfo(id1, x1, y1, true);
                } else {
                    this.mTouchInfo.setFingerInfo(id0, x0, y0, true);
                    this.mTouchInfo.setFingerInfo(id1, x1, y1, false);
                }
                this.mRemoteTouch.sendMultiTouchEvent(this.mTouchInfo);
                this.mRemoteTouch.sendMultiTouchEvent(this.mTouchInfo);
                break;
            case 2:
                if (this.countMove != 1) {
                    this.countMove++;
                    break;
                } else {
                    this.countMove = 0;
                    if (pointCount == 1) {
                        this.mTouchInfo.setFingerInfo(id0, x0, y0, true);
                    } else {
                        this.mTouchInfo.setFingerInfo(id0, x0, y0, true);
                        this.mTouchInfo.setFingerInfo(id1, x1, y1, true);
                    }
                    this.mRemoteTouch.sendMultiTouchEvent(this.mTouchInfo);
                    break;
                }
            case 4:
            default:
                LogTool.e("Unkown Motion Action:" + event.getAction());
                break;
        }
    }

    private int getMappingX(int x) {
        int localWidth;
        if (this.mMirrorView == null || this.mMirrorView.mSurfaceHolder == null) {
            localWidth = this.screenWidth - 1;
        } else {
            localWidth = this.mMirrorView.mSurfaceHolder.getSurfaceFrame().width() - 1;
        }
        if (x > localWidth) {
            return MAX_STB_TOUCH_WIDTH;
        }
        int ret_value = (x * MAX_STB_TOUCH_WIDTH) / localWidth;
        return ret_value;
    }

    private int getMappingY(int y) {
        int localHeight;
        if (this.mMirrorView == null || this.mMirrorView.mSurfaceHolder == null) {
            localHeight = this.screenHeight - 1;
        } else {
            localHeight = this.mMirrorView.mSurfaceHolder.getSurfaceFrame().height() - 1;
        }
        if (y > localHeight) {
            return MAX_STB_TOUCH_HEIGHT;
        }
        int ret_value = (y * MAX_STB_TOUCH_HEIGHT) / localHeight;
        return ret_value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void moveViewBy(View view, int x, int y) {
        int left = view.getLeft() + x;
        int right = view.getRight() + x;
        int top = view.getTop() + y;
        int bottom = view.getBottom() + y;
        this.mLayoutWidth = this.mControl_layout.getWidth();
        this.mCloseWidth = this.mControl_close.getWidth();
        int width = this.mLayoutWidth - this.mCloseWidth;
        if (left >= (-width) && right <= this.screenWidth) {
            view.layout(left, top, right, bottom);
        }
    }

    class MultiScreenMorePop extends PopupWindow {
        public MultiScreenMorePop(Activity context, View.OnClickListener click) {
            super(context);
            MirrorPageView.this.parent = LayoutInflater.from(context).inflate(R.layout.mybox_image_pop, (ViewGroup) null);
            MirrorPageView.this.mControl_pop_home = (Button) MirrorPageView.this.parent.findViewById(R.id.control_pop_home);
            MirrorPageView.this.mControl_pop_home.setOnClickListener(click);
            MirrorPageView.this.mControl_pop_menu = (Button) MirrorPageView.this.parent.findViewById(R.id.control_pop_menu);
            MirrorPageView.this.mControl_pop_menu.setOnClickListener(click);
            MirrorPageView.this.mControl_pop_vol = (Button) MirrorPageView.this.parent.findViewById(R.id.control_pop_vol);
            MirrorPageView.this.mControl_pop_vol.setOnClickListener(click);
            MirrorPageView.this.mControl_pop_game = (Button) MirrorPageView.this.parent.findViewById(R.id.control_pop_game);
            MirrorPageView.this.mControl_pop_game.setOnClickListener(click);
            setContentView(MirrorPageView.this.parent);
            setWidth(-2);
            setHeight(-2);
            setFocusable(true);
            setTouchable(true);
            setOutsideTouchable(false);
            setBackgroundDrawable(new BitmapDrawable());
            update();
        }
    }

    private static class AccessEventHandler extends WeakHandler<MirrorPageView> {
        public AccessEventHandler(MirrorPageView owner) {
            super(owner);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MirrorPageView target = getOwner();
            if (target != null) {
                switch (msg.what) {
                    case 10:
                        LogTool.e("keep alive packet loss!");
                        target.dealPacketLoss();
                        break;
                    case 20:
                        break;
                    case 30:
                        break;
                    case 40:
                        break;
                    case 80:
                        break;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dealPacketLoss() {
        Toast.makeText(this.mContext, "network is not well!", 0).show();
    }

    public OnFullScreenListener getFullScreenListner() {
        return this.mFullScreenListner;
    }

    public void setFullScreenListner(OnFullScreenListener mFullScreenListner) {
        this.mFullScreenListner = mFullScreenListner;
    }

    public void initMirrorLayout() {
        Rect rect = new Rect();
        getWindowVisibleDisplayFrame(rect);
        int sw = rect.width();
        int sh = rect.height();
        Log.d("MirrorPageView", "visible mirrorview dw = " + sw + " dh = " + sh);
        double dw = sw;
        double dh = sh;
        boolean isPortrait = getResources().getConfiguration().orientation == 1;
        if ((sw > sh && isPortrait) || (sw < sh && !isPortrait)) {
            dw = sh;
            dh = sw;
        }
        if (dw * dh != 0.0d) {
            if (isPortrait) {
                double dar = dw / dh;
                if (dar < 1.6d) {
                    dh = dw / 1.6d;
                } else {
                    dw = dh * 1.6d;
                }
            }
            Log.d("MirrorPageView", "set mirrorview dw = " + dw + " dh = " + dh);
            this.mMirrorView.setViewSize((int) dw, (int) dh);
            this.mMirrorView.update_view_size();
        }
    }

    public class OrientationDetector extends OrientationEventListener {
        public OrientationDetector(Context context) {
            super(context);
        }

        @Override // android.view.OrientationEventListener
        public void onOrientationChanged(int orientation) {
            if (orientation != -1 && orientation <= 350) {
            }
        }
    }
}
