package mktvsmart.screen.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import mktvsmart.screen.view.IrregularButton;

/* loaded from: classes.dex */
public final class OrientationButton extends IrregularButton {
    private final int LONG_PRESS_TIMEOUT;
    private final int MOVE_THRESHOLD;
    private final int PERFORM_LONG_CKICK;
    private float mDownX;
    private float mDownY;
    private Handler mHandler;
    private boolean mIsLongPressable;
    private boolean mIsLongPressed;
    private float mLastX;
    private float mLastY;
    private Runnable mLongPressRunnable;
    private IrregularButton.OnActionDownListener mOnActionDownListener;
    private IrregularButton.OnActionMoveListener mOnActionMoveListener;
    private IrregularButton.OnActionUpListener mOnActionUpListener;
    private OnLongPressListener mOnLongPressListener;
    private float mThisX;
    private float mThisY;
    private IrregularButton.TouchChecker mTouchChecker;

    public interface OnLongPressListener {
        void onLongPress(View view, int i);
    }

    public OrientationButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.PERFORM_LONG_CKICK = 1;
        this.LONG_PRESS_TIMEOUT = 1000;
        this.MOVE_THRESHOLD = 30;
        this.mDownX = -1.0f;
        this.mDownY = -1.0f;
        this.mLastX = -1.0f;
        this.mLastY = -1.0f;
        this.mThisX = -1.0f;
        this.mThisY = -1.0f;
        this.mLongPressRunnable = null;
        this.mHandler = new Handler() { // from class: mktvsmart.screen.view.OrientationButton.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        OrientationButton.this.mIsLongPressed = true;
                        if (OrientationButton.this.mOnLongPressListener != null) {
                            OrientationButton.this.mOnLongPressListener.onLongPress(OrientationButton.this, OrientationButton.this.parseTouchOrientation((int) OrientationButton.this.mDownX, (int) OrientationButton.this.mDownY));
                            break;
                        }
                        break;
                }
            }
        };
    }

    public OrientationButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.PERFORM_LONG_CKICK = 1;
        this.LONG_PRESS_TIMEOUT = 1000;
        this.MOVE_THRESHOLD = 30;
        this.mDownX = -1.0f;
        this.mDownY = -1.0f;
        this.mLastX = -1.0f;
        this.mLastY = -1.0f;
        this.mThisX = -1.0f;
        this.mThisY = -1.0f;
        this.mLongPressRunnable = null;
        this.mHandler = new Handler() { // from class: mktvsmart.screen.view.OrientationButton.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        OrientationButton.this.mIsLongPressed = true;
                        if (OrientationButton.this.mOnLongPressListener != null) {
                            OrientationButton.this.mOnLongPressListener.onLongPress(OrientationButton.this, OrientationButton.this.parseTouchOrientation((int) OrientationButton.this.mDownX, (int) OrientationButton.this.mDownY));
                            break;
                        }
                        break;
                }
            }
        };
    }

    public OrientationButton(Context context) {
        super(context);
        this.PERFORM_LONG_CKICK = 1;
        this.LONG_PRESS_TIMEOUT = 1000;
        this.MOVE_THRESHOLD = 30;
        this.mDownX = -1.0f;
        this.mDownY = -1.0f;
        this.mLastX = -1.0f;
        this.mLastY = -1.0f;
        this.mThisX = -1.0f;
        this.mThisY = -1.0f;
        this.mLongPressRunnable = null;
        this.mHandler = new Handler() { // from class: mktvsmart.screen.view.OrientationButton.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        OrientationButton.this.mIsLongPressed = true;
                        if (OrientationButton.this.mOnLongPressListener != null) {
                            OrientationButton.this.mOnLongPressListener.onLongPress(OrientationButton.this, OrientationButton.this.parseTouchOrientation((int) OrientationButton.this.mDownX, (int) OrientationButton.this.mDownY));
                            break;
                        }
                        break;
                }
            }
        };
    }

    public void setOnLongPressListener(OnLongPressListener l) {
        this.mOnLongPressListener = l;
    }

    public boolean isLongPressed() {
        return this.mIsLongPressed;
    }

    public boolean isLongPressable() {
        return this.mIsLongPressable;
    }

    public void setLongPressable(boolean flag) {
        this.mIsLongPressable = flag;
        if (!this.mIsLongPressable && this.mLongPressRunnable != null) {
            removeCallbacks(this.mLongPressRunnable);
        }
    }

    @Override // mktvsmart.screen.view.IrregularButton, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        this.mTouchChecker = getTouchChecker();
        this.mOnActionDownListener = getOnActionDownListener();
        this.mOnActionUpListener = getOnActionUpListener();
        this.mOnActionMoveListener = getOnActionMoveListener();
        switch (action) {
            case 0:
                this.mIsLongPressed = false;
                this.mDownX = event.getX();
                this.mDownY = event.getY();
                this.mLongPressRunnable = new Runnable() { // from class: mktvsmart.screen.view.OrientationButton.2
                    @Override // java.lang.Runnable
                    public void run() {
                        OrientationButton.this.mHandler.sendEmptyMessage(1);
                    }
                };
                postDelayed(this.mLongPressRunnable, 1000L);
                if (this.mTouchChecker != null && this.mTouchChecker.isInTouchArea((int) this.mDownX, (int) this.mDownY, getWidth(), getHeight())) {
                    if (this.mOnActionDownListener != null) {
                        this.mOnActionDownListener.onActionDown(this, parseTouchOrientation((int) this.mDownX, (int) this.mDownY));
                        break;
                    }
                }
                break;
            case 1:
                if (this.mOnActionUpListener != null) {
                    this.mOnActionUpListener.onAtionUp(this, parseTouchOrientation((int) event.getX(), (int) event.getY()));
                }
                removeCallbacks(this.mLongPressRunnable);
                this.mLongPressRunnable = null;
                break;
            case 2:
                this.mThisX = event.getX();
                this.mThisY = event.getY();
                if (isMoved(this.mDownX, this.mDownY, this.mThisX, this.mThisY)) {
                    removeCallbacks(this.mLongPressRunnable);
                    this.mLongPressRunnable = null;
                }
                if (this.mTouchChecker != null) {
                    if (this.mTouchChecker.isInTouchArea((int) this.mThisX, (int) this.mThisY, getWidth(), getHeight())) {
                        if (this.mOnActionMoveListener != null) {
                            this.mOnActionMoveListener.onActionMove(this, parseTouchOrientation((int) this.mThisX, (int) this.mThisY));
                        }
                    } else if (this.mOnActionMoveListener != null) {
                        this.mOnActionMoveListener.onActionMove(this, -1);
                    }
                }
                this.mLastX = this.mThisX;
                this.mLastY = this.mThisY;
                break;
        }
        return true;
    }

    public int rotateOrientation() {
        int lastOrientation = parseTouchOrientation((int) this.mLastX, (int) this.mLastY);
        int thisOrientation = parseTouchOrientation((int) this.mThisX, (int) this.mThisY);
        int diff = thisOrientation - lastOrientation;
        if (diff > 0) {
            return 13;
        }
        if (diff < 0) {
            return lastOrientation != 12 ? 14 : 13;
        }
        return 0;
    }

    private boolean isMoved(float lastX, float lastY, float thisX, float thisY) {
        float offsetX = Math.abs(thisX - lastX);
        float offsetY = Math.abs(thisY - lastY);
        return offsetX > 30.0f || offsetY > 30.0f;
    }
}
