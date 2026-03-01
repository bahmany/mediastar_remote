package mktvsmart.screen.vlc;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.internal.view.SupportMenu;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.Date;
import mktvsmart.screen.R;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.vlc.util.Strings;

/* compiled from: LocalPlayActivity.java */
/* loaded from: classes.dex */
class PlayController extends RelativeLayout implements IVideoPlayer {
    private static final int SURFACE_16_9 = 4;
    private static final int SURFACE_4_3 = 5;
    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FILL = 3;
    private static final int SURFACE_FIT_HORIZONTAL = 1;
    private static final int SURFACE_FIT_VERTICAL = 2;
    private static final int SURFACE_ORIGINAL = 6;
    private boolean bCanSeek;
    private ImageButton mBackIcon;
    private ImageButton mBackword;
    private TextView mBatteryyView;
    private int mCurrentSize;
    private boolean mDisplayRemainingTime;
    private boolean mDragging;
    private ImageButton mForword;
    private boolean mIsLocked;
    private TextView mLength;
    private LibVLC mLibVlc;
    private ImageButton mLockIcon;
    private View mOverlayBottom;
    private View mOverlayHead;
    private View mOverlayOption;
    private LocalPlayActivity mOwner;
    private ImageButton mPlayIcon;
    public int mSarDen;
    public int mSarNum;
    private final SeekBar.OnSeekBarChangeListener mSeekListener;
    private SeekBar mSeekbar;
    private boolean mShowing;
    private ImageButton mSizeIcon;
    private ImageButton mSnapShotIcon;
    private TextView mSystime;
    private TextView mTime;
    private TextView mTitleView;
    public int mVideoHeight;
    public int mVideoVisibleHeight;
    public int mVideoVisibleWidth;
    public int mVideoWidth;

