package com.hisilicon.multiscreen.gsensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import com.hisilicon.multiscreen.mybox.MultiScreenControlService;
import com.hisilicon.multiscreen.mybox.MultiSettingActivity;
import com.hisilicon.multiscreen.protocol.message.PushMessageHead;
import com.hisilicon.multiscreen.protocol.remote.RemoteKeyboard;
import com.hisilicon.multiscreen.protocol.remote.RemoteMouse;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import java.io.IOException;
import java.util.List;

/* loaded from: classes.dex */
public class AirMouse {
    private static final float ANTI_SHAKE_MOVE_STEP = 8.0f;
    private static final long DELAY_MILLIS_ANTI_SHAKE = 500;
    private static final long DELAY_MILLIS_HIDE_MOUSE = 100;
    private static final float MAX_MOVE_X = 3840.0f;
    private static final float MAX_MOVE_Y = 2160.0f;
    private static final float MIN_MOVE_STEP = 2.0f;
    private static final int MSG_ANTI_SHAKE = 1;
    private static final int MSG_HIDE_MOUSE = 2;
    public static boolean sLoaded = false;
    private boolean mIsEnable = false;
    private SensorManager mSensormanager = null;
    private SensorEventListener mSensorEventListener = null;
    private Sensor gyroSensor = null;
    private Sensor accSensor = null;
    private Sensor mgcSensor = null;
    private Sensor gravSensor = null;
    private float[] newInputArray = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
    private float[] sendCoord = {0.0f, 0.0f};
    private RemoteMouse mMouse = null;
    private RemoteKeyboard mRemoteKeyboard = null;
    private int mMouseState = 256;
    private long timestamp = 0;
    private float mMinStep = MIN_MOVE_STEP;
    private HandlerThread mHandlerThread = null;
    private AntiShakeHandler AntiShakeHandler = null;
    private HandlerThread mHideHandlerThread = null;
    private HideMouseHandler mHideMouseHandler = null;
    public float[] newAcc = new float[3];
    public float[] newMgc = new float[3];
    public float[] newRotation = new float[3];
    private float[] rotationMatrix = new float[9];

    private native int native_getAirCoord(float f, float[] fArr, float[] fArr2);

    public AirMouse(Context context) {
        reset(context);
    }

    public void reset(Context context) {
        initRemote();
        initSensor(context);
        initHideHandler();
    }

    public void deinit() {
        unregisterSensor();
        deinitAntiShakeHandler();
        deinitHideHandler();
        this.mIsEnable = false;
    }

    public boolean isSupported() {
        List<Sensor> sensorList = this.mSensormanager.getSensorList(4);
        if (sensorList.isEmpty()) {
            LogTool.e("Airmouse is not supported without GYR sensor.");
            return false;
        }
        return true;
    }

    public boolean enable() {
        cancelHide();
        this.mIsEnable = registerSensor(this.gyroSensor);
        if (this.mIsEnable) {
            this.mIsEnable = registerSensor(this.gravSensor);
            initAntiShakeHandler();
        }
        return this.mIsEnable;
    }

    public void disable() throws IOException {
        unregisterSensor();
        deinitAntiShakeHandler();
        hideMouse();
        this.mIsEnable = false;
    }

    public void down() throws IOException {
        if (this.mMouseState != 773) {
            increaseAntiShake();
            this.mMouse.sendMouseClickEvent(PushMessageHead.GET_APPS_RESPONSE);
            this.mMouseState = PushMessageHead.PLAY_MEDIA;
        }
    }

