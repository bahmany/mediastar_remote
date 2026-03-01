package com.hisilicon.multiscreen.gsensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import com.hisilicon.multiscreen.mybox.MultiSettingActivity;
import com.hisilicon.multiscreen.protocol.utils.LogTool;

/* loaded from: classes.dex */
public class ShakeMe {
    private static final int MAX_LINE_ACC_TIMES = 6;
    private static final int MAX_RECORDING_TIMES = 20;
    private static final float SHAKE_SENSOR_VALUE = 10.0f;
    private SensorManager mSensormanager = null;
    private Sensor mLineSensor = null;
    private SensorEventListener mSensorEventListener = null;
    private IShakeListener mShakeListener = null;
    private Vibrator mVibrator = null;
    private boolean isEnable = false;
    private int mShakeTimes = 0;
    private boolean mIsRecording = false;
    private int mRecordingTimes = 0;
    private float mTotalLineAccTimes = 0.0f;

    public ShakeMe(Context context, IShakeListener shakeListener) {
        initSensor(context);
        setListener(shakeListener);
        initVibrator(context);
    }

    public void setListener(IShakeListener shakeListener) {
        this.mShakeListener = shakeListener;
    }

    public void enable() {
        reset();
        this.isEnable = registerSensor();
    }

    public void disable() {
        reset();
        unregisterSensor();
        this.isEnable = false;
    }

    public void doVibrate() {
        long[] pattern = {0, 300, 200, 300};
        this.mVibrator.vibrate(pattern, -1);
    }

    private void initSensor(Context context) {
        this.mSensormanager = (SensorManager) context.getSystemService(MultiSettingActivity.SENSOR_STATUS_KEY);
        this.mLineSensor = this.mSensormanager.getDefaultSensor(10);
        this.mSensorEventListener = new SensorEventListener() { // from class: com.hisilicon.multiscreen.gsensor.ShakeMe.1
            @Override // android.hardware.SensorEventListener
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() != 10) {
                    return;
                }
                ShakeMe.this.handleSensorEvent(event.values[0], event.values[1]);
            }

            @Override // android.hardware.SensorEventListener
            public void onAccuracyChanged(Sensor s, int accuracy) {
            }
        };
    }

    private void initVibrator(Context context) {
        this.mVibrator = (Vibrator) context.getSystemService(MultiSettingActivity.VIBRATOR_STATUS_KEY);
    }

    private void reset() {
        this.mShakeTimes = 0;
        resetRecord();
    }

    private void resetRecord() {
        this.mIsRecording = false;
        this.mRecordingTimes = 0;
        this.mTotalLineAccTimes = 0.0f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSensorEvent(float x, float y) {
        if (!this.mIsRecording && isLineAcc(x, y)) {
            this.mIsRecording = true;
            this.mRecordingTimes = 20;
            this.mTotalLineAccTimes = 1.0f;
        } else if (this.mIsRecording && this.mRecordingTimes > 0) {
            this.mRecordingTimes--;
            if (isLineAcc(x, y)) {
                this.mTotalLineAccTimes += 1.0f;
            }
            if (this.mTotalLineAccTimes >= 6.0f) {
                shake();
                resetRecord();
            }
            if (this.mRecordingTimes == 0) {
                resetRecord();
            }
        }
    }

    private boolean isLineAcc(float x, float y) {
        return Math.abs(x) > SHAKE_SENSOR_VALUE || Math.abs(y) > SHAKE_SENSOR_VALUE;
    }

    private void shake() {
        this.mShakeTimes++;
        this.mShakeListener.shake(this.mShakeTimes, 0);
    }

    private boolean registerSensor() {
        if (this.mSensormanager == null) {
            LogTool.e("Fail to enable shakeMe, sensormanger is null.");
            return false;
        }
        if (this.isEnable) {
            return true;
        }
        boolean isOK = registerSensor(this.mLineSensor);
        return isOK;
    }

    private boolean registerSensor(Sensor sensor) {
        return this.mSensormanager.registerListener(this.mSensorEventListener, sensor, 2);
    }

    private void unregisterSensor() {
        if (this.mSensormanager != null) {
            this.mSensormanager.unregisterListener(this.mSensorEventListener);
        }
    }
}
