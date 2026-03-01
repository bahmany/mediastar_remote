package com.google.android.gms.tagmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/* loaded from: classes.dex */
public class PreviewActivity extends Activity {
    private void d(String str, String str2, String str3) {
        AlertDialog alertDialogCreate = new AlertDialog.Builder(this).create();
        alertDialogCreate.setTitle(str);
        alertDialogCreate.setMessage(str2);
        alertDialogCreate.setButton(-1, str3, new DialogInterface.OnClickListener() { // from class: com.google.android.gms.tagmanager.PreviewActivity.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialogCreate.show();
    }

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            bh.U("Preview activity");
            Uri data = getIntent().getData();
            if (!TagManager.getInstance(this).i(data)) {
                String str = "Cannot preview the app with the uri: " + data + ". Launching current version instead.";
                bh.W(str);
                d("Preview failure", str, "Continue");
            }
            Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(getPackageName());
            if (launchIntentForPackage == null) {
                bh.U("No launch activity found for package name: " + getPackageName());
            } else {
                bh.U("Invoke the launch activity for package name: " + getPackageName());
                startActivity(launchIntentForPackage);
            }
        } catch (Exception e) {
            bh.T("Calling preview threw an exception: " + e.getMessage());
        }
    }
}
