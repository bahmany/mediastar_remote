package tv.cjump.jni;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import java.lang.reflect.Field;

/* loaded from: classes.dex */
public class NativeBitmapFactory {
    static Field nativeIntField = null;
    static boolean nativeLibLoaded = false;

    private static native Bitmap createBitmap(int i, int i2, int i3, boolean z);

    private static native Bitmap createBitmap19(int i, int i2, int i3, boolean z);

    private static native boolean init();

    private static native boolean release();

    public static boolean isInNativeAlloc() {
        return Build.VERSION.SDK_INT < 11 || (nativeLibLoaded && nativeIntField != null);
    }

    public static void loadLibs() throws Throwable {
        if (!DeviceUtils.isRealARMArch() && !DeviceUtils.isRealX86Arch()) {
            nativeLibLoaded = false;
            return;
        }
        if (!nativeLibLoaded) {
            try {
                if (Build.VERSION.SDK_INT >= 11) {
                    System.loadLibrary("ndkbitmap");
                    nativeLibLoaded = true;
                } else {
                    nativeLibLoaded = false;
                }
            } catch (Error e) {
                e.printStackTrace();
                nativeLibLoaded = false;
            } catch (Exception e2) {
                e2.printStackTrace();
                nativeLibLoaded = false;
            }
            if (nativeLibLoaded) {
                boolean libInit = init();
                if (!libInit) {
                    release();
                    nativeLibLoaded = false;
                } else {
                    initField();
                    boolean confirm = testLib();
                    if (!confirm) {
                        release();
                        nativeLibLoaded = false;
                    }
                }
            }
            Log.e("NativeBitmapFactory", "loaded" + nativeLibLoaded);
        }
    }

    public static void releaseLibs() {
        if (nativeLibLoaded) {
            release();
        }
        nativeIntField = null;
        nativeLibLoaded = false;
    }

