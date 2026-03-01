package mktvsmart.screen.filebroswer;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/* loaded from: classes.dex */
public class VideoGridAnimator {
    public static final String TAG = "VLC/VideoGridAnimator";
    private final GridView mGridView;
    private int mLastNItems;
    private boolean isAnimating = false;
    private int mAnimationsRunning = 0;
    ViewGroup.OnHierarchyChangeListener mHCL = new ViewGroup.OnHierarchyChangeListener() { // from class: mktvsmart.screen.filebroswer.VideoGridAnimator.1
        @Override // android.view.ViewGroup.OnHierarchyChangeListener
        public void onChildViewRemoved(View parent, View child) {
        }

        @Override // android.view.ViewGroup.OnHierarchyChangeListener
        public void onChildViewAdded(View parent, View child) {
            if (VideoGridAnimator.this.isAnimating && parent == VideoGridAnimator.this.mGridView) {
                VideoGridAnimator.this.setAlpha(0.0f, child);
            }
        }
    };
    final Runnable r = new Runnable() { // from class: mktvsmart.screen.filebroswer.VideoGridAnimator.2
        @Override // java.lang.Runnable
        public void run() {
            if (VideoGridAnimator.this.mGridView.getChildCount() == VideoGridAnimator.this.mLastNItems) {
                VideoGridAnimator.this.isAnimating = false;
                for (int i = 0; i < VideoGridAnimator.this.mGridView.getChildCount(); i++) {
                    AnimationSet animSet = new AnimationSet(true);
                    Animation animation = new AlphaAnimation(0.0f, 1.0f);
                    animation.setDuration(300L);
                    animation.setStartOffset(i * 80);
                    animSet.addAnimation(animation);
                    if (((VideoListAdapter) VideoGridAnimator.this.mGridView.getAdapter()).isListMode()) {
                        Animation animation2 = new TranslateAnimation(1, -1.0f, 1, 0.0f, 1, 0.0f, 1, 0.0f);
                        animation2.setDuration(400L);
                        animation2.setStartOffset(i * 80);
                        animSet.addAnimation(animation2);
                    }
                    animSet.setAnimationListener(new Animation.AnimationListener() { // from class: mktvsmart.screen.filebroswer.VideoGridAnimator.2.1
                        @Override // android.view.animation.Animation.AnimationListener
                        public void onAnimationStart(Animation animation3) {
                            VideoGridAnimator.this.mAnimationsRunning++;
                        }

                        @Override // android.view.animation.Animation.AnimationListener
                        public void onAnimationRepeat(Animation animation3) {
                        }

                        @Override // android.view.animation.Animation.AnimationListener
                        public void onAnimationEnd(Animation animation3) {
                            VideoGridAnimator videoGridAnimator = VideoGridAnimator.this;
                            videoGridAnimator.mAnimationsRunning--;
                        }
                    });
                    VideoGridAnimator.this.isAnimating = false;
                    View v = VideoGridAnimator.this.mGridView.getChildAt(i);
                    VideoGridAnimator.this.setAlpha(1.0f, v);
                    v.startAnimation(animSet);
                }
                return;
            }
            VideoGridAnimator.this.mLastNItems = VideoGridAnimator.this.mGridView.getChildCount();
            Log.e(VideoGridAnimator.TAG, "Rescheduling animation: list not ready");
            VideoGridAnimator.this.mGridView.postDelayed(this, 10L);
        }
    };

    public VideoGridAnimator(GridView gridview) {
        this.mGridView = gridview;
        this.mGridView.setOnHierarchyChangeListener(this.mHCL);
    }

    public void animate() {
        this.isAnimating = true;
        this.mLastNItems = -1;
        this.mGridView.removeCallbacks(this.r);
        this.mGridView.post(this.r);
    }

    public boolean isAnimationDone() {
        return this.mAnimationsRunning == 0;
    }

    @TargetApi(11)
    public void setAlpha(float alpha, View view) {
        if (Build.VERSION.SDK_INT >= 11) {
            view.setAlpha(alpha);
            return;
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setAlpha(alpha, ((ViewGroup) view).getChildAt(i));
                if (((ViewGroup) view).getBackground() != null) {
                    ((ViewGroup) view).getBackground().setAlpha((int) (alpha * 255.0f));
                }
            }
            return;
        }
        if (view instanceof ImageView) {
            if (((ImageView) view).getDrawable() != null) {
                ((ImageView) view).getDrawable().setAlpha((int) (alpha * 255.0f));
            }
            if (((ImageView) view).getBackground() != null) {
                ((ImageView) view).getBackground().setAlpha((int) (alpha * 255.0f));
                return;
            }
            return;
        }
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(((TextView) view).getTextColors().withAlpha((int) (alpha * 255.0f)));
            if (((TextView) view).getBackground() != null) {
                ((TextView) view).getBackground().setAlpha((int) (alpha * 255.0f));
            }
        }
    }
}
