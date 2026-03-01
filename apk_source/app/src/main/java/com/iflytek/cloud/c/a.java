package com.iflytek.cloud.c;

import android.content.Context;
import android.media.AudioTrack;
import android.os.MemoryFile;
import android.text.TextUtils;
import com.iflytek.cloud.ErrorCode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class a {
    private ArrayList<C0146a> b;
    private Context c;
    private int d;
    private volatile int e;
    private volatile long g;
    private String k;
    private int a = 3145728;
    private MemoryFile f = null;
    private volatile int h = 0;
    private C0146a i = null;
    private String j = "";

    /* renamed from: com.iflytek.cloud.c.a$a, reason: collision with other inner class name */
    public class C0146a {
        long a;
        long b;
        int c;
        int d;

        public C0146a(long j, long j2, int i, int i2) {
            this.a = j;
            this.b = j2;
            this.c = i;
            this.d = i2;
        }
    }

    public a(Context context, int i, String str) {
        this.b = null;
        this.c = null;
        this.d = ErrorCode.MSP_ERROR_LMOD_BASE;
        this.e = 0;
        this.g = 0L;
        this.k = null;
        this.c = context;
        this.e = 0;
        this.b = new ArrayList<>();
        this.g = 0L;
        this.d = i;
        this.k = str;
    }

    private void a(byte[] bArr) throws IOException {
        if (bArr == null || bArr.length == 0) {
            return;
        }
        if (this.f == null) {
            this.j = i();
            this.f = new MemoryFile(this.j, this.a);
            this.f.allowPurging(false);
        }
        this.f.writeBytes(bArr, 0, (int) this.g, bArr.length);
        this.g += bArr.length;
    }

    private String i() {
        return com.iflytek.cloud.a.f.e.a(this.c) + "tts.pcm";
    }

    public int a() {
        return this.d;
    }

    public void a(AudioTrack audioTrack, int i) throws IOException {
        int i2 = this.g - ((long) this.h) < ((long) i) ? (int) (this.g - this.h) : i;
        byte[] bArr = new byte[i2];
        this.f.readBytes(bArr, this.h, 0, i2);
        this.h += i2;
        audioTrack.write(bArr, 0, i2);
        if (i2 < i) {
            b(audioTrack, i);
        }
    }

    public void a(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.a = (str.length() / 5) * 4 * 32 * 1024;
        this.a = this.a > 614400 ? this.a : 614400;
    }

    public void a(ArrayList<byte[]> arrayList, int i, int i2, int i3) throws IOException {
        com.iflytek.cloud.a.f.a.a.a("buffer percent = " + i + " beg=" + i2 + " end=" + i3);
        C0146a c0146a = new C0146a(this.g, this.g, i2, i3);
        int i4 = 0;
        while (true) {
            int i5 = i4;
            if (i5 >= arrayList.size()) {
                c0146a.b = this.g;
                this.e = i;
                this.b.add(c0146a);
                com.iflytek.cloud.a.f.a.a.a("allSize = " + this.g + " maxSize=" + this.a);
                return;
            }
            a(arrayList.get(i5));
            i4 = i5 + 1;
        }
    }

    public boolean a(int i) {
        return this.e > 95 || ((int) (this.g / 32768)) >= i / 1000;
    }

    public void b(AudioTrack audioTrack, int i) {
        byte[] bArr = new byte[i];
        for (int i2 = 0; i2 < i; i2++) {
            bArr[i2] = 0;
        }
        audioTrack.write(bArr, 0, i);
    }

    public boolean b() {
        com.iflytek.cloud.a.f.a.a.a("save to local: totalSize = " + this.g + " maxSize=" + this.a);
        return com.iflytek.cloud.a.f.e.a(this.f, this.g, this.k);
    }

    public void c() throws IOException {
        this.h = 0;
        this.i = null;
        if (this.b.size() > 0) {
            this.i = this.b.get(0);
        }
    }

    public int d() {
        if (this.g <= 0) {
            return 0;
        }
        return (int) ((this.h * this.e) / this.g);
    }

    public C0146a e() {
        if (this.i != null) {
            if (this.h >= this.i.a && this.h <= this.i.b) {
                return this.i;
            }
            Iterator<C0146a> it = this.b.iterator();
            while (it.hasNext()) {
                this.i = it.next();
                if (this.h >= this.i.a && this.h <= this.i.b) {
                    return this.i;
                }
            }
        }
        return null;
    }

    public boolean f() {
        return 100 == this.e && ((long) this.h) >= this.g;
    }

    protected void finalize() throws Throwable {
        super.finalize();
    }

    public boolean g() throws IOException {
        return ((long) this.h) < this.g;
    }

    public void h() {
        try {
            if (this.f != null) {
                this.f.close();
                this.f = null;
            }
            File file = new File(this.j);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
