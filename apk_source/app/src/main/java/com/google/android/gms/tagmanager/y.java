package com.google.android.gms.tagmanager;

import android.content.Context;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/* loaded from: classes.dex */
class y implements aq {
    private static y aoQ;
    private static final Object xz = new Object();
    private String aoR;
    private String aoS;
    private ar aoT;
    private cg aoh;

    private y(Context context) {
        this(as.Y(context), new cw());
    }

    y(ar arVar, cg cgVar) {
        this.aoT = arVar;
        this.aoh = cgVar;
    }

    public static aq W(Context context) {
        y yVar;
        synchronized (xz) {
            if (aoQ == null) {
                aoQ = new y(context);
            }
            yVar = aoQ;
        }
        return yVar;
    }

    @Override // com.google.android.gms.tagmanager.aq
    public boolean cw(String str) {
        if (!this.aoh.eK()) {
            bh.W("Too many urls sent too quickly with the TagManagerSender, rate limiting invoked.");
            return false;
        }
        if (this.aoR != null && this.aoS != null) {
            try {
                str = this.aoR + "?" + this.aoS + "=" + URLEncoder.encode(str, "UTF-8");
                bh.V("Sending wrapped url hit: " + str);
            } catch (UnsupportedEncodingException e) {
                bh.d("Error wrapping URL for testing.", e);
                return false;
            }
        }
        this.aoT.cz(str);
        return true;
    }
}
