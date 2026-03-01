package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.maps.model.internal.i;

/* loaded from: classes.dex */
public final class TileOverlayOptions implements SafeParcelable {
    public static final w CREATOR = new w();
    private final int BR;
    private float ajA;
    private boolean ajB;
    private com.google.android.gms.maps.model.internal.i akg;
    private TileProvider akh;
    private boolean aki;

    public TileOverlayOptions() {
        this.ajB = true;
        this.aki = true;
        this.BR = 1;
    }

    TileOverlayOptions(int versionCode, IBinder delegate, boolean visible, float zIndex, boolean fadeIn) {
        this.ajB = true;
        this.aki = true;
        this.BR = versionCode;
        this.akg = i.a.by(delegate);
        this.akh = this.akg == null ? null : new TileProvider() { // from class: com.google.android.gms.maps.model.TileOverlayOptions.1
            private final com.google.android.gms.maps.model.internal.i akj;

            {
                this.akj = TileOverlayOptions.this.akg;
            }

            @Override // com.google.android.gms.maps.model.TileProvider
            public Tile getTile(int x, int y, int zoom) {
                try {
                    return this.akj.getTile(x, y, zoom);
                } catch (RemoteException e) {
                    return null;
                }
            }
        };
        this.ajB = visible;
        this.ajA = zIndex;
        this.aki = fadeIn;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public TileOverlayOptions fadeIn(boolean fadeIn) {
        this.aki = fadeIn;
        return this;
    }

    public boolean getFadeIn() {
        return this.aki;
    }

    public TileProvider getTileProvider() {
        return this.akh;
    }

    int getVersionCode() {
        return this.BR;
    }

    public float getZIndex() {
        return this.ajA;
    }

    public boolean isVisible() {
        return this.ajB;
    }

    IBinder mP() {
        return this.akg.asBinder();
    }

    public TileOverlayOptions tileProvider(final TileProvider tileProvider) {
        this.akh = tileProvider;
        this.akg = this.akh == null ? null : new i.a() { // from class: com.google.android.gms.maps.model.TileOverlayOptions.2
            @Override // com.google.android.gms.maps.model.internal.i
            public Tile getTile(int x, int y, int zoom) {
                return tileProvider.getTile(x, y, zoom);
            }
        };
        return this;
    }

    public TileOverlayOptions visible(boolean visible) {
        this.ajB = visible;
        return this;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        if (com.google.android.gms.maps.internal.v.mK()) {
            x.a(this, out, flags);
        } else {
            w.a(this, out, flags);
        }
    }

    public TileOverlayOptions zIndex(float zIndex) {
        this.ajA = zIndex;
        return this;
    }
}