    public void up() throws IOException {
        reduceAntiShake();
        this.mMouse.sendMouseClickEvent(PushMessageHead.LAUNCH_APP);
        this.mMouseState = 256;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void move() throws IOException {
        native_getAirCoord(this.mMinStep, this.newInputArray, this.sendCoord);
        this.mMouse.sendMouseMoveEvent(this.mMouseState, this.sendCoord[0], this.sendCoord[1]);
    }

    private void initRemote() {
        loadAirMouseLibs();
        MultiScreenControlService controlService = MultiScreenControlService.getInstance();
        if (controlService != null) {
            this.mMouse = controlService.getRemoteControlCenter().getRemoteMouse();
            this.mRemoteKeyboard = controlService.getRemoteControlCenter().getRemoteKeyboard();
        } else {
            LogTool.e("Fail to init remote, MultiScreenControlService is null.");
        }
    }

    private void setSensorManager(Context context) {
        if (context == null) {
            LogTool.e("context is null");
            return;
        }
        this.mSensormanager = (SensorManager) context.getSystemService(MultiSettingActivity.SENSOR_STATUS_KEY);
        this.gyroSensor = this.mSensormanager.getDefaultSensor(4);
        this.accSensor = this.mSensormanager.getDefaultSensor(1);
        this.mgcSensor = this.mSensormanager.getDefaultSensor(2);
        this.gravSensor = this.mSensormanager.getDefaultSensor(9);
    }

    private void initSensor(Context context) {
        setSensorManager(context);
        if (this.mSensorEventListener == null) {
            this.mSensorEventListener = new SensorEventListener() { // from class: com.hisilicon.multiscreen.gsensor.AirMouse.1
                @Override // android.hardware.SensorEventListener
                public void onSensorChanged(SensorEvent event) throws IOException {
                    switch (event.sensor.getType()) {
                        case 1:
                            AirMouse.this.newAcc[0] = event.values[0];
                            AirMouse.this.newAcc[1] = event.values[1];
                            AirMouse.this.newAcc[2] = event.values[2];
                            break;
                        case 2:
                            AirMouse.this.newMgc[0] = event.values[0];
                            AirMouse.this.newMgc[1] = event.values[1];
                            AirMouse.this.newMgc[2] = event.values[2];
                            AirMouse.this.calculateOrientation();
                            break;
                        case 4:
                            AirMouse.this.newInputArray[0] = event.values[0];
                            AirMouse.this.newInputArray[1] = event.values[1];
                            AirMouse.this.newInputArray[2] = event.values[2];
                            AirMouse.this.move();
                            break;
                        case 9:
                            AirMouse.this.newInputArray[3] = event.values[0];
                            AirMouse.this.newInputArray[4] = event.values[1];
                            AirMouse.this.newInputArray[5] = event.values[2];
                            break;
                    }
                }

                @Override // android.hardware.SensorEventListener
                public void onAccuracyChanged(Sensor s, int accuracy) {
                }
            };
        }
    }

    private boolean registerSensor(Sensor sensor) {
        return this.mSensormanager.registerListener(this.mSensorEventListener, sensor, 1);
    }

    private void unregisterSensor() {
        if (this.mSensormanager != null) {
            this.mSensormanager.unregisterListener(this.mSensorEventListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMinStep(float step) {
        if (step > 10.0f) {
            step = 10.0f;
            LogTool.e("ERROR: minStep is large than 10.0");
        }
        this.mMinStep = step;
    }

    private void increaseAntiShake() {
        setMinStep(ANTI_SHAKE_MOVE_STEP);
        reduceAntiShakeDelay();
    }

    private void reduceAntiShake() {
        setMinStep(MIN_MOVE_STEP);
    }

    private void initAntiShakeHandler() {
        if (this.mHandlerThread == null) {
            this.mHandlerThread = new HandlerThread("AirMouseHandlerThread");
            this.mHandlerThread.start();
            this.AntiShakeHandler = new AntiShakeHandler(this.mHandlerThread.getLooper());
        }
    }

    private void deinitAntiShakeHandler() {
        if (this.mHandlerThread != null) {
            clearAntiShakeMessage();
            Looper looper = this.mHandlerThread.getLooper();
            if (looper != null) {
                looper.quit();
            }
            this.mHandlerThread = null;
        }
    }

    private void clearAntiShakeMessage() {
        if (this.AntiShakeHandler.hasMessages(1)) {
            this.AntiShakeHandler.removeMessages(1);
        }
    }

    private void reduceAntiShakeDelay() {
        clearAntiShakeMessage();
        Message msg = this.AntiShakeHandler.obtainMessage(1);
        this.AntiShakeHandler.sendMessageDelayed(msg, DELAY_MILLIS_ANTI_SHAKE);
    }

    private class AntiShakeHandler extends Handler {
        AntiShakeHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    AirMouse.this.setMinStep(AirMouse.MIN_MOVE_STEP);
                    break;
            }
        }
    }

    private boolean isEnable() {
        return this.mIsEnable;
    }

    private void initHideHandler() {
        this.mHideHandlerThread = new HandlerThread("AirMouseHideHandlerThread");
        this.mHideHandlerThread.start();
        this.mHideMouseHandler = new HideMouseHandler(this.mHideHandlerThread.getLooper());
    }

    private void deinitHideHandler() {
        if (this.mHideHandlerThread != null) {
            clearHideMessage();
            Looper looper = this.mHideHandlerThread.getLooper();
            if (looper != null) {
                looper.quit();
            }
            this.mHideHandlerThread = null;
        }
    }

    private void hideMouse() throws IOException {
        if (this.mMouse != null && isEnable()) {
            up();
            startHideMouse();
        }
    }

    private void cancelHide() {
        clearHideMessage();
    }

    private void startHideMouse() {
        clearHideMessage();
        Message msg = this.mHideMouseHandler.obtainMessage(2);
        this.mHideMouseHandler.sendMessageDelayed(msg, DELAY_MILLIS_HIDE_MOUSE);
    }

    private void clearHideMessage() {
        if (this.mHideMouseHandler.hasMessages(2)) {
            this.mHideMouseHandler.removeMessages(2);
        }
    }

    private class HideMouseHandler extends Handler {
        HideMouseHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) throws InterruptedException, IOException {
            switch (msg.what) {
                case 2:
                    AirMouse.this.handleHideMouse();
                    break;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleHideMouse() throws InterruptedException, IOException {
        if (this.mMouse != null) {
            this.mMouse.sendMouseMoveEvent(256, MAX_MOVE_X, MAX_MOVE_Y);
            threadSleep(50);
            this.mRemoteKeyboard.sendDownAndUpKeyCode(88);
            threadSleep(500);
            this.mRemoteKeyboard.sendDownAndUpKeyCode(88);
        }
    }

    private void threadSleep(int time) throws InterruptedException {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            LogTool.w(e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void calculateOrientation() {
        SensorManager.getRotationMatrix(this.rotationMatrix, null, this.newAcc, this.newMgc);
        SensorManager.getOrientation(this.rotationMatrix, this.newRotation);
        this.newRotation[0] = (float) Math.toDegrees(this.newRotation[0]);
        this.newRotation[1] = (float) Math.toDegrees(this.newRotation[1]);
        this.newRotation[2] = (float) Math.toDegrees(this.newRotation[2]);
    }

    private static boolean loadAirMouseLibs() {
        return true;
    }
}
