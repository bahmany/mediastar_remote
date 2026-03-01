package com.google.android.gms.maps;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.dynamic.LifecycleDelegate;
import com.google.android.gms.dynamic.e;
import com.google.android.gms.dynamic.f;
import com.google.android.gms.maps.internal.IStreetViewPanoramaViewDelegate;
import com.google.android.gms.maps.internal.u;
import com.google.android.gms.maps.model.RuntimeRemoteException;

/* loaded from: classes.dex */
public class StreetViewPanoramaView extends FrameLayout {
    private StreetViewPanorama aiW;
    private final a ajf;

    static class a extends com.google.android.gms.dynamic.a<b> {
        protected f<b> aiI;
        private final ViewGroup aiN;
        private final StreetViewPanoramaOptions ajg;
        private final Context mContext;

        a(ViewGroup viewGroup, Context context, StreetViewPanoramaOptions streetViewPanoramaOptions) {
            this.aiN = viewGroup;
            this.mContext = context;
            this.ajg = streetViewPanoramaOptions;
        }

        @Override // com.google.android.gms.dynamic.a
        protected void a(f<b> fVar) {
            this.aiI = fVar;
            my();
        }

        public void my() {
            if (this.aiI == null || it() != null) {
                return;
            }
            try {
                this.aiI.a(new b(this.aiN, u.R(this.mContext).a(e.k(this.mContext), this.ajg)));
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            } catch (GooglePlayServicesNotAvailableException e2) {
            }
        }
    }

    static class b implements LifecycleDelegate {
        private final ViewGroup aiK;
        private final IStreetViewPanoramaViewDelegate ajh;
        private View aji;

        public b(ViewGroup viewGroup, IStreetViewPanoramaViewDelegate iStreetViewPanoramaViewDelegate) {
            this.ajh = (IStreetViewPanoramaViewDelegate) n.i(iStreetViewPanoramaViewDelegate);
            this.aiK = (ViewGroup) n.i(viewGroup);
        }

        public IStreetViewPanoramaViewDelegate mF() {
            return this.ajh;
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onCreate(Bundle savedInstanceState) {
            try {
                this.ajh.onCreate(savedInstanceState);
                this.aji = (View) e.f(this.ajh.getView());
                this.aiK.removeAllViews();
                this.aiK.addView(this.aji);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            throw new UnsupportedOperationException("onCreateView not allowed on StreetViewPanoramaViewDelegate");
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onDestroy() {
            try {
                this.ajh.onDestroy();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onDestroyView() {
            throw new UnsupportedOperationException("onDestroyView not allowed on StreetViewPanoramaViewDelegate");
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onInflate(Activity activity, Bundle attrs, Bundle savedInstanceState) {
            throw new UnsupportedOperationException("onInflate not allowed on StreetViewPanoramaViewDelegate");
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onLowMemory() {
            try {
                this.ajh.onLowMemory();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onPause() {
            try {
                this.ajh.onPause();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onResume() {
            try {
                this.ajh.onResume();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onSaveInstanceState(Bundle outState) {
            try {
                this.ajh.onSaveInstanceState(outState);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onStart() {
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onStop() {
        }
    }

    public StreetViewPanoramaView(Context context) {
        super(context);
        this.ajf = new a(this, context, null);
    }

    public StreetViewPanoramaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.ajf = new a(this, context, null);
    }

    public StreetViewPanoramaView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.ajf = new a(this, context, null);
    }

    public StreetViewPanoramaView(Context context, StreetViewPanoramaOptions options) {
        super(context);
        this.ajf = new a(this, context, options);
    }

    public final StreetViewPanorama getStreetViewPanorama() {
        if (this.aiW != null) {
            return this.aiW;
        }
        this.ajf.my();
        if (this.ajf.it() == null) {
            return null;
        }
        try {
            this.aiW = new StreetViewPanorama(this.ajf.it().mF().getStreetViewPanorama());
            return this.aiW;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void onCreate(Bundle savedInstanceState) {
        this.ajf.onCreate(savedInstanceState);
        if (this.ajf.it() == null) {
            a aVar = this.ajf;
            a.b(this);
        }
    }

    public final void onDestroy() {
        this.ajf.onDestroy();
    }

    public final void onLowMemory() {
        this.ajf.onLowMemory();
    }

    public final void onPause() {
        this.ajf.onPause();
    }

    public final void onResume() {
        this.ajf.onResume();
    }

    public final void onSaveInstanceState(Bundle outState) {
        this.ajf.onSaveInstanceState(outState);
    }
}
