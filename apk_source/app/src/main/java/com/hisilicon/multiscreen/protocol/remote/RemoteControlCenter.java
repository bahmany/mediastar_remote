package com.hisilicon.multiscreen.protocol.remote;

import com.hisilicon.multiscreen.protocol.HiDeviceInfo;

/* loaded from: classes.dex */
public class RemoteControlCenter {
    private HiDeviceInfo mHiDevice;
    private RemoteKeyboard mKeyboard = null;
    private RemoteTouch mTouch = null;
    private RemoteMouse mMouse = null;
    private RemoteSensor mSensor = null;
    private RemoteMedia mRemoteMedia = null;
    private RemoteSpeech mRemoteSpeech = null;

    public RemoteControlCenter(HiDeviceInfo device) {
        this.mHiDevice = null;
        this.mHiDevice = device;
    }

    public void reset(HiDeviceInfo device) {
        this.mHiDevice = device;
        getRemoteKeyboard().resetDevice(device);
        getRemoteTouch().resetDevice(device);
        getRemoteMouse().resetDevice(device);
        getRemoteSensor().resetDevice(device);
        getRemoteSpeechControl().resetDevice(device);
        getRemoteMedia().resetDevice(device);
    }

    public String getDeviceIP() {
        return this.mHiDevice.getDeviceIP();
    }

    public RemoteKeyboard getRemoteKeyboard() {
        if (this.mKeyboard == null) {
            this.mKeyboard = new RemoteKeyboard(this.mHiDevice);
        }
        return this.mKeyboard;
    }

    public RemoteTouch getRemoteTouch() {
        if (this.mTouch == null) {
            this.mTouch = new RemoteTouch(this.mHiDevice);
        }
        return this.mTouch;
    }

    public RemoteMouse getRemoteMouse() {
        if (this.mMouse == null) {
            this.mMouse = new RemoteMouse(this.mHiDevice);
        }
        return this.mMouse;
    }

    public RemoteSensor getRemoteSensor() {
        if (this.mSensor == null) {
            this.mSensor = new RemoteSensor(this.mHiDevice);
        }
        return this.mSensor;
    }

    public RemoteSpeech getRemoteSpeechControl() {
        if (this.mRemoteSpeech == null) {
            this.mRemoteSpeech = new RemoteSpeech(this.mHiDevice);
        }
        return this.mRemoteSpeech;
    }

    public RemoteMedia getRemoteMedia() {
        if (this.mRemoteMedia == null) {
            this.mRemoteMedia = new RemoteMedia(this.mHiDevice);
        }
        return this.mRemoteMedia;
    }

    public boolean isDestroyed() {
        return this.mHiDevice == null;
    }

    public void destroy() {
        destroyKeyboard();
        destroyTouch();
        destroyMouse();
        destroySensor();
        destroyRemoteMedia();
        destroyRemoteSpeech();
        this.mHiDevice = null;
    }

    private void destroyKeyboard() {
        if (this.mKeyboard != null) {
            this.mKeyboard.destroy();
            this.mKeyboard = null;
        }
    }

    private void destroyTouch() {
        if (this.mTouch != null) {
            this.mTouch.destroy();
            this.mTouch = null;
        }
    }

    private void destroyMouse() {
        if (this.mMouse != null) {
            this.mMouse.destroy();
            this.mMouse = null;
        }
    }

    private void destroySensor() {
        if (this.mSensor != null) {
            this.mSensor.destroy();
            this.mSensor = null;
        }
    }

    private void destroyRemoteMedia() {
        if (this.mRemoteMedia != null) {
            this.mRemoteMedia.destroy();
            this.mRemoteMedia = null;
        }
    }

    private void destroyRemoteSpeech() {
        if (this.mRemoteSpeech != null) {
            this.mRemoteSpeech.destroy();
            this.mRemoteSpeech = null;
        }
    }
}
