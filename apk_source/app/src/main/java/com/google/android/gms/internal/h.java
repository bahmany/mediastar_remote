package com.google.android.gms.internal;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/* loaded from: classes.dex */
public abstract class h implements g {
    protected MotionEvent kw;
    protected DisplayMetrics kx;
    protected m ky;
    private n kz;

    protected h(Context context, m mVar, n nVar) {
        this.ky = mVar;
        this.kz = nVar;
        try {
            this.kx = context.getResources().getDisplayMetrics();
        } catch (UnsupportedOperationException e) {
            this.kx = new DisplayMetrics();
            this.kx.density = 1.0f;
        }
    }

    private String a(Context context, String str, boolean z) {
        byte[] bArrU;
        try {
            synchronized (this) {
                t();
                if (z) {
                    c(context);
                } else {
                    b(context);
                }
                bArrU = u();
            }
            return bArrU.length == 0 ? Integer.toString(5) : a(bArrU, str);
        } catch (UnsupportedEncodingException e) {
            return Integer.toString(7);
        } catch (IOException e2) {
            return Integer.toString(3);
        } catch (NoSuchAlgorithmException e3) {
            return Integer.toString(7);
        }
    }

    private void t() {
        this.kz.reset();
    }

    private byte[] u() throws IOException {
        return this.kz.A();
    }

    @Override // com.google.android.gms.internal.g
    public String a(Context context) {
        return a(context, (String) null, false);
    }

    @Override // com.google.android.gms.internal.g
    public String a(Context context, String str) {
        return a(context, str, true);
    }

    String a(byte[] bArr, String str) throws NoSuchAlgorithmException, IOException {
        byte[] bArrArray;
        if (bArr.length > 239) {
            t();
            a(20, 1L);
            bArr = u();
        }
        if (bArr.length < 239) {
            byte[] bArr2 = new byte[239 - bArr.length];
            new SecureRandom().nextBytes(bArr2);
            bArrArray = ByteBuffer.allocate(240).put((byte) bArr.length).put(bArr).put(bArr2).array();
        } else {
            bArrArray = ByteBuffer.allocate(240).put((byte) bArr.length).put(bArr).array();
        }
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(bArrArray);
        byte[] bArrArray2 = ByteBuffer.allocate(256).put(messageDigest.digest()).put(bArrArray).array();
        byte[] bArr3 = new byte[256];
        new f().a(bArrArray2, bArr3);
        if (str != null && str.length() > 0) {
            a(str, bArr3);
        }
        return this.ky.a(bArr3, true);
    }

    @Override // com.google.android.gms.internal.g
    public void a(int i, int i2, int i3) {
        if (this.kw != null) {
            this.kw.recycle();
        }
        this.kw = MotionEvent.obtain(0L, i3, 1, i * this.kx.density, i2 * this.kx.density, 0.0f, 0.0f, 0, 0.0f, 0.0f, 0, 0);
    }

    protected void a(int i, long j) throws IOException {
        this.kz.b(i, j);
    }

    protected void a(int i, String str) throws IOException {
        this.kz.b(i, str);
    }

    @Override // com.google.android.gms.internal.g
    public void a(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1) {
            if (this.kw != null) {
                this.kw.recycle();
            }
            this.kw = MotionEvent.obtain(motionEvent);
        }
    }

    void a(String str, byte[] bArr) throws UnsupportedEncodingException {
        if (str.length() > 32) {
            str = str.substring(0, 32);
        }
        new pd(str.getBytes("UTF-8")).o(bArr);
    }

    protected abstract void b(Context context);

    protected abstract void c(Context context);
}
