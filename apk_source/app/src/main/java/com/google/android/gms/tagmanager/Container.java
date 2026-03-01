package com.google.android.gms.tagmanager;

import android.content.Context;
import com.google.android.gms.internal.c;
import com.google.android.gms.tagmanager.ce;
import com.google.android.gms.tagmanager.cr;
import com.google.android.gms.tagmanager.s;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class Container {
    private final String anR;
    private final DataLayer anS;
    private ct anT;
    private volatile long anW;
    private final Context mContext;
    private Map<String, FunctionCallMacroCallback> anU = new HashMap();
    private Map<String, FunctionCallTagCallback> anV = new HashMap();
    private volatile String anX = "";

    public interface FunctionCallMacroCallback {
        Object getValue(String str, Map<String, Object> map);
    }

    public interface FunctionCallTagCallback {
        void execute(String str, Map<String, Object> map);
    }

    private class a implements s.a {
        private a() {
        }

        @Override // com.google.android.gms.tagmanager.s.a
        public Object b(String str, Map<String, Object> map) {
            FunctionCallMacroCallback functionCallMacroCallbackCk = Container.this.ck(str);
            if (functionCallMacroCallbackCk == null) {
                return null;
            }
            return functionCallMacroCallbackCk.getValue(str, map);
        }
    }

    private class b implements s.a {
        private b() {
        }

        @Override // com.google.android.gms.tagmanager.s.a
        public Object b(String str, Map<String, Object> map) {
            FunctionCallTagCallback functionCallTagCallbackCl = Container.this.cl(str);
            if (functionCallTagCallbackCl != null) {
                functionCallTagCallbackCl.execute(str, map);
            }
            return di.pH();
        }
    }

    Container(Context context, DataLayer dataLayer, String containerId, long lastRefreshTime, c.j resource) {
        this.mContext = context;
        this.anS = dataLayer;
        this.anR = containerId;
        this.anW = lastRefreshTime;
        a(resource.gs);
        if (resource.gr != null) {
            a(resource.gr);
        }
    }

    Container(Context context, DataLayer dataLayer, String containerId, long lastRefreshTime, cr.c resource) {
        this.mContext = context;
        this.anS = dataLayer;
        this.anR = containerId;
        this.anW = lastRefreshTime;
        a(resource);
    }

    private void a(c.f fVar) {
        if (fVar == null) {
            throw new NullPointerException();
        }
        try {
            a(cr.b(fVar));
        } catch (cr.g e) {
            bh.T("Not loading resource: " + fVar + " because it is invalid: " + e.toString());
        }
    }

    private void a(cr.c cVar) {
        this.anX = cVar.getVersion();
        a(new ct(this.mContext, cVar, this.anS, new a(), new b(), cn(this.anX)));
    }

    private synchronized void a(ct ctVar) {
        this.anT = ctVar;
    }

    private void a(c.i[] iVarArr) {
        ArrayList arrayList = new ArrayList();
        for (c.i iVar : iVarArr) {
            arrayList.add(iVar);
        }
        nR().k(arrayList);
    }

    private synchronized ct nR() {
        return this.anT;
    }

    FunctionCallMacroCallback ck(String str) {
        FunctionCallMacroCallback functionCallMacroCallback;
        synchronized (this.anU) {
            functionCallMacroCallback = this.anU.get(str);
        }
        return functionCallMacroCallback;
    }

    FunctionCallTagCallback cl(String str) {
        FunctionCallTagCallback functionCallTagCallback;
        synchronized (this.anV) {
            functionCallTagCallback = this.anV.get(str);
        }
        return functionCallTagCallback;
    }

    void cm(String str) {
        nR().cm(str);
    }

    ag cn(String str) {
        if (ce.oH().oI().equals(ce.a.CONTAINER_DEBUG)) {
        }
        return new br();
    }

    public boolean getBoolean(String key) {
        ct ctVarNR = nR();
        if (ctVarNR == null) {
            bh.T("getBoolean called for closed container.");
            return di.pF().booleanValue();
        }
        try {
            return di.n(ctVarNR.cO(key).getObject()).booleanValue();
        } catch (Exception e) {
            bh.T("Calling getBoolean() threw an exception: " + e.getMessage() + " Returning default value.");
            return di.pF().booleanValue();
        }
    }

    public String getContainerId() {
        return this.anR;
    }

    public double getDouble(String key) {
        ct ctVarNR = nR();
        if (ctVarNR == null) {
            bh.T("getDouble called for closed container.");
            return di.pE().doubleValue();
        }
        try {
            return di.m(ctVarNR.cO(key).getObject()).doubleValue();
        } catch (Exception e) {
            bh.T("Calling getDouble() threw an exception: " + e.getMessage() + " Returning default value.");
            return di.pE().doubleValue();
        }
    }

    public long getLastRefreshTime() {
        return this.anW;
    }

    public long getLong(String key) {
        ct ctVarNR = nR();
        if (ctVarNR == null) {
            bh.T("getLong called for closed container.");
            return di.pD().longValue();
        }
        try {
            return di.l(ctVarNR.cO(key).getObject()).longValue();
        } catch (Exception e) {
            bh.T("Calling getLong() threw an exception: " + e.getMessage() + " Returning default value.");
            return di.pD().longValue();
        }
    }

    public String getString(String key) {
        ct ctVarNR = nR();
        if (ctVarNR == null) {
            bh.T("getString called for closed container.");
            return di.pH();
        }
        try {
            return di.j(ctVarNR.cO(key).getObject());
        } catch (Exception e) {
            bh.T("Calling getString() threw an exception: " + e.getMessage() + " Returning default value.");
            return di.pH();
        }
    }

    public boolean isDefault() {
        return getLastRefreshTime() == 0;
    }

    String nQ() {
        return this.anX;
    }

    public void registerFunctionCallMacroCallback(String customMacroName, FunctionCallMacroCallback customMacroCallback) {
        if (customMacroCallback == null) {
            throw new NullPointerException("Macro handler must be non-null");
        }
        synchronized (this.anU) {
            this.anU.put(customMacroName, customMacroCallback);
        }
    }

    public void registerFunctionCallTagCallback(String customTagName, FunctionCallTagCallback customTagCallback) {
        if (customTagCallback == null) {
            throw new NullPointerException("Tag callback must be non-null");
        }
        synchronized (this.anV) {
            this.anV.put(customTagName, customTagCallback);
        }
    }

    void release() {
        this.anT = null;
    }

    public void unregisterFunctionCallMacroCallback(String customMacroName) {
        synchronized (this.anU) {
            this.anU.remove(customMacroName);
        }
    }

    public void unregisterFunctionCallTagCallback(String customTagName) {
        synchronized (this.anV) {
            this.anV.remove(customTagName);
        }
    }
}
