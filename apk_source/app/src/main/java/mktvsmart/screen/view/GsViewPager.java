package mktvsmart.screen.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/* loaded from: classes.dex */
public class GsViewPager extends ViewPager {
    private boolean mIsScrollable;

    @Override // android.support.v4.view.ViewPager, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mIsScrollable) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override // android.support.v4.view.ViewPager, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.mIsScrollable) {
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }

    public GsViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIsScrollable = true;
    }

    public GsViewPager(Context context) {
        super(context);
        this.mIsScrollable = true;
    }

    public void setScrollable(boolean flag) {
        this.mIsScrollable = flag;
    }
}
