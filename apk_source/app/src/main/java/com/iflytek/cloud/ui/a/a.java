package com.iflytek.cloud.ui.a;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/* loaded from: classes.dex */
public class a {
    private static HashMap<String, Drawable> e = new HashMap<>();
    private static HashMap<String, Drawable> f = new HashMap<>();
    public static int a = 3;
    public static int b = 4;
    public static int c = 7;
    public static int d = 8;

    public static synchronized Drawable a(Context context, String str) throws Exception {
        Drawable drawableC;
        drawableC = e.get(str);
        if (drawableC == null) {
            drawableC = c(context, str);
            e.put(str, drawableC);
        }
        return drawableC;
    }

    public static View a(Context context, String str, ViewGroup viewGroup) throws Exception {
        return ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(context.getAssets().openXmlResourceParser("assets/iflytek/" + str + ".xml"), viewGroup);
    }

    public static int[] a() {
        return new int[]{-1579033, -9933198};
    }

    private static InputStream b(Context context, String str) throws IOException {
        return context.getAssets().open(str);
    }

    public static int[] b() {
        return new int[]{20, 16};
    }

    private static Drawable c(Context context, String str) throws Exception {
        InputStream inputStreamB = b(context, "iflytek/" + str + ".png");
        TypedValue typedValue = new TypedValue();
        typedValue.density = 240;
        Drawable drawableA = Build.VERSION.SDK_INT > a ? b.a(context.getResources(), typedValue, inputStreamB, str, (BitmapFactory.Options) null) : Drawable.createFromResourceStream(context.getResources(), typedValue, inputStreamB, str);
        if (inputStreamB != null) {
            inputStreamB.close();
        }
        return drawableA;
    }
}
