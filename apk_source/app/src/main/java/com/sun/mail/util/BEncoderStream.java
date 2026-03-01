package com.sun.mail.util;

import android.support.v7.internal.widget.ActivityChooserView;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class BEncoderStream extends BASE64EncoderStream {
    public BEncoderStream(OutputStream out) {
        super(out, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
    }

    public static int encodedLength(byte[] b) {
        return ((b.length + 2) / 3) * 4;
    }
}
