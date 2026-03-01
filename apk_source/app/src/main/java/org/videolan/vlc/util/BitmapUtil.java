package org.videolan.vlc.util;

import android.content.Context;
import android.graphics.Bitmap;
import org.videolan.vlc.MediaDatabase;
import org.videolan.vlc.MediaWrapper;

/* loaded from: classes.dex */
public class BitmapUtil {
    public static final String TAG = "VLC/Util/BitmapUtil";

    public static Bitmap cropBorders(Bitmap bitmap, int width, int height) {
        int top = 0;
        for (int i = 0; i < height / 2; i++) {
            int pixel1 = bitmap.getPixel(width / 2, i);
            int pixel2 = bitmap.getPixel(width / 2, (height - i) - 1);
            if ((pixel1 != 0 && pixel1 != -16777216) || (pixel2 != 0 && pixel2 != -16777216)) {
                break;
            }
            top = i;
        }
        int left = 0;
        for (int i2 = 0; i2 < width / 2; i2++) {
            int pixel12 = bitmap.getPixel(i2, height / 2);
            int pixel22 = bitmap.getPixel((width - i2) - 1, height / 2);
            if ((pixel12 != 0 && pixel12 != -16777216) || (pixel22 != 0 && pixel22 != -16777216)) {
                break;
            }
            left = i2;
        }
        return (left >= (width / 2) + (-10) || top >= (height / 2) + (-10)) ? bitmap : Bitmap.createBitmap(bitmap, left, top, width - (left * 2), height - (top * 2));
    }

    public static Bitmap scaleDownBitmap(Context context, Bitmap bitmap, int width) {
        if (bitmap != null) {
            float densityMultiplier = context.getResources().getDisplayMetrics().density;
            int w = (int) (width * densityMultiplier);
            int h = (int) ((bitmap.getHeight() * w) / bitmap.getWidth());
            return Bitmap.createScaledBitmap(bitmap, w, h, true);
        }
        return bitmap;
    }

    public static Bitmap getPictureFromCache(MediaWrapper media) {
        Bitmap b = media.getPicture();
        if (b != null) {
            return b;
        }
        BitmapCache cache = BitmapCache.getInstance();
        Bitmap picture = cache.getBitmapFromMemCache(media.getLocation());
        if (picture == null) {
            Context c = VLCInstance.getAppContext();
            Bitmap picture2 = MediaDatabase.getInstance().getPicture(c, media.getLocation());
            cache.addBitmapToMemCache(media.getLocation(), picture2);
            return picture2;
        }
        return picture;
    }
}
