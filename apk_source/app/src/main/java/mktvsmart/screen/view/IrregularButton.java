package mktvsmart.screen.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import com.alibaba.fastjson.asm.Opcodes;

/* loaded from: classes.dex */
public class IrregularButton extends ImageButton {
    private OnActionDownListener mOnActionDownListener;
    private OnActionMoveListener mOnActionMoveListener;
    private OnActionUpListener mOnActionUpListener;
    private TouchChecker mTouchChecker;

    public interface OnActionDownListener {
        void onActionDown(View view, int i);
    }

    public interface OnActionMoveListener {
        void onActionMove(View view, int i);
    }

    public interface OnActionUpListener {
        void onAtionUp(View view, int i);
    }

    public interface TouchChecker {
        boolean isInTouchArea(int i, int i2, int i3, int i4);
    }

    public IrregularButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mTouchChecker = null;
        this.mOnActionDownListener = null;
        this.mOnActionUpListener = null;
        this.mOnActionMoveListener = null;
    }

    public IrregularButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTouchChecker = null;
        this.mOnActionDownListener = null;
        this.mOnActionUpListener = null;
        this.mOnActionMoveListener = null;
    }

    public IrregularButton(Context context) {
        super(context);
        this.mTouchChecker = null;
        this.mOnActionDownListener = null;
        this.mOnActionUpListener = null;
        this.mOnActionMoveListener = null;
    }

    public TouchChecker getTouchChecker() {
        return this.mTouchChecker;
    }

    public void setTouchChecker(TouchChecker checker) {
        this.mTouchChecker = checker;
    }

    public OnActionDownListener getOnActionDownListener() {
        return this.mOnActionDownListener;
    }

    public void setOnActionDownListener(OnActionDownListener l) {
        this.mOnActionDownListener = l;
    }

    public OnActionUpListener getOnActionUpListener() {
        return this.mOnActionUpListener;
    }

    public void setOnActionUpListener(OnActionUpListener l) {
        this.mOnActionUpListener = l;
    }

    public OnActionMoveListener getOnActionMoveListener() {
        return this.mOnActionMoveListener;
    }

    public void setOnActionMoveListener(OnActionMoveListener l) {
        this.mOnActionMoveListener = l;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        boolean flag = true;
        int action = event.getAction();
        switch (action) {
            case 0:
                if (this.mTouchChecker != null && this.mTouchChecker.isInTouchArea((int) event.getX(), (int) event.getY(), getWidth(), getHeight())) {
                    if (this.mOnActionDownListener != null) {
                        this.mOnActionDownListener.onActionDown(this, parseTouchOrientation((int) event.getX(), (int) event.getY()));
                    }
                    flag = true;
                    break;
                } else {
                    flag = false;
                    break;
                }
                break;
            case 1:
                if (this.mOnActionUpListener != null) {
                    this.mOnActionUpListener.onAtionUp(this, parseTouchOrientation((int) event.getX(), (int) event.getY()));
                    break;
                }
                break;
            case 2:
                if (this.mTouchChecker != null && !this.mTouchChecker.isInTouchArea((int) event.getX(), (int) event.getY(), getWidth(), getHeight()) && this.mOnActionMoveListener != null) {
                    this.mOnActionMoveListener.onActionMove(this, -1);
                    break;
                }
                break;
        }
        if (flag) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    private Point getRectangleCenterPoint(int left, int top, int width, int height) {
        Point center = new Point();
        center.x = (width / 2) + left;
        center.y = (height / 2) + top;
        return center;
    }

    private int computeTouchPointAngle(Point center, Point touchPoint) {
        if (touchPoint.y == center.y) {
            if (touchPoint.x > center.x) {
                return 90;
            }
            if (touchPoint.x < center.x) {
                return 270;
            }
            return -1;
        }
        if (touchPoint.x == center.x) {
            if (touchPoint.y < center.y) {
                return 0;
            }
            if (touchPoint.y > center.y) {
                return Opcodes.GETFIELD;
            }
        }
        int a = Math.abs(touchPoint.x - center.x);
        int b = Math.abs(touchPoint.y - center.y);
        double radian = Math.atan(a / b);
        int alpha = (int) (radian * 57.29577951308232d);
        if (touchPoint.x > center.x) {
            if (touchPoint.y < center.y) {
                return alpha + 0;
            }
            if (touchPoint.y > center.y) {
                return 180 - alpha;
            }
            return alpha;
        }
        if (touchPoint.x < center.x) {
            if (touchPoint.y < center.y) {
                return 360 - alpha;
            }
            if (touchPoint.y > center.y) {
                return alpha + Opcodes.GETFIELD;
            }
            return alpha;
        }
        return alpha;
    }

    public int parseTouchOrientation(int x, int y) {
        if (x < 0 || y < 0) {
            return 0;
        }
        Point touchPoint = new Point(x, y);
        Point center = getRectangleCenterPoint(0, 0, getWidth(), getHeight());
        int angle = computeTouchPointAngle(center, touchPoint);
        if ((angle >= 345 && angle < 360) || (angle >= 0 && angle < 15)) {
            return 12;
        }
        if (angle >= 15 && angle < 45) {
            return 1;
        }
        if (angle >= 45 && angle < 75) {
            return 2;
        }
        if (angle >= 75 && angle < 105) {
            return 3;
        }
        if (angle >= 105 && angle < 135) {
            return 4;
        }
        if (angle >= 135 && angle < 165) {
            return 5;
        }
        if (angle >= 165 && angle < 195) {
            return 6;
        }
        if (angle >= 195 && angle < 225) {
            return 7;
        }
        if (angle >= 225 && angle < 255) {
            return 8;
        }
        if (angle >= 255 && angle < 285) {
            return 9;
        }
        if (angle < 285 || angle >= 315) {
            return (angle < 315 || angle >= 345) ? 0 : 11;
        }
        return 10;
    }
}
