package com.hisilicon.multiscreen.protocol.utils;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.hisilicon.multiscreen.protocol.message.Action;
import com.hisilicon.multiscreen.protocol.message.AppInfo;
import com.hisilicon.multiscreen.protocol.message.Argument;
import com.hisilicon.multiscreen.protocol.message.ArgumentValue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class AppListTransmitter {
    public static List<AppInfo> fromTransmitPacket(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bIn = new ByteArrayInputStream(data);
        ObjectInputStream oIn = new ObjectInputStream(bIn);
        ArrayList<AppInfo> appList = (ArrayList) oIn.readObject();
        oIn.close();
        return appList;
    }

    public static byte[] toTransmitPacket(PackageManager pm) throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ObjectOutputStream oOut = new ObjectOutputStream(bOut);
        ByteArrayOutputStream iOut = new ByteArrayOutputStream();
        Intent i = new Intent("android.intent.action.MAIN", (Uri) null);
        i.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> rList = pm.queryIntentActivities(i, 0);
        if (rList == null) {
            return null;
        }
        Collections.sort(rList, new ResolveInfo.DisplayNameComparator(pm));
        int pIndex = 0;
        ArrayList<AppInfo> appList = new ArrayList<>();
        for (ResolveInfo resolve : rList) {
            AppInfo app = new AppInfo();
            app.setAppName(resolve.loadLabel(pm).toString());
            app.setPackageName(resolve.activityInfo.packageName);
            app.setPackageIndex(pIndex);
            Drawable img = resolve.loadIcon(pm);
            Bitmap src = Bitmap.createBitmap(img.getIntrinsicWidth(), img.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(src);
            img.setBounds(0, 0, img.getIntrinsicWidth(), img.getIntrinsicHeight());
            img.draw(canvas);
            Bitmap zoom = zoomBitmap(src, 40, 40);
            iOut.reset();
            zoom.compress(Bitmap.CompressFormat.PNG, 50, iOut);
            iOut.flush();
            app.setPackageIcon(iOut.toByteArray());
            appList.add(app);
            src.recycle();
            zoom.recycle();
            pIndex++;
        }
        oOut.writeObject(appList);
        oOut.flush();
        byte[] byteArray = bOut.toByteArray();
        oOut.close();
        iOut.close();
        return byteArray;
    }

    public static Action toTransmitPacket(Action action, PackageManager pm) throws IOException {
        ByteArrayOutputStream iOut = new ByteArrayOutputStream();
        Intent i = new Intent("android.intent.action.MAIN", (Uri) null);
        i.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> rList = pm.queryIntentActivities(i, 0);
        if (rList == null) {
            return null;
        }
        Collections.sort(rList, new ResolveInfo.DisplayNameComparator(pm));
        int pIndex = 0;
        for (ResolveInfo resolve : rList) {
            Drawable img = resolve.loadIcon(pm);
            Bitmap src = Bitmap.createBitmap(img.getIntrinsicWidth(), img.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(src);
            img.setBounds(0, 0, img.getIntrinsicWidth(), img.getIntrinsicHeight());
            img.draw(canvas);
            Bitmap zoom = zoomBitmap(src, 40, 40);
            iOut.reset();
            zoom.compress(Bitmap.CompressFormat.PNG, 50, iOut);
            iOut.flush();
            byte[] data = iOut.toByteArray();
            Argument argument = new Argument();
            for (int j = 0; j < 4; j++) {
                ArgumentValue argumentValue = new ArgumentValue();
                switch (j) {
                    case 0:
                        argumentValue.setKey("AppName");
                        argumentValue.setValue(resolve.loadLabel(pm).toString());
                        break;
                    case 1:
                        argumentValue.setKey("PackageName");
                        argumentValue.setValue(resolve.activityInfo.packageName);
                        break;
                    case 2:
                        argumentValue.setKey("PackageIndex");
                        argumentValue.setValue(Integer.valueOf(pIndex));
                        break;
                    case 3:
                        argumentValue.setKey("PackageIcon");
                        argumentValue.setValue(data);
                        break;
                }
                argument.addArgumentValue(argumentValue);
            }
            action.addArgument(argument);
            src.recycle();
            zoom.recycle();
            pIndex++;
        }
        iOut.close();
        return action;
    }

    private static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int h = bitmap.getHeight();
        int w = bitmap.getWidth();
        float sx = width / w;
        float sy = height / h;
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy);
        Bitmap zoom = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return zoom;
    }
}
