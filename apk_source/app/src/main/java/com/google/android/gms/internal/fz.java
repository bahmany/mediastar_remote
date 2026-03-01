package com.google.android.gms.internal;

import com.google.android.gms.internal.bq;
import java.util.Collections;
import java.util.List;
import org.json.JSONObject;

@ez
/* loaded from: classes.dex */
public final class fz {
    public final int errorCode;
    public final int orientation;
    public final String qA;
    public final co qB;
    public final List<String> qf;
    public final List<String> qg;
    public final long qj;
    public final cl qy;
    public final cu qz;
    public final gv rN;
    public final String tA;
    public final long tH;
    public final boolean tI;
    public final long tJ;
    public final List<String> tK;
    public final String tN;
    public final av tx;
    public final JSONObject vp;
    public final cm vq;
    public final ay vr;
    public final long vs;
    public final long vt;
    public final bq.a vu;

    @ez
    public static final class a {
        public final int errorCode;
        public final ay lH;
        public final JSONObject vp;
        public final cm vq;
        public final long vs;
        public final long vt;
        public final fi vv;
        public final fk vw;

        public a(fi fiVar, fk fkVar, cm cmVar, ay ayVar, int i, long j, long j2, JSONObject jSONObject) {
            this.vv = fiVar;
            this.vw = fkVar;
            this.vq = cmVar;
            this.lH = ayVar;
            this.errorCode = i;
            this.vs = j;
            this.vt = j2;
            this.vp = jSONObject;
        }
    }

    public fz(av avVar, gv gvVar, List<String> list, int i, List<String> list2, List<String> list3, int i2, long j, String str, boolean z, cl clVar, cu cuVar, String str2, cm cmVar, co coVar, long j2, ay ayVar, long j3, long j4, long j5, String str3, JSONObject jSONObject, bq.a aVar) {
        this.tx = avVar;
        this.rN = gvVar;
        this.qf = list != null ? Collections.unmodifiableList(list) : null;
        this.errorCode = i;
        this.qg = list2 != null ? Collections.unmodifiableList(list2) : null;
        this.tK = list3 != null ? Collections.unmodifiableList(list3) : null;
        this.orientation = i2;
        this.qj = j;
        this.tA = str;
        this.tI = z;
        this.qy = clVar;
        this.qz = cuVar;
        this.qA = str2;
        this.vq = cmVar;
        this.qB = coVar;
        this.tJ = j2;
        this.vr = ayVar;
        this.tH = j3;
        this.vs = j4;
        this.vt = j5;
        this.tN = str3;
        this.vp = jSONObject;
        this.vu = aVar;
    }

    public fz(a aVar, gv gvVar, cl clVar, cu cuVar, String str, co coVar, bq.a aVar2) {
        this(aVar.vv.tx, gvVar, aVar.vw.qf, aVar.errorCode, aVar.vw.qg, aVar.vw.tK, aVar.vw.orientation, aVar.vw.qj, aVar.vv.tA, aVar.vw.tI, clVar, cuVar, str, aVar.vq, coVar, aVar.vw.tJ, aVar.lH, aVar.vw.tH, aVar.vs, aVar.vt, aVar.vw.tN, aVar.vp, aVar2);
    }
}
