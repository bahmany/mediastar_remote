package com.google.android.gms.games.internal;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import com.google.android.gms.internal.kc;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class PopupManager {
    protected GamesClientImpl XO;
    protected PopupLocationInfo XP;

    public static final class PopupLocationInfo {
        public IBinder XQ;
        public int XR;
        public int bottom;
        public int gravity;
        public int left;
        public int right;
        public int top;

        private PopupLocationInfo(int gravity, IBinder windowToken) {
            this.XR = -1;
            this.left = 0;
            this.top = 0;
            this.right = 0;
            this.bottom = 0;
            this.gravity = gravity;
            this.XQ = windowToken;
        }

        /* synthetic */ PopupLocationInfo(int x0, IBinder x1, AnonymousClass1 x2) {
            this(x0, x1);
        }

        public Bundle kM() {
            Bundle bundle = new Bundle();
            bundle.putInt("popupLocationInfo.gravity", this.gravity);
            bundle.putInt("popupLocationInfo.displayId", this.XR);
            bundle.putInt("popupLocationInfo.left", this.left);
            bundle.putInt("popupLocationInfo.top", this.top);
            bundle.putInt("popupLocationInfo.right", this.right);
            bundle.putInt("popupLocationInfo.bottom", this.bottom);
            return bundle;
        }
    }

    private static final class PopupManagerHCMR1 extends PopupManager implements View.OnAttachStateChangeListener, ViewTreeObserver.OnGlobalLayoutListener {
        private boolean Wn;
        private WeakReference<View> XS;

        protected PopupManagerHCMR1(GamesClientImpl gamesClientImpl, int gravity) {
            super(gamesClientImpl, gravity);
            this.Wn = false;
        }

        private void m(View view) {
            Display display;
            int displayId = -1;
            if (kc.hG() && (display = view.getDisplay()) != null) {
                displayId = display.getDisplayId();
            }
            IBinder windowToken = view.getWindowToken();
            int[] iArr = new int[2];
            view.getLocationInWindow(iArr);
            int width = view.getWidth();
            int height = view.getHeight();
            this.XP.XR = displayId;
            this.XP.XQ = windowToken;
            this.XP.left = iArr[0];
            this.XP.top = iArr[1];
            this.XP.right = iArr[0] + width;
            this.XP.bottom = iArr[1] + height;
            if (this.Wn) {
                kJ();
                this.Wn = false;
            }
        }

        @Override // com.google.android.gms.games.internal.PopupManager
        protected void dG(int i) {
            this.XP = new PopupLocationInfo(i, null);
        }

        @Override // com.google.android.gms.games.internal.PopupManager
        public void kJ() {
            if (this.XP.XQ != null) {
                super.kJ();
            } else {
                this.Wn = this.XS != null;
            }
        }

        @Override // com.google.android.gms.games.internal.PopupManager
        public void l(View view) {
            this.XO.ku();
            if (this.XS != null) {
                View decorView = this.XS.get();
                Context context = this.XO.getContext();
                if (decorView == null && (context instanceof Activity)) {
                    decorView = ((Activity) context).getWindow().getDecorView();
                }
                if (decorView != null) {
                    decorView.removeOnAttachStateChangeListener(this);
                    ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();
                    if (kc.hF()) {
                        viewTreeObserver.removeOnGlobalLayoutListener(this);
                    } else {
                        viewTreeObserver.removeGlobalOnLayoutListener(this);
                    }
                }
            }
            this.XS = null;
            Context context2 = this.XO.getContext();
            if (view == null && (context2 instanceof Activity)) {
                View viewFindViewById = ((Activity) context2).findViewById(R.id.content);
                if (viewFindViewById == null) {
                    viewFindViewById = ((Activity) context2).getWindow().getDecorView();
                }
                GamesLog.p("PopupManager", "You have not specified a View to use as content view for popups. Falling back to the Activity content view which may not work properly in future versions of the API. Use setViewForPopups() to set your content view.");
                view = viewFindViewById;
            }
            if (view == null) {
                GamesLog.q("PopupManager", "No content view usable to display popups. Popups will not be displayed in response to this client's calls. Use setViewForPopups() to set your content view.");
                return;
            }
            m(view);
            this.XS = new WeakReference<>(view);
            view.addOnAttachStateChangeListener(this);
            view.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }

        @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
        public void onGlobalLayout() {
            View view;
            if (this.XS == null || (view = this.XS.get()) == null) {
                return;
            }
            m(view);
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View v) {
            m(v);
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View v) {
            this.XO.ku();
            v.removeOnAttachStateChangeListener(this);
        }
    }

    private PopupManager(GamesClientImpl gamesClientImpl, int gravity) {
        this.XO = gamesClientImpl;
        dG(gravity);
    }

    /* synthetic */ PopupManager(GamesClientImpl x0, int x1, AnonymousClass1 x2) {
        this(x0, x1);
    }

    public static PopupManager a(GamesClientImpl gamesClientImpl, int i) {
        return kc.hC() ? new PopupManagerHCMR1(gamesClientImpl, i) : new PopupManager(gamesClientImpl, i);
    }

    protected void dG(int i) {
        this.XP = new PopupLocationInfo(i, new Binder());
    }

    public void kJ() {
        this.XO.a(this.XP.XQ, this.XP.kM());
    }

    public Bundle kK() {
        return this.XP.kM();
    }

    public IBinder kL() {
        return this.XP.XQ;
    }

    public void l(View view) {
    }

    public void setGravity(int gravity) {
        this.XP.gravity = gravity;
    }
}
