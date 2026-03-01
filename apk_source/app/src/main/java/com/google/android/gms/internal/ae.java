package com.google.android.gms.internal;

import android.content.Context;
import android.view.View;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.WeakHashMap;

@ez
/* loaded from: classes.dex */
public final class ae implements ag {
    private final Object mw = new Object();
    private final WeakHashMap<fz, af> mx = new WeakHashMap<>();
    private final ArrayList<af> my = new ArrayList<>();

    public af a(Context context, ay ayVar, fz fzVar, View view, gt gtVar) {
        af afVar;
        synchronized (this.mw) {
            if (c(fzVar)) {
                afVar = this.mx.get(fzVar);
            } else {
                afVar = new af(context, ayVar, fzVar, view, gtVar);
                afVar.a(this);
                this.mx.put(fzVar, afVar);
                this.my.add(afVar);
            }
        }
        return afVar;
    }

    public af a(ay ayVar, fz fzVar) {
        return a(fzVar.rN.getContext(), ayVar, fzVar, fzVar.rN, fzVar.rN.dy());
    }

    @Override // com.google.android.gms.internal.ag
    public void a(af afVar) {
        synchronized (this.mw) {
            if (!afVar.aH()) {
                this.my.remove(afVar);
            }
        }
    }

    public boolean c(fz fzVar) {
        boolean z;
        synchronized (this.mw) {
            af afVar = this.mx.get(fzVar);
            z = afVar != null && afVar.aH();
        }
        return z;
    }

    public void d(fz fzVar) {
        synchronized (this.mw) {
            af afVar = this.mx.get(fzVar);
            if (afVar != null) {
                afVar.aF();
            }
        }
    }

    public void pause() {
        synchronized (this.mw) {
            Iterator<af> it = this.my.iterator();
            while (it.hasNext()) {
                it.next().pause();
            }
        }
    }

    public void resume() {
        synchronized (this.mw) {
            Iterator<af> it = this.my.iterator();
            while (it.hasNext()) {
                it.next().resume();
            }
        }
    }

    public void stop() {
        synchronized (this.mw) {
            Iterator<af> it = this.my.iterator();
            while (it.hasNext()) {
                it.next().stop();
            }
        }
    }
}
