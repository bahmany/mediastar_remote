package com.google.android.gms.internal;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import org.teleal.cling.model.ServiceReference;

@ez
/* loaded from: classes.dex */
public final class dh {
    public static boolean a(Context context, dj djVar, dq dqVar) {
        if (djVar == null) {
            gs.W("No intent data for launcher overlay.");
            return false;
        }
        Intent intent = new Intent();
        if (TextUtils.isEmpty(djVar.rq)) {
            gs.W("Open GMSG did not contain a URL.");
            return false;
        }
        if (TextUtils.isEmpty(djVar.mimeType)) {
            intent.setData(Uri.parse(djVar.rq));
        } else {
            intent.setDataAndType(Uri.parse(djVar.rq), djVar.mimeType);
        }
        intent.setAction("android.intent.action.VIEW");
        if (!TextUtils.isEmpty(djVar.packageName)) {
            intent.setPackage(djVar.packageName);
        }
        if (!TextUtils.isEmpty(djVar.rr)) {
            String[] strArrSplit = djVar.rr.split(ServiceReference.DELIMITER, 2);
            if (strArrSplit.length < 2) {
                gs.W("Could not parse component name from open GMSG: " + djVar.rr);
                return false;
            }
            intent.setClassName(strArrSplit[0], strArrSplit[1]);
        }
        try {
            gs.V("Launching an intent: " + intent);
            context.startActivity(intent);
            dqVar.ab();
            return true;
        } catch (ActivityNotFoundException e) {
            gs.W(e.getMessage());
            return false;
        }
    }
}
