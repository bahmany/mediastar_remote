package mktvsmart.screen.vlc;

import android.R;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.Toast;
import com.alitech.dvbtoip.DVBtoIP;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.ContentTree;
import com.hisilicon.multiscreen.mybox.MultiSettingActivity;
import com.voicetechnology.rtspclient.test.Sat2IP_Rtsp;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mktvsmart.screen.CreateSocket;
import mktvsmart.screen.GMScreenGlobalInfo;
import mktvsmart.screen.GlobalConstantValue;
import mktvsmart.screen.GsSendSocket;
import mktvsmart.screen.channel.ChannelData;
import mktvsmart.screen.channel.Sat2ipUtil;
import mktvsmart.screen.dataconvert.model.DataConvertChannelModel;
import mktvsmart.screen.dataconvert.model.DataConvertOneDataModel;
import mktvsmart.screen.dataconvert.model.DataConvertPvrInfoModel;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.exception.ProgramNotFoundException;
import mktvsmart.screen.gchat.DanmakuManager;
import mktvsmart.screen.gchat.ui.GChatByMobileFragment;
import mktvsmart.screen.message.process.MessageProcessor;
import mktvsmart.screen.pvr2small.Pvr2smallData;
import mktvsmart.screen.util.AdsControllor;
import mktvsmart.screen.util.BitmapUtils;
import mktvsmart.screen.vlc.BaseVLCPlayActivity;
import mktvsmart.screen.vlc.PlayViewControllor;
import mktvsmart.screen.vlc.ProgramListDrawer;
import mktvsmart.screen.widget.PasswordDialog;
import org.teleal.cling.model.ServiceReference;
import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcUtil;
import org.videolan.vlc.util.VLCInstance;
import org.videolan.vlc.util.WeakHandler;

