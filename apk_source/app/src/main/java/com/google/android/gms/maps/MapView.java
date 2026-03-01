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
import com.google.android.gms.maps.internal.IMapViewDelegate;
import com.google.android.gms.maps.internal.u;
import com.google.android.gms.maps.model.RuntimeRemoteException;

/* loaded from: classes.dex */
public class MapView extends FrameLayout {
    private GoogleMap aiG;
    private final b aiJ;

    static class a implements LifecycleDelegate {
        private final ViewGroup aiK;
        private final IMapViewDelegate aiL;
        private View aiM;

        public a(ViewGroup viewGroup, IMapViewDelegate iMapViewDelegate) {
            this.aiL = (IMapViewDelegate) n.i(iMapViewDelegate);
            this.aiK = (ViewGroup) n.i(viewGroup);
        }

        public IMapViewDelegate mz() {
            return this.aiL;
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onCreate(Bundle savedInstanceState) {
            try {
                this.aiL.onCreate(savedInstanceState);
                this.aiM = (View) e.f(this.aiL.getView());
                this.aiK.removeAllViews();
                this.aiK.addView(this.aiM);
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            throw new UnsupportedOperationException("onCreateView not allowed on MapViewDelegate");
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onDestroy() {
            try {
                this.aiL.onDestroy();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onDestroyView() {
            throw new UnsupportedOperationException("onDestroyView not allowed on MapViewDelegate");
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onInflate(Activity activity, Bundle attrs, Bundle savedInstanceState) {
            throw new UnsupportedOperationException("onInflate not allowed on MapViewDelegate");
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onLowMemory() {
            try {
                this.aiL.onLowMemory();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onPause() {
            try {
                this.aiL.onPause();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onResume() {
            try {
                this.aiL.onResume();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }

        @Override // com.google.android.gms.dynamic.LifecycleDelegate
        public void onSaveInstanceState(Bundle outState) {
            try {
                this.aiL.onSaveInstanceState(outState);
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

    static class b extends com.google.android.gms.dynamic.a<a> {
        protected f<a> aiI;
        private final ViewGroup aiN;
        private final GoogleMapOptions aiO;
        private final Context mContext;

        b(ViewGroup viewGroup, Context context, GoogleMapOptions googleMapOptions) {
            this.aiN = viewGroup;
            this.mContext = context;
            this.aiO = googleMapOptions;
        }

        @Override // com.google.android.gms.dynamic.a
        protected void a(f<a> fVar) {
            this.aiI = fVar;
            my();
        }

        public void my() {
            if (this.aiI == null || it() != null) {
                return;
            }
            try {
                this.aiI.a(new a(this.aiN, u.R(this.mContext).a(e.k(this.mContext), this.aiO)));
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            } catch (GooglePlayServicesNotAvailableException e2) {
            }
        }
    }

    public MapView(Context context) {
        super(context);
        this.aiJ = new b(this, context, null);
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.aiJ = new b(this, context, GoogleMapOptions.createFromAttributes(context, attrs));
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.aiJ = new b(this, context, GoogleMapOptions.createFromAttributes(context, attrs));
    }

    public MapView(Context context, GoogleMapOptions options) {
        super(context);
        this.aiJ = new b(this, context, options);
    }

    public final GoogleMap getMap() {
        if (this.aiG != null) {
            return this.aiG;
        }
        this.aiJ.my();
        if (this.aiJ.it() == null) {
            return null;
        }
        try {
            this.aiG = new GoogleMap(this.aiJ.it().mz().getMap());
            return this.aiG;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void onCreate(Bundle savedInstanceState) {
        this.aiJ.onCreate(savedInstanceState);
        if (this.aiJ.it() == null) {
            b bVar = this.aiJ;
            b.b(this);
        }
    }

    public final void onDestroy() {
        this.aiJ.onDestroy();
    }

    public final void onLowMemory() {
        this.aiJ.onLowMemory();
    }

    public final void onPause() {
        this.aiJ.onPause();
    }

    public final void onResume() {
        this.aiJ.onResume();
    }

    public final void onSaveInstanceState(Bundle outState) {
        this.aiJ.onSaveInstanceState(outState);
    }
}
