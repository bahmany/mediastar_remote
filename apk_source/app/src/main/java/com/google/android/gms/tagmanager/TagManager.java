package com.google.android.gms.tagmanager;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.tagmanager.DataLayer;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/* loaded from: classes.dex */
public class TagManager {
    private static TagManager arC;
    private final DataLayer anS;
    private final r aqj;
    private final cx arA;
    private final ConcurrentMap<n, Boolean> arB;
    private final a arz;
    private final Context mContext;

    interface a {
        o a(Context context, TagManager tagManager, Looper looper, String str, int i, r rVar);
    }

    TagManager(Context context, a containerHolderLoaderProvider, DataLayer dataLayer, cx serviceManager) {
        if (context == null) {
            throw new NullPointerException("context cannot be null");
        }
        this.mContext = context.getApplicationContext();
        this.arA = serviceManager;
        this.arz = containerHolderLoaderProvider;
        this.arB = new ConcurrentHashMap();
        this.anS = dataLayer;
        this.anS.a(new DataLayer.b() { // from class: com.google.android.gms.tagmanager.TagManager.1
            @Override // com.google.android.gms.tagmanager.DataLayer.b
            public void D(Map<String, Object> map) {
                Object obj = map.get(DataLayer.EVENT_KEY);
                if (obj != null) {
                    TagManager.this.cQ(obj.toString());
                }
            }
        });
        this.anS.a(new d(this.mContext));
        this.aqj = new r();
        pw();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cQ(String str) {
        Iterator<n> it = this.arB.keySet().iterator();
        while (it.hasNext()) {
            it.next().cm(str);
        }
    }

    public static TagManager getInstance(Context context) {
        TagManager tagManager;
        synchronized (TagManager.class) {
            if (arC == null) {
                if (context == null) {
                    bh.T("TagManager.getInstance requires non-null context.");
                    throw new NullPointerException();
                }
                arC = new TagManager(context, new a() { // from class: com.google.android.gms.tagmanager.TagManager.2
                    @Override // com.google.android.gms.tagmanager.TagManager.a
                    public o a(Context context2, TagManager tagManager2, Looper looper, String str, int i, r rVar) {
                        return new o(context2, tagManager2, looper, str, i, rVar);
                    }
                }, new DataLayer(new v(context)), cy.pu());
            }
            tagManager = arC;
        }
        return tagManager;
    }

    private void pw() {
        if (Build.VERSION.SDK_INT >= 14) {
            this.mContext.registerComponentCallbacks(new ComponentCallbacks2() { // from class: com.google.android.gms.tagmanager.TagManager.3
                @Override // android.content.ComponentCallbacks
                public void onConfigurationChanged(Configuration configuration) {
                }

                @Override // android.content.ComponentCallbacks
                public void onLowMemory() {
                }

                @Override // android.content.ComponentCallbacks2
                public void onTrimMemory(int i) {
                    if (i == 20) {
                        TagManager.this.dispatch();
                    }
                }
            });
        }
    }

    void a(n nVar) {
        this.arB.put(nVar, true);
    }

    boolean b(n nVar) {
        return this.arB.remove(nVar) != null;
    }

    public void dispatch() {
        this.arA.dispatch();
    }

    public DataLayer getDataLayer() {
        return this.anS;
    }

    synchronized boolean i(Uri uri) {
        boolean z;
        ce ceVarOH = ce.oH();
        if (ceVarOH.i(uri)) {
            String containerId = ceVarOH.getContainerId();
            switch (ceVarOH.oI()) {
                case NONE:
                    for (n nVar : this.arB.keySet()) {
                        if (nVar.getContainerId().equals(containerId)) {
                            nVar.co(null);
                            nVar.refresh();
                        }
                    }
                    break;
                case CONTAINER:
                case CONTAINER_DEBUG:
                    for (n nVar2 : this.arB.keySet()) {
                        if (nVar2.getContainerId().equals(containerId)) {
                            nVar2.co(ceVarOH.oJ());
                            nVar2.refresh();
                        } else if (nVar2.nS() != null) {
                            nVar2.co(null);
                            nVar2.refresh();
                        }
                    }
                    break;
            }
            z = true;
        } else {
            z = false;
        }
        return z;
    }

    public PendingResult<ContainerHolder> loadContainerDefaultOnly(String containerId, int defaultContainerResourceId) {
        o oVarA = this.arz.a(this.mContext, this, null, containerId, defaultContainerResourceId, this.aqj);
        oVarA.nV();
        return oVarA;
    }

    public PendingResult<ContainerHolder> loadContainerDefaultOnly(String containerId, int defaultContainerResourceId, Handler handler) {
        o oVarA = this.arz.a(this.mContext, this, handler.getLooper(), containerId, defaultContainerResourceId, this.aqj);
        oVarA.nV();
        return oVarA;
    }

    public PendingResult<ContainerHolder> loadContainerPreferFresh(String containerId, int defaultContainerResourceId) {
        o oVarA = this.arz.a(this.mContext, this, null, containerId, defaultContainerResourceId, this.aqj);
        oVarA.nX();
        return oVarA;
    }

    public PendingResult<ContainerHolder> loadContainerPreferFresh(String containerId, int defaultContainerResourceId, Handler handler) {
        o oVarA = this.arz.a(this.mContext, this, handler.getLooper(), containerId, defaultContainerResourceId, this.aqj);
        oVarA.nX();
        return oVarA;
    }

    public PendingResult<ContainerHolder> loadContainerPreferNonDefault(String containerId, int defaultContainerResourceId) {
        o oVarA = this.arz.a(this.mContext, this, null, containerId, defaultContainerResourceId, this.aqj);
        oVarA.nW();
        return oVarA;
    }

    public PendingResult<ContainerHolder> loadContainerPreferNonDefault(String containerId, int defaultContainerResourceId, Handler handler) {
        o oVarA = this.arz.a(this.mContext, this, handler.getLooper(), containerId, defaultContainerResourceId, this.aqj);
        oVarA.nW();
        return oVarA;
    }

    public void setVerboseLoggingEnabled(boolean enableVerboseLogging) {
        bh.setLogLevel(enableVerboseLogging ? 2 : 5);
    }
}
