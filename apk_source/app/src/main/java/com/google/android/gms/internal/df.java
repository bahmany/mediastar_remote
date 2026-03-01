package com.google.android.gms.internal;

import org.json.JSONException;
import org.json.JSONObject;

@ez
/* loaded from: classes.dex */
public class df {
    private final boolean rb;
    private final boolean rc;
    private final boolean rd;
    private final boolean re;
    private final boolean rf;

    public static final class a {
        private boolean rb;
        private boolean rc;
        private boolean rd;
        private boolean re;
        private boolean rf;

        public df bM() {
            return new df(this);
        }

        public a i(boolean z) {
            this.rb = z;
            return this;
        }

        public a j(boolean z) {
            this.rc = z;
            return this;
        }

        public a k(boolean z) {
            this.rd = z;
            return this;
        }

        public a l(boolean z) {
            this.re = z;
            return this;
        }

        public a m(boolean z) {
            this.rf = z;
            return this;
        }
    }

    private df(a aVar) {
        this.rb = aVar.rb;
        this.rc = aVar.rc;
        this.rd = aVar.rd;
        this.re = aVar.re;
        this.rf = aVar.rf;
    }

    public JSONObject bL() {
        try {
            return new JSONObject().put("sms", this.rb).put("tel", this.rc).put("calendar", this.rd).put("storePicture", this.re).put("inlineVideo", this.rf);
        } catch (JSONException e) {
            gs.b("Error occured while obtaining the MRAID capabilities.", e);
            return null;
        }
    }
}