    public PlayController(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mCurrentSize = 0;
        this.mIsLocked = false;
        this.mDisplayRemainingTime = false;
        this.bCanSeek = false;
        this.mSeekListener = new SeekBar.OnSeekBarChangeListener() { // from class: mktvsmart.screen.vlc.PlayController.1
            AnonymousClass1() {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
                PlayController.this.setDragging(true);
                PlayController.this.mOwner.showOverlay(3600000);
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
                PlayController.this.setDragging(false);
                PlayController.this.mOwner.showOverlay();
                PlayController.this.mOwner.hideInfo();
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && PlayController.this.canSeek()) {
                    PlayController.this.mLibVlc.setTime(progress);
                    PlayController.this.setOverlayProgress();
                    PlayController.this.mTime.setText(Strings.millisToString(progress));
                    PlayController.this.mOwner.showInfo(Strings.millisToString(progress));
                }
            }
        };
        initView();
    }

    public PlayController(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCurrentSize = 0;
        this.mIsLocked = false;
        this.mDisplayRemainingTime = false;
        this.bCanSeek = false;
        this.mSeekListener = new SeekBar.OnSeekBarChangeListener() { // from class: mktvsmart.screen.vlc.PlayController.1
            AnonymousClass1() {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
                PlayController.this.setDragging(true);
                PlayController.this.mOwner.showOverlay(3600000);
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
                PlayController.this.setDragging(false);
                PlayController.this.mOwner.showOverlay();
                PlayController.this.mOwner.hideInfo();
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && PlayController.this.canSeek()) {
                    PlayController.this.mLibVlc.setTime(progress);
                    PlayController.this.setOverlayProgress();
                    PlayController.this.mTime.setText(Strings.millisToString(progress));
                    PlayController.this.mOwner.showInfo(Strings.millisToString(progress));
                }
            }
        };
        initView();
    }

    public PlayController(Context context) {
        super(context);
        this.mCurrentSize = 0;
        this.mIsLocked = false;
        this.mDisplayRemainingTime = false;
        this.bCanSeek = false;
        this.mSeekListener = new SeekBar.OnSeekBarChangeListener() { // from class: mktvsmart.screen.vlc.PlayController.1
            AnonymousClass1() {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
                PlayController.this.setDragging(true);
                PlayController.this.mOwner.showOverlay(3600000);
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
                PlayController.this.setDragging(false);
                PlayController.this.mOwner.showOverlay();
                PlayController.this.mOwner.hideInfo();
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && PlayController.this.canSeek()) {
                    PlayController.this.mLibVlc.setTime(progress);
                    PlayController.this.setOverlayProgress();
                    PlayController.this.mTime.setText(Strings.millisToString(progress));
                    PlayController.this.mOwner.showInfo(Strings.millisToString(progress));
                }
            }
        };
        initView();
    }

    void initView() {
        inflate(getContext(), R.layout.local_play_control_view, this);
        this.mOverlayHead = findViewById(R.id.player_overlay_header);
        this.mTitleView = (TextView) findViewById(R.id.player_overlay_title);
        this.mBatteryyView = (TextView) findViewById(R.id.player_overlay_battery);
        this.mSystime = (TextView) findViewById(R.id.player_overlay_systime);
        this.mOverlayHead.setVisibility(8);
        this.mOverlayBottom = findViewById(R.id.bottom_View);
        this.mPlayIcon = (ImageButton) findViewById(R.id.control_icon);
        this.mLockIcon = (ImageButton) findViewById(R.id.lock_overlay_button);
        this.mOverlayBottom.setVisibility(8);
        this.mOverlayOption = findViewById(R.id.option_overlay);
        this.mBackIcon = (ImageButton) findViewById(R.id.player_overlay_back);
        this.mSizeIcon = (ImageButton) findViewById(R.id.player_overlay_size);
        this.mOverlayOption.setVisibility(8);
        this.mBackword = (ImageButton) findViewById(R.id.player_overlay_backward);
        this.mForword = (ImageButton) findViewById(R.id.player_overlay_forward);
        this.mSeekbar = (SeekBar) findViewById(R.id.player_overlay_seekbar);
        this.mTime = (TextView) findViewById(R.id.player_overlay_time);
        this.mLength = (TextView) findViewById(R.id.player_overlay_length);
        this.mSeekbar.setOnSeekBarChangeListener(this.mSeekListener);
        initListener();
    }

    public void initListener() {
        View.OnClickListener onclick = new View.OnClickListener() { // from class: mktvsmart.screen.vlc.PlayController.2
            AnonymousClass2() {
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.player_overlay_back /* 2131493269 */:
                        PlayController.this.mOwner.finish();
                        break;
                    case R.id.player_overlay_size /* 2131493273 */:
                        PlayController.this.changeSurfaceSizeManual();
                        break;
                    case R.id.control_icon /* 2131493275 */:
                        if (PlayController.this.mLibVlc.isPlaying()) {
                            PlayController.this.mLibVlc.pause();
                            break;
                        } else {
                            PlayController.this.mLibVlc.play();
                            break;
                        }
                    case R.id.screen_lock_icon /* 2131493276 */:
                        PlayController.this.changeScreenLock();
                        break;
                    case R.id.player_overlay_time /* 2131493281 */:
                    case R.id.player_overlay_length /* 2131493283 */:
                        PlayController.this.mDisplayRemainingTime = !PlayController.this.mDisplayRemainingTime;
                        break;
                    case R.id.player_overlay_backward /* 2131493284 */:
                        PlayController.this.seek(-10000);
                        break;
                    case R.id.player_overlay_forward /* 2131493285 */:
                        PlayController.this.seek(10000);
                        break;
                }
            }
        };
        this.mPlayIcon.setOnClickListener(onclick);
        this.mLockIcon.setOnClickListener(onclick);
        this.mBackIcon.setOnClickListener(onclick);
        this.mSizeIcon.setOnClickListener(onclick);
        this.mTime.setOnClickListener(onclick);
        this.mLength.setOnClickListener(onclick);
        this.mBackword.setOnClickListener(onclick);
        this.mForword.setOnClickListener(onclick);
    }

    /* compiled from: LocalPlayActivity.java */
    /* renamed from: mktvsmart.screen.vlc.PlayController$2 */
    class AnonymousClass2 implements View.OnClickListener {
        AnonymousClass2() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.player_overlay_back /* 2131493269 */:
                    PlayController.this.mOwner.finish();
                    break;
                case R.id.player_overlay_size /* 2131493273 */:
                    PlayController.this.changeSurfaceSizeManual();
                    break;
                case R.id.control_icon /* 2131493275 */:
                    if (PlayController.this.mLibVlc.isPlaying()) {
                        PlayController.this.mLibVlc.pause();
                        break;
                    } else {
                        PlayController.this.mLibVlc.play();
                        break;
                    }
                case R.id.screen_lock_icon /* 2131493276 */:
                    PlayController.this.changeScreenLock();
                    break;
                case R.id.player_overlay_time /* 2131493281 */:
                case R.id.player_overlay_length /* 2131493283 */:
                    PlayController.this.mDisplayRemainingTime = !PlayController.this.mDisplayRemainingTime;
                    break;
                case R.id.player_overlay_backward /* 2131493284 */:
                    PlayController.this.seek(-10000);
                    break;
                case R.id.player_overlay_forward /* 2131493285 */:
                    PlayController.this.seek(10000);
                    break;
            }
        }
    }

    public void setOwner(LocalPlayActivity activity) {
        this.mOwner = activity;
    }

    public void setLibVlc(LibVLC libVlc) {
        this.mLibVlc = libVlc;
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

    public void show() throws Resources.NotFoundException {
        if (!this.mShowing) {
            Animation animation1 = AnimationUtils.loadAnimation(this.mOwner, android.R.anim.fade_in);
            animation1.setFillAfter(true);
            setAllViewVisibility(this.mOverlayHead, 0);
            setAllViewVisibility(this.mOverlayOption, 0);
            setAllViewVisibility(this.mOverlayBottom, 0);
            setAllViewVisibility(this.mPlayIcon, 0);
            this.mOverlayHead.startAnimation(animation1);
            this.mOverlayOption.startAnimation(animation1);
            this.mOverlayBottom.startAnimation(animation1);
            this.mPlayIcon.startAnimation(animation1);
            this.mSystime.setText(DateFormat.getTimeFormat(this.mOwner).format(new Date(System.currentTimeMillis())));
            this.mShowing = true;
        }
    }

    public void hide() throws Resources.NotFoundException {
        Animation animation1 = AnimationUtils.loadAnimation(this.mOwner, android.R.anim.fade_out);
        animation1.setFillAfter(true);
        this.mOverlayHead.startAnimation(animation1);
        this.mOverlayOption.startAnimation(animation1);
        this.mOverlayBottom.startAnimation(animation1);
        this.mPlayIcon.startAnimation(animation1);
        setAllViewVisibility(this.mOverlayHead, 8);
        setAllViewVisibility(this.mOverlayOption, 8);
        setAllViewVisibility(this.mOverlayBottom, 8);
        setAllViewVisibility(this.mPlayIcon, 8);
        this.mShowing = false;
    }

    private void setAllViewVisibility(View target, int visibility) {
        if (target != null) {
            target.setVisibility(visibility);
            if (target instanceof ViewGroup) {
                int count = ((ViewGroup) target).getChildCount();
                for (int i = 0; i < count; i++) {
                    setAllViewVisibility(((ViewGroup) target).getChildAt(i), visibility);
                }
            }
        }
    }

    public void setTitle(String title) {
        this.mTitleView.setText(title);
    }

    public void updateOverlayPausePlay() {
        if (this.mLibVlc != null) {
            this.mPlayIcon.setBackgroundResource(this.mLibVlc.isPlaying() ? R.drawable.ic_pause_circle_big_o : R.drawable.ic_play_circle_big_o);
        }
    }

    public void changeScreenLock() {
        if (this.mIsLocked) {
            this.mIsLocked = false;
            if (this.mOwner.getScreenOrientation() == 4) {
                this.mOwner.setRequestedOrientation(4);
            }
            this.mOwner.showInfo("Unlocked", 1000);
            this.mLockIcon.setBackgroundResource(R.drawable.ic_lock);
            return;
        }
        this.mIsLocked = true;
        if (this.mOwner.getScreenOrientation() == 4) {
            if (Build.VERSION.SDK_INT >= 18) {
                this.mOwner.setRequestedOrientation(14);
            } else {
                this.mOwner.setRequestedOrientation(ScreenUtil.getScreenOrientation(this.mOwner));
            }
            this.mOwner.showInfo("Locked", 1000);
        }
        this.mLockIcon.setBackgroundResource(R.drawable.ic_locked);
    }

    public void changeSurfaceSizeManual() {
        if (this.mCurrentSize < 6) {
            this.mCurrentSize++;
        } else {
            this.mCurrentSize = 0;
        }
        changeSurfaceSize();
        switch (this.mCurrentSize) {
            case 0:
                this.mOwner.showInfo("Best fit", 1000);
                break;
            case 1:
                this.mOwner.showInfo("Fit horizontal", 1000);
                break;
            case 2:
                this.mOwner.showInfo("Fit vertical", 1000);
                break;
            case 3:
                this.mOwner.showInfo("Fill", 1000);
                break;
            case 4:
                this.mOwner.showInfo("16:9", 1000);
                break;
            case 5:
                this.mOwner.showInfo("4:3", 1000);
                break;
            case 6:
                this.mOwner.showInfo("Center", 1000);
                break;
        }
        this.mOwner.showOverlay();
    }

    public void changeSurfaceSize() {
        double vw;
        double ar;
        int sw = this.mOwner.getWindow().getDecorView().getWidth();
        int sh = this.mOwner.getWindow().getDecorView().getHeight();
        double dw = sw;
        double dh = sh;
        boolean isPortrait = this.mOwner.getResources().getConfiguration().orientation == 1;
        if ((sw > sh && isPortrait) || (sw < sh && !isPortrait)) {
            dw = sh;
            dh = sw;
        }
        if (dw * dh == 0.0d || this.mVideoWidth * this.mVideoHeight == 0) {
            Log.e("LocalPlayActivity", "Invalid surface size");
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
        SurfaceView surface = this.mOwner.getSurfaceView();
        SurfaceHolder surfaceHolder = this.mOwner.getSurfaceHolder();
        FrameLayout surfaceFrame = this.mOwner.getSurfaceFrame();
        surfaceHolder.setFixedSize(this.mVideoWidth, this.mVideoHeight);
        ViewGroup.LayoutParams lp = surface.getLayoutParams();
        lp.width = (int) Math.ceil((this.mVideoWidth * dw) / this.mVideoVisibleWidth);
        lp.height = (int) Math.ceil((this.mVideoHeight * dh) / this.mVideoVisibleHeight);
        surface.setLayoutParams(lp);
        ViewGroup.LayoutParams lp2 = surfaceFrame.getLayoutParams();
        lp2.width = (int) Math.floor(dw);
        lp2.height = (int) Math.floor(dh);
        surfaceFrame.setLayoutParams(lp2);
        surface.invalidate();
    }

    public int setOverlayProgress() {
        if (this.mLibVlc == null) {
            return 0;
        }
        int time = (int) this.mLibVlc.getTime();
        int length = (int) this.mLibVlc.getLength();
        boolean isSeekable = length > 0;
        this.mBackword.setVisibility(isSeekable ? 0 : 8);
        this.mForword.setVisibility(isSeekable ? 0 : 8);
        this.mSeekbar.setMax(length);
        this.mSeekbar.setProgress(time);
        if (time >= 0) {
            this.mTime.setText(Strings.millisToString(time));
        }
        if (length >= 0) {
            this.mLength.setText((!this.mDisplayRemainingTime || length <= 0) ? Strings.millisToString(length) : "- " + Strings.millisToString(length - time));
        }
        return time;
    }

    public boolean isShowing() {
        return this.mShowing;
    }

    /* compiled from: LocalPlayActivity.java */
    /* renamed from: mktvsmart.screen.vlc.PlayController$1 */
    class AnonymousClass1 implements SeekBar.OnSeekBarChangeListener {
        AnonymousClass1() {
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(SeekBar seekBar) {
            PlayController.this.setDragging(true);
            PlayController.this.mOwner.showOverlay(3600000);
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(SeekBar seekBar) {
            PlayController.this.setDragging(false);
            PlayController.this.mOwner.showOverlay();
            PlayController.this.mOwner.hideInfo();
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser && PlayController.this.canSeek()) {
                PlayController.this.mLibVlc.setTime(progress);
                PlayController.this.setOverlayProgress();
                PlayController.this.mTime.setText(Strings.millisToString(progress));
                PlayController.this.mOwner.showInfo(Strings.millisToString(progress));
            }
        }
    }

    public void seek(int delta) {
        if (this.mLibVlc.getLength() > 0 && canSeek()) {
            long position = this.mLibVlc.getTime() + delta;
            if (position < 0) {
                position = 0;
            }
            this.mLibVlc.setTime(position);
            this.mOwner.showOverlay();
        }
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

    public boolean canSeek() {
        return this.bCanSeek;
    }

    public void setCanSeek(boolean bCanSeek) {
        this.bCanSeek = bCanSeek;
    }

    public boolean isDragging() {
        return this.mDragging;
    }

    public void setDragging(boolean mDragging) {
        this.mDragging = mDragging;
    }

    @Override // org.videolan.libvlc.IVideoPlayer
    public int configureSurface(Surface arg0, int arg1, int arg2, int arg3) {
        return 0;
    }

    @Override // org.videolan.libvlc.IVideoPlayer
    public void eventHardwareAccelerationError() {
    }
}
