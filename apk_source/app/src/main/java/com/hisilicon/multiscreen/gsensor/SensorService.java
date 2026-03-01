package com.hisilicon.multiscreen.gsensor;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import com.hisilicon.multiscreen.mybox.MultiScreenControlService;
import com.hisilicon.multiscreen.mybox.MultiSettingActivity;
import com.hisilicon.multiscreen.protocol.remote.RemoteSensor;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import java.io.IOException;

/* loaded from: classes.dex */
public class SensorService extends Service {
    public static final float GRAVITY_EARTH = 9.80665f;
    public static final float MAX_ACC = 19.6133f;
    private RemoteSensor mSensor = null;
    private SensorManager mSensormanager = null;
    private Sensor mAccSensor = null;
    private Sensor mGyrSensor = null;
    private SensorEventListener mSensorEventListener = new SensorEventListener() { // from class: com.hisilicon.multiscreen.gsensor.SensorService.1
        private float x = 0.0f;
        private float y = 0.0f;
        private float z = 0.0f;

        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent event) throws IOException {
            if (SensorService.this.mSensor != null) {
                this.x = event.values[0];
                this.y = event.values[1];
                this.z = event.values[2];
                SensorService.this.mSensor.sendSensorEvent(event.sensor.getType(), this.x, this.y, this.z);
            }
        }

        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor s, int accuracy) {
        }
    };

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        LogTool.d("onStart.");
        setSensorManager();
        this.mSensor = MultiScreenControlService.getInstance().getRemoteControlCenter().getRemoteSensor();
        return 2;
    }

    @Override // android.app.Service
    public void onDestroy() {
        LogTool.d("onDestroy");
        super.onDestroy();
        this.mSensormanager.unregisterListener(this.mSensorEventListener);
    }

    private void setSensorManager() {
        this.mSensormanager = (SensorManager) getSystemService(MultiSettingActivity.SENSOR_STATUS_KEY);
        this.mAccSensor = this.mSensormanager.getDefaultSensor(1);
        this.mGyrSensor = this.mSensormanager.getDefaultSensor(4);
        registerSensor(this.mAccSensor);
        registerSensor(this.mGyrSensor);
    }

    private void registerSensor(Sensor sensor) {
        this.mSensormanager.registerListener(this.mSensorEventListener, sensor, 1);
    }
}
