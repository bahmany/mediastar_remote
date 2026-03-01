package mktvsmart.screen.view;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GesturePoint;
import android.gesture.GestureUtils;
import android.gesture.OrientedBoundingBox;
import android.gesture.Prediction;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import mktvsmart.screen.R;

/* loaded from: classes.dex */
public class RemoteControlTouchPad extends GestureOverlayView implements GestureOverlayView.OnGesturePerformedListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    private static final int DOWN_SCROLL = 3;
    private static final float FLING_ANGLE_MAX_DEVIATION = 30.0f;
    private static final int FLING_MIN_DISTANCE = 60;
    private static final int FLING_MIN_VELOCITY = 120;
    private static final int LEFT_SCROLL = 4;
    private static final float ORITATION_DELTA = 10.0f;
    private static final int RIGHT_SCROLL = 2;
    private static final float SCROLL_MAX_ANGLE = 15.0f;
    private static final int SCROLL_MIN_DISTANCE = 180;
    private static final int SCROLL_TIMEOUT = 300;
    private static final String SUPPORT_GESTURE_EXIT = "exit";
    private static final String SUPPORT_GESTURE_MENU = "menu";
    private static final double TOUCH_PAD_PRECISION_THRESHOLD = 5.0d;
    private static final int UP_SCROLL = 1;
    private final int MOVE_THRESHOLD;
    private GestureDetector mDetector;
    private OnDoublePressListener mDoublePressListener;
    private OnFlingDownListener mFlingDownListener;
    private OnFlingLeftListener mFlingLeftListener;
    private OnFlingRightListener mFlingRightListener;
    private OnFlingUpListener mFlingUpListener;
    private OnGestureExitListener mGestureExitListener;
    private OnGestureMenuListener mGestureMenuListener;
    private final float mGestureStrokeAngleThreshold;
    private GestureLibrary mLibrary;
    private OnPressDownListener mPressDownListener;
    private OnPressUpListener mPressUpListener;
    private float mPreviousX;
    private float mPreviousY;
    private OnScrollCancelListener mScrollCancelListener;
    private OnScrollDownListener mScrollDownListener;
    private OnScrollLeftListener mScrollLeftListener;
    private OnScrollRightListener mScrollRightListener;
    private OnScrollUpListener mScrollUpListener;
    private boolean mStartedScroll;
    private float mThisX;
    private float mThisY;
    private Handler mTouchPadHandler;

    public interface OnDoublePressListener {
        void onDoublePress(View view);
    }

    public interface OnFlingDownListener {
        void onFlingDown(View view);
    }

    public interface OnFlingLeftListener {
        void onFlingLeft(View view);
    }

    public interface OnFlingRightListener {
        void onFlingRight(View view);
    }

    public interface OnFlingUpListener {
        void onFlingUp(View view);
    }

    public interface OnGestureExitListener {
        void onGestureExit(View view);
    }

    public interface OnGestureMenuListener {
        void onGestureMenu(View view);
    }

    public interface OnPressDownListener {
        void onPressDown(View view);
    }

    public interface OnPressUpListener {
        void onPressUp(View view);
    }

    public interface OnScrollCancelListener {
        void onScrollCancel(View view);
    }

    public interface OnScrollDownListener {
        void onScrollDown(View view);
    }

    public interface OnScrollLeftListener {
        void onScrollLeft(View view);
    }

    public interface OnScrollRightListener {
        void onScrollRight(View view);
    }

    public interface OnScrollUpListener {
        void onScrollUp(View view);
    }

    public RemoteControlTouchPad(Context context) {
        super(context);
        this.MOVE_THRESHOLD = 30;
        this.mPressDownListener = null;
        this.mPressUpListener = null;
        this.mFlingUpListener = null;
        this.mFlingRightListener = null;
        this.mFlingDownListener = null;
        this.mFlingLeftListener = null;
        this.mScrollUpListener = null;
        this.mScrollRightListener = null;
        this.mScrollDownListener = null;
        this.mScrollLeftListener = null;
        this.mScrollCancelListener = null;
        this.mDoublePressListener = null;
        this.mGestureMenuListener = null;
        this.mGestureExitListener = null;
        this.mStartedScroll = false;
        this.mGestureStrokeAngleThreshold = getGestureStrokeAngleThreshold();
        this.mTouchPadHandler = new Handler() { // from class: mktvsmart.screen.view.RemoteControlTouchPad.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                RemoteControlTouchPad.this.mStartedScroll = true;
                switch (msg.what) {
                    case 1:
                        if (RemoteControlTouchPad.this.mScrollUpListener != null) {
                            RemoteControlTouchPad.this.mScrollUpListener.onScrollUp(RemoteControlTouchPad.this);
                            break;
                        }
                        break;
                    case 2:
                        if (RemoteControlTouchPad.this.mScrollRightListener != null) {
                            RemoteControlTouchPad.this.mScrollRightListener.onScrollRight(RemoteControlTouchPad.this);
                            break;
                        }
                        break;
                    case 3:
                        if (RemoteControlTouchPad.this.mScrollDownListener != null) {
                            RemoteControlTouchPad.this.mScrollDownListener.onScrollDown(RemoteControlTouchPad.this);
                            break;
                        }
                        break;
                    case 4:
                        if (RemoteControlTouchPad.this.mScrollLeftListener != null) {
                            RemoteControlTouchPad.this.mScrollLeftListener.onScrollLeft(RemoteControlTouchPad.this);
                            break;
                        }
                        break;
                }
            }
        };
        init(context);
    }

    public RemoteControlTouchPad(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.MOVE_THRESHOLD = 30;
        this.mPressDownListener = null;
        this.mPressUpListener = null;
        this.mFlingUpListener = null;
        this.mFlingRightListener = null;
        this.mFlingDownListener = null;
        this.mFlingLeftListener = null;
        this.mScrollUpListener = null;
        this.mScrollRightListener = null;
        this.mScrollDownListener = null;
        this.mScrollLeftListener = null;
        this.mScrollCancelListener = null;
        this.mDoublePressListener = null;
        this.mGestureMenuListener = null;
        this.mGestureExitListener = null;
        this.mStartedScroll = false;
        this.mGestureStrokeAngleThreshold = getGestureStrokeAngleThreshold();
        this.mTouchPadHandler = new Handler() { // from class: mktvsmart.screen.view.RemoteControlTouchPad.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                RemoteControlTouchPad.this.mStartedScroll = true;
                switch (msg.what) {
                    case 1:
                        if (RemoteControlTouchPad.this.mScrollUpListener != null) {
                            RemoteControlTouchPad.this.mScrollUpListener.onScrollUp(RemoteControlTouchPad.this);
                            break;
                        }
                        break;
                    case 2:
                        if (RemoteControlTouchPad.this.mScrollRightListener != null) {
                            RemoteControlTouchPad.this.mScrollRightListener.onScrollRight(RemoteControlTouchPad.this);
                            break;
                        }
                        break;
                    case 3:
                        if (RemoteControlTouchPad.this.mScrollDownListener != null) {
                            RemoteControlTouchPad.this.mScrollDownListener.onScrollDown(RemoteControlTouchPad.this);
                            break;
                        }
                        break;
                    case 4:
                        if (RemoteControlTouchPad.this.mScrollLeftListener != null) {
                            RemoteControlTouchPad.this.mScrollLeftListener.onScrollLeft(RemoteControlTouchPad.this);
                            break;
                        }
                        break;
                }
            }
        };
        init(context);
    }

    public RemoteControlTouchPad(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.MOVE_THRESHOLD = 30;
        this.mPressDownListener = null;
        this.mPressUpListener = null;
        this.mFlingUpListener = null;
        this.mFlingRightListener = null;
        this.mFlingDownListener = null;
        this.mFlingLeftListener = null;
        this.mScrollUpListener = null;
        this.mScrollRightListener = null;
        this.mScrollDownListener = null;
        this.mScrollLeftListener = null;
        this.mScrollCancelListener = null;
        this.mDoublePressListener = null;
        this.mGestureMenuListener = null;
        this.mGestureExitListener = null;
        this.mStartedScroll = false;
        this.mGestureStrokeAngleThreshold = getGestureStrokeAngleThreshold();
        this.mTouchPadHandler = new Handler() { // from class: mktvsmart.screen.view.RemoteControlTouchPad.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                RemoteControlTouchPad.this.mStartedScroll = true;
                switch (msg.what) {
                    case 1:
                        if (RemoteControlTouchPad.this.mScrollUpListener != null) {
                            RemoteControlTouchPad.this.mScrollUpListener.onScrollUp(RemoteControlTouchPad.this);
                            break;
                        }
                        break;
                    case 2:
                        if (RemoteControlTouchPad.this.mScrollRightListener != null) {
                            RemoteControlTouchPad.this.mScrollRightListener.onScrollRight(RemoteControlTouchPad.this);
                            break;
                        }
                        break;
                    case 3:
                        if (RemoteControlTouchPad.this.mScrollDownListener != null) {
                            RemoteControlTouchPad.this.mScrollDownListener.onScrollDown(RemoteControlTouchPad.this);
                            break;
                        }
                        break;
                    case 4:
                        if (RemoteControlTouchPad.this.mScrollLeftListener != null) {
                            RemoteControlTouchPad.this.mScrollLeftListener.onScrollLeft(RemoteControlTouchPad.this);
                            break;
                        }
                        break;
                }
            }
        };
        init(context);
    }

    private void init(Context context) {
        addOnGesturePerformedListener(this);
        setGestureColor(getResources().getColor(R.color.touch_pad_gesture_color));
        setUncertainGestureColor(getResources().getColor(R.color.touch_pad_gesture_color));
        this.mDetector = new GestureDetector(context, this);
        this.mDetector.setOnDoubleTapListener(this);
        this.mDetector.setIsLongpressEnabled(false);
    }

    @Override // android.gesture.GestureOverlayView.OnGesturePerformedListener
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = null;
        if (this.mLibrary != null) {
            predictions = this.mLibrary.recognize(gesture);
        }
        if (predictions != null && predictions.size() > 0) {
            Prediction prediction = predictions.get(0);
            if (prediction.score > TOUCH_PAD_PRECISION_THRESHOLD) {
                if (prediction.name.equals(SUPPORT_GESTURE_MENU)) {
                    if (this.mGestureMenuListener != null) {
                        this.mGestureMenuListener.onGestureMenu(this);
                    }
                } else if (prediction.name.equals(SUPPORT_GESTURE_EXIT) && this.mGestureExitListener != null) {
                    this.mGestureExitListener.onGestureExit(this);
                }
            }
        }
        if (this.mPressUpListener != null) {
            this.mPressUpListener.onPressUp(this);
        }
        this.mStartedScroll = false;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case 1:
                if (this.mPressUpListener != null) {
                    this.mPressUpListener.onPressUp(this);
                }
                removeAllMessages();
                this.mStartedScroll = false;
                break;
            case 2:
                this.mThisX = event.getX();
                this.mThisY = event.getY();
                OrientedBoundingBox box = GestureUtils.computeOrientedBoundingBox(getCurrentStroke());
                float angle = Math.abs(box.orientation);
                if (angle > 90.0f) {
                    angle = 180.0f - angle;
                }
                if ((angle > -10.0f && angle < ORITATION_DELTA) || Double.isNaN(angle)) {
                    setEventsInterceptionEnabled(false);
                    setGestureStrokeAngleThreshold(0.0f);
                } else {
                    setEventsInterceptionEnabled(true);
                    setGestureStrokeAngleThreshold(this.mGestureStrokeAngleThreshold);
                }
                if (this.mStartedScroll && isMoved(this.mPreviousX, this.mPreviousY, this.mThisX, this.mThisY) && this.mScrollCancelListener != null) {
                    this.mScrollCancelListener.onScrollCancel(this);
                    break;
                }
                break;
            case 3:
                removeAllMessages();
                break;
        }
        return this.mDetector.onTouchEvent(event);
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onDown(MotionEvent e) {
        if (this.mPressDownListener != null) {
            this.mPressDownListener.onPressDown(this);
            return false;
        }
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onShowPress(MotionEvent e) {
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override // android.view.GestureDetector.OnDoubleTapListener
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override // android.view.GestureDetector.OnDoubleTapListener
    public boolean onDoubleTap(MotionEvent e) {
        if (this.mDoublePressListener != null) {
            this.mDoublePressListener.onDoublePress(this);
            return false;
        }
        return false;
    }

    @Override // android.view.GestureDetector.OnDoubleTapListener
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        OrientedBoundingBox box = GestureUtils.computeOrientedBoundingBox(getCurrentStroke());
        float angle = Math.abs(box.orientation);
        if (!this.mStartedScroll) {
            if (e1.getX() - e2.getX() > 180.0f && angle < SCROLL_MAX_ANGLE) {
                this.mTouchPadHandler.removeMessages(4);
                this.mTouchPadHandler.sendEmptyMessageDelayed(4, 300L);
            } else if (e2.getX() - e1.getX() > 180.0f && angle < SCROLL_MAX_ANGLE) {
                this.mTouchPadHandler.removeMessages(2);
                this.mTouchPadHandler.sendEmptyMessageDelayed(2, 300L);
            } else if (e1.getY() - e2.getY() > 180.0f && 90.0f - angle < SCROLL_MAX_ANGLE) {
                this.mTouchPadHandler.removeMessages(1);
                this.mTouchPadHandler.sendEmptyMessageDelayed(1, 300L);
            } else if (e2.getY() - e1.getY() > 180.0f && 90.0f - angle < SCROLL_MAX_ANGLE) {
                this.mTouchPadHandler.removeMessages(3);
                this.mTouchPadHandler.sendEmptyMessageDelayed(3, 300L);
            }
            this.mPreviousX = e2.getX();
            this.mPreviousY = e2.getY();
            return false;
        }
        removeAllMessages();
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onLongPress(MotionEvent e) {
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        GesturePoint point1 = new GesturePoint(e1.getX(), e1.getY(), e1.getEventTime());
        GesturePoint point2 = new GesturePoint(e2.getX(), e2.getY(), e2.getEventTime());
        ArrayList<GesturePoint> array = new ArrayList<>();
        array.add(point1);
        array.add(point2);
        OrientedBoundingBox box = GestureUtils.computeOrientedBoundingBox(array);
        float angle = Math.abs(box.orientation);
        if (e1.getX() - e2.getX() > 60.0f && angle < 30.0f && Math.abs(velocityX) > 120.0f) {
            if (this.mFlingLeftListener != null) {
                this.mFlingLeftListener.onFlingLeft(this);
                return false;
            }
            return false;
        }
        if (e2.getX() - e1.getX() > 60.0f && angle < 30.0f && Math.abs(velocityX) > 120.0f) {
            if (this.mFlingRightListener != null) {
                this.mFlingRightListener.onFlingRight(this);
                return false;
            }
            return false;
        }
        if (e1.getY() - e2.getY() > 60.0f && 90.0f - angle < 30.0f && Math.abs(velocityY) > 120.0f) {
            if (this.mFlingUpListener != null) {
                this.mFlingUpListener.onFlingUp(this);
                return false;
            }
            return false;
        }
        if (e2.getY() - e1.getY() > 60.0f && 90.0f - angle < 30.0f && Math.abs(velocityY) > 120.0f && this.mFlingDownListener != null) {
            this.mFlingDownListener.onFlingDown(this);
            return false;
        }
        return false;
    }

    public OnPressDownListener getOnPressDownListener() {
        return this.mPressDownListener;
    }

    public void setOnPressDownListener(OnPressDownListener l) {
        this.mPressDownListener = l;
    }

    public OnPressUpListener getOnPressUpListener() {
        return this.mPressUpListener;
    }

    public void setOnPressUpListener(OnPressUpListener l) {
        this.mPressUpListener = l;
    }

    public OnDoublePressListener getOnDoublePressListener() {
        return this.mDoublePressListener;
    }

    public void setOnDoublePressListener(OnDoublePressListener l) {
        this.mDoublePressListener = l;
    }

    public OnFlingUpListener getOnFlingUpListener() {
        return this.mFlingUpListener;
    }

    public void setOnFlingUpListener(OnFlingUpListener l) {
        this.mFlingUpListener = l;
    }

    public OnFlingRightListener getOnFlingRightListener() {
        return this.mFlingRightListener;
    }

    public void setOnFlingRightListener(OnFlingRightListener l) {
        this.mFlingRightListener = l;
    }

    public OnFlingDownListener getOnFlingDownListener() {
        return this.mFlingDownListener;
    }

    public void setOnFlingDownListener(OnFlingDownListener l) {
        this.mFlingDownListener = l;
    }

    public OnFlingLeftListener getOnFlingLeftListener() {
        return this.mFlingLeftListener;
    }

    public void setOnFlingLeftListener(OnFlingLeftListener l) {
        this.mFlingLeftListener = l;
    }

    public OnScrollUpListener getOnScrollUpListener() {
        return this.mScrollUpListener;
    }

    public void setOnScrollUpListener(OnScrollUpListener l) {
        this.mScrollUpListener = l;
    }

    public OnScrollRightListener getOnScrollRightListener() {
        return this.mScrollRightListener;
    }

    public void setOnScrollRightListener(OnScrollRightListener l) {
        this.mScrollRightListener = l;
    }

    public OnScrollDownListener getOnScrollDownListener() {
        return this.mScrollDownListener;
    }

    public void setOnScrollDownListener(OnScrollDownListener l) {
        this.mScrollDownListener = l;
    }

    public OnScrollLeftListener getOnScrollLeftListener() {
        return this.mScrollLeftListener;
    }

    public void setOnScrollLeftListener(OnScrollLeftListener l) {
        this.mScrollLeftListener = l;
    }

    public OnScrollCancelListener getOnScrollCancelListener() {
        return this.mScrollCancelListener;
    }

    public void setOnScrollCancelListener(OnScrollCancelListener l) {
        this.mScrollCancelListener = l;
    }

    public OnGestureMenuListener getOnGestureMenuListener() {
        return this.mGestureMenuListener;
    }

    public void setOnGestureMenuListener(OnGestureMenuListener l) {
        this.mGestureMenuListener = l;
    }

    public OnGestureExitListener getOnGestureExitListener() {
        return this.mGestureExitListener;
    }

    public void setOnGestureExitListener(OnGestureExitListener l) {
        this.mGestureExitListener = l;
    }

    public void setGestureLibrary(GestureLibrary library) {
        this.mLibrary = library;
        if (this.mLibrary != null && !this.mLibrary.load()) {
            throw new NullPointerException();
        }
    }

    private boolean isMoved(float lastX, float lastY, float thisX, float thisY) {
        float offsetX = Math.abs(thisX - lastX);
        float offsetY = Math.abs(thisY - lastY);
        return offsetX > 30.0f || offsetY > 30.0f;
    }

    private void removeAllMessages() {
        this.mTouchPadHandler.removeMessages(1);
        this.mTouchPadHandler.removeMessages(2);
        this.mTouchPadHandler.removeMessages(3);
        this.mTouchPadHandler.removeMessages(4);
    }
}
