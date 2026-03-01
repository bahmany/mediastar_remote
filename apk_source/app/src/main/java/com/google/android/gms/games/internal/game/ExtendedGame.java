package com.google.android.gms.games.internal.game;

import android.os.Parcelable;
import com.google.android.gms.common.data.Freezable;
import com.google.android.gms.games.Game;
import com.google.android.gms.games.snapshot.SnapshotMetadata;
import java.util.ArrayList;

/* loaded from: classes.dex */
public interface ExtendedGame extends Parcelable, Freezable<ExtendedGame> {
    Game getGame();

    ArrayList<GameBadge> kO();

    int kP();

    boolean kQ();

    int kR();

    long kS();

    long kT();

    String kU();

    long kV();

    String kW();

    SnapshotMetadata kX();
}
