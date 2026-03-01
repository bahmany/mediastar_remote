package mktvsmart.screen.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/* loaded from: classes.dex */
public class BitmapUtils {
    private static final long MB = 1048576;

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap Bytes2Bitmap(Intent intent) {
        byte[] buff = intent.getByteArrayExtra("bitmap");
        Bitmap bm = BitmapFactory.decodeByteArray(buff, 0, buff.length);
        return bm;
    }

    public static Bitmap shot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Display display = activity.getWindowManager().getDefaultDisplay();
        view.layout(0, 500, display.getWidth() - 200, display.getHeight() - 250);
        Bitmap bitmap = view.getDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(bitmap);
        return bmp;
    }

    public static Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(cacheBitmap);
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmapCreateBitmap;
    }

    public static void save(String path, String name, Bitmap bitmap) throws IOException {
        File file = new File(path, name);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
    }

    public static Bitmap getBitmap(String path, String name) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        File file = new File(path, name);
        if (file.exists() && file.length() / MB > 1) {
            options.inSampleSize = 2;
        }
        Bitmap imageBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        return imageBitmap;
    }

    public static Bitmap getBitmap(String path) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            File file = new File(path);
            if (file.exists() && file.length() / MB > 1) {
                options.inSampleSize = 2;
            }
            Bitmap imageBitmap = BitmapFactory.decodeFile(path, options);
            return imageBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap zoomImage(Bitmap bm, double newWidth, double newHeight) {
        float width = bm.getWidth();
        float height = bm.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bm, 0, 0, (int) width, (int) height, matrix, true);
        return bitmap;
    }

    public static String getSDPath() {
        boolean hasSDCard = Environment.getExternalStorageState().equals("mounted");
        return hasSDCard ? Environment.getExternalStorageDirectory().toString() : Environment.getDownloadCacheDirectory().toString();
    }
}
