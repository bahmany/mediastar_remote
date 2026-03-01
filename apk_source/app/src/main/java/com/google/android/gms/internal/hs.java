package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import com.google.android.gms.appindexing.AppIndexApi;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.internal.he;
import com.google.android.gms.internal.hq;
import com.google.android.gms.internal.lk;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.zip.CRC32;

/* loaded from: classes.dex */
public class hs implements SafeParcelable {
    public static final ht CREATOR = new ht();
    final int BR;
    final hg CD;
    final long CE;
    final int CF;
    final he CG;
    public final String oT;

    hs(int i, hg hgVar, long j, int i2, String str, he heVar) {
        this.BR = i;
        this.CD = hgVar;
        this.CE = j;
        this.CF = i2;
        this.oT = str;
        this.CG = heVar;
    }

    public hs(hg hgVar, long j, int i) {
        this(1, hgVar, j, i, (String) null, (he) null);
    }

    public hs(String str, Intent intent, String str2, Uri uri, String str3, List<AppIndexApi.AppIndexingLink> list) {
        this(1, a(str, intent), System.currentTimeMillis(), 0, (String) null, a(intent, str2, uri, str3, list).fk());
    }

    public static he.a a(Intent intent, String str, Uri uri, String str2, List<AppIndexApi.AppIndexingLink> list) {
        String string;
        he.a aVar = new he.a();
        aVar.a(av(str));
        if (uri != null) {
            aVar.a(f(uri));
        }
        if (list != null) {
            aVar.a(b(list));
        }
        String action = intent.getAction();
        if (action != null) {
            aVar.a(j("intent_action", action));
        }
        String dataString = intent.getDataString();
        if (dataString != null) {
            aVar.a(j("intent_data", dataString));
        }
        ComponentName component = intent.getComponent();
        if (component != null) {
            aVar.a(j("intent_activity", component.getClassName()));
        }
        Bundle extras = intent.getExtras();
        if (extras != null && (string = extras.getString("intent_extra_data_key")) != null) {
            aVar.a(j("intent_extra_data", string));
        }
        return aVar.ar(str2).D(true);
    }

    public static hg a(String str, Intent intent) {
        return i(str, g(intent));
    }

    private static hi av(String str) {
        return new hi(str, new hq.a("title").P(1).F(true).au("name").fn(), "text1");
    }

    private static hi b(List<AppIndexApi.AppIndexingLink> list) {
        lk.a aVar = new lk.a();
        lk.a.C0075a[] c0075aArr = new lk.a.C0075a[list.size()];
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= c0075aArr.length) {
                aVar.adt = c0075aArr;
                return new hi(pm.f(aVar), new hq.a("outlinks").E(true).au(".private:outLinks").at("blob").fn());
            }
            c0075aArr[i2] = new lk.a.C0075a();
            AppIndexApi.AppIndexingLink appIndexingLink = list.get(i2);
            c0075aArr[i2].adv = appIndexingLink.appIndexingUrl.toString();
            c0075aArr[i2].adw = appIndexingLink.webUrl.toString();
            c0075aArr[i2].viewId = appIndexingLink.viewId;
            i = i2 + 1;
        }
    }

    private static hi f(Uri uri) {
        return new hi(uri.toString(), new hq.a("web_url").P(4).E(true).au("url").fn());
    }

    private static String g(Intent intent) {
        String uri = intent.toUri(1);
        CRC32 crc32 = new CRC32();
        try {
            crc32.update(uri.getBytes("UTF-8"));
            return Long.toHexString(crc32.getValue());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    private static hg i(String str, String str2) {
        return new hg(str, "", str2);
    }

    private static hi j(String str, String str2) {
        return new hi(str2, new hq.a(str).E(true).fn(), str);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        ht htVar = CREATOR;
        return 0;
    }

    public String toString() {
        return String.format("UsageInfo[documentId=%s, timestamp=%d, usageType=%d]", this.CD, Long.valueOf(this.CE), Integer.valueOf(this.CF));
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        ht htVar = CREATOR;
        ht.a(this, dest, flags);
    }
}
