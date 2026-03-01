package com.google.android.gms.common.data;

import android.database.CharArrayBuffer;
import android.database.CursorIndexOutOfBoundsException;
import android.database.CursorWindow;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class DataHolder implements SafeParcelable {
    public static final f CREATOR = new f();
    private static final a Kc = new a(new String[0], null) { // from class: com.google.android.gms.common.data.DataHolder.1
        AnonymousClass1(String[] strArr, String str) {
            super(strArr, str);
        }
    };
    private final int BR;
    private final int HF;
    private final String[] JU;
    Bundle JV;
    private final CursorWindow[] JW;
    private final Bundle JX;
    int[] JY;
    int JZ;
    private Object Ka;
    private boolean Kb;
    boolean mClosed;

    /* renamed from: com.google.android.gms.common.data.DataHolder$1 */
    static class AnonymousClass1 extends a {
        AnonymousClass1(String[] strArr, String str) {
            super(strArr, str);
        }
    }

    public static class a {
        private final String[] JU;
        private final ArrayList<HashMap<String, Object>> Kd;
        private final String Ke;
        private final HashMap<Object, Integer> Kf;
        private boolean Kg;
        private String Kh;

        private a(String[] strArr, String str) {
            this.JU = (String[]) n.i(strArr);
            this.Kd = new ArrayList<>();
            this.Ke = str;
            this.Kf = new HashMap<>();
            this.Kg = false;
            this.Kh = null;
        }

        /* synthetic */ a(String[] strArr, String str, AnonymousClass1 anonymousClass1) {
            this(strArr, str);
        }
    }

    DataHolder(int versionCode, String[] columns, CursorWindow[] windows, int statusCode, Bundle metadata) {
        this.mClosed = false;
        this.Kb = true;
        this.BR = versionCode;
        this.JU = columns;
        this.JW = windows;
        this.HF = statusCode;
        this.JX = metadata;
    }

    private DataHolder(a builder, int statusCode, Bundle metadata) {
        this(builder.JU, a(builder, -1), statusCode, metadata);
    }

    public DataHolder(String[] columns, CursorWindow[] windows, int statusCode, Bundle metadata) {
        this.mClosed = false;
        this.Kb = true;
        this.BR = 1;
        this.JU = (String[]) n.i(columns);
        this.JW = (CursorWindow[]) n.i(windows);
        this.HF = statusCode;
        this.JX = metadata;
        gB();
    }

    public static DataHolder a(int i, Bundle bundle) {
        return new DataHolder(Kc, i, bundle);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v40, types: [java.util.List] */
    private static CursorWindow[] a(a aVar, int i) {
        int i2;
        int i3;
        int i4;
        CursorWindow cursorWindow;
        if (aVar.JU.length == 0) {
            return new CursorWindow[0];
        }
        ArrayList arrayListSubList = (i < 0 || i >= aVar.Kd.size()) ? aVar.Kd : aVar.Kd.subList(0, i);
        int size = arrayListSubList.size();
        CursorWindow cursorWindow2 = new CursorWindow(false);
        ArrayList arrayList = new ArrayList();
        arrayList.add(cursorWindow2);
        cursorWindow2.setNumColumns(aVar.JU.length);
        int i5 = 0;
        int i6 = 0;
        while (i5 < size) {
            try {
                if (cursorWindow2.allocRow()) {
                    i2 = i6;
                } else {
                    Log.d("DataHolder", "Allocating additional cursor window for large data set (row " + i5 + ")");
                    cursorWindow2 = new CursorWindow(false);
                    cursorWindow2.setStartPosition(i5);
                    cursorWindow2.setNumColumns(aVar.JU.length);
                    arrayList.add(cursorWindow2);
                    if (!cursorWindow2.allocRow()) {
                        Log.e("DataHolder", "Unable to allocate row to hold data.");
                        arrayList.remove(cursorWindow2);
                        return (CursorWindow[]) arrayList.toArray(new CursorWindow[arrayList.size()]);
                    }
                    i2 = 0;
                }
                Map map = (Map) arrayListSubList.get(i5);
                boolean zPutDouble = true;
                for (int i7 = 0; i7 < aVar.JU.length && zPutDouble; i7++) {
                    String str = aVar.JU[i7];
                    Object obj = map.get(str);
                    if (obj == null) {
                        zPutDouble = cursorWindow2.putNull(i2, i7);
                    } else if (obj instanceof String) {
                        zPutDouble = cursorWindow2.putString((String) obj, i2, i7);
                    } else if (obj instanceof Long) {
                        zPutDouble = cursorWindow2.putLong(((Long) obj).longValue(), i2, i7);
                    } else if (obj instanceof Integer) {
                        zPutDouble = cursorWindow2.putLong(((Integer) obj).intValue(), i2, i7);
                    } else if (obj instanceof Boolean) {
                        zPutDouble = cursorWindow2.putLong(((Boolean) obj).booleanValue() ? 1L : 0L, i2, i7);
                    } else if (obj instanceof byte[]) {
                        zPutDouble = cursorWindow2.putBlob((byte[]) obj, i2, i7);
                    } else if (obj instanceof Double) {
                        zPutDouble = cursorWindow2.putDouble(((Double) obj).doubleValue(), i2, i7);
                    } else {
                        if (!(obj instanceof Float)) {
                            throw new IllegalArgumentException("Unsupported object for column " + str + ": " + obj);
                        }
                        zPutDouble = cursorWindow2.putDouble(((Float) obj).floatValue(), i2, i7);
                    }
                }
                if (zPutDouble) {
                    i3 = i2 + 1;
                    i4 = i5;
                    cursorWindow = cursorWindow2;
                } else {
                    Log.d("DataHolder", "Couldn't populate window data for row " + i5 + " - allocating new window.");
                    cursorWindow2.freeLastRow();
                    CursorWindow cursorWindow3 = new CursorWindow(false);
                    cursorWindow3.setNumColumns(aVar.JU.length);
                    arrayList.add(cursorWindow3);
                    i4 = i5 - 1;
                    cursorWindow = cursorWindow3;
                    i3 = 0;
                }
                cursorWindow2 = cursorWindow;
                i5 = i4 + 1;
                i6 = i3;
            } catch (RuntimeException e) {
                int size2 = arrayList.size();
                for (int i8 = 0; i8 < size2; i8++) {
                    ((CursorWindow) arrayList.get(i8)).close();
                }
                throw e;
            }
        }
        return (CursorWindow[]) arrayList.toArray(new CursorWindow[arrayList.size()]);
    }

    public static DataHolder as(int i) {
        return a(i, (Bundle) null);
    }

    private void g(String str, int i) {
        if (this.JV == null || !this.JV.containsKey(str)) {
            throw new IllegalArgumentException("No such column: " + str);
        }
        if (isClosed()) {
            throw new IllegalArgumentException("Buffer is closed.");
        }
        if (i < 0 || i >= this.JZ) {
            throw new CursorIndexOutOfBoundsException(i, this.JZ);
        }
    }

    public long a(String str, int i, int i2) {
        g(str, i);
        return this.JW[i2].getLong(i, this.JV.getInt(str));
    }

    public void a(String str, int i, int i2, CharArrayBuffer charArrayBuffer) {
        g(str, i);
        this.JW[i2].copyStringToBuffer(i, this.JV.getInt(str), charArrayBuffer);
    }

    public boolean aQ(String str) {
        return this.JV.containsKey(str);
    }

    public int ar(int i) {
        int i2 = 0;
        n.I(i >= 0 && i < this.JZ);
        while (true) {
            if (i2 >= this.JY.length) {
                break;
            }
            if (i < this.JY[i2]) {
                i2--;
                break;
            }
            i2++;
        }
        return i2 == this.JY.length ? i2 - 1 : i2;
    }

    public int b(String str, int i, int i2) {
        g(str, i);
        return this.JW[i2].getInt(i, this.JV.getInt(str));
    }

    public String c(String str, int i, int i2) {
        g(str, i);
        return this.JW[i2].getString(i, this.JV.getInt(str));
    }

    public void close() {
        synchronized (this) {
            if (!this.mClosed) {
                this.mClosed = true;
                for (int i = 0; i < this.JW.length; i++) {
                    this.JW[i].close();
                }
            }
        }
    }

    public boolean d(String str, int i, int i2) {
        g(str, i);
        return Long.valueOf(this.JW[i2].getLong(i, this.JV.getInt(str))).longValue() == 1;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public float e(String str, int i, int i2) {
        g(str, i);
        return this.JW[i2].getFloat(i, this.JV.getInt(str));
    }

    public void e(Object obj) {
        this.Ka = obj;
    }

    public byte[] f(String str, int i, int i2) {
        g(str, i);
        return this.JW[i2].getBlob(i, this.JV.getInt(str));
    }

    protected void finalize() throws Throwable {
        try {
            if (this.Kb && this.JW.length > 0 && !isClosed()) {
                Log.e("DataBuffer", "Internal data leak within a DataBuffer object detected!  Be sure to explicitly call release() on all DataBuffer extending objects when you are done with them. (" + (this.Ka == null ? "internal object: " + toString() : this.Ka.toString()) + ")");
                close();
            }
        } finally {
            super.finalize();
        }
    }

    public Uri g(String str, int i, int i2) {
        String strC = c(str, i, i2);
        if (strC == null) {
            return null;
        }
        return Uri.parse(strC);
    }

    public void gB() {
        this.JV = new Bundle();
        for (int i = 0; i < this.JU.length; i++) {
            this.JV.putInt(this.JU[i], i);
        }
        this.JY = new int[this.JW.length];
        int numRows = 0;
        for (int i2 = 0; i2 < this.JW.length; i2++) {
            this.JY[i2] = numRows;
            numRows += this.JW[i2].getNumRows() - (numRows - this.JW[i2].getStartPosition());
        }
        this.JZ = numRows;
    }

    String[] gC() {
        return this.JU;
    }

    CursorWindow[] gD() {
        return this.JW;
    }

    public int getCount() {
        return this.JZ;
    }

    public int getStatusCode() {
        return this.HF;
    }

    int getVersionCode() {
        return this.BR;
    }

    public Bundle gz() {
        return this.JX;
    }

    public boolean h(String str, int i, int i2) {
        g(str, i);
        return this.JW[i2].isNull(i, this.JV.getInt(str));
    }

    public boolean isClosed() {
        boolean z;
        synchronized (this) {
            z = this.mClosed;
        }
        return z;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        f.a(this, dest, flags);
    }
}
