package com.google.android.gms.internal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.CalendarContract;
import android.text.TextUtils;
import com.google.android.gms.R;
import com.google.android.gms.plus.PlusShare;
import java.util.Map;
import org.json.JSONObject;

@ez
/* loaded from: classes.dex */
public class dc {
    private final Context mContext;
    private final gv md;
    private final Map<String, String> qM;
    private String qN;
    private long qO;
    private long qP;
    private String qQ;
    private String qR;

    public dc(gv gvVar, Map<String, String> map) {
        this.md = gvVar;
        this.qM = map;
        this.mContext = gvVar.dA();
        bG();
    }

    private String A(String str) {
        return TextUtils.isEmpty(this.qM.get(str)) ? "" : this.qM.get(str);
    }

    private void bG() {
        this.qN = A(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_DESCRIPTION);
        this.qQ = A("summary");
        this.qO = gj.O(this.qM.get("start"));
        this.qP = gj.O(this.qM.get("end"));
        this.qR = A("location");
    }

    Intent bH() {
        Intent data = new Intent("android.intent.action.EDIT").setData(CalendarContract.Events.CONTENT_URI);
        data.putExtra("title", this.qQ);
        data.putExtra("eventLocation", this.qR);
        data.putExtra(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_DESCRIPTION, this.qN);
        data.putExtra("beginTime", this.qO);
        data.putExtra("endTime", this.qP);
        data.setFlags(268435456);
        return data;
    }

    public void execute() {
        if (!new bl(this.mContext).bo()) {
            gs.W("This feature is not available on this version of the device.");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setTitle(gb.c(R.string.create_calendar_title, "Create calendar event"));
        builder.setMessage(gb.c(R.string.create_calendar_message, "Allow Ad to create a calendar event?"));
        builder.setPositiveButton(gb.c(R.string.accept, "Accept"), new DialogInterface.OnClickListener() { // from class: com.google.android.gms.internal.dc.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dc.this.mContext.startActivity(dc.this.bH());
            }
        });
        builder.setNegativeButton(gb.c(R.string.decline, "Decline"), new DialogInterface.OnClickListener() { // from class: com.google.android.gms.internal.dc.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dc.this.md.b("onCalendarEventCanceled", new JSONObject());
            }
        });
        builder.create().show();
    }
}
