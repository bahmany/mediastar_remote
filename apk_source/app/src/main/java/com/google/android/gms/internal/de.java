package com.google.android.gms.internal;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.URLUtil;
import com.google.android.gms.R;
import java.util.Map;
import org.json.JSONObject;

@ez
/* loaded from: classes.dex */
public class de {
    private final Context mContext;
    private final gv md;
    private final Map<String, String> qM;

    public de(gv gvVar, Map<String, String> map) {
        this.md = gvVar;
        this.qM = map;
        this.mContext = gvVar.dA();
    }

    String B(String str) {
        return Uri.parse(str).getLastPathSegment();
    }

    DownloadManager.Request b(String str, String str2) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(str));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, str2);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(1);
        return request;
    }

    public void execute() {
        if (!new bl(this.mContext).bl()) {
            gs.W("Store picture feature is not supported on this device.");
            return;
        }
        if (TextUtils.isEmpty(this.qM.get("iurl"))) {
            gs.W("Image url cannot be empty.");
            return;
        }
        final String str = this.qM.get("iurl");
        if (!URLUtil.isValidUrl(str)) {
            gs.W("Invalid image url:" + str);
            return;
        }
        final String strB = B(str);
        if (!gj.N(strB)) {
            gs.W("Image type not recognized:");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setTitle(gb.c(R.string.store_picture_title, "Save image"));
        builder.setMessage(gb.c(R.string.store_picture_message, "Allow Ad to store image in Picture gallery?"));
        builder.setPositiveButton(gb.c(R.string.accept, "Accept"), new DialogInterface.OnClickListener() { // from class: com.google.android.gms.internal.de.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                try {
                    ((DownloadManager) de.this.mContext.getSystemService("download")).enqueue(de.this.b(str, strB));
                } catch (IllegalStateException e) {
                    gs.U("Could not store picture.");
                }
            }
        });
        builder.setNegativeButton(gb.c(R.string.decline, "Decline"), new DialogInterface.OnClickListener() { // from class: com.google.android.gms.internal.de.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                de.this.md.b("onStorePictureCanceled", new JSONObject());
            }
        });
        builder.create().show();
    }
}
