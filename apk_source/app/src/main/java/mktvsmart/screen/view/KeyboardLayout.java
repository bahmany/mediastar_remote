package mktvsmart.screen.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

/* loaded from: classes.dex */
public class KeyboardLayout extends LinearLayout {
    public static final byte KEYBOARD_STATE_HIDE = -2;
    public static final byte KEYBOARD_STATE_INIT = -1;
    public static final byte KEYBOARD_STATE_SHOW = -3;
    private static final String TAG = KeyboardLayout.class.getName();
    private boolean mHasInit;
    private boolean mHasKeybord;
    private int mHeight;
    private OnKeyboardStateChangedListener mListener;

    public interface OnKeyboardStateChangedListener {
        void onKeyBoardStateChanged(int i);
    }

    public KeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyboardLayout(Context context) {
        super(context);
    }

    public void setOnKeyboardStateListener(OnKeyboardStateChangedListener listener) {
        this.mListener = listener;
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!this.mHasInit) {
            this.mHasInit = true;
            this.mHeight = b;
            if (this.mListener != null) {
                this.mListener.onKeyBoardStateChanged(-1);
            }
        } else {
            this.mHeight = this.mHeight < b ? b : this.mHeight;
        }
        if (this.mHasInit && this.mHeight > b) {
            this.mHasKeybord = true;
            if (this.mListener != null) {
                this.mListener.onKeyBoardStateChanged(-3);
            }
            Log.w(TAG, "show keyboard.......");
        }
        if (this.mHasInit && this.mHasKeybord && this.mHeight == b) {
            this.mHasKeybord = false;
            if (this.mListener != null) {
                this.mListener.onKeyBoardStateChanged(-2);
            }
            Log.w(TAG, "hide keyboard.......");
        }
    }
}
