package mktvsmart.screen.vlc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.internal.view.SupportMenu;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.Date;
import mktvsmart.screen.GMScreenGlobalInfo;
import mktvsmart.screen.R;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.vlc.util.VLCInstance;

/* compiled from: LivePlayActivity.java */
/* loaded from: classes.dex */
class PlayViewControllor implements IVideoPlayer {
    private static final int SURFACE_16_9 = 4;
    private static final int SURFACE_4_3 = 5;
    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FILL = 3;
    private static final int SURFACE_FIT_HORIZONTAL = 1;
    private static final int SURFACE_FIT_VERTICAL = 2;
    private static final int SURFACE_ORIGINAL = 6;
    private static final String TAG = PlayViewControllor.class.getSimpleName();
    private ImageButton mBackIcon;
    private TextView mBatteryyView;
    private ImageButton mDanmakuControlIcon;
    private ImageButton mFullScreenIcon;
    private LibVLC mLibVLC;
    private ImageButton mListIcon;
    private ImageButton mLockIcon;
    private View mOverlayBottom;
    private View mOverlayHead;
    private View mOverlayOption;
    private ImageButton mPlayIcon;
    private ImageButton mRecIcon;
    private ImageView mRecordFlag;
    public int mSarDen;
    public int mSarNum;
    private ImageButton mSettingIcon;
    private boolean mShowing;
    private ImageButton mSizeIcon;
    private ImageButton mSnapShotIcon;
    private TextView mSystime;
    private TextView mTitleView;
    public int mVideoHeight;
    public int mVideoVisibleHeight;
    public int mVideoVisibleWidth;
    public int mVideoWidth;
    private LivePlayActivity owner;
    private TranscodeListenner settingLisnner;
    private int mCurrentSize = 0;
    private boolean mIsLocked = false;
    private boolean bTrancodeEnable = false;
    private boolean mIsDanmakuShow = true;

    /* compiled from: LivePlayActivity.java */
    public interface TranscodeListenner {
        void OnTranscodeChange(int i, int i2);
    }

    public PlayViewControllor(LivePlayActivity owner) {
        this.owner = owner;
        initView();
        initListener();
        this.mLibVLC = VLCInstance.getLibVlcInstance();
    }

