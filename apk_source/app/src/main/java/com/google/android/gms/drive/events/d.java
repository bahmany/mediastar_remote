package com.google.android.gms.drive.events;

import com.google.android.gms.drive.DriveId;

/* loaded from: classes.dex */
public class d {
    public static boolean a(int i, DriveId driveId) {
        return driveId != null || bd(i);
    }

    public static boolean bd(int i) {
        return (2 & ((long) (1 << i))) != 0;
    }
}
