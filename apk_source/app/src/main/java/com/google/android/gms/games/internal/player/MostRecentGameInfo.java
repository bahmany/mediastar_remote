package com.google.android.gms.games.internal.player;

import android.net.Uri;
import android.os.Parcelable;
import com.google.android.gms.common.data.Freezable;

/* loaded from: classes.dex */
public interface MostRecentGameInfo extends Parcelable, Freezable<MostRecentGameInfo> {
    String ln();

    String lo();

    long lp();

    Uri lq();

    Uri lr();

    Uri ls();
}