    private void initView() {
        this.mOverlayHead = findViewById(R.id.player_overlay_header);
        this.mTitleView = (TextView) findViewById(R.id.player_overlay_title);
        this.mBatteryyView = (TextView) findViewById(R.id.player_overlay_battery);
        this.mSystime = (TextView) findViewById(R.id.player_overlay_systime);
        this.mOverlayHead.setVisibility(8);
        this.mOverlayBottom = findViewById(R.id.bottom_layout);
        this.mListIcon = (ImageButton) findViewById(R.id.channel_list_icon);
        this.mPlayIcon = (ImageButton) findViewById(R.id.control_icon);
        this.mLockIcon = (ImageButton) findViewById(R.id.screen_lock_icon);
        this.mFullScreenIcon = (ImageButton) findViewById(R.id.full_screen_icon);
        if (!GMScreenGlobalInfo.isChatSupport()) {
            this.mFullScreenIcon.setVisibility(8);
        }
        this.mOverlayBottom.setVisibility(8);
        this.mOverlayOption = findViewById(R.id.option_overlay);
        this.mBackIcon = (ImageButton) findViewById(R.id.player_overlay_back);
        this.mDanmakuControlIcon = (ImageButton) findViewById(R.id.danmaku);
        setDanmakuControlIconVisibility();
        this.mSettingIcon = (ImageButton) findViewById(R.id.transcode_setting);
        this.mSizeIcon = (ImageButton) findViewById(R.id.player_overlay_size);
        this.mRecIcon = (ImageButton) findViewById(R.id.player_overlay_rec);
        this.mRecIcon.setVisibility(8);
        this.mOverlayOption.setVisibility(8);
        this.mRecordFlag = (ImageView) findViewById(R.id.record_flag_icon);
        this.mRecordFlag.setVisibility(8);
        switch (GMScreenGlobalInfo.getCurStbPlatform()) {
            case 32:
            case 71:
            case 72:
            case 74:
                Log.d(TAG, "7588 or hisi3719");
                this.bTrancodeEnable = true;
                this.mSettingIcon.setVisibility(0);
                break;
            default:
                Log.d(TAG, "other platform");
                this.bTrancodeEnable = false;
                this.mSettingIcon.setVisibility(8);
                break;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDanmakuControlIconVisibility() {
        if (!GMScreenGlobalInfo.isChatSupport()) {
            this.mDanmakuControlIcon.setVisibility(8);
        } else if (this.owner.getChatViewControllor().isChatViewVisibility()) {
            this.mDanmakuControlIcon.setVisibility(8);
        } else {
            this.mDanmakuControlIcon.setVisibility(0);
        }
    }

    private void setAllViewVisibility(View target, int visibility) {
        if (target != null) {
            target.setVisibility(visibility);
            if (target.getId() != R.id.transcode_setting || this.bTrancodeEnable) {
                target.setVisibility(visibility);
            } else {
                target.setVisibility(8);
            }
            if (target instanceof ViewGroup) {
                int count = ((ViewGroup) target).getChildCount();
                for (int i = 0; i < count; i++) {
                    if (visibility == 0) {
                        switch (((ViewGroup) target).getChildAt(i).getId()) {
                            case R.id.danmaku /* 2131493270 */:
                                setDanmakuControlIconVisibility();
                                break;
                            case R.id.full_screen_icon /* 2131493277 */:
                                if (GMScreenGlobalInfo.isChatSupport()) {
                                    this.mFullScreenIcon.setVisibility(0);
                                    break;
                                } else {
                                    this.mFullScreenIcon.setVisibility(8);
                                    break;
                                }
                            default:
                                setAllViewVisibility(((ViewGroup) target).getChildAt(i), visibility);
                                break;
                        }
                    } else {
                        setAllViewVisibility(((ViewGroup) target).getChildAt(i), visibility);
                    }
                }
            }
        }
    }

    public void initListener() {
        View.OnClickListener onclick = new View.OnClickListener() { // from class: mktvsmart.screen.vlc.PlayViewControllor.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.player_overlay_back /* 2131493269 */:
                        PlayViewControllor.this.owner.finish();
                        break;
                    case R.id.danmaku /* 2131493270 */:
                        if (PlayViewControllor.this.mIsDanmakuShow) {
                            PlayViewControllor.this.mIsDanmakuShow = false;
                            PlayViewControllor.this.mDanmakuControlIcon.setBackgroundResource(R.drawable.ic_danmaku);
                            PlayViewControllor.this.owner.getChatViewControllor().setDanmakuViewVisibility(false);
                            break;
                        } else {
                            PlayViewControllor.this.mIsDanmakuShow = true;
                            PlayViewControllor.this.mDanmakuControlIcon.setBackgroundResource(R.drawable.ic_danmaku_press);
                            PlayViewControllor.this.owner.getChatViewControllor().setDanmakuViewVisibility(true);
                            break;
                        }
                    case R.id.player_overlay_rec /* 2131493271 */:
                        if (PlayViewControllor.this.owner.isRecording()) {
                            PlayViewControllor.this.owner.recordStop();
                            break;
                        } else {
                            PlayViewControllor.this.owner.recordStart();
                            break;
                        }
                    case R.id.transcode_setting /* 2131493272 */:
                        PlayViewControllor.this.showSettingDialog();
                        break;
                    case R.id.player_overlay_size /* 2131493273 */:
                        PlayViewControllor.this.changeSurfaceSizeManual();
                        break;
                    case R.id.channel_list_icon /* 2131493274 */:
                        PlayViewControllor.this.owner.showChannelList();
                        PlayViewControllor.this.owner.hideOverlay();
                        Log.d(PlayViewControllor.TAG, "curren focus " + PlayViewControllor.this.owner.getCurrentFocus());
                        break;
                    case R.id.control_icon /* 2131493275 */:
                        if (PlayViewControllor.this.owner.isPlaying()) {
                            PlayViewControllor.this.owner.pause();
                            break;
                        } else {
                            PlayViewControllor.this.owner.play();
                            break;
                        }
                    case R.id.screen_lock_icon /* 2131493276 */:
                        PlayViewControllor.this.changeScreenLock();
                        break;
                    case R.id.full_screen_icon /* 2131493277 */:
                        PlayViewControllor.this.owner.getChatViewControllor().changeChatViewVisibility();
                        PlayViewControllor.this.setDanmakuControlIconVisibility();
                        break;
                }
            }
        };
        this.mListIcon.setOnClickListener(onclick);
        this.mPlayIcon.setOnClickListener(onclick);
        this.mLockIcon.setOnClickListener(onclick);
        this.mBackIcon.setOnClickListener(onclick);
        this.mSettingIcon.setOnClickListener(onclick);
        this.mSizeIcon.setOnClickListener(onclick);
        this.mRecIcon.setOnClickListener(onclick);
        this.mFullScreenIcon.setOnClickListener(onclick);
        this.mDanmakuControlIcon.setOnClickListener(onclick);
    }