    static void initField() {
        try {
            nativeIntField = Bitmap.Config.class.getDeclaredField("nativeInt");
            nativeIntField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            nativeIntField = null;
            e.printStackTrace();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:40:0x00a1  */
    @android.annotation.SuppressLint({"NewApi"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static boolean testLib() throws java.lang.Throwable {
        /*
            r12 = 17
            r9 = 1
            r11 = 2
            r10 = 0
            java.lang.reflect.Field r1 = tv.cjump.jni.NativeBitmapFactory.nativeIntField
            if (r1 != 0) goto Lb
            r9 = r10
        La:
            return r9
        Lb:
            r6 = 0
            r7 = 0
            r1 = 2
            r2 = 2
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch: java.lang.Exception -> L6f java.lang.Error -> L92 java.lang.Throwable -> L9d
            r4 = 1
            android.graphics.Bitmap r6 = createNativeBitmap(r1, r2, r3, r4)     // Catch: java.lang.Exception -> L6f java.lang.Error -> L92 java.lang.Throwable -> L9d
            if (r6 == 0) goto L6d
            int r1 = r6.getWidth()     // Catch: java.lang.Exception -> L6f java.lang.Error -> L92 java.lang.Throwable -> L9d
            if (r1 != r11) goto L6d
            int r1 = r6.getHeight()     // Catch: java.lang.Exception -> L6f java.lang.Error -> L92 java.lang.Throwable -> L9d
            if (r1 != r11) goto L6d
        L24:
            if (r9 == 0) goto Lac
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch: java.lang.Exception -> L6f java.lang.Error -> L92 java.lang.Throwable -> L9d
            if (r1 < r12) goto L34
            boolean r1 = r6.isPremultiplied()     // Catch: java.lang.Exception -> L6f java.lang.Error -> L92 java.lang.Throwable -> L9d
            if (r1 != 0) goto L34
            r1 = 1
            r6.setPremultiplied(r1)     // Catch: java.lang.Exception -> L6f java.lang.Error -> L92 java.lang.Throwable -> L9d
        L34:
            android.graphics.Canvas r0 = new android.graphics.Canvas     // Catch: java.lang.Exception -> L6f java.lang.Error -> L92 java.lang.Throwable -> L9d
            r0.<init>(r6)     // Catch: java.lang.Exception -> L6f java.lang.Error -> L92 java.lang.Throwable -> L9d
            android.graphics.Paint r5 = new android.graphics.Paint     // Catch: java.lang.Throwable -> La6 java.lang.Error -> La8 java.lang.Exception -> Laa
            r5.<init>()     // Catch: java.lang.Throwable -> La6 java.lang.Error -> La8 java.lang.Exception -> Laa
            r1 = -65536(0xffffffffffff0000, float:NaN)
            r5.setColor(r1)     // Catch: java.lang.Throwable -> La6 java.lang.Error -> La8 java.lang.Exception -> Laa
            r1 = 1101004800(0x41a00000, float:20.0)
            r5.setTextSize(r1)     // Catch: java.lang.Throwable -> La6 java.lang.Error -> La8 java.lang.Exception -> Laa
            r1 = 0
            r2 = 0
            int r3 = r6.getWidth()     // Catch: java.lang.Throwable -> La6 java.lang.Error -> La8 java.lang.Exception -> Laa
            float r3 = (float) r3     // Catch: java.lang.Throwable -> La6 java.lang.Error -> La8 java.lang.Exception -> Laa
            int r4 = r6.getHeight()     // Catch: java.lang.Throwable -> La6 java.lang.Error -> La8 java.lang.Exception -> Laa
            float r4 = (float) r4     // Catch: java.lang.Throwable -> La6 java.lang.Error -> La8 java.lang.Exception -> Laa
            r0.drawRect(r1, r2, r3, r4, r5)     // Catch: java.lang.Throwable -> La6 java.lang.Error -> La8 java.lang.Exception -> Laa
            java.lang.String r1 = "TestLib"
            r2 = 0
            r3 = 0
            r0.drawText(r1, r2, r3, r5)     // Catch: java.lang.Throwable -> La6 java.lang.Error -> La8 java.lang.Exception -> Laa
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch: java.lang.Throwable -> La6 java.lang.Error -> La8 java.lang.Exception -> Laa
            if (r1 < r12) goto L66
            boolean r9 = r6.isPremultiplied()     // Catch: java.lang.Throwable -> La6 java.lang.Error -> La8 java.lang.Exception -> Laa
        L66:
            if (r6 == 0) goto La
            r6.recycle()
            r6 = 0
            goto La
        L6d:
            r9 = r10
            goto L24
        L6f:
            r8 = move-exception
            r0 = r7
        L71:
            java.lang.String r1 = "NativeBitmapFactory"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> La6
            java.lang.String r3 = "exception:"
            r2.<init>(r3)     // Catch: java.lang.Throwable -> La6
            java.lang.String r3 = r8.toString()     // Catch: java.lang.Throwable -> La6
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Throwable -> La6
            java.lang.String r2 = r2.toString()     // Catch: java.lang.Throwable -> La6
            android.util.Log.e(r1, r2)     // Catch: java.lang.Throwable -> La6
            if (r6 == 0) goto L8f
            r6.recycle()
            r6 = 0
        L8f:
            r9 = r10
            goto La
        L92:
            r8 = move-exception
            r0 = r7
        L94:
            if (r6 == 0) goto L9a
            r6.recycle()
            r6 = 0
        L9a:
            r9 = r10
            goto La
        L9d:
            r1 = move-exception
            r0 = r7
        L9f:
            if (r6 == 0) goto La5
            r6.recycle()
            r6 = 0
        La5:
            throw r1
        La6:
            r1 = move-exception
            goto L9f
        La8:
            r8 = move-exception
            goto L94
        Laa:
            r8 = move-exception
            goto L71
        Lac:
            r0 = r7
            goto L66
        */
        throw new UnsupportedOperationException("Method not decompiled: tv.cjump.jni.NativeBitmapFactory.testLib():boolean");
    }

    public static int getNativeConfig(Bitmap.Config config) {
        try {
            if (nativeIntField == null) {
                return 0;
            }
            return nativeIntField.getInt(config);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
            return 0;
        }
    }

    public static Bitmap createBitmap(int width, int height, Bitmap.Config config) {
        return createBitmap(width, height, config, config.equals(Bitmap.Config.ARGB_8888));
    }

    public static void recycle(Bitmap bitmap) {
        bitmap.recycle();
    }

    public static Bitmap createBitmap(int width, int height, Bitmap.Config config, boolean hasAlpha) {
        return (!nativeLibLoaded || nativeIntField == null) ? Bitmap.createBitmap(width, height, config) : createNativeBitmap(width, height, config, hasAlpha);
    }

    private static Bitmap createNativeBitmap(int width, int height, Bitmap.Config config, boolean hasAlpha) {
        int nativeConfig = getNativeConfig(config);
        return Build.VERSION.SDK_INT == 19 ? createBitmap19(width, height, nativeConfig, hasAlpha) : createBitmap(width, height, nativeConfig, hasAlpha);
    }
}
