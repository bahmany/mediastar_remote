package mktvsmart.screen.vlc;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.ContentTree;
import com.hisilicon.multiscreen.mybox.MultiSettingActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Locale;
import mktvsmart.screen.R;
import mktvsmart.screen.exception.ReportPage;
import mktvsmart.screen.util.AdsControllor;
import mktvsmart.screen.vlc.BaseVLCPlayActivity;
import org.teleal.cling.model.ServiceReference;
import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcUtil;
import org.videolan.vlc.MediaDatabase;
import org.videolan.vlc.MediaWrapper;
import org.videolan.vlc.util.VLCInstance;
import org.videolan.vlc.util.WeakHandler;

/* loaded from: classes.dex */
public class LocalPlayActivity extends BaseVLCPlayActivity implements IVideoPlayer, View.OnClickListener {
    private static final int FADE_OUT = 1;
    private static final int FADE_OUT_INFO = 3;
    private static final int HIDE_VOLUME_BRIGHTNESS_BAR = 4;
    private static final int SHOW_PROGRESS = 6;
    private static final int START_PLAY = 5;
    private static final int SURFACE_SIZE = 2;
    private static final String TAG = LocalPlayActivity.class.getSimpleName();
    private AdView mAdView;
    private AudioManager mAudioManager;
    private PlayController mController;
    private GestureDetector mGestureDetector;
    private TextView mInfo;
    private LibVLC mLibVLC;
    private View mMainView;
    private ImageView mOperationBg;
    private ImageView mOperationPercent;
    private int mPreviousHardwareAccelerationMode;
    private BroadcastReceiver mReceiver;
    private int mScreenOrientation;
    private SharedPreferences mSettings;
    private SurfaceHolder mSubtitlesSurfaceHolder;
    private SurfaceView mSubtitlesSurfaceView;
    private FrameLayout mSurfaceFrame;
    private SurfaceHolder mSurfaceHolder;
    private SurfaceView mSurfaceView;
    private BaseVLCPlayActivity.VLCEventHandler mVlcEventHandler;
    private View mVolumeBrightnessLayout;
    private TextView mWaittingText;
    private View mWaittingView;
    private Surface mSurface = null;
    private Surface mSubtitleSurface = null;
    private float mCurrentVolumn = -1.0f;
    private int mSurfaceYDisplayRange = 0;
    private boolean mIsFirstBrightnessGesture = true;
    private boolean mDisabledHardwareAcceleration = false;
    private boolean bOnCreateFinish = false;
    public String address = "";
    private boolean isPauseByAd = false;
    private final SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() { // from class: mktvsmart.screen.vlc.LocalPlayActivity.1
        @Override // android.view.SurfaceHolder.Callback
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (LocalPlayActivity.this.mLibVLC != null) {
                Surface newSurface = holder.getSurface();
                Log.d(LocalPlayActivity.TAG, "mSurface != newSurface: " + (LocalPlayActivity.this.mSurface != newSurface));
                if (LocalPlayActivity.this.mSurface != newSurface) {
                    Log.d(LocalPlayActivity.TAG, "attachSurface");
                    LocalPlayActivity.this.mSurface = newSurface;
                    LocalPlayActivity.this.mLibVLC.attachSurface(LocalPlayActivity.this.mSurface, LocalPlayActivity.this);
                }
            }
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (LocalPlayActivity.this.mLibVLC != null) {
                LocalPlayActivity.this.mSurface = null;
                Log.d(LocalPlayActivity.TAG, "LibVLC.detachSurface");
                LocalPlayActivity.this.mLibVLC.detachSurface();
            }
        }
    };
    private final SurfaceHolder.Callback mSubtitlesSurfaceCallback = new SurfaceHolder.Callback() { // from class: mktvsmart.screen.vlc.LocalPlayActivity.2
        @Override // android.view.SurfaceHolder.Callback
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Surface newSurface;
            if (LocalPlayActivity.this.mLibVLC != null && LocalPlayActivity.this.mSubtitleSurface != (newSurface = holder.getSurface())) {
                LocalPlayActivity.this.mSubtitleSurface = newSurface;
                LocalPlayActivity.this.mLibVLC.attachSubtitlesSurface(LocalPlayActivity.this.mSubtitleSurface);
            }
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (LocalPlayActivity.this.mLibVLC != null) {
                LocalPlayActivity.this.mSubtitleSurface = null;
                LocalPlayActivity.this.mLibVLC.detachSubtitlesSurface();
            }
        }
    };
    private final Handler mHandler = new UIHandler(this);
    GestureDetector.OnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() { // from class: mktvsmart.screen.vlc.LocalPlayActivity.3
        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onDown(MotionEvent e) {
            LocalPlayActivity.this.mCurrentVolumn = LocalPlayActivity.this.mAudioManager.getStreamVolume(3);
            return false;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d(LocalPlayActivity.TAG, "onScroll");
            float y_changed = e2.getRawY() - e1.getRawY();
            DisplayMetrics screen = new DisplayMetrics();
            LocalPlayActivity.this.getWindowManager().getDefaultDisplay().getMetrics(screen);
            if (LocalPlayActivity.this.mSurfaceYDisplayRange == 0) {
                LocalPlayActivity.this.mSurfaceYDisplayRange = Math.min(screen.widthPixels, screen.heightPixels);
            }
            if (e1.getRawX() > (screen.widthPixels * 3) / 4) {
                LocalPlayActivity.this.onVolumeSlide(y_changed);
                return true;
            }
            if (e1.getRawX() < screen.widthPixels / 4) {
                LocalPlayActivity.this.onBrightnessSlide(y_changed);
                return true;
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    };

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean bVLCInit = VLCInstance.checkVlcLibs(this);
        Log.d(TAG, "bVLCInit = " + bVLCInit);
        if (bVLCInit) {
            if (!init()) {
                finish();
            } else {
                this.bOnCreateFinish = true;
                this.mHandler.sendEmptyMessage(5);
            }
        }
    }

    private boolean init() {
        this.mLibVLC = VLCInstance.getLibVlcInstance();
        this.mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        initView();
        this.mVlcEventHandler = new BaseVLCPlayActivity.VLCEventHandler(this);
        EventHandler.getInstance().addHandler(this.mVlcEventHandler);
        if (!initData()) {
            return false;
        }
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        sendBroadcast(i);
        setVolumeControlStream(3);
        this.mAudioManager = (AudioManager) getSystemService(MultiSettingActivity.AUDIO_STATUS_KEY);
        this.mReceiver = new BroadcastReceiver() { // from class: mktvsmart.screen.vlc.LocalPlayActivity.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                LocalPlayActivity.this.handleBroadcast(intent);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.BATTERY_CHANGED");
        registerReceiver(this.mReceiver, filter);
        return true;
    }

    public boolean initData() throws IOException, IllegalArgumentException {
        this.address = null;
        Intent intent = getIntent();
        Uri uri = intent.getData();
        String scheme = null;
        if (uri != null) {
            scheme = uri.getScheme();
        }
        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.VIEW")) {
            if (scheme != null && scheme.equals(ReportPage.REPORT_CONTENT)) {
                if (uri.getHost().equals("media") || uri.getHost().equals("mms")) {
                    try {
                        Cursor cursor = getContentResolver().query(getIntent().getData(), new String[]{"_data"}, null, null, null);
                        if (cursor != null) {
                            int column_index = cursor.getColumnIndexOrThrow("_data");
                            if (cursor.moveToFirst()) {
                                this.address = LibVLC.PathToURI(cursor.getString(column_index));
                            }
                            cursor.close();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Couldn't read the file from media or MMS");
                    }
                } else if (uri.getHost().equals("com.fsck.k9.attachmentprovider") || uri.getHost().equals("gmail-ls")) {
                    try {
                        Cursor cursor2 = getContentResolver().query(getIntent().getData(), new String[]{"_display_name"}, null, null, null);
                        if (cursor2 != null) {
                            cursor2.moveToFirst();
                            String filename = cursor2.getString(cursor2.getColumnIndex("_display_name"));
                            cursor2.close();
                            Log.i(TAG, "Getting file " + filename + " from content:// URI");
                            InputStream is = getContentResolver().openInputStream(getIntent().getData());
                            OutputStream os = new FileOutputStream(String.valueOf(Environment.getExternalStorageDirectory().getPath()) + "/Download/" + filename);
                            byte[] buffer = new byte[1024];
                            while (true) {
                                int bytesRead = is.read(buffer);
                                if (bytesRead < 0) {
                                    break;
                                }
                                os.write(buffer, 0, bytesRead);
                            }
                            os.close();
                            is.close();
                            this.address = LibVLC.PathToURI(String.valueOf(Environment.getExternalStorageDirectory().getPath()) + "/Download/" + filename);
                        }
                    } catch (Exception e2) {
                        Log.e(TAG, "Couldn't download file from mail URI");
                    }
                } else {
                    this.address = getIntent().getData().getPath();
                }
            } else if (getIntent().getDataString() != null) {
                this.address = getIntent().getDataString();
                if (this.address.startsWith("vlc://")) {
                    this.address = this.address.substring(6);
                }
                if (!this.address.contains(ServiceReference.DELIMITER)) {
                    try {
                        this.address = URLDecoder.decode(this.address, "UTF-8");
                    } catch (UnsupportedEncodingException e3) {
                        Log.w(TAG, "UnsupportedEncodingException while decoding MRL " + this.address);
                    }
                }
            } else {
                Log.e(TAG, "Couldn't understand the intent");
            }
        } else {
            this.address = getIntent().getDataString();
        }
        if (this.address == null) {
            return false;
        }
        return true;
    }

    private void initView() {
        this.mMainView = LayoutInflater.from(this).inflate(R.layout.local_play_activity, (ViewGroup) null);
        setContentView(this.mMainView);
        this.mMainView.setOnClickListener(this);
        this.mGestureDetector = new GestureDetector(this.mOnGestureListener);
        this.mMainView.setOnTouchListener(new View.OnTouchListener() { // from class: mktvsmart.screen.vlc.LocalPlayActivity.5
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                return LocalPlayActivity.this.mGestureDetector.onTouchEvent(event);
            }
        });
        this.mSurfaceView = (SurfaceView) findViewById(R.id.player_surface);
        this.mSurfaceHolder = this.mSurfaceView.getHolder();
        this.mSubtitlesSurfaceView = (SurfaceView) findViewById(R.id.subtitles_surface);
        this.mSubtitlesSurfaceHolder = this.mSubtitlesSurfaceView.getHolder();
        this.mSurfaceFrame = (FrameLayout) findViewById(R.id.player_surface_frame);
        this.mSubtitlesSurfaceView.setZOrderMediaOverlay(true);
        this.mSubtitlesSurfaceHolder.setFormat(-3);
        String chroma = this.mSettings.getString("chroma_format", "");
        if (LibVlcUtil.isGingerbreadOrLater() && chroma.equals("YV12")) {
            this.mSurfaceHolder.setFormat(842094169);
        } else if (chroma.equals("RV16")) {
            this.mSurfaceHolder.setFormat(4);
        } else {
            this.mSurfaceHolder.setFormat(2);
        }
        this.mSurfaceHolder.addCallback(this.mSurfaceCallback);
        this.mSubtitlesSurfaceHolder.addCallback(this.mSubtitlesSurfaceCallback);
        this.mWaittingView = findViewById(R.id.waitting_view);
        this.mWaittingText = (TextView) findViewById(R.id.waitting_text);
        this.mInfo = (TextView) findViewById(R.id.player_overlay_info);
        this.mScreenOrientation = Integer.valueOf(this.mSettings.getString("screen_orientation_value", ContentTree.ALL_VIDEO_ID)).intValue();
        setRequestedOrientation(this.mScreenOrientation != 100 ? this.mScreenOrientation : ScreenUtil.getScreenOrientation(this));
        this.mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
        this.mOperationBg = (ImageView) findViewById(R.id.operation_bg);
        this.mOperationPercent = (ImageView) findViewById(R.id.operation_percent);
        this.mController = (PlayController) findViewById(R.id.play_controller);
        this.mController.setOwner(this);
        this.mController.setLibVlc(this.mLibVLC);
        this.mAdView = (AdView) findViewById(R.id.wide_adView);
        this.mAdView.loadAd(new AdRequest.Builder().build());
        if (LibVlcUtil.isHoneycombOrLater()) {
            View.OnLayoutChangeListener mOnLayoutChangeListener = new View.OnLayoutChangeListener() { // from class: mktvsmart.screen.vlc.LocalPlayActivity.6
                @Override // android.view.View.OnLayoutChangeListener
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                        LocalPlayActivity.this.setSurfaceLayout(LocalPlayActivity.this.mController.mVideoWidth, LocalPlayActivity.this.mController.mVideoHeight, LocalPlayActivity.this.mController.mVideoVisibleWidth, LocalPlayActivity.this.mController.mVideoVisibleHeight, LocalPlayActivity.this.mController.mSarNum, LocalPlayActivity.this.mController.mSarDen);
                    }
                }
            };
            this.mSurfaceFrame.addOnLayoutChangeListener(mOnLayoutChangeListener);
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onStart() {
        super.onStart();
        if (AdsControllor.obtain().getmStatus() == AdsControllor.AdStatus.LEFT_APP) {
            AdsControllor.obtain().hideInterstitialAd();
        }
        Log.d(TAG, "onStart");
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        if (AdsControllor.obtain().getmStatus() == AdsControllor.AdStatus.CLOSE && this.isPauseByAd) {
            play();
            this.isPauseByAd = false;
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");
        Message msg = this.mHandler.obtainMessage(2);
        this.mHandler.sendMessage(msg);
        super.onConfigurationChanged(newConfig);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        if (this.mLibVLC.isPlaying()) {
            if (AdsControllor.obtain().isOpen()) {
                pause();
                this.isPauseByAd = true;
            } else {
                this.mLibVLC.stop();
                this.mSurfaceView.setKeepScreenOn(false);
            }
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onDestroy() throws NoSuchMethodException, ClassNotFoundException, SecurityException {
        super.onDestroy();
        if (this.bOnCreateFinish) {
            if (this.mReceiver != null) {
                unregisterReceiver(this.mReceiver);
            }
            EventHandler.getInstance().removeHandler(this.mVlcEventHandler);
            this.mLibVLC.eventVideoPlayerActivityCreated(false);
            if (this.mDisabledHardwareAcceleration) {
                this.mLibVLC.setHardwareAcceleration(this.mPreviousHardwareAccelerationMode);
            }
        }
    }

    public void play() {
        this.mLibVLC.play();
        this.mSurfaceView.setKeepScreenOn(true);
    }

    public void pause() {
        this.mLibVLC.pause();
        this.mSurfaceView.setKeepScreenOn(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBroadcast(Intent intent) {
        String action = intent.getAction();
        if (action.equalsIgnoreCase("android.intent.action.BATTERY_CHANGED")) {
            int batteryLevel = intent.getIntExtra("level", 0);
            this.mController.setBatteryLevel(batteryLevel);
        }
    }

    @Override // mktvsmart.screen.vlc.BaseVLCPlayActivity
    public void onMediaPlayerPlaying(Bundle data) {
        stopLoadingAnimation();
        showOverlay();
        this.mController.updateOverlayPausePlay();
        if (AdsControllor.obtain().isOpen()) {
            pause();
            this.isPauseByAd = true;
        }
        if (this.mAdView.isShown()) {
            this.mAdView.setVisibility(8);
        }
    }

    @Override // mktvsmart.screen.vlc.BaseVLCPlayActivity
    public void onMediaPlayerPaused(Bundle data) {
        showOverlay();
        this.mController.updateOverlayPausePlay();
        this.mHandler.removeMessages(1);
        if (!this.mAdView.isShown() && !this.isPauseByAd) {
            this.mAdView.setVisibility(0);
        }
    }

    @Override // mktvsmart.screen.vlc.BaseVLCPlayActivity
    public void onMediaPlayerStopped(Bundle data) {
        this.mController.updateOverlayPausePlay();
    }

    @Override // mktvsmart.screen.vlc.BaseVLCPlayActivity
    public void onMediaPlayerPositionChanged(Bundle data) {
        this.mController.setCanSeek(true);
    }

    @Override // mktvsmart.screen.vlc.BaseVLCPlayActivity
    public void onMediaPlayerEndReached(Bundle data) {
        finish();
    }

    @Override // mktvsmart.screen.vlc.BaseVLCPlayActivity
    public void onMediaPlayerVout(Bundle data) {
        if (data == null || data.getInt("data") == 0) {
            finish();
        }
    }

    @Override // mktvsmart.screen.vlc.BaseVLCPlayActivity
    public void onMediaPlayerEncounteredError(Bundle data) {
        AlertDialog dialog = new AlertDialog.Builder(this).setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: mktvsmart.screen.vlc.LocalPlayActivity.7
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog2, int id) {
                LocalPlayActivity.this.finish();
            }
        }).setTitle("Playback error").setMessage("Encountered an error with this media.\nPlease try refreshing the media library.").create();
        dialog.show();
        stopLoadingAnimation();
    }

    @Override // mktvsmart.screen.vlc.BaseVLCPlayActivity
    public void onHardwareAccelerationError(Bundle data) {
        this.mLibVLC.stop();
        AlertDialog dialog = new AlertDialog.Builder(this).setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: mktvsmart.screen.vlc.LocalPlayActivity.8
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog2, int id) throws NoSuchMethodException, ClassNotFoundException, SecurityException {
                LocalPlayActivity.this.mDisabledHardwareAcceleration = true;
                LocalPlayActivity.this.mPreviousHardwareAccelerationMode = LocalPlayActivity.this.mLibVLC.getHardwareAcceleration();
                LocalPlayActivity.this.mLibVLC.setHardwareAcceleration(0);
            }
        }).setNegativeButton(R.string.cancel_str, new DialogInterface.OnClickListener() { // from class: mktvsmart.screen.vlc.LocalPlayActivity.9
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog2, int id) {
                LocalPlayActivity.this.finish();
            }
        }).setTitle("HW acceleration error").setMessage("An error occurred with hardware acceleration. Would you like to disable it and try again?").create();
        if (!isFinishing()) {
            dialog.show();
        }
    }

    @Override // org.videolan.libvlc.IVideoPlayer
    public void eventHardwareAccelerationError() {
        EventHandler em = EventHandler.getInstance();
        em.callback(EventHandler.HardwareAccelerationError, new Bundle());
    }

    private static class UIHandler extends WeakHandler<LocalPlayActivity> {
        public UIHandler(LocalPlayActivity owner) {
            super(owner);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) throws Throwable {
            LocalPlayActivity activity = getOwner();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        activity.hideOverlay();
                        break;
                    case 2:
                        activity.mController.changeSurfaceSize();
                        break;
                    case 3:
                        activity.fadeOutInfo();
                        break;
                    case 4:
                        activity.mVolumeBrightnessLayout.setVisibility(8);
                        break;
                    case 5:
                        activity.startLoadingAnimation(activity.getString(R.string.please_wait));
                        activity.hideOverlay();
                        activity.startPlay();
                        break;
                    case 6:
                        int pos = activity.mController.setOverlayProgress();
                        if (activity.canShowProgress()) {
                            Message msg2 = obtainMessage(6);
                            sendMessageDelayed(msg2, 1000 - (pos % 1000));
                            break;
                        }
                        break;
                }
            }
        }
    }

    public void hideOverlay() throws Resources.NotFoundException {
        this.mHandler.removeMessages(6);
        this.mHandler.removeMessages(1);
        this.mController.hide();
    }

    public void fadeOutInfo() {
        if (this.mInfo.getVisibility() == 0) {
            this.mInfo.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        }
        this.mInfo.setVisibility(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean canShowProgress() {
        return !this.mController.isDragging() && this.mController.isShowing() && this.mLibVLC.isPlaying();
    }

    @Override // org.videolan.libvlc.IVideoPlayer
    public void setSurfaceLayout(int width, int height, int visible_width, int visible_height, int sar_num, int sar_den) {
        Log.d(TAG, "setSurfaceSize width * height = " + width + "* " + height);
        if (width * height != 0) {
            this.mController.setSurfaceLayout(width, height, visible_width, visible_height, sar_num, sar_den);
            Message msg = this.mHandler.obtainMessage(2);
            this.mHandler.sendMessage(msg);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) throws Resources.NotFoundException {
        if (v.equals(this.mMainView)) {
            Log.d(TAG, "mViewControl.isShowing() = " + this.mController.isShowing());
            if (this.mController.isShowing()) {
                hideOverlay();
            } else {
                showOverlay();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startPlay() throws Throwable {
        int dotIndex;
        if (this.address != null && this.address.length() > 0) {
            if (LibVlcUtil.isKitKatOrLater()) {
                String locationLC = this.address.toLowerCase(Locale.ENGLISH);
                if (locationLC.endsWith(".ts") || locationLC.endsWith(".tts") || locationLC.endsWith(".m2t") || locationLC.endsWith(".mts") || locationLC.endsWith(".m2ts")) {
                    this.mDisabledHardwareAcceleration = true;
                    this.mPreviousHardwareAccelerationMode = this.mLibVLC.getHardwareAcceleration();
                    this.mLibVLC.setHardwareAcceleration(0);
                }
            }
            Log.d(TAG, "address = " + this.address);
            this.mLibVLC.playMRL(this.address);
            MediaWrapper media = MediaDatabase.getInstance().getMedia(this.address);
            if (media != null) {
                if (media.getTime() > 0) {
                    this.mLibVLC.setTime(media.getTime());
                }
                getIntent().putExtra("fromStart", false);
            } else {
                long rTime = this.mSettings.getLong("VideoResumeTime", -1L);
                SharedPreferences.Editor editor = this.mSettings.edit();
                editor.putLong("VideoResumeTime", -1L);
                editor.commit();
                if (rTime > 0) {
                    this.mLibVLC.setTime(rTime);
                }
            }
            String title = this.address;
            try {
                title = URLDecoder.decode(this.address, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            } catch (IllegalArgumentException e2) {
            }
            if (title.startsWith("file:") && (dotIndex = (title = new File(title).getName()).lastIndexOf(46)) != -1) {
                title = title.substring(0, dotIndex);
            }
            this.mController.setTitle(title);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startLoadingAnimation(String text) {
        if (this.mWaittingView.getVisibility() != 0) {
            this.mWaittingView.setVisibility(0);
        }
        this.mWaittingText.setText(text);
    }

    private void stopLoadingAnimation() {
        this.mWaittingView.setVisibility(8);
    }

    public void showOverlay() {
        showOverlay(4000);
    }

    public void showOverlay(int timeout) {
        this.mHandler.sendEmptyMessage(6);
        this.mController.show();
        if (timeout != 0) {
            this.mHandler.removeMessages(1);
            Message msg = this.mHandler.obtainMessage(1);
            this.mHandler.sendMessageDelayed(msg, timeout);
        }
    }

    public void onVolumeSlide(float distance) {
        int mMaxVolume = this.mAudioManager.getStreamMaxVolume(3);
        int delta = -((int) ((distance / this.mSurfaceYDisplayRange) * mMaxVolume));
        int vol = (int) Math.min(Math.max(this.mCurrentVolumn + delta, 0.0f), mMaxVolume);
        if (this.mVolumeBrightnessLayout.getVisibility() != 0) {
            this.mVolumeBrightnessLayout.setVisibility(0);
        }
        this.mOperationBg.setImageResource(R.drawable.video_volumn_bg);
        if (delta != 0) {
            this.mAudioManager.setStreamVolume(3, vol, 0);
        }
        ViewGroup.LayoutParams lp = this.mOperationPercent.getLayoutParams();
        lp.width = (findViewById(R.id.operation_full).getLayoutParams().width * vol) / mMaxVolume;
        this.mOperationPercent.setLayoutParams(lp);
        this.mHandler.removeMessages(4);
        this.mHandler.sendEmptyMessageDelayed(4, 1000L);
    }

    private void initBrightnessTouch() {
        float brightnesstemp = 0.01f;
        try {
            brightnesstemp = Settings.System.getInt(getContentResolver(), "screen_brightness") / 255.0f;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = brightnesstemp;
        getWindow().setAttributes(lp);
        this.mIsFirstBrightnessGesture = false;
    }

    public void onBrightnessSlide(float distance) {
        if (this.mIsFirstBrightnessGesture) {
            initBrightnessTouch();
        }
        float delta = ((-distance) / this.mSurfaceYDisplayRange) * 0.07f;
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = Math.min(Math.max(lp.screenBrightness + delta, 0.01f), 1.0f);
        getWindow().setAttributes(lp);
        if (this.mVolumeBrightnessLayout.getVisibility() != 0) {
            this.mVolumeBrightnessLayout.setVisibility(0);
        }
        this.mOperationBg.setImageResource(R.drawable.video_brightness_bg);
        ViewGroup.LayoutParams lp1 = this.mOperationPercent.getLayoutParams();
        lp1.width = (int) (findViewById(R.id.operation_full).getLayoutParams().width * lp.screenBrightness);
        this.mOperationPercent.setLayoutParams(lp1);
        this.mHandler.removeMessages(4);
        this.mHandler.sendEmptyMessageDelayed(4, 1000L);
    }

    public int getScreenOrientation() {
        return this.mScreenOrientation;
    }

    public SurfaceView getSurfaceView() {
        return this.mSurfaceView;
    }

    public SurfaceHolder getSurfaceHolder() {
        return this.mSurfaceHolder;
    }

    public FrameLayout getSurfaceFrame() {
        return this.mSurfaceFrame;
    }

    public void showInfo(String text, int duration) {
        this.mInfo.setVisibility(0);
        this.mInfo.setText(text);
        this.mHandler.removeMessages(3);
        this.mHandler.sendEmptyMessageDelayed(3, duration);
    }

    public void showInfo(int textid, int duration) {
        this.mInfo.setVisibility(0);
        this.mInfo.setText(textid);
        this.mHandler.removeMessages(3);
        this.mHandler.sendEmptyMessageDelayed(3, duration);
    }

    public void showInfo(String text) {
        this.mInfo.setVisibility(0);
        this.mInfo.setText(text);
        this.mHandler.removeMessages(3);
    }

    public void hideInfo(int delay) {
        this.mHandler.sendEmptyMessageDelayed(3, delay);
    }

    public void hideInfo() {
        hideInfo(0);
    }

    @Override // org.videolan.libvlc.IVideoPlayer
    public int configureSurface(Surface surface, final int width, final int height, final int hal) {
        Log.d(TAG, "configureSurface:" + surface + "  " + width + "x" + height);
        Log.d(TAG, "LibVlcUtil.isICSOrLater():" + LibVlcUtil.isICSOrLater());
        if (LibVlcUtil.isICSOrLater() || surface == null) {
            return -1;
        }
        if (width * height == 0) {
            return 0;
        }
        final ConfigureSurfaceHolder holder = new ConfigureSurfaceHolder(surface);
        this.mHandler.post(new Runnable() { // from class: mktvsmart.screen.vlc.LocalPlayActivity.10
            @Override // java.lang.Runnable
            public void run() {
                Log.d(LocalPlayActivity.TAG, "setFixedSize:" + width + "x" + height + " hal = " + hal);
                if (LocalPlayActivity.this.mSurface != holder.surface || LocalPlayActivity.this.mSurfaceHolder == null) {
                    if (LocalPlayActivity.this.mSubtitleSurface == holder.surface && LocalPlayActivity.this.mSubtitlesSurfaceHolder != null) {
                        if (hal != 0) {
                            LocalPlayActivity.this.mSubtitlesSurfaceHolder.setFormat(hal);
                        }
                        LocalPlayActivity.this.mSubtitlesSurfaceHolder.setFixedSize(width, height);
                    }
                } else {
                    if (hal != 0) {
                        LocalPlayActivity.this.mSurfaceHolder.setFormat(hal);
                    }
                    LocalPlayActivity.this.mSurfaceHolder.setFixedSize(width, height);
                }
                synchronized (holder) {
                    holder.configured = true;
                    holder.notifyAll();
                }
            }
        });
        try {
            synchronized (holder) {
                if (!holder.configured) {
                    Log.d(TAG, "holder.wait()");
                    holder.wait();
                }
            }
            return 1;
        } catch (InterruptedException e) {
            return 0;
        }
    }
}
