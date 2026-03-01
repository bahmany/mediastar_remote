package mktvsmart.screen.sliderlayout;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/* loaded from: classes.dex */
public class LeftSliderLayout extends ViewGroup {
    private static final float DEF_SHADOW_WIDTH = 10.0f;
    private static final float MINOR_VELOCITY = 150.0f;
    private static final float SLIDING_WIDTH = 180.0f;
    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_SCROLLING = 1;
    private static final int VELOCITY_UNITS = 1000;
    private int mDefShadowWidth;
    private boolean mEnableSlide;
    private boolean mIsOpen;
    private boolean mIsTouchEventDone;
    private float mLastMotionX;
    private float mLastMotionY;
    private OnLeftSliderLayoutStateListener mListener;
    private View mMainChild;
    private int mMinorVelocity;
    private int mSaveScrollX;
    private Scroller mScroller;
    private int mSlidingWidth;
    private int mTouchSlop;
    private int mTouchState;
    private VelocityTracker mVelocityTracker;
    private int mVelocityUnits;

    public interface OnLeftSliderLayoutStateListener {
        boolean OnLeftSliderLayoutInterceptTouch(MotionEvent motionEvent);

        void OnLeftSliderLayoutStateChanged(boolean z);
    }

    public LeftSliderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeftSliderLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mTouchState = 0;
        this.mIsTouchEventDone = false;
        this.mIsOpen = false;
        this.mSaveScrollX = 0;
        this.mEnableSlide = true;
        this.mMainChild = null;
        this.mListener = null;
        this.mScroller = new Scroller(context);
        this.mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        float fDensity = getResources().getDisplayMetrics().density;
        this.mVelocityUnits = (int) ((1000.0f * fDensity) + 0.5f);
        this.mMinorVelocity = (int) ((MINOR_VELOCITY * fDensity) + 0.5f);
        this.mSlidingWidth = (int) ((180.0f * fDensity) + 0.5f);
        this.mDefShadowWidth = (int) ((DEF_SHADOW_WIDTH * fDensity) + 0.5f);
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != 1073741824) {
            throw new IllegalStateException("LeftSliderLayout only canmCurScreen run at EXACTLY mode!");
        }
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != 1073741824) {
            throw new IllegalStateException("LeftSliderLayout only can run at EXACTLY mode!");
        }
        int nCount = getChildCount();
        for (int i = 2; i < nCount; i++) {
            removeViewAt(i);
        }
        int nCount2 = getChildCount();
        if (nCount2 > 0) {
            if (nCount2 > 1) {
                this.mMainChild = getChildAt(1);
                getChildAt(0).measure(widthMeasureSpec, heightMeasureSpec);
            } else {
                this.mMainChild = getChildAt(0);
            }
            this.mMainChild.measure(widthMeasureSpec, heightMeasureSpec);
        }
        scrollTo(this.mSaveScrollX, 0);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int nLeftChildWidth;
        int nCount = getChildCount();
        if (nCount > 0) {
            if (this.mMainChild != null) {
                this.mMainChild.layout(l, t, this.mMainChild.getMeasuredWidth() + l, this.mMainChild.getMeasuredHeight() + t);
            }
            if (nCount > 1) {
                View leftChild = getChildAt(0);
                ViewGroup.LayoutParams layoutParams = leftChild.getLayoutParams();
                if (layoutParams.width == -1 || layoutParams.width == -1) {
                    nLeftChildWidth = this.mDefShadowWidth;
                } else {
                    nLeftChildWidth = layoutParams.width;
                }
                leftChild.layout(l - nLeftChildWidth, t, l, leftChild.getMeasuredHeight() + t);
            }
        }
    }

    @Override // android.view.View
    public void computeScroll() {
        if (this.mScroller.computeScrollOffset()) {
            scrollTo(this.mScroller.getCurrX(), this.mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        int nCurScrollX = getScrollX();
        if (this.mMainChild != null && this.mTouchState != 1 && this.mIsTouchEventDone) {
            Rect rect = new Rect();
            this.mMainChild.getHitRect(rect);
            if (!rect.contains(((int) event.getX()) + nCurScrollX, (int) event.getY())) {
                return false;
            }
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(event);
        int action = event.getAction();
        float x = event.getX();
        switch (action) {
            case 0:
                if (!this.mScroller.isFinished()) {
                    this.mScroller.abortAnimation();
                }
                this.mIsTouchEventDone = false;
                this.mLastMotionX = x;
                break;
            case 1:
            case 3:
                if (this.mEnableSlide) {
                    VelocityTracker velocityTracker = this.mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(this.mVelocityUnits);
                    if (nCurScrollX < 0) {
                        int velocityX = (int) velocityTracker.getXVelocity();
                        if (velocityX > this.mMinorVelocity) {
                            scrollByWithAnim(getMinScrollX() - nCurScrollX);
                            setState(true);
                        } else if (velocityX < (-this.mMinorVelocity) || nCurScrollX >= getMinScrollX() / 2) {
                            scrollByWithAnim(-nCurScrollX);
                            setState(false);
                        } else {
                            scrollByWithAnim(getMinScrollX() - nCurScrollX);
                            setState(true);
                        }
                    } else {
                        if (nCurScrollX > 0) {
                            scrollByWithAnim(-nCurScrollX);
                        }
                        setState(false);
                    }
                    if (this.mVelocityTracker != null) {
                        this.mVelocityTracker.recycle();
                        this.mVelocityTracker = null;
                    }
                    this.mTouchState = 0;
                    this.mIsTouchEventDone = true;
                    break;
                }
                break;
            case 2:
                if (this.mEnableSlide) {
                    int deltaX = (int) (this.mLastMotionX - x);
                    if (nCurScrollX + deltaX < getMinScrollX()) {
                        deltaX = getMinScrollX() - nCurScrollX;
                        this.mLastMotionX -= deltaX;
                    } else if (nCurScrollX + deltaX > getMaxScrollX()) {
                        deltaX = getMaxScrollX() - nCurScrollX;
                        this.mLastMotionX -= deltaX;
                    } else {
                        this.mLastMotionX = x;
                    }
                    if (deltaX != 0) {
                        scrollBy(deltaX, 0);
                    }
                    this.mSaveScrollX = getScrollX();
                    break;
                }
                break;
        }
        return true;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (this.mListener != null && !this.mListener.OnLeftSliderLayoutInterceptTouch(ev)) {
            return false;
        }
        if (action == 2 && this.mTouchState != 0) {
            return true;
        }
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        if (this.mMainChild != null && this.mTouchState != 1 && !this.mIsOpen) {
            Rect rect = new Rect(this.mMainChild.getLeft(), this.mMainChild.getTop(), Math.max(metrics.widthPixels / 8, 100), this.mMainChild.getBottom());
            if (!rect.contains((int) ev.getX(), (int) ev.getY())) {
                return false;
            }
        }
        float x = ev.getX();
        float y = ev.getY();
        switch (action) {
            case 0:
                this.mLastMotionX = x;
                this.mLastMotionY = y;
                this.mTouchState = this.mScroller.isFinished() ? 0 : 1;
                break;
            case 1:
            case 3:
                this.mTouchState = 0;
                break;
            case 2:
                int xDiff = (int) Math.abs(this.mLastMotionX - x);
                if (xDiff > this.mTouchSlop && Math.abs(this.mLastMotionY - y) / Math.abs(this.mLastMotionX - x) < 1.0f) {
                    this.mTouchState = 1;
                    break;
                }
                break;
        }
        return this.mTouchState != 0;
    }

    void scrollByWithAnim(int nDx) {
        if (nDx != 0) {
            this.mScroller.startScroll(getScrollX(), 0, nDx, 0, Math.abs(nDx));
            invalidate();
        }
    }

    private int getMaxScrollX() {
        return 0;
    }

    private int getMinScrollX() {
        return -this.mSlidingWidth;
    }

    public void open() {
        if (this.mEnableSlide) {
            scrollByWithAnim(getMinScrollX() - getScrollX());
            setState(true);
        }
    }

    public void close() {
        if (this.mEnableSlide) {
            scrollByWithAnim(getScrollX() * (-1));
            setState(false);
        }
    }

    public boolean isOpen() {
        return this.mIsOpen;
    }

    private void setState(boolean bIsOpen) {
        boolean bStateChanged = false;
        if (this.mIsOpen && !bIsOpen) {
            bStateChanged = true;
        } else if (!this.mIsOpen && bIsOpen) {
            bStateChanged = true;
        }
        this.mIsOpen = bIsOpen;
        if (bIsOpen) {
            this.mSaveScrollX = getMaxScrollX();
        } else {
            this.mSaveScrollX = 0;
        }
        if (bStateChanged && this.mListener != null) {
            this.mListener.OnLeftSliderLayoutStateChanged(bIsOpen);
        }
    }

    public void enableSlide(boolean bEnable) {
        this.mEnableSlide = bEnable;
    }

    public void setOnLeftSliderLayoutListener(OnLeftSliderLayoutStateListener listener) {
        this.mListener = listener;
    }
}