/* loaded from: classes.dex */
public class LivePlayActivity extends BaseVLCPlayActivity implements IVideoPlayer, View.OnClickListener, PlayViewControllor.TranscodeListenner, Sat2IP_Rtsp.EndOfFileListener, DanmakuManager {
    public static final int ERROR_PLAY_TIMEOUT = -5;
    public static final int ERROR_STB_INX_TMS_MODE = -2;
    public static final int ERROR_STB_IN_REC_MODE = -1;
    public static final int ERROR_STB_LIVE_STOPED = -3;
    public static final int ERROR_UNKNOWN = 0;
    public static final int ERROR_VIDEO_CANNOT_PLAY = -4;
    private static final int FADE_OUT = 1;
    private static final int FADE_OUT_INFO = 3;
    private static final int HIDE_VOLUME_BRIGHTNESS_BAR = 4;
    public static final int PLAY_SUCCESS = 1;
    private static final int SHOW_INTERSTITIALAD = 6;
    private static final int SHOW_RECORD_TIP = 5;
    private static final int SUB_MSG_INIT_SAT2IP_PIPE = 11;
    private static final int SUB_MSG_SAVE_TRANSCODE_SETTING = 12;
    private static final int SURFACE_SIZE = 2;
    private AdView mAdView;
    private ProgramListDrawer.ProgramListAdapter mAdapter;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusListener;
    private AudioManager mAudioManager;
    public GChatByMobileFragment mChatFragment;
    public FrameLayout mChatView;
    private GChatControllor mChatViewControllor;
    private DataConvertChannelModel mCurChannel;
    private GestureDetector mGestureDetector;
    private final Handler mHandler;
    private TextView mInfo;
    private LibVLC mLibVLC;
    private ProgramListDrawer mLiveList;
    private View mMainView;
    private ImageView mOperationBg;
    private ImageView mOperationPercent;
    private List<?> mPlayList;
    private int mPlayProgIndex;
    private int mPreviousHardwareAccelerationMode;
    private BroadcastReceiver mReceiver;
    private int mScreenOrientation;
    private SharedPreferences mSettings;
    private Handler mSubHandler;
    private final Handler.Callback mSubMsgCallback;
    private HandlerThread mSubThread;
    private SurfaceHolder mSubtitlesSurfaceHolder;
    private SurfaceView mSubtitlesSurfaceView;
    private FrameLayout mSurfaceFrame;
    private SurfaceHolder mSurfaceHolder;
    private SurfaceView mSurfaceView;
    private PlayViewControllor mViewControl;
    private View mVolumeBrightnessLayout;
    private View mWaittingView;
    private AlertDialog mWarningDialog;
    private TextView mWattingText;
    private MessageProcessor msgProc;
    private DataParser parser;
    private PasswordDialog pswInputDialog;
    private Socket tcpSocket;
    private String url;
    private static final String TAG = LivePlayActivity.class.getSimpleName();
    private static Sat2IP_Rtsp sRtsp = null;
    private static final String REC_PATH = String.valueOf(BitmapUtils.getSDPath()) + "/G-MScreen/video/";
    private static final String SNAPSHOT_PATH = String.valueOf(BitmapUtils.getSDPath()) + "/G-MScreen/capture/";
    private static boolean bEnableFullAds = false;
    private static int sAcessPlayCount = 0;
    private Surface mSurface = null;
    private Surface mSubtitleSurface = null;
    private String serverAddr = "";
    private boolean bOnErrorHappen = false;
    private int mSelectChannelPosition = 0;
    private boolean mDisabledHardwareAcceleration = false;
    private float mCurrentVolumn = -1.0f;
    private int mSurfaceYDisplayRange = 0;
    private boolean mIsFirstBrightnessGesture = true;
    private boolean bOnCreateFinish = false;
    private boolean isPauseByAd = false;
    private final Handler eventHandler = new BaseVLCPlayActivity.VLCEventHandler(this);
    private boolean bPlayEnd = false;
    private boolean mLostFocus = false;
    private boolean mHasAudioFocus = false;
    private int mLastAudioTrack = -1;
    private int mLastSpuTrack = -2;
    MessageProcessor.PerformOnForeground onServerReturn = new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.vlc.LivePlayActivity.1
        AnonymousClass1() {
        }

        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
        public void doInForeground(Message msg) throws SocketException, UnsupportedEncodingException {
            if (LivePlayActivity.this.bOnErrorHappen) {
                if (LivePlayActivity.this.mWarningDialog != null && LivePlayActivity.this.mWarningDialog.isShowing()) {
                    LivePlayActivity.this.mWarningDialog.dismiss();
                }
                LivePlayActivity.this.bOnErrorHappen = false;
            }
            switch (msg.what) {
                case 3:
                    Log.d(LivePlayActivity.TAG, "GMS_MSG_REQUEST_PLAYING_CHANNEL");
                    LivePlayActivity.this.handleSTBChannelPlayChange(msg);
                    break;
                case GlobalConstantValue.GMS_MSG_DO_SAT2IP_CHANNEL_PLAY /* 1009 */:
                    Log.d(LivePlayActivity.TAG, "GMS_MSG_DO_SAT2IP_CHANNEL_PLAY");
                    LivePlayActivity.this.handleSat2TpReturn(msg);
                    break;
                case GlobalConstantValue.GMS_MSG_DO_PASSWORD_CHECK /* 1056 */:
                    LivePlayActivity.this.handlePasswordCheckBack(msg);
                    break;
                case 2016:
                case 2017:
                case 2018:
                    LivePlayActivity.this.handleFactoryReset(msg);
                    break;
                case GlobalConstantValue.GSCMD_NOTIFY_SOCKET_CLOSED /* 4112 */:
                    Log.d(LivePlayActivity.TAG, "GSCMD_NOTIFY_SOCKET_CLOSED");
                    LivePlayActivity.this.finish();
                    break;
            }
        }
    };
    GestureDetector.OnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() { // from class: mktvsmart.screen.vlc.LivePlayActivity.2
        AnonymousClass2() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onDown(MotionEvent e) {
            LivePlayActivity.this.mCurrentVolumn = LivePlayActivity.this.mAudioManager.getStreamVolume(3);
            return false;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d(LivePlayActivity.TAG, "onScroll");
            float y_changed = e2.getRawY() - e1.getRawY();
            float x_changed = e2.getRawX() - e1.getRawX();
            DisplayMetrics screen = new DisplayMetrics();
            LivePlayActivity.this.getWindowManager().getDefaultDisplay().getMetrics(screen);
            if (LivePlayActivity.this.mSurfaceYDisplayRange == 0) {
                LivePlayActivity.this.mSurfaceYDisplayRange = Math.min(screen.widthPixels, screen.heightPixels);
            }
            if (e1.getRawX() > (screen.widthPixels * 3) / 4) {
                LivePlayActivity.this.onVolumeSlide(y_changed);
                return true;
            }
            if (e1.getRawY() < screen.heightPixels / 3) {
                LivePlayActivity.this.onBrightnessSlide(-x_changed);
                return true;
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    };
    private final SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() { // from class: mktvsmart.screen.vlc.LivePlayActivity.3
        AnonymousClass3() {
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (LivePlayActivity.this.mLibVLC != null) {
                Surface newSurface = holder.getSurface();
                Log.d(LivePlayActivity.TAG, "mSurface != newSurface: " + (LivePlayActivity.this.mSurface != newSurface));
                if (LivePlayActivity.this.mSurface != newSurface) {
                    Log.d(LivePlayActivity.TAG, "attachSurface");
                    LivePlayActivity.this.mSurface = newSurface;
                    LivePlayActivity.this.mLibVLC.attachSurface(LivePlayActivity.this.mSurface, LivePlayActivity.this);
                }
            }
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (LivePlayActivity.this.mLibVLC != null) {
                LivePlayActivity.this.mSurface = null;
                Log.d(LivePlayActivity.TAG, "LibVLC.detachSurface");
                LivePlayActivity.this.mLibVLC.detachSurface();
            }
        }
    };
    private final SurfaceHolder.Callback mSubtitlesSurfaceCallback = new SurfaceHolder.Callback() { // from class: mktvsmart.screen.vlc.LivePlayActivity.4
        AnonymousClass4() {
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Surface newSurface;
            if (LivePlayActivity.this.mLibVLC != null && LivePlayActivity.this.mSubtitleSurface != (newSurface = holder.getSurface())) {
                LivePlayActivity.this.mSubtitleSurface = newSurface;
                LivePlayActivity.this.mLibVLC.attachSubtitlesSurface(LivePlayActivity.this.mSubtitleSurface);
            }
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (LivePlayActivity.this.mLibVLC != null) {
                LivePlayActivity.this.mSubtitleSurface = null;
                LivePlayActivity.this.mLibVLC.detachSubtitlesSurface();
            }
        }
    };
    private Runnable mPlayEOFOccurred = new Runnable() { // from class: mktvsmart.screen.vlc.LivePlayActivity.5
        AnonymousClass5() {
        }

        @Override // java.lang.Runnable
        public void run() {
            LivePlayActivity.this.mViewControl.updateOverlayPausePlay();
            if (LivePlayActivity.this.bPlayEnd) {
                AlertDialog dialog = new AlertDialog.Builder(LivePlayActivity.this).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // from class: mktvsmart.screen.vlc.LivePlayActivity.5.1
                    AnonymousClass1() {
                    }

                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog2, int id) {
                        LivePlayActivity.this.finish();
                    }
                }).setTitle(mktvsmart.screen.R.string.str_play_back_error).setMessage("The connection has been disconnected").create();
                dialog.show();
            }
        }

        /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$5$1 */
        class AnonymousClass1 implements DialogInterface.OnClickListener {
            AnonymousClass1() {
            }

            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog2, int id) {
                LivePlayActivity.this.finish();
            }
        }
    };

    public LivePlayActivity() {
        this.mAudioFocusListener = LibVlcUtil.isFroyoOrLater() ? new AudioManager.OnAudioFocusChangeListener() { // from class: mktvsmart.screen.vlc.LivePlayActivity.6
            AnonymousClass6() {
            }

            @Override // android.media.AudioManager.OnAudioFocusChangeListener
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case -3:
                    case -2:
                        break;
                    case -1:
                        LivePlayActivity.this.changeAudioFocus(false);
                        break;
                    case 0:
                    default:
                        return;
                    case 1:
                    case 2:
                    case 3:
                        if (!LivePlayActivity.this.mLibVLC.isPlaying() && LivePlayActivity.this.mLostFocus) {
                            LivePlayActivity.this.mLibVLC.play();
                            LivePlayActivity.this.mLostFocus = false;
                            return;
                        }
                        return;
                }
                if (LivePlayActivity.this.mLibVLC.isPlaying()) {
                    LivePlayActivity.this.mLostFocus = true;
                    LivePlayActivity.this.mLibVLC.pause();
                }
            }
        } : null;
        this.mHandler = new UIHandler(this);
        this.mSubMsgCallback = new Handler.Callback() { // from class: mktvsmart.screen.vlc.LivePlayActivity.7
            AnonymousClass7() {
            }

            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) throws SocketException, NumberFormatException, UnsupportedEncodingException {
                switch (msg.what) {
                    case 11:
                        LivePlayActivity.sRtsp = new Sat2IP_Rtsp();
                        String base = "";
                        String query = "";
                        if (GMScreenGlobalInfo.playType == 1) {
                            LivePlayActivity.sRtsp.set_eof_listener(LivePlayActivity.this);
                            base = Pvr2smallData.getInstance().getPlayUrlBase(LivePlayActivity.this.mSelectChannelPosition, LivePlayActivity.this.tcpSocket.getInetAddress().toString());
                            query = Pvr2smallData.getInstance().getPlayUrlQuery();
                        } else if (GMScreenGlobalInfo.playType == 2) {
                            base = Sat2ipUtil.getRtspUriBase(LivePlayActivity.this.tcpSocket.getInetAddress().toString());
                            query = Sat2ipUtil.getRtspUriQuery((DataConvertChannelModel) LivePlayActivity.this.mPlayList.get(((Integer) msg.obj).intValue()));
                        }
                        boolean isSetupOk = LivePlayActivity.sRtsp.setup_blocked(base, query);
                        Log.d(LivePlayActivity.TAG, "isSetupOk = " + isSetupOk);
                        if (!isSetupOk) {
                            LivePlayActivity.sRtsp = null;
                        } else {
                            if (GMScreenGlobalInfo.playType == 2) {
                                LivePlayActivity.this.sendSat2ipChannelIdToStb(((Integer) msg.obj).intValue());
                            }
                            DVBtoIP.initResourceForPlayer(LivePlayActivity.sRtsp.get_rtp_port(), LivePlayActivity.this.getRtspPipeFilePath(), GMScreenGlobalInfo.playType, GMScreenGlobalInfo.getKeyWay());
                            Uri uri = Uri.parse(LibVLC.PathToURI(LivePlayActivity.this.getRtspPipeFilePath()));
                            LivePlayActivity.this.url = uri.toString();
                            LivePlayActivity.this.mHandler.post(new Runnable() { // from class: mktvsmart.screen.vlc.LivePlayActivity.7.1
                                AnonymousClass1() {
                                }

                                @Override // java.lang.Runnable
                                public void run() {
                                    LivePlayActivity.this.playChannel();
                                }
                            });
                        }
                        return true;
                    case 12:
                        TranscodeConstants.saveTranscodeSetting(msg.arg1, msg.arg2);
                        return true;
                    default:
                        return true;
                }
            }

            /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$7$1 */
            class AnonymousClass1 implements Runnable {
                AnonymousClass1() {
                }

                @Override // java.lang.Runnable
                public void run() {
                    LivePlayActivity.this.playChannel();
                }
            }
        };
    }

    public int getScreenOrientation() {
        return this.mScreenOrientation;
    }

    public SurfaceView getSurfaceView() {
        return this.mSurfaceView;
    }

    public SurfaceView getSubtitleSurfaceView() {
        return this.mSubtitlesSurfaceView;
    }

    public SurfaceHolder getSurfaceHolder() {
        return this.mSurfaceHolder;
    }

    public FrameLayout getSurfaceFrame() {
        return this.mSurfaceFrame;
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean bVLCInit = VLCInstance.checkVlcLibs(this);
        Log.d(TAG, "bVLCInit = " + bVLCInit);
        if (bVLCInit) {
            if (!init()) {
                finish();
                return;
            }
            if (bEnableFullAds) {
                if (sAcessPlayCount / 3 == 0) {
                    this.mHandler.sendEmptyMessage(6);
                }
                if (AdsControllor.obtain().isAdLoaded()) {
                    sAcessPlayCount++;
                }
            }
            Log.d(TAG, "Hardware acceleration mode: " + Integer.toString(this.mLibVLC.getHardwareAcceleration()));
            this.bOnCreateFinish = true;
        }
    }

    private boolean init() {
        this.mLibVLC = VLCInstance.getLibVlcInstance();
        this.mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        initView();
        initPath();
        EventHandler em = EventHandler.getInstance();
        em.addHandler(this.eventHandler);
        if (!initData()) {
            return false;
        }
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        sendBroadcast(i);
        setVolumeControlStream(3);
        this.mAudioManager = (AudioManager) getSystemService(MultiSettingActivity.AUDIO_STATUS_KEY);
        this.mReceiver = new BroadcastReceiver() { // from class: mktvsmart.screen.vlc.LivePlayActivity.8
            AnonymousClass8() {
            }

            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                LivePlayActivity.this.handleBroadcast(intent);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.BATTERY_CHANGED");
        registerReceiver(this.mReceiver, filter);
        this.mLibVLC.eventVideoPlayerActivityCreated(true);
        return true;
    }

    /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$8 */
    class AnonymousClass8 extends BroadcastReceiver {
        AnonymousClass8() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            LivePlayActivity.this.handleBroadcast(intent);
        }
    }

    @SuppressLint({"NewApi"})
    private void initView() {
        this.mMainView = LayoutInflater.from(this).inflate(mktvsmart.screen.R.layout.activity_vlc_live_play, (ViewGroup) null);
        setContentView(this.mMainView);
        findViewById(mktvsmart.screen.R.id.play_view).setOnClickListener(this);
        this.mGestureDetector = new GestureDetector(this.mOnGestureListener);
        findViewById(mktvsmart.screen.R.id.play_view).setOnTouchListener(new View.OnTouchListener() { // from class: mktvsmart.screen.vlc.LivePlayActivity.9
            AnonymousClass9() {
            }

            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                return LivePlayActivity.this.mGestureDetector.onTouchEvent(event);
            }
        });
        this.mSurfaceView = (SurfaceView) findViewById(mktvsmart.screen.R.id.player_surface);
        this.mSurfaceHolder = this.mSurfaceView.getHolder();
        this.mSubtitlesSurfaceView = (SurfaceView) findViewById(mktvsmart.screen.R.id.subtitles_surface);
        this.mSubtitlesSurfaceHolder = this.mSubtitlesSurfaceView.getHolder();
        this.mSurfaceFrame = (FrameLayout) findViewById(mktvsmart.screen.R.id.player_surface_frame);
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
        if (this.mLibVLC.useCompatSurface()) {
            this.mSubtitlesSurfaceView.setVisibility(8);
        }
        this.mInfo = (TextView) findViewById(mktvsmart.screen.R.id.player_overlay_info);
        this.mVolumeBrightnessLayout = findViewById(mktvsmart.screen.R.id.operation_volume_brightness);
        this.mOperationBg = (ImageView) findViewById(mktvsmart.screen.R.id.operation_bg);
        this.mOperationPercent = (ImageView) findViewById(mktvsmart.screen.R.id.operation_percent);
        this.mWaittingView = findViewById(mktvsmart.screen.R.id.waitting_view);
        this.mWattingText = (TextView) findViewById(mktvsmart.screen.R.id.waitting_text);
        this.mLiveList = (ProgramListDrawer) getSupportFragmentManager().findFragmentById(mktvsmart.screen.R.id.navigation_drawer);
        this.mLiveList.setUp(mktvsmart.screen.R.id.navigation_drawer, (DrawerLayout) findViewById(mktvsmart.screen.R.id.drawer_layout));
        this.mAdapter = new ProgramListDrawer.ProgramListAdapter(this, this.mPlayList);
        this.mLiveList.setAdapter(this.mAdapter);
        this.mScreenOrientation = Integer.valueOf(this.mSettings.getString("screen_orientation_value", ContentTree.ALL_VIDEO_ID)).intValue();
        this.mChatViewControllor = new GChatControllor(this);
        this.mChatFragment = (GChatByMobileFragment) getSupportFragmentManager().findFragmentById(mktvsmart.screen.R.id.chat_fragment);
        this.mChatView = (FrameLayout) findViewById(mktvsmart.screen.R.id.chat_view);
        if (GMScreenGlobalInfo.isChatSupport()) {
            setRequestedOrientation(1);
        } else {
            this.mChatView.setVisibility(8);
            setRequestedOrientation(this.mScreenOrientation != 100 ? this.mScreenOrientation : ScreenUtil.getScreenOrientation(this));
        }
        this.mViewControl = new PlayViewControllor(this);
        this.mViewControl.setOnTranscodeListenner(this);
        this.mAdView = (AdView) findViewById(mktvsmart.screen.R.id.wide_adView);
        this.mAdView.loadAd(new AdRequest.Builder().build());
        this.mAdView.setVisibility(8);
        if (LibVlcUtil.isHoneycombOrLater()) {
            View.OnLayoutChangeListener mOnLayoutChangeListener = new View.OnLayoutChangeListener() { // from class: mktvsmart.screen.vlc.LivePlayActivity.10
                AnonymousClass10() {
                }

                @Override // android.view.View.OnLayoutChangeListener
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                        LivePlayActivity.this.setSurfaceLayout(LivePlayActivity.this.mViewControl.mVideoWidth, LivePlayActivity.this.mViewControl.mVideoHeight, LivePlayActivity.this.mViewControl.mVideoVisibleWidth, LivePlayActivity.this.mViewControl.mVideoVisibleHeight, LivePlayActivity.this.mViewControl.mSarNum, LivePlayActivity.this.mViewControl.mSarDen);
                    }
                }
            };
            this.mSurfaceFrame.addOnLayoutChangeListener(mOnLayoutChangeListener);
        }
    }

    /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$9 */
    class AnonymousClass9 implements View.OnTouchListener {
        AnonymousClass9() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            return LivePlayActivity.this.mGestureDetector.onTouchEvent(event);
        }
    }

    /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$10 */
    class AnonymousClass10 implements View.OnLayoutChangeListener {
        AnonymousClass10() {
        }

        @Override // android.view.View.OnLayoutChangeListener
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                LivePlayActivity.this.setSurfaceLayout(LivePlayActivity.this.mViewControl.mVideoWidth, LivePlayActivity.this.mViewControl.mVideoHeight, LivePlayActivity.this.mViewControl.mVideoVisibleWidth, LivePlayActivity.this.mViewControl.mVideoVisibleHeight, LivePlayActivity.this.mViewControl.mSarNum, LivePlayActivity.this.mViewControl.mSarDen);
            }
        }
    }

    private void initPath() {
        File file = new File(REC_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file1 = new File(SNAPSHOT_PATH);
        if (!file1.exists()) {
            file1.mkdirs();
        }
    }

    private boolean initData() throws SocketException, UnsupportedEncodingException {
        setMessageProcess();
        CreateSocket cSocket = new CreateSocket("", 0);
        try {
            this.tcpSocket = cSocket.GetSocket();
            this.serverAddr = this.tcpSocket.getInetAddress().getHostAddress();
            Log.d(TAG, "serverAddr = " + this.serverAddr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.parser = ParserFactory.getParser();
        Intent intent = getIntent();
        this.mPlayProgIndex = intent.getIntExtra("position", -1);
        if (this.mPlayProgIndex == -1) {
            return false;
        }
        if (GMScreenGlobalInfo.playType == 2) {
            initSameTpChannes(this.mPlayProgIndex);
        } else if (GMScreenGlobalInfo.playType == 1) {
            this.mPlayList = Pvr2smallData.getInstance().getPvr2smallList();
            if (this.mPlayList == null) {
                return false;
            }
            this.mSelectChannelPosition = this.mPlayProgIndex;
            this.mAdapter.setListData(this.mPlayList);
            this.mAdapter.notifyDataSetChanged();
            askPlayUrl(this.mSelectChannelPosition);
        }
        requestChannelInfoForChat();
        return true;
    }

    private void requestChannelInfoForChat() throws SocketException, UnsupportedEncodingException {
        List<DataConvertChannelModel> channelList = ChannelData.getInstance().getChannelListByTvRadioType();
        if (this.mPlayProgIndex >= 0 && this.mPlayProgIndex < channelList.size()) {
            DataConvertChannelModel channel = channelList.get(this.mPlayProgIndex);
            List<DataConvertChannelModel> models = new ArrayList<>();
            models.add(channel);
            DataParser xParser = ParserFactory.getParser();
            try {
                byte[] data_buff = xParser.serialize(models, 104).getBytes("UTF-8");
                this.tcpSocket.setSoTimeout(3000);
                GsSendSocket.sendSocketToStb(data_buff, this.tcpSocket, 0, data_buff.length, 104);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initSameTpChannes(int position) throws SocketException, UnsupportedEncodingException {
        this.bPlayEnd = false;
        this.mHandler.removeCallbacks(this.mPlayEOFOccurred);
        List<DataConvertChannelModel> channelList = ChannelData.getInstance().getChannelListByTvRadioType();
        List<DataConvertChannelModel> sameTpList = new ArrayList<>();
        String newSattpID = channelList.get(position).GetProgramId().substring(0, 9);
        for (int i = 0; i < channelList.size(); i++) {
            DataConvertChannelModel ch = channelList.get(i);
            if (ch.GetProgramId().startsWith(newSattpID)) {
                sameTpList.add(ch);
                if (ch.GetProgramId().equals(channelList.get(position).GetProgramId())) {
                    this.mSelectChannelPosition = sameTpList.size() - 1;
                }
            }
        }
        this.mPlayList = sameTpList;
        this.mAdapter.setListData(this.mPlayList);
        this.mAdapter.notifyDataSetChanged();
        if (checkChannelLock(this.mSelectChannelPosition)) {
            inputPermissionPassword(this.mSelectChannelPosition);
        } else {
            askPlayUrl(this.mSelectChannelPosition);
        }
    }

    public boolean checkChannelLock(int iPosition) {
        if (((DataConvertChannelModel) this.mPlayList.get(iPosition)).getLockMark() != 1) {
            return false;
        }
        return true;
    }

    public boolean isPlaying(int iPosition) {
        if (this.mSelectChannelPosition == iPosition) {
            return this.mLibVLC.isPlaying();
        }
        return false;
    }

    public void askPlayUrl(int iPosition) throws SocketException, UnsupportedEncodingException {
        if (this.mLibVLC.videoIsRecording()) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(5, new Runnable() { // from class: mktvsmart.screen.vlc.LivePlayActivity.11
                private final /* synthetic */ int val$iPosition;

                AnonymousClass11(int iPosition2) {
                    i = iPosition2;
                }

                @Override // java.lang.Runnable
                public void run() throws SocketException, UnsupportedEncodingException {
                    LivePlayActivity.this.requestPlayUrl(i);
                }
            }));
        } else {
            requestPlayUrl(iPosition2);
        }
    }

    /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$11 */
    class AnonymousClass11 implements Runnable {
        private final /* synthetic */ int val$iPosition;

        AnonymousClass11(int iPosition2) {
            i = iPosition2;
        }

        @Override // java.lang.Runnable
        public void run() throws SocketException, UnsupportedEncodingException {
            LivePlayActivity.this.requestPlayUrl(i);
        }
    }

    public void requestPlayUrl(int iPosition) throws SocketException, UnsupportedEncodingException {
        if (this.mLibVLC.isPlaying()) {
            this.mLibVLC.stop();
        }
        startLoadingAnimation(getString(mktvsmart.screen.R.string.please_wait));
        this.mSelectChannelPosition = iPosition;
        this.mLiveList.setSelectedItem(this.mSelectChannelPosition);
        switch (GMScreenGlobalInfo.getCurStbInfo().getPlatform_id()) {
            case 8:
            case 9:
            case 14:
                stopStream();
                if (this.mSubThread == null) {
                    this.mSubThread = new HandlerThread("live_play_work_thread");
                    this.mSubThread.start();
                }
                if (this.mSubHandler == null) {
                    this.mSubHandler = new Handler(this.mSubThread.getLooper(), this.mSubMsgCallback);
                }
                this.mSubHandler.sendMessage(this.mSubHandler.obtainMessage(11, Integer.valueOf(this.mSelectChannelPosition)));
                break;
            default:
                sendSat2ipChannelIdToStb(this.mSelectChannelPosition);
                break;
        }
    }

    public String getRtspPipeFilePath() {
        String path = getCacheDir().getAbsolutePath();
        return String.valueOf(path) + ServiceReference.DELIMITER + getString(mktvsmart.screen.R.string.app_name) + ".ts";
    }

    private void stopStream() throws UnsupportedEncodingException {
        if (sRtsp != null) {
            sRtsp.teardown();
            sRtsp = null;
            DVBtoIP.destroyResourceForPlayer();
            GsSendSocket.sendOnlyCommandSocketToStb(this.tcpSocket, GlobalConstantValue.GMS_MSG_DO_SAT2IP_PLAY_STOP);
        }
    }

    public boolean sendSat2ipChannelIdToStb(int position) throws SocketException, UnsupportedEncodingException {
        ArrayList<DataConvertChannelModel> sat2ipChannels = new ArrayList<>();
        try {
            this.mCurChannel = (DataConvertChannelModel) this.mPlayList.get(position);
            sat2ipChannels.add(this.mCurChannel);
            byte[] data_buff = this.parser.serialize(sat2ipChannels, GlobalConstantValue.GMS_MSG_DO_SAT2IP_CHANNEL_PLAY).getBytes("UTF-8");
            this.tcpSocket.setSoTimeout(3000);
            GsSendSocket.sendSocketToStb(data_buff, this.tcpSocket, 0, data_buff.length, GlobalConstantValue.GMS_MSG_DO_SAT2IP_CHANNEL_PLAY);
            return true;
        } catch (ProgramNotFoundException e1) {
            e1.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void inputPermissionPassword(int position) {
        this.mSelectChannelPosition = position;
        if (this.pswInputDialog != null && this.pswInputDialog.isShowing()) {
            this.pswInputDialog.dismiss();
        }
        this.pswInputDialog = new PasswordDialog(this);
        this.pswInputDialog.setOnTextChangeListener(new PasswordDialog.OnTextChangeListener() { // from class: mktvsmart.screen.vlc.LivePlayActivity.12
            AnonymousClass12() {
            }

            @Override // mktvsmart.screen.widget.PasswordDialog.OnTextChangeListener
            public void onTextChanged(CharSequence s, int start, int before, int count) throws SocketException, UnsupportedEncodingException {
                if (s != null && s.length() == GMScreenGlobalInfo.getmMaxPasswordNum()) {
                    List<DataConvertOneDataModel> lockModels = new ArrayList<>();
                    DataConvertOneDataModel model = new DataConvertOneDataModel();
                    model.setData(s.toString());
                    lockModels.add(model);
                    try {
                        LivePlayActivity.this.parser = ParserFactory.getParser();
                        byte[] data_buff = LivePlayActivity.this.parser.serialize(lockModels, GlobalConstantValue.GMS_MSG_DO_PASSWORD_CHECK).getBytes("UTF-8");
                        LivePlayActivity.this.tcpSocket.setSoTimeout(3000);
                        GsSendSocket.sendSocketToStb(data_buff, LivePlayActivity.this.tcpSocket, 0, data_buff.length, GlobalConstantValue.GMS_MSG_DO_PASSWORD_CHECK);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    LivePlayActivity.this.pswInputDialog.dismiss();
                }
            }
        });
        this.pswInputDialog.setTitle(((DataConvertChannelModel) this.mPlayList.get(this.mSelectChannelPosition)).getProgramName());
        this.pswInputDialog.setCanceledOnTouchOutside(false);
        this.pswInputDialog.show();
    }

    /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$12 */
    class AnonymousClass12 implements PasswordDialog.OnTextChangeListener {
        AnonymousClass12() {
        }

        @Override // mktvsmart.screen.widget.PasswordDialog.OnTextChangeListener
        public void onTextChanged(CharSequence s, int start, int before, int count) throws SocketException, UnsupportedEncodingException {
            if (s != null && s.length() == GMScreenGlobalInfo.getmMaxPasswordNum()) {
                List<DataConvertOneDataModel> lockModels = new ArrayList<>();
                DataConvertOneDataModel model = new DataConvertOneDataModel();
                model.setData(s.toString());
                lockModels.add(model);
                try {
                    LivePlayActivity.this.parser = ParserFactory.getParser();
                    byte[] data_buff = LivePlayActivity.this.parser.serialize(lockModels, GlobalConstantValue.GMS_MSG_DO_PASSWORD_CHECK).getBytes("UTF-8");
                    LivePlayActivity.this.tcpSocket.setSoTimeout(3000);
                    GsSendSocket.sendSocketToStb(data_buff, LivePlayActivity.this.tcpSocket, 0, data_buff.length, GlobalConstantValue.GMS_MSG_DO_PASSWORD_CHECK);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LivePlayActivity.this.pswInputDialog.dismiss();
            }
        }
    }

    private void setMessageProcess() {
        this.msgProc = MessageProcessor.obtain();
        this.msgProc.recycle();
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_DO_SAT2IP_CHANNEL_PLAY, this, this.onServerReturn);
        this.msgProc.setOnMessageProcess(3, this, this.onServerReturn);
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GMS_MSG_DO_PASSWORD_CHECK, this, this.onServerReturn);
        this.msgProc.setOnMessageProcess(GlobalConstantValue.GSCMD_NOTIFY_SOCKET_CLOSED, this, this.onServerReturn);
        this.msgProc.setOnMessageProcess(2017, this, this.onServerReturn);
        this.msgProc.setOnMessageProcess(2018, this, this.onServerReturn);
        this.msgProc.setOnMessageProcess(2016, this, this.onServerReturn);
    }

    /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$1 */
    class AnonymousClass1 implements MessageProcessor.PerformOnForeground {
        AnonymousClass1() {
        }

        @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
        public void doInForeground(Message msg) throws SocketException, UnsupportedEncodingException {
            if (LivePlayActivity.this.bOnErrorHappen) {
                if (LivePlayActivity.this.mWarningDialog != null && LivePlayActivity.this.mWarningDialog.isShowing()) {
                    LivePlayActivity.this.mWarningDialog.dismiss();
                }
                LivePlayActivity.this.bOnErrorHappen = false;
            }
            switch (msg.what) {
                case 3:
                    Log.d(LivePlayActivity.TAG, "GMS_MSG_REQUEST_PLAYING_CHANNEL");
                    LivePlayActivity.this.handleSTBChannelPlayChange(msg);
                    break;
                case GlobalConstantValue.GMS_MSG_DO_SAT2IP_CHANNEL_PLAY /* 1009 */:
                    Log.d(LivePlayActivity.TAG, "GMS_MSG_DO_SAT2IP_CHANNEL_PLAY");
                    LivePlayActivity.this.handleSat2TpReturn(msg);
                    break;
                case GlobalConstantValue.GMS_MSG_DO_PASSWORD_CHECK /* 1056 */:
                    LivePlayActivity.this.handlePasswordCheckBack(msg);
                    break;
                case 2016:
                case 2017:
                case 2018:
                    LivePlayActivity.this.handleFactoryReset(msg);
                    break;
                case GlobalConstantValue.GSCMD_NOTIFY_SOCKET_CLOSED /* 4112 */:
                    Log.d(LivePlayActivity.TAG, "GSCMD_NOTIFY_SOCKET_CLOSED");
                    LivePlayActivity.this.finish();
                    break;
            }
        }
    }

    public void handleSat2TpReturn(Message msg) {
        Bundle data = msg.getData();
        byte[] recvData = data.getByteArray("ReceivedData");
        if (recvData != null) {
            Log.d(TAG, new String(recvData));
            InputStream istream = new ByteArrayInputStream(recvData, 0, msg.arg1);
            try {
                Map<String, Object> map = (Map) this.parser.parse(istream, 16).get(0);
                if (map.get("success") == null) {
                    showDialog(0);
                    return;
                }
                int iState = ((Integer) map.get("success")).intValue();
                if (iState != 1) {
                    Bundle bundle = null;
                    if (map.get("errormsg") != null) {
                        String errorMsg = (String) map.get("errormsg");
                        bundle = new Bundle();
                        bundle.putString("message", errorMsg);
                    } else if (map.get("url") == null || ((String) map.get("url")).length() <= 0) {
                        bundle = new Bundle();
                        bundle.putString("message", "Get play url fail");
                    }
                    showDialog(iState, bundle);
                    return;
                }
                this.url = (String) map.get("url");
                if (this.url == null) {
                    Toast.makeText(this, mktvsmart.screen.R.string.str_empty_url, 2000).show();
                } else {
                    Log.d(TAG, "play url : " + this.url);
                    playChannel();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void handleSTBChannelPlayChange(Message msg) throws SocketException, UnsupportedEncodingException {
        if (msg.arg1 > 0) {
            Bundle data = msg.getData();
            byte[] recvData = data.getByteArray("ReceivedData");
            DataParser parser = ParserFactory.getParser();
            List<?> list = null;
            try {
                InputStream istream = new ByteArrayInputStream(recvData, 0, msg.arg1);
                list = parser.parse(istream, 15);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String playingProgId = (String) list.get(0);
            DataConvertChannelModel playingProgram = null;
            DataConvertChannelModel sat2ipChannal = (DataConvertChannelModel) this.mPlayList.get(this.mSelectChannelPosition);
            try {
                playingProgram = ChannelData.getInstance().getProgramByProgramId(playingProgId);
            } catch (ProgramNotFoundException e2) {
                e2.printStackTrace();
            }
            if (!ChannelData.getInstance().canSat2ipChannelPlay(playingProgram, sat2ipChannal)) {
                switch (GMScreenGlobalInfo.getCurStbInfo().getPlatform_id()) {
                    case 8:
                    case 9:
                    case 14:
                        finish();
                        break;
                    default:
                        initSameTpChannes(ChannelData.getInstance().getIndexByProgIdInCurTvRadioProgList((String) list.get(0)));
                        break;
                }
                Toast.makeText(this, mktvsmart.screen.R.string.str_transponder_changed, 0).show();
            }
        }
    }

    public void handlePasswordCheckBack(Message msg) {
        try {
            Bundle data = msg.getData();
            byte[] recvData = data.getByteArray("ReceivedData");
            this.parser = ParserFactory.getParser();
            InputStream instream = new ByteArrayInputStream(recvData, 0, recvData.length);
            if (Integer.parseInt((String) this.parser.parse(instream, 15).get(0)) == 0) {
                inputPermissionPassword(this.mSelectChannelPosition);
            } else {
                askPlayUrl(this.mSelectChannelPosition);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleFactoryReset(Message msg) {
        if (msg.arg2 == 0) {
            String showMsg = "";
            switch (msg.what) {
                case 2016:
                    showMsg = getString(mktvsmart.screen.R.string.factory_default_all);
                    break;
                case 2017:
                    if (((DataConvertChannelModel) this.mPlayList.get(this.mSelectChannelPosition)).getChannelTpye() == 0) {
                        showMsg = getString(mktvsmart.screen.R.string.factory_default_channel);
                        break;
                    }
                    break;
                case 2018:
                    if (((DataConvertChannelModel) this.mPlayList.get(this.mSelectChannelPosition)).getChannelTpye() == 1) {
                        showMsg = getString(mktvsmart.screen.R.string.factory_default_radio);
                        break;
                    }
                    break;
            }
            finish();
            Toast.makeText(this, showMsg, 0).show();
        }
    }

    public void playChannel() {
        if (this.url != null && this.url.length() > 0 && !this.url.startsWith("file://") && !this.url.contains(this.serverAddr)) {
            Pattern pattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
            Matcher matcher = pattern.matcher(this.url);
            this.url = matcher.replaceAll(this.serverAddr);
        }
        this.mSurfaceView.setKeepScreenOn(true);
        this.mLibVLC.playMRL(this.url);
        String title = "";
        if (GMScreenGlobalInfo.playType == 2) {
            title = this.mCurChannel.getProgramName();
        } else if (GMScreenGlobalInfo.playType == 1) {
            title = ((DataConvertPvrInfoModel) this.mPlayList.get(this.mSelectChannelPosition)).getProgramName();
        }
        this.mViewControl.setTitle(title);
    }

    private void startLoadingAnimation(String text) {
        if (this.mWaittingView.getVisibility() != 0) {
            this.mWaittingView.setVisibility(0);
        }
        this.mWattingText.setText(text);
    }

    public void stopLoadingAnimation() {
        this.mWaittingView.setVisibility(4);
    }

    public boolean isPlaying() {
        return this.mLibVLC.isPlaying();
    }

    public void play() {
        this.mLibVLC.play();
        this.mSurfaceView.setKeepScreenOn(true);
    }

    public void pause() {
        if (isRecording()) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(5, new Runnable() { // from class: mktvsmart.screen.vlc.LivePlayActivity.13
                AnonymousClass13() {
                }

                @Override // java.lang.Runnable
                public void run() {
                    LivePlayActivity.this.mLibVLC.stop();
                    LivePlayActivity.this.mSurfaceView.setKeepScreenOn(false);
                }
            }));
        } else {
            this.mLibVLC.pause();
            this.mSurfaceView.setKeepScreenOn(false);
        }
    }

    /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$13 */
    class AnonymousClass13 implements Runnable {
        AnonymousClass13() {
        }

        @Override // java.lang.Runnable
        public void run() {
            LivePlayActivity.this.mLibVLC.stop();
            LivePlayActivity.this.mSurfaceView.setKeepScreenOn(false);
        }
    }

    public boolean isRecordable() {
        return this.mLibVLC.videoIsRecordable();
    }

    public boolean isRecording() {
        return this.mLibVLC.videoIsRecording();
    }

    public void recordStart() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH:mm", Locale.US);
        String name = "";
        if (GMScreenGlobalInfo.playType == 2) {
            name = String.valueOf(this.mCurChannel.getProgramName()) + "_" + df.format(new Date());
        } else if (GMScreenGlobalInfo.playType == 1) {
            name = String.valueOf(Pvr2smallData.getInstance().getPvr2smallList().get(this.mSelectChannelPosition).getProgramName()) + "_" + df.format(new Date());
        }
        String name2 = String.valueOf(REC_PATH) + name;
        Log.d(TAG, "send record path : " + name2);
        if (this.mLibVLC.videoRecordStart(name2)) {
            showInfo("Recording started", 3000);
            this.mViewControl.setRecordFlagVisible(true);
        } else {
            showInfo("Recording start failed", 3000);
        }
    }

    public void recordStop() {
        if (this.mLibVLC.videoRecordStop()) {
            showInfo("Recording stoped", 3000);
            this.mViewControl.setRecordFlagVisible(false);
        } else {
            showInfo("Recording stop failed", 3000);
        }
    }

    public void snapShot() throws IOException {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm", Locale.US);
            String name = "";
            if (GMScreenGlobalInfo.playType == 2) {
                name = String.valueOf(this.mCurChannel.getProgramName()) + "_" + df.format(new Date());
            } else if (GMScreenGlobalInfo.playType == 1) {
                name = String.valueOf(Pvr2smallData.getInstance().getPvr2smallList().get(this.mSelectChannelPosition).getProgramName()) + "_" + df.format(new Date());
            }
            String name2 = String.valueOf(SNAPSHOT_PATH) + name + ".png";
            File file = new File(name2);
            if (!file.exists()) {
                file.createNewFile();
            }
            if (this.mLibVLC.takeSnapShot(name2, 640, 360)) {
                showInfo("Save snapshot", 3000);
            } else {
                showInfo("Save snapshot fail", 3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showChannelList() {
        if (!this.mLiveList.isShowing()) {
            this.mLiveList.show();
        }
    }

    public void hideChannelList() {
        if (this.mLiveList.isShowing()) {
            this.mLiveList.hide();
        }
    }

    @Override // org.videolan.libvlc.IVideoPlayer
    public void setSurfaceLayout(int width, int height, int visible_width, int visible_height, int sar_num, int sar_den) {
        Log.d(TAG, "setSurfaceSize width * height = " + width + "* " + height);
        if (width * height != 0) {
            this.mViewControl.setSurfaceLayout(width, height, visible_width, visible_height, sar_num, sar_den);
            Message msg = this.mHandler.obtainMessage(2);
            this.mHandler.sendMessage(msg);
        }
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

    public void fadeOutInfo() {
        if (this.mInfo.getVisibility() == 0) {
            this.mInfo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
        }
        this.mInfo.setVisibility(4);
    }

    public void showOverlay() {
        showOverlay(4000);
    }

    public void showOverlay(int timeout) {
        this.mViewControl.show();
        if (timeout != 0) {
            this.mHandler.removeMessages(1);
            Message msg = this.mHandler.obtainMessage(1);
            this.mHandler.sendMessageDelayed(msg, timeout);
        }
    }

    public void hideOverlay() {
        this.mHandler.removeMessages(1);
        this.mViewControl.hide();
    }

    public void showRecordStopTips(Runnable positiveRun) {
        this.mWarningDialog = new AlertDialog.Builder(this).setTitle("Stop Record").setMessage("Do you want stop recording ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: mktvsmart.screen.vlc.LivePlayActivity.14
            private final /* synthetic */ Runnable val$positiveRun;

            AnonymousClass14(Runnable positiveRun2) {
                runnable = positiveRun2;
            }

            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                LivePlayActivity.this.recordStop();
                if (runnable != null) {
                    runnable.run();
                }
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: mktvsmart.screen.vlc.LivePlayActivity.15
            AnonymousClass15() {
            }

            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(false).create();
        this.mWarningDialog.show();
    }

    /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$14 */
    class AnonymousClass14 implements DialogInterface.OnClickListener {
        private final /* synthetic */ Runnable val$positiveRun;

        AnonymousClass14(Runnable positiveRun2) {
            runnable = positiveRun2;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            LivePlayActivity.this.recordStop();
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$15 */
    class AnonymousClass15 implements DialogInterface.OnClickListener {
        AnonymousClass15() {
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    public String getSTBReturnMessage(int what, Bundle args) {
        if (args != null && args.getString("message") != null) {
            return args.getString("message");
        }
        switch (what) {
            case -3:
                return getString(mktvsmart.screen.R.string.str_live_channel_stoped);
            case -2:
                return getString(mktvsmart.screen.R.string.str_stb_in_X_TMS_mode);
            case -1:
                return getString(mktvsmart.screen.R.string.str_stb_in_REC_or_TMS_mode);
            default:
                return getString(mktvsmart.screen.R.string.str_unknow_error_about_stb);
        }
    }

    @Override // android.app.Activity
    protected Dialog onCreateDialog(int id, Bundle args) {
        switch (id) {
            case -5:
            case -4:
            case -3:
            case -2:
            case -1:
            case 0:
                this.mWarningDialog = new AlertDialog.Builder(this).setTitle(mktvsmart.screen.R.string.str_play_back_error).setMessage(getSTBReturnMessage(id, args)).setPositiveButton(mktvsmart.screen.R.string.str_back, new DialogInterface.OnClickListener() { // from class: mktvsmart.screen.vlc.LivePlayActivity.16
                    AnonymousClass16() {
                    }

                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        LivePlayActivity.this.bOnErrorHappen = false;
                        LivePlayActivity.this.stopLoadingAnimation();
                        if (!LivePlayActivity.this.mLiveList.isShowing()) {
                            LivePlayActivity.this.mLiveList.show();
                        }
                    }
                }).setCancelable(false).create();
                return this.mWarningDialog;
            default:
                return null;
        }
    }

    /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$16 */
    class AnonymousClass16 implements DialogInterface.OnClickListener {
        AnonymousClass16() {
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            LivePlayActivity.this.bOnErrorHappen = false;
            LivePlayActivity.this.stopLoadingAnimation();
            if (!LivePlayActivity.this.mLiveList.isShowing()) {
                LivePlayActivity.this.mLiveList.show();
            }
        }
    }

    @Override // android.app.Activity
    @Deprecated
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case -5:
            case -4:
            case -3:
            case -2:
            case -1:
            case 0:
                this.bOnErrorHappen = true;
                break;
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        if (!this.mLiveList.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v.equals(this.mMainView) || v.getId() == mktvsmart.screen.R.id.play_view) {
            if (this.mLiveList.isShowing()) {
                Log.d(TAG, "hide mLiveList");
                this.mLiveList.hide();
                return;
            }
            Log.d(TAG, "mViewControl.isShowing() = " + this.mViewControl.isShowing());
            if (this.mViewControl.isShowing()) {
                hideOverlay();
            } else {
                showOverlay();
            }
        }
    }

    /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$2 */
    class AnonymousClass2 extends GestureDetector.SimpleOnGestureListener {
        AnonymousClass2() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onDown(MotionEvent e) {
            LivePlayActivity.this.mCurrentVolumn = LivePlayActivity.this.mAudioManager.getStreamVolume(3);
            return false;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d(LivePlayActivity.TAG, "onScroll");
            float y_changed = e2.getRawY() - e1.getRawY();
            float x_changed = e2.getRawX() - e1.getRawX();
            DisplayMetrics screen = new DisplayMetrics();
            LivePlayActivity.this.getWindowManager().getDefaultDisplay().getMetrics(screen);
            if (LivePlayActivity.this.mSurfaceYDisplayRange == 0) {
                LivePlayActivity.this.mSurfaceYDisplayRange = Math.min(screen.widthPixels, screen.heightPixels);
            }
            if (e1.getRawX() > (screen.widthPixels * 3) / 4) {
                LivePlayActivity.this.onVolumeSlide(y_changed);
                return true;
            }
            if (e1.getRawY() < screen.heightPixels / 3) {
                LivePlayActivity.this.onBrightnessSlide(-x_changed);
                return true;
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    public void onVolumeSlide(float distance) {
        int mMaxVolume = this.mAudioManager.getStreamMaxVolume(3);
        int delta = -((int) ((distance / this.mSurfaceYDisplayRange) * mMaxVolume));
        int vol = (int) Math.min(Math.max(this.mCurrentVolumn + delta, 0.0f), mMaxVolume);
        if (this.mVolumeBrightnessLayout.getVisibility() != 0) {
            this.mVolumeBrightnessLayout.setVisibility(0);
        }
        this.mOperationBg.setImageResource(mktvsmart.screen.R.drawable.video_volumn_bg);
        if (delta != 0) {
            this.mAudioManager.setStreamVolume(3, vol, 0);
        }
        ViewGroup.LayoutParams lp = this.mOperationPercent.getLayoutParams();
        lp.width = (findViewById(mktvsmart.screen.R.id.operation_full).getLayoutParams().width * vol) / mMaxVolume;
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
        this.mOperationBg.setImageResource(mktvsmart.screen.R.drawable.video_brightness_bg);
        ViewGroup.LayoutParams lp1 = this.mOperationPercent.getLayoutParams();
        lp1.width = (int) (findViewById(mktvsmart.screen.R.id.operation_full).getLayoutParams().width * lp.screenBrightness);
        this.mOperationPercent.setLayoutParams(lp1);
        this.mHandler.removeMessages(4);
        this.mHandler.sendEmptyMessageDelayed(4, 1000L);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");
        Message msg = this.mHandler.obtainMessage(2);
        this.mHandler.sendMessage(msg);
        super.onConfigurationChanged(newConfig);
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
        Log.d(TAG, "onResume");
        if (AdsControllor.obtain().getmStatus() == AdsControllor.AdStatus.CLOSE && this.isPauseByAd) {
            play();
            this.isPauseByAd = false;
            changeAudioFocus(true);
            setESTracks();
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause111");
        if (this.mLibVLC.isPlaying()) {
            if (this.mLibVLC.videoIsRecording()) {
                this.mLibVLC.videoRecordStop();
            }
            Log.d(TAG, "onPause121");
            this.mLibVLC.stop();
            Log.d(TAG, "onPause212");
            this.mSurfaceView.setKeepScreenOn(false);
        }
        if (this.mLiveList.isShowing()) {
            Log.d(TAG, "hide mLiveList");
            this.mLiveList.hide();
        }
        Log.d(TAG, "onPause222");
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onDestroy() throws NoSuchMethodException, ClassNotFoundException, SecurityException, UnsupportedEncodingException {
        super.onDestroy();
        Log.d(TAG, "onDestroy111");
        if (this.bOnCreateFinish) {
            this.mHandler.removeCallbacksAndMessages(null);
            if (this.mReceiver != null) {
                unregisterReceiver(this.mReceiver);
            }
            switch (GMScreenGlobalInfo.getCurStbInfo().getPlatform_id()) {
                case 8:
                case 9:
                case 14:
                    stopStream();
                    break;
            }
            EventHandler em = EventHandler.getInstance();
            em.removeHandler(this.eventHandler);
            this.mLibVLC.eventVideoPlayerActivityCreated(false);
            if (this.mDisabledHardwareAcceleration) {
                this.mLibVLC.setHardwareAcceleration(this.mPreviousHardwareAccelerationMode);
            }
            this.msgProc.removeMessages(GlobalConstantValue.GMS_MSG_DO_SAT2IP_CHANNEL_PLAY);
            this.msgProc.removeMessages(3);
            this.msgProc.recycle();
            if (this.mSubHandler != null) {
                this.mSubHandler.removeCallbacksAndMessages(null);
                this.mSubHandler.getLooper().quit();
            }
        }
        Log.d(TAG, "onDestroy2222");
    }

    @Override // android.app.Activity
    public void finish() {
        super.finish();
        GMScreenGlobalInfo.playType = 0;
    }

    public void handleBroadcast(Intent intent) {
        String action = intent.getAction();
        if (action.equalsIgnoreCase("android.intent.action.BATTERY_CHANGED")) {
            int batteryLevel = intent.getIntExtra("level", 0);
            this.mViewControl.setBatteryLevel(batteryLevel);
        }
    }

    /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$3 */
    class AnonymousClass3 implements SurfaceHolder.Callback {
        AnonymousClass3() {
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (LivePlayActivity.this.mLibVLC != null) {
                Surface newSurface = holder.getSurface();
                Log.d(LivePlayActivity.TAG, "mSurface != newSurface: " + (LivePlayActivity.this.mSurface != newSurface));
                if (LivePlayActivity.this.mSurface != newSurface) {
                    Log.d(LivePlayActivity.TAG, "attachSurface");
                    LivePlayActivity.this.mSurface = newSurface;
                    LivePlayActivity.this.mLibVLC.attachSurface(LivePlayActivity.this.mSurface, LivePlayActivity.this);
                }
            }
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (LivePlayActivity.this.mLibVLC != null) {
                LivePlayActivity.this.mSurface = null;
                Log.d(LivePlayActivity.TAG, "LibVLC.detachSurface");
                LivePlayActivity.this.mLibVLC.detachSurface();
            }
        }
    }

    /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$4 */
    class AnonymousClass4 implements SurfaceHolder.Callback {
        AnonymousClass4() {
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Surface newSurface;
            if (LivePlayActivity.this.mLibVLC != null && LivePlayActivity.this.mSubtitleSurface != (newSurface = holder.getSurface())) {
                LivePlayActivity.this.mSubtitleSurface = newSurface;
                LivePlayActivity.this.mLibVLC.attachSubtitlesSurface(LivePlayActivity.this.mSubtitleSurface);
            }
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (LivePlayActivity.this.mLibVLC != null) {
                LivePlayActivity.this.mSubtitleSurface = null;
                LivePlayActivity.this.mLibVLC.detachSubtitlesSurface();
            }
        }
    }

    @Override // mktvsmart.screen.vlc.BaseVLCPlayActivity
    public void onMediaPlayerPlaying(Bundle data) {
        stopLoadingAnimation();
        showOverlay();
        if (AdsControllor.obtain().isOpen()) {
            pause();
            this.isPauseByAd = true;
        } else {
            setESTracks();
            changeAudioFocus(true);
        }
        this.mViewControl.updateOverlayPausePlay();
        if (this.mAdView.isShown()) {
            this.mAdView.setVisibility(8);
        }
    }

    @Override // mktvsmart.screen.vlc.BaseVLCPlayActivity
    public void onMediaPlayerEndReached(Bundle data) {
        this.mLibVLC.stop();
        this.bPlayEnd = true;
        this.mHandler.postDelayed(this.mPlayEOFOccurred, 3000L);
        changeAudioFocus(false);
    }

    /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$5 */
    class AnonymousClass5 implements Runnable {
        AnonymousClass5() {
        }

        @Override // java.lang.Runnable
        public void run() {
            LivePlayActivity.this.mViewControl.updateOverlayPausePlay();
            if (LivePlayActivity.this.bPlayEnd) {
                AlertDialog dialog = new AlertDialog.Builder(LivePlayActivity.this).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // from class: mktvsmart.screen.vlc.LivePlayActivity.5.1
                    AnonymousClass1() {
                    }

                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog2, int id) {
                        LivePlayActivity.this.finish();
                    }
                }).setTitle(mktvsmart.screen.R.string.str_play_back_error).setMessage("The connection has been disconnected").create();
                dialog.show();
            }
        }

        /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$5$1 */
        class AnonymousClass1 implements DialogInterface.OnClickListener {
            AnonymousClass1() {
            }

            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog2, int id) {
                LivePlayActivity.this.finish();
            }
        }
    }

    @Override // mktvsmart.screen.vlc.BaseVLCPlayActivity
    public void onMediaPlayerVout(Bundle data) {
        Log.d(TAG, "Vout count " + data.getInt("data"));
        if (data.getInt("data") == 0) {
            startLoadingAnimation(getString(mktvsmart.screen.R.string.please_wait));
        }
    }

    @Override // mktvsmart.screen.vlc.BaseVLCPlayActivity
    public void onMediaPlayerEncounteredError(Bundle data) {
        stopLoadingAnimation();
        this.bOnErrorHappen = true;
        AlertDialog dialog = new AlertDialog.Builder(this).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // from class: mktvsmart.screen.vlc.LivePlayActivity.17
            AnonymousClass17() {
            }

            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog2, int id) {
                LivePlayActivity.this.bOnErrorHappen = false;
                LivePlayActivity.this.stopLoadingAnimation();
                if (!LivePlayActivity.this.mLiveList.isShowing()) {
                    LivePlayActivity.this.mLiveList.show();
                }
            }
        }).setTitle(mktvsmart.screen.R.string.str_play_back_error).setMessage(mktvsmart.screen.R.string.str_cannot_play_this_channel).create();
        if (!isFinishing()) {
            dialog.show();
        }
        this.mViewControl.updateOverlayPausePlay();
    }

    /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$17 */
    class AnonymousClass17 implements DialogInterface.OnClickListener {
        AnonymousClass17() {
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog2, int id) {
            LivePlayActivity.this.bOnErrorHappen = false;
            LivePlayActivity.this.stopLoadingAnimation();
            if (!LivePlayActivity.this.mLiveList.isShowing()) {
                LivePlayActivity.this.mLiveList.show();
            }
        }
    }

    @Override // mktvsmart.screen.vlc.BaseVLCPlayActivity
    public void onHardwareAccelerationError(Bundle data) {
        this.mLibVLC.stop();
        AlertDialog dialog = new AlertDialog.Builder(this).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // from class: mktvsmart.screen.vlc.LivePlayActivity.18
            AnonymousClass18() {
            }

            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog2, int id) throws SocketException, NoSuchMethodException, ClassNotFoundException, SecurityException, UnsupportedEncodingException {
                LivePlayActivity.this.mDisabledHardwareAcceleration = true;
                LivePlayActivity.this.mPreviousHardwareAccelerationMode = LivePlayActivity.this.mLibVLC.getHardwareAcceleration();
                LivePlayActivity.this.mLibVLC.setHardwareAcceleration(0);
                LivePlayActivity.this.askPlayUrl(LivePlayActivity.this.mSelectChannelPosition);
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: mktvsmart.screen.vlc.LivePlayActivity.19
            AnonymousClass19() {
            }

            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog2, int id) {
                LivePlayActivity.this.finish();
            }
        }).setTitle("Would you like to disable it and try again?").setMessage("An error occurred with hardware acceleration.").create();
        if (!isFinishing()) {
            dialog.show();
        }
        this.mViewControl.updateOverlayPausePlay();
    }

    /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$18 */
    class AnonymousClass18 implements DialogInterface.OnClickListener {
        AnonymousClass18() {
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog2, int id) throws SocketException, NoSuchMethodException, ClassNotFoundException, SecurityException, UnsupportedEncodingException {
            LivePlayActivity.this.mDisabledHardwareAcceleration = true;
            LivePlayActivity.this.mPreviousHardwareAccelerationMode = LivePlayActivity.this.mLibVLC.getHardwareAcceleration();
            LivePlayActivity.this.mLibVLC.setHardwareAcceleration(0);
            LivePlayActivity.this.askPlayUrl(LivePlayActivity.this.mSelectChannelPosition);
        }
    }

    /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$19 */
    class AnonymousClass19 implements DialogInterface.OnClickListener {
        AnonymousClass19() {
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog2, int id) {
            LivePlayActivity.this.finish();
        }
    }

    @Override // mktvsmart.screen.vlc.BaseVLCPlayActivity
    public void onMediaPlayerBuffering(Bundle data) {
        int percent = (int) data.getFloat("data");
        if (percent > 0 && percent < 100) {
            startLoadingAnimation("Buffering..." + percent + "%");
            if (this.mAdView.isShown()) {
                this.mAdView.setVisibility(8);
                return;
            }
            return;
        }
        if (percent >= 100) {
            stopLoadingAnimation();
        }
    }

    @Override // mktvsmart.screen.vlc.BaseVLCPlayActivity
    public void onMediaPlayerRecordableChanged(Bundle data) {
        this.mViewControl.setRecordVisible(data.getBoolean("recordable"));
    }

    @Override // mktvsmart.screen.vlc.BaseVLCPlayActivity
    public void onMediaPlayerPaused(Bundle data) {
        showOverlay();
        this.mViewControl.updateOverlayPausePlay();
        this.mHandler.removeMessages(1);
        if (!this.mAdView.isShown() && !this.isPauseByAd && !this.mWaittingView.isShown()) {
            this.mAdView.setVisibility(0);
        }
    }

    @Override // mktvsmart.screen.vlc.BaseVLCPlayActivity
    public void onMediaPlayerStopped(Bundle data) {
        showOverlay();
        this.mViewControl.updateOverlayPausePlay();
        this.mHandler.removeMessages(1);
        if (!this.mAdView.isShown() && !this.mWaittingView.isShown()) {
            this.mAdView.setVisibility(0);
        }
        changeAudioFocus(false);
    }

    @Override // mktvsmart.screen.vlc.BaseVLCPlayActivity
    public void onMediaPlayerPositionChanged(Bundle data) {
        this.mViewControl.updateOverlayPausePlay();
    }

    @Override // mktvsmart.screen.vlc.PlayViewControllor.TranscodeListenner
    public void OnTranscodeChange(int iResolution, int iBitrate) throws SocketException, UnsupportedEncodingException {
        if (this.mSubThread == null) {
            this.mSubThread = new HandlerThread("live_play_work_thread");
            this.mSubThread.start();
        }
        if (this.mSubHandler == null) {
            this.mSubHandler = new Handler(this.mSubThread.getLooper(), this.mSubMsgCallback);
        }
        this.mSubHandler.sendMessage(this.mSubHandler.obtainMessage(12, iResolution, iBitrate));
        askPlayUrl(this.mSelectChannelPosition);
    }

    /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$6 */
    class AnonymousClass6 implements AudioManager.OnAudioFocusChangeListener {
        AnonymousClass6() {
        }

        @Override // android.media.AudioManager.OnAudioFocusChangeListener
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case -3:
                case -2:
                    break;
                case -1:
                    LivePlayActivity.this.changeAudioFocus(false);
                    break;
                case 0:
                default:
                    return;
                case 1:
                case 2:
                case 3:
                    if (!LivePlayActivity.this.mLibVLC.isPlaying() && LivePlayActivity.this.mLostFocus) {
                        LivePlayActivity.this.mLibVLC.play();
                        LivePlayActivity.this.mLostFocus = false;
                        return;
                    }
                    return;
            }
            if (LivePlayActivity.this.mLibVLC.isPlaying()) {
                LivePlayActivity.this.mLostFocus = true;
                LivePlayActivity.this.mLibVLC.pause();
            }
        }
    }

    @TargetApi(8)
    public int changeAudioFocus(boolean acquire) {
        if (!LibVlcUtil.isFroyoOrLater()) {
            return 1;
        }
        if (this.mAudioManager == null) {
            return 0;
        }
        if (acquire) {
            if (this.mHasAudioFocus) {
                return 1;
            }
            int result = this.mAudioManager.requestAudioFocus(this.mAudioFocusListener, 3, 1);
            this.mAudioManager.setParameters("bgm_state=true");
            this.mHasAudioFocus = true;
            return result;
        }
        if (!this.mHasAudioFocus) {
            return 1;
        }
        int result2 = this.mAudioManager.abandonAudioFocus(this.mAudioFocusListener);
        this.mAudioManager.setParameters("bgm_state=false");
        this.mHasAudioFocus = true;
        return result2;
    }

    private void setESTracks() {
        if (this.mLastAudioTrack >= 0) {
            this.mLibVLC.setAudioTrack(this.mLastAudioTrack);
            this.mLastAudioTrack = -1;
        }
        if (this.mLastSpuTrack >= -1) {
            this.mLibVLC.setSpuTrack(this.mLastSpuTrack);
            this.mLastSpuTrack = -2;
        }
    }

    private static class UIHandler extends WeakHandler<LivePlayActivity> {
        public UIHandler(LivePlayActivity owner) {
            super(owner);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            LivePlayActivity activity = getOwner();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        activity.hideOverlay();
                        break;
                    case 2:
                        activity.mViewControl.changeSurfaceSize();
                        activity.mLiveList.setViewSize();
                        break;
                    case 3:
                        activity.fadeOutInfo();
                        break;
                    case 4:
                        activity.mVolumeBrightnessLayout.setVisibility(8);
                        break;
                    case 5:
                        activity.showRecordStopTips((Runnable) msg.obj);
                        break;
                    case 6:
                        AdsControllor.obtain().showInterstitialAd(5);
                        break;
                }
            }
        }
    }

    /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$7 */
    class AnonymousClass7 implements Handler.Callback {
        AnonymousClass7() {
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) throws SocketException, NumberFormatException, UnsupportedEncodingException {
            switch (msg.what) {
                case 11:
                    LivePlayActivity.sRtsp = new Sat2IP_Rtsp();
                    String base = "";
                    String query = "";
                    if (GMScreenGlobalInfo.playType == 1) {
                        LivePlayActivity.sRtsp.set_eof_listener(LivePlayActivity.this);
                        base = Pvr2smallData.getInstance().getPlayUrlBase(LivePlayActivity.this.mSelectChannelPosition, LivePlayActivity.this.tcpSocket.getInetAddress().toString());
                        query = Pvr2smallData.getInstance().getPlayUrlQuery();
                    } else if (GMScreenGlobalInfo.playType == 2) {
                        base = Sat2ipUtil.getRtspUriBase(LivePlayActivity.this.tcpSocket.getInetAddress().toString());
                        query = Sat2ipUtil.getRtspUriQuery((DataConvertChannelModel) LivePlayActivity.this.mPlayList.get(((Integer) msg.obj).intValue()));
                    }
                    boolean isSetupOk = LivePlayActivity.sRtsp.setup_blocked(base, query);
                    Log.d(LivePlayActivity.TAG, "isSetupOk = " + isSetupOk);
                    if (!isSetupOk) {
                        LivePlayActivity.sRtsp = null;
                    } else {
                        if (GMScreenGlobalInfo.playType == 2) {
                            LivePlayActivity.this.sendSat2ipChannelIdToStb(((Integer) msg.obj).intValue());
                        }
                        DVBtoIP.initResourceForPlayer(LivePlayActivity.sRtsp.get_rtp_port(), LivePlayActivity.this.getRtspPipeFilePath(), GMScreenGlobalInfo.playType, GMScreenGlobalInfo.getKeyWay());
                        Uri uri = Uri.parse(LibVLC.PathToURI(LivePlayActivity.this.getRtspPipeFilePath()));
                        LivePlayActivity.this.url = uri.toString();
                        LivePlayActivity.this.mHandler.post(new Runnable() { // from class: mktvsmart.screen.vlc.LivePlayActivity.7.1
                            AnonymousClass1() {
                            }

                            @Override // java.lang.Runnable
                            public void run() {
                                LivePlayActivity.this.playChannel();
                            }
                        });
                    }
                    return true;
                case 12:
                    TranscodeConstants.saveTranscodeSetting(msg.arg1, msg.arg2);
                    return true;
                default:
                    return true;
            }
        }

        /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$7$1 */
        class AnonymousClass1 implements Runnable {
            AnonymousClass1() {
            }

            @Override // java.lang.Runnable
            public void run() {
                LivePlayActivity.this.playChannel();
            }
        }
    }

    @Override // com.voicetechnology.rtspclient.test.Sat2IP_Rtsp.EndOfFileListener
    public void onEndOfFile() throws UnsupportedEncodingException {
        stopStream();
        finish();
    }

    @Override // org.videolan.libvlc.IVideoPlayer
    public int configureSurface(Surface surface, int width, int height, int hal) {
        Log.d(TAG, "configureSurface:" + surface + "  " + width + "x" + height);
        Log.d(TAG, "LibVlcUtil.isICSOrLater():" + LibVlcUtil.isICSOrLater());
        if (LibVlcUtil.isICSOrLater() || surface == null) {
            return -1;
        }
        if (width * height == 0) {
            return 0;
        }
        ConfigureSurfaceHolder holder = new ConfigureSurfaceHolder(surface);
        this.mHandler.post(new Runnable() { // from class: mktvsmart.screen.vlc.LivePlayActivity.20
            private final /* synthetic */ int val$hal;
            private final /* synthetic */ int val$height;
            private final /* synthetic */ ConfigureSurfaceHolder val$holder;
            private final /* synthetic */ int val$width;

            AnonymousClass20(int width2, int height2, int hal2, ConfigureSurfaceHolder holder2) {
                i = width2;
                i = height2;
                i = hal2;
                configureSurfaceHolder = holder2;
            }

            @Override // java.lang.Runnable
            public void run() {
                Log.d(LivePlayActivity.TAG, "setFixedSize:" + i + "x" + i + " hal = " + i);
                if (LivePlayActivity.this.mSurface != configureSurfaceHolder.surface || LivePlayActivity.this.mSurfaceHolder == null) {
                    if (LivePlayActivity.this.mSubtitleSurface == configureSurfaceHolder.surface && LivePlayActivity.this.mSubtitlesSurfaceHolder != null) {
                        if (i != 0) {
                            LivePlayActivity.this.mSubtitlesSurfaceHolder.setFormat(i);
                        }
                        LivePlayActivity.this.mSubtitlesSurfaceHolder.setFixedSize(i, i);
                    }
                } else {
                    if (i != 0) {
                        LivePlayActivity.this.mSurfaceHolder.setFormat(i);
                    }
                    LivePlayActivity.this.mSurfaceHolder.setFixedSize(i, i);
                }
                synchronized (configureSurfaceHolder) {
                    configureSurfaceHolder.configured = true;
                    configureSurfaceHolder.notifyAll();
                }
            }
        });
        try {
            synchronized (holder2) {
                if (!holder2.configured) {
                    Log.d(TAG, "holder.wait()");
                    holder2.wait();
                }
            }
            return 1;
        } catch (InterruptedException e) {
            return 0;
        }
    }

    /* renamed from: mktvsmart.screen.vlc.LivePlayActivity$20 */
    class AnonymousClass20 implements Runnable {
        private final /* synthetic */ int val$hal;
        private final /* synthetic */ int val$height;
        private final /* synthetic */ ConfigureSurfaceHolder val$holder;
        private final /* synthetic */ int val$width;

        AnonymousClass20(int width2, int height2, int hal2, ConfigureSurfaceHolder holder2) {
            i = width2;
            i = height2;
            i = hal2;
            configureSurfaceHolder = holder2;
        }

        @Override // java.lang.Runnable
        public void run() {
            Log.d(LivePlayActivity.TAG, "setFixedSize:" + i + "x" + i + " hal = " + i);
            if (LivePlayActivity.this.mSurface != configureSurfaceHolder.surface || LivePlayActivity.this.mSurfaceHolder == null) {
                if (LivePlayActivity.this.mSubtitleSurface == configureSurfaceHolder.surface && LivePlayActivity.this.mSubtitlesSurfaceHolder != null) {
                    if (i != 0) {
                        LivePlayActivity.this.mSubtitlesSurfaceHolder.setFormat(i);
                    }
                    LivePlayActivity.this.mSubtitlesSurfaceHolder.setFixedSize(i, i);
                }
            } else {
                if (i != 0) {
                    LivePlayActivity.this.mSurfaceHolder.setFormat(i);
                }
                LivePlayActivity.this.mSurfaceHolder.setFixedSize(i, i);
            }
            synchronized (configureSurfaceHolder) {
                configureSurfaceHolder.configured = true;
                configureSurfaceHolder.notifyAll();
            }
        }
    }

    @Override // org.videolan.libvlc.IVideoPlayer
    public void eventHardwareAccelerationError() {
        EventHandler em = EventHandler.getInstance();
        em.callback(EventHandler.HardwareAccelerationError, new Bundle());
    }

    public GChatControllor getChatViewControllor() {
        return this.mChatViewControllor;
    }

    @Override // mktvsmart.screen.gchat.DanmakuManager
    public void addDanmaku(boolean islive, String content) {
        getChatViewControllor().addDanmaku(islive, content);
    }
}