    public View findViewById(int id) {
        return this.owner.findViewById(id);
    }

    public void setRecordFlagVisible(boolean bVisible) {
        int i = R.drawable.ic_recording_circle;
        if (bVisible) {
            this.mRecordFlag.setVisibility(0);
            AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.0f, 1.0f);
            alphaAnimation1.setDuration(2000L);
            alphaAnimation1.setRepeatCount(-1);
            alphaAnimation1.setRepeatMode(2);
            this.mRecordFlag.setAnimation(alphaAnimation1);
            alphaAnimation1.start();
            ImageButton imageButton = this.mRecIcon;
            if (!this.owner.isRecording()) {
                i = R.drawable.ic_record_circle;
            }
            imageButton.setBackgroundResource(i);
            return;
        }
        this.mRecordFlag.clearAnimation();
        this.mRecordFlag.setVisibility(8);
        ImageButton imageButton2 = this.mRecIcon;
        if (!this.owner.isRecording()) {
            i = R.drawable.ic_record_circle;
        }
        imageButton2.setBackgroundResource(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeSurfaceSizeManual() {
        if (this.mCurrentSize < 6) {
            this.mCurrentSize++;
        } else {
            this.mCurrentSize = 0;
        }
        changeSurfaceSize();
        switch (this.mCurrentSize) {
            case 0:
                this.owner.showInfo("Best fit", 1000);
                break;
            case 1:
                this.owner.showInfo("Fit horizontal", 1000);
                break;
            case 2:
                this.owner.showInfo("Fit vertical", 1000);
                break;
            case 3:
                this.owner.showInfo("Fill", 1000);
                break;
            case 4:
                this.owner.showInfo("16:9", 1000);
                break;
            case 5:
                this.owner.showInfo("4:3", 1000);
                break;
            case 6:
                this.owner.showInfo("Center", 1000);
                break;
        }
        this.owner.showOverlay();
    }

    public void changeSurfaceSize() {
        double vw;
        double ar;
        int sw = this.owner.getWindow().getDecorView().getWidth();
        int sh = this.owner.getWindow().getDecorView().getHeight();
        if (this.mLibVLC != null && !this.mLibVLC.useCompatSurface()) {
            this.mLibVLC.setWindowSize(sw, sh);
        }
        double dw = sw;
        double dh = sh;
        boolean isPortrait = this.owner.getResources().getConfiguration().orientation == 1;
        if ((sw > sh && isPortrait) || (sw < sh && !isPortrait)) {
            dw = sh;
            dh = sw;
        }
        if (dw * dh == 0.0d || this.mVideoWidth * this.mVideoHeight == 0) {
            Log.e(TAG, "Invalid surface size");
            return;
        }
        if (this.mSarDen == this.mSarNum) {
            vw = this.mVideoVisibleWidth;
            ar = this.mVideoVisibleWidth / this.mVideoVisibleHeight;
        } else {
            vw = (this.mVideoVisibleWidth * this.mSarNum) / this.mSarDen;
            ar = vw / this.mVideoVisibleHeight;
        }
        double dar = dw / dh;
        switch (this.mCurrentSize) {
            case 0:
                if (dar < ar) {
                    dh = dw / ar;
                    break;
                } else {
                    dw = dh * ar;
                    break;
                }
            case 1:
                dh = dw / ar;
                break;
            case 2:
                dw = dh * ar;
                break;
            case 4:
                if (dar < 1.7777777777777777d) {
                    dh = dw / 1.7777777777777777d;
                    break;
                } else {
                    dw = dh * 1.7777777777777777d;
                    break;
                }
            case 5:
                if (dar < 1.3333333333333333d) {
                    dh = dw / 1.3333333333333333d;
                    break;
                } else {
                    dw = dh * 1.3333333333333333d;
                    break;
                }
            case 6:
                dh = this.mVideoVisibleHeight;
                dw = vw;
                break;
        }
        SurfaceView surface = this.owner.getSurfaceView();
        SurfaceView subtitlesSurface = this.owner.getSubtitleSurfaceView();
        FrameLayout surfaceFrame = this.owner.getSurfaceFrame();
        ViewGroup.LayoutParams lp = surface.getLayoutParams();
        lp.width = (int) Math.ceil((this.mVideoWidth * dw) / this.mVideoVisibleWidth);
        lp.height = (int) Math.ceil((this.mVideoHeight * dh) / this.mVideoVisibleHeight);
        surface.setLayoutParams(lp);
        subtitlesSurface.setLayoutParams(lp);
        ViewGroup.LayoutParams lp2 = surfaceFrame.getLayoutParams();
        lp2.width = (int) Math.floor(dw);
        lp2.height = (int) Math.floor(dh);
        surfaceFrame.setLayoutParams(lp2);
        surface.invalidate();
        subtitlesSurface.invalidate();
    }

    public void changeScreenLock() {
        if (this.mIsLocked) {
            this.mIsLocked = false;
            if (this.owner.getScreenOrientation() == 4) {
                this.owner.setRequestedOrientation(4);
            }
            this.owner.showInfo("Unlocked", 1000);
            this.mLockIcon.setBackgroundResource(R.drawable.ic_lock);
            return;
        }
        this.mIsLocked = true;
        if (this.owner.getScreenOrientation() == 4) {
            if (Build.VERSION.SDK_INT >= 18) {
                this.owner.setRequestedOrientation(14);
            } else {
                this.owner.setRequestedOrientation(ScreenUtil.getScreenOrientation(this.owner));
            }
            this.owner.showInfo("Locked", 1000);
        }
        this.mLockIcon.setBackgroundResource(R.drawable.ic_locked);
    }

    public void setTitle(String title) {
        this.mTitleView.setText(title);
    }

    public void setBatteryLevel(int batteryLevel) {
        if (batteryLevel >= 50) {
            this.mBatteryyView.setTextColor(-16711936);
        } else if (batteryLevel >= 30) {
            this.mBatteryyView.setTextColor(-256);
        } else {
            this.mBatteryyView.setTextColor(SupportMenu.CATEGORY_MASK);
        }
        this.mBatteryyView.setText(String.format("%d%%", Integer.valueOf(batteryLevel)));
    }

    public void updateOverlayPausePlay() {
        this.mPlayIcon.setBackgroundResource(this.owner.isPlaying() ? R.drawable.ic_pause_circle_big_o : R.drawable.ic_play_circle_big_o);
    }

    public void show() {
        if (!this.mShowing) {
            Animation animation1 = AnimationUtils.loadAnimation(this.owner, android.R.anim.fade_in);
            animation1.setFillAfter(true);
            setAllViewVisibility(this.mOverlayHead, 0);
            setAllViewVisibility(this.mOverlayOption, 0);
            setAllViewVisibility(this.mOverlayBottom, 0);
            this.mOverlayHead.startAnimation(animation1);
            this.mOverlayOption.startAnimation(animation1);
            this.mOverlayBottom.startAnimation(animation1);
            this.mSystime.setText(DateFormat.getTimeFormat(this.owner).format(new Date(System.currentTimeMillis())));
            this.mShowing = true;
        }
    }

    public void hide() {
        Animation animation1 = AnimationUtils.loadAnimation(this.owner, android.R.anim.fade_out);
        animation1.setFillAfter(true);
        this.mOverlayHead.startAnimation(animation1);
        this.mOverlayOption.startAnimation(animation1);
        this.mOverlayBottom.startAnimation(animation1);
        setAllViewVisibility(this.mOverlayHead, 8);
        setAllViewVisibility(this.mOverlayOption, 8);
        setAllViewVisibility(this.mOverlayBottom, 8);
        this.mShowing = false;
    }

    public boolean isShowing() {
        return this.mShowing;
    }

    public void setRecordVisible(boolean bVisible) {
        if (bVisible) {
            this.mRecIcon.setVisibility(0);
        } else {
            this.mRecIcon.setVisibility(8);
        }
    }

    public void showSettingDialog() {
        String text;
        AlertDialog.Builder builder = new AlertDialog.Builder(this.owner);
        View view = LayoutInflater.from(this.owner).inflate(R.layout.player_setting_layout, (ViewGroup) null);
        builder.setView(view);
        final RadioGroup mResolutionGroupView = getRadioGroupView(view);
        for (int i = 0; i < mResolutionGroupView.getChildCount(); i++) {
            View v = mResolutionGroupView.getChildAt(i);
            if (v instanceof RadioButton) {
                int iTag = Integer.valueOf((String) v.getTag()).intValue();
                ((RadioButton) v).setText(getResolutions()[iTag]);
                if (iTag == TranscodeConstants.iCurResolution) {
                    mResolutionGroupView.check(v.getId());
                }
                v.setTag(Integer.valueOf(iTag));
            }
        }
        final RadioGroup mBitrateGroupView = getBitrateGroupView(view);
        int[] bitrates = getBitrates();
        int platform = GMScreenGlobalInfo.getCurStbPlatform();
        for (int i2 = 0; i2 < mBitrateGroupView.getChildCount(); i2++) {
            View v2 = mBitrateGroupView.getChildAt(i2);
            if (v2 instanceof RadioButton) {
                int iTag2 = Integer.valueOf((String) v2.getTag()).intValue();
                if (platform != 32) {
                    if (bitrates[i2] < 1024) {
                        text = String.valueOf(bitrates[i2]) + "K";
                    } else {
                        text = String.valueOf(bitrates[i2] / 1024) + "M";
                    }
                } else {
                    text = new StringBuilder(String.valueOf(bitrates[i2])).toString();
                }
                ((RadioButton) v2).setText(new StringBuilder(String.valueOf(text)).toString());
                if (iTag2 == TranscodeConstants.iCurBitrate) {
                    mBitrateGroupView.check(v2.getId());
                }
                v2.setTag(Integer.valueOf(iTag2));
            }
        }
        builder.setNegativeButton(R.string.cancel_str, new DialogInterface.OnClickListener() { // from class: mktvsmart.screen.vlc.PlayViewControllor.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.ok_str, new DialogInterface.OnClickListener() { // from class: mktvsmart.screen.vlc.PlayViewControllor.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                int viewID = mResolutionGroupView.getCheckedRadioButtonId();
                int iResolution = ((Integer) mResolutionGroupView.findViewById(viewID).getTag()).intValue();
                int viewID2 = mBitrateGroupView.getCheckedRadioButtonId();
                int iBitrate = ((Integer) mBitrateGroupView.findViewById(viewID2).getTag()).intValue();
                if (iResolution != TranscodeConstants.iCurResolution || iBitrate != TranscodeConstants.iCurBitrate) {
                    TranscodeConstants.iCurResolution = iResolution;
                    TranscodeConstants.iCurBitrate = iBitrate;
                    if (PlayViewControllor.this.settingLisnner != null) {
                        PlayViewControllor.this.settingLisnner.OnTranscodeChange(iResolution, iBitrate);
                    }
                }
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String[] getResolutions() {
        int platform = GMScreenGlobalInfo.getCurStbPlatform();
        return platform != 32 ? TranscodeConstants.asResolution_hisi : TranscodeConstants.asResolution_7588;
    }

    private int[] getBitrates() {
        int platform = GMScreenGlobalInfo.getCurStbPlatform();
        return platform != 32 ? TranscodeConstants.aiBitrate_hisi : TranscodeConstants.aiBitrate_7588;
    }

    private RadioGroup getRadioGroupView(View parent) {
        int platform = GMScreenGlobalInfo.getCurStbPlatform();
        RadioGroup rg = (RadioGroup) parent.findViewById(R.id.resolution_group);
        if (platform == 32) {
            rg.removeViewAt(rg.getChildCount() - 1);
        }
        return rg;
    }

    private RadioGroup getBitrateGroupView(View parent) {
        int platform = GMScreenGlobalInfo.getCurStbPlatform();
        RadioGroup rg = (RadioGroup) parent.findViewById(R.id.bitrate_group);
        if (platform != 32) {
            rg.removeViewAt(rg.getChildCount() - 1);
        }
        return rg;
    }

    public void setOnTranscodeListenner(TranscodeListenner listenner) {
        this.settingLisnner = listenner;
    }

    @Override // org.videolan.libvlc.IVideoPlayer
    public int configureSurface(Surface arg0, int arg1, int arg2, int arg3) {
        return 0;
    }

    @Override // org.videolan.libvlc.IVideoPlayer
    public void eventHardwareAccelerationError() {
    }

    @Override // org.videolan.libvlc.IVideoPlayer
    public void setSurfaceLayout(int width, int height, int visible_width, int visible_height, int sar_num, int sar_den) {
        this.mVideoHeight = height;
        this.mVideoWidth = width;
        this.mVideoVisibleHeight = visible_height;
        this.mVideoVisibleWidth = visible_width;
        this.mSarNum = sar_num;
        this.mSarDen = sar_den;
    }
}
