package com.hisilicon.multiscreen.mirror;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioTrack;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServerUtil;
import com.hisilicon.multiscreen.mybox.MultiScreenControlService;
import com.hisilicon.multiscreen.protocol.HiDeviceInfo;
import com.hisilicon.multiscreen.protocol.utils.HostNetInterface;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import com.huawei.videoengine.ViERenderer;
import com.huawei.videoengineapp.ViEAndroidJavaAPI;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class MirrorView extends SurfaceView {
    private static AudioTrack mAudioTrack = null;
    private static String sLastSurfaceType = null;
    private ViEAndroidJavaAPI ViEAndroidAPI;
    private String mAccessIp;
    private Context mContext;
    public boolean mIsOnWhilePlaying;
    private int mNativeContext;
    SurfaceHolder.Callback mSHCallback;
    public Surface mSurface;
    public SurfaceHolder mSurfaceHolder;
    private SurfaceView mSurfaceView;
    private LinearLayout mViewLayout;
    private boolean useHME;
    private boolean viERunning;
    private int viewHeight;
    private int viewWidth;

    public native void configAudio(int i);

    public native void destroyH264Surface();

    public native void destroyMirrorSurface();

    public native int getdecodefps();

    public native void nativeSetup(Object obj);

    public native void startAudio(Object obj);

    public native void startH264Surface(Surface surface, int i, String str);

    public native void startMirrorSurface(Surface surface, int i, String str);

    public native void stopAudio();

    public MirrorView(Context context) {
        super(context);
        this.mSurfaceHolder = null;
        this.mIsOnWhilePlaying = false;
        this.mSurface = null;
        this.mAccessIp = null;
        this.mContext = null;
        this.ViEAndroidAPI = null;
        this.mSurfaceView = null;
        this.viERunning = false;
        this.mViewLayout = null;
        this.useHME = true;
        this.mSHCallback = new SurfaceHolder.Callback() { // from class: com.hisilicon.multiscreen.mirror.MirrorView.1
            @Override // android.view.SurfaceHolder.Callback
            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                LogTool.v("surfaceChanged w = " + w + " h = " + h);
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceCreated(SurfaceHolder holder) {
                LogTool.v("surfaceCreated start");
                MirrorView.this.mSurfaceHolder = holder;
                MirrorView.this.mSurfaceHolder.setFormat(4);
                MirrorView.this.openSurface();
                LogTool.v("surfaceCreated end");
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceDestroyed(SurfaceHolder holder) throws IllegalStateException {
                LogTool.v("surfaceDestroyed start");
                MirrorView.this.destroySurface();
                MirrorView.this.mSurfaceHolder = null;
                LogTool.v("surfaceDestroyed end");
            }
        };
        initSurfaceView(context);
    }

    public MirrorView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.mSurfaceHolder = null;
        this.mIsOnWhilePlaying = false;
        this.mSurface = null;
        this.mAccessIp = null;
        this.mContext = null;
        this.ViEAndroidAPI = null;
        this.mSurfaceView = null;
        this.viERunning = false;
        this.mViewLayout = null;
        this.useHME = true;
        this.mSHCallback = new SurfaceHolder.Callback() { // from class: com.hisilicon.multiscreen.mirror.MirrorView.1
            @Override // android.view.SurfaceHolder.Callback
            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                LogTool.v("surfaceChanged w = " + w + " h = " + h);
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceCreated(SurfaceHolder holder) {
                LogTool.v("surfaceCreated start");
                MirrorView.this.mSurfaceHolder = holder;
                MirrorView.this.mSurfaceHolder.setFormat(4);
                MirrorView.this.openSurface();
                LogTool.v("surfaceCreated end");
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceDestroyed(SurfaceHolder holder) throws IllegalStateException {
                LogTool.v("surfaceDestroyed start");
                MirrorView.this.destroySurface();
                MirrorView.this.mSurfaceHolder = null;
                LogTool.v("surfaceDestroyed end");
            }
        };
        initSurfaceView(context);
    }

    public MirrorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mSurfaceHolder = null;
        this.mIsOnWhilePlaying = false;
        this.mSurface = null;
        this.mAccessIp = null;
        this.mContext = null;
        this.ViEAndroidAPI = null;
        this.mSurfaceView = null;
        this.viERunning = false;
        this.mViewLayout = null;
        this.useHME = true;
        this.mSHCallback = new SurfaceHolder.Callback() { // from class: com.hisilicon.multiscreen.mirror.MirrorView.1
            @Override // android.view.SurfaceHolder.Callback
            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                LogTool.v("surfaceChanged w = " + w + " h = " + h);
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceCreated(SurfaceHolder holder) {
                LogTool.v("surfaceCreated start");
                MirrorView.this.mSurfaceHolder = holder;
                MirrorView.this.mSurfaceHolder.setFormat(4);
                MirrorView.this.openSurface();
                LogTool.v("surfaceCreated end");
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceDestroyed(SurfaceHolder holder) throws IllegalStateException {
                LogTool.v("surfaceDestroyed start");
                MirrorView.this.destroySurface();
                MirrorView.this.mSurfaceHolder = null;
                LogTool.v("surfaceDestroyed end");
            }
        };
        initSurfaceView(context);
    }

    public void initSurfaceView(Context context) {
        HiDeviceInfo device = MultiScreenControlService.getInstance().getHiDevice();
        if (device != null) {
            this.mAccessIp = device.getDeviceIP();
        } else {
            this.mAccessIp = HttpServerUtil.LOOP;
        }
        this.mContext = context;
        LogTool.d("nativeSetup ---------------------");
        nativeSetup(new WeakReference(this));
        LogTool.d("nativeSetup ~~~~~~~~~~~~~~~~~~~~~~~~");
        getHolder().addCallback(this.mSHCallback);
    }

    public void setViewSize(int width, int height) {
        this.viewWidth = width;
        this.viewHeight = height;
    }

    public void update_view_size() {
        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.width = this.viewWidth;
        lp.height = this.viewHeight;
        setLayoutParams(lp);
        invalidate();
    }

    public void setViewLayout(LinearLayout mLayout) {
        this.mViewLayout = mLayout;
    }

    private String GetLocalIpAddress() {
        WifiManager wifiMgr = (WifiManager) this.mContext.getSystemService("wifi");
        WifiInfo info = wifiMgr == null ? null : wifiMgr.getConnectionInfo();
        if (info == null) {
            return HttpServerUtil.LOOP;
        }
        String localIP = HostNetInterface.int2Ip(info.getIpAddress());
        return localIP;
    }

    private void startVideo() {
        LogTool.d("startVideo start");
        ViEAndroidJavaAPI.szRemoteIP = this.mAccessIp;
        ViEAndroidJavaAPI.szLocalIP = GetLocalIpAddress();
        LogTool.v("LocalIP  = " + ViEAndroidJavaAPI.szLocalIP + ",RemoteIP " + ViEAndroidJavaAPI.szRemoteIP);
        if (this.ViEAndroidAPI == null) {
            this.ViEAndroidAPI = new ViEAndroidJavaAPI(null);
        }
        int ret = this.ViEAndroidAPI.Init(ViEAndroidJavaAPI.logLevel);
        if (ret != 0) {
            LogTool.v("ViEAndroidAPI Init err..");
        }
        int ret2 = this.ViEAndroidAPI.SetLocalReceiver(ViEAndroidJavaAPI.uiLocalPort, ViEAndroidJavaAPI.szLocalIP.getBytes());
        if (ret2 != 0) {
            LogTool.v("ViEAndroidAPI SetLocalReceiver err..");
        }
        int ret3 = this.ViEAndroidAPI.SetSendDestination(ViEAndroidJavaAPI.uiRemotePort, ViEAndroidJavaAPI.szRemoteIP.getBytes());
        if (ret3 != 0) {
            LogTool.v("SetSendDestination err.." + ret3);
        }
        this.mSurfaceView = ViERenderer.CreateRenderer(this.mContext, true);
        if (this.mSurfaceView == null) {
            LogTool.v("ViEAndroidAPI CreateRenderer err..");
        }
        this.ViEAndroidAPI.SetReceiveCodec(ViEAndroidJavaAPI.uiBitRate, ViEAndroidJavaAPI.uiWidth, ViEAndroidJavaAPI.uiHeight, ViEAndroidJavaAPI.fFrameRate, ViEAndroidJavaAPI.eCipherType);
        int ret4 = this.ViEAndroidAPI.StartReceive(this.mSurfaceView);
        if (ret4 != 0) {
            LogTool.v("ViEAndroidAPI StartReceive err..");
        }
        this.ViEAndroidAPI.SetSendCodec(ViEAndroidJavaAPI.uiBitRate, ViEAndroidJavaAPI.eCipherType);
        int ret5 = this.ViEAndroidAPI.StartSend();
        if (ret5 != 0) {
            LogTool.v("ViEAndroidAPI StartSend err..");
        }
        this.mViewLayout.addView(this.mSurfaceView);
        this.viERunning = true;
        LogTool.d("startVideo end");
    }

    private void StopVideo() {
        LogTool.d("StopVideo begin");
        if (this.ViEAndroidAPI != null && this.viERunning) {
            this.viERunning = false;
            this.ViEAndroidAPI.Terminate();
            this.mViewLayout.removeView(this.mSurfaceView);
            ViERenderer.setSurfaceNull(this.mSurfaceView);
            ViERenderer.FreeLocalRenderResource();
            this.mSurfaceView = null;
        }
        LogTool.d("StopVideo end");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openSurface() {
        if (this.mSurfaceHolder != null) {
            this.mSurface = this.mSurfaceHolder.getSurface();
        } else {
            this.mSurface = null;
        }
        String osVersion = Build.VERSION.SDK;
        LogTool.v("osVersion: " + osVersion);
        String type = MultiScreenControlService.getInstance().getSupportVideoType();
        if (type.equalsIgnoreCase(MultiScreenControlService.VIDEO_H264_TYPE)) {
            if (this.useHME) {
                startVideo();
            } else {
                startH264Surface(this.mSurface, Integer.valueOf(osVersion).intValue(), this.mAccessIp);
            }
        } else {
            startMirrorSurface(this.mSurface, Integer.valueOf(osVersion).intValue(), this.mAccessIp);
        }
        sLastSurfaceType = type;
        startAudio(new WeakReference(this));
        if (MultiScreenControlService.getInstance().isAudioPlay()) {
            configAudio(1);
        } else {
            configAudio(0);
        }
        setScreenOnWhilePlaying(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public void destroySurface() throws IllegalStateException {
        LogTool.d("destroySurface begin");
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
        if (sLastSurfaceType.equalsIgnoreCase(MultiScreenControlService.VIDEO_H264_TYPE)) {
            LogTool.d("StopVideo begin");
            if (this.useHME) {
                StopVideo();
            } else {
                destroyH264Surface();
            }
            LogTool.d("StopVideo end");
        } else {
            destroyMirrorSurface();
        }
        stopAudio();
        this.mSurface.release();
        this.mSurface = null;
        LogTool.d("destroySurface end");
    }

    private void setScreenOnWhilePlaying(boolean screenOn) {
        if (this.mIsOnWhilePlaying != screenOn) {
            this.mIsOnWhilePlaying = screenOn;
            updateSurfaceScreenOn();
        }
    }

    private void updateSurfaceScreenOn() {
        if (this.mSurfaceHolder != null) {
            this.mSurfaceHolder.setKeepScreenOn(this.mIsOnWhilePlaying);
        }
    }

    private static void writePCM(byte[] byteArray) {
        if (mAudioTrack != null && 3 == mAudioTrack.getPlayState()) {
            mAudioTrack.write(byteArray, 0, byteArray.length);
        }
    }

    private static int configATrack(int streamType, int sampleRate, int channelConfig, int bytesPerSample, int trackMode) throws IllegalStateException {
        int latency = 0;
        int chanConfig = channelConfig != 2 ? 3 : 2;
        int iMinBufSize = AudioTrack.getMinBufferSize(sampleRate, chanConfig, bytesPerSample);
        LogTool.d("iMinBufSize" + iMinBufSize);
        if (iMinBufSize == -2 || iMinBufSize == -1) {
            return 0;
        }
        try {
            mAudioTrack = new AudioTrack(streamType, sampleRate, chanConfig, bytesPerSample, iMinBufSize * 4, trackMode);
            LogTool.d("mAudioTrack OK" + streamType + sampleRate + chanConfig + bytesPerSample + (iMinBufSize * 2) + trackMode);
            mAudioTrack.play();
            latency = (int) (((((iMinBufSize * 4.0d) * 1000.0d) / bytesPerSample) / sampleRate) / channelConfig);
            LogTool.d("mAudioTrack play OK");
        } catch (IllegalArgumentException iae) {
            LogTool.d("new AudioTrack Exceeption:" + iae.toString());
        }
        return latency;
    }
}
