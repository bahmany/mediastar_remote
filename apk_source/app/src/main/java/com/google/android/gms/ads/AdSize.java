package com.google.android.gms.ads;

import android.content.Context;
import com.alibaba.fastjson.asm.Opcodes;
import com.google.android.gms.internal.ay;
import com.google.android.gms.internal.gr;
import com.hisilicon.multiscreen.protocol.message.KeyInfo;

/* loaded from: classes.dex */
public final class AdSize {
    public static final int AUTO_HEIGHT = -2;
    public static final int FULL_WIDTH = -1;
    private final int lf;
    private final int lg;
    private final String lh;
    public static final AdSize BANNER = new AdSize(320, 50, "320x50_mb");
    public static final AdSize FULL_BANNER = new AdSize(468, 60, "468x60_as");
    public static final AdSize LARGE_BANNER = new AdSize(320, 100, "320x100_as");
    public static final AdSize LEADERBOARD = new AdSize(728, 90, "728x90_as");
    public static final AdSize MEDIUM_RECTANGLE = new AdSize(300, KeyInfo.KEYCODE_M, "300x250_as");
    public static final AdSize WIDE_SKYSCRAPER = new AdSize(Opcodes.IF_ICMPNE, 600, "160x600_as");
    public static final AdSize SMART_BANNER = new AdSize(-1, -2, "smart_banner");

    public AdSize(int width, int height) {
        this(width, height, (width == -1 ? "FULL" : String.valueOf(width)) + "x" + (height == -2 ? "AUTO" : String.valueOf(height)) + "_as");
    }

    AdSize(int width, int height, String formatString) {
        if (width < 0 && width != -1) {
            throw new IllegalArgumentException("Invalid width for AdSize: " + width);
        }
        if (height < 0 && height != -2) {
            throw new IllegalArgumentException("Invalid height for AdSize: " + height);
        }
        this.lf = width;
        this.lg = height;
        this.lh = formatString;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AdSize)) {
            return false;
        }
        AdSize adSize = (AdSize) other;
        return this.lf == adSize.lf && this.lg == adSize.lg && this.lh.equals(adSize.lh);
    }

    public int getHeight() {
        return this.lg;
    }

    public int getHeightInPixels(Context context) {
        return this.lg == -2 ? ay.b(context.getResources().getDisplayMetrics()) : gr.a(context, this.lg);
    }

    public int getWidth() {
        return this.lf;
    }

    public int getWidthInPixels(Context context) {
        return this.lf == -1 ? ay.a(context.getResources().getDisplayMetrics()) : gr.a(context, this.lf);
    }

    public int hashCode() {
        return this.lh.hashCode();
    }

    public boolean isAutoHeight() {
        return this.lg == -2;
    }

    public boolean isFullWidth() {
        return this.lf == -1;
    }

    public String toString() {
        return this.lh;
    }
}
