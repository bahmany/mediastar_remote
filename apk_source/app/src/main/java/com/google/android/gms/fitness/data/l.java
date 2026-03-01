package com.google.android.gms.fitness.data;

import android.os.RemoteException;
import com.google.android.gms.fitness.data.k;
import com.google.android.gms.fitness.request.DataSourceListener;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class l extends k.a {
    private final DataSourceListener SY;

    public static class a {
        private static final a SZ = new a();
        private final Map<DataSourceListener, l> Ta = new HashMap();

        private a() {
        }

        public static a iO() {
            return SZ;
        }

        public l a(DataSourceListener dataSourceListener) {
            l lVar;
            synchronized (this.Ta) {
                lVar = this.Ta.get(dataSourceListener);
                if (lVar == null) {
                    lVar = new l(dataSourceListener);
                    this.Ta.put(dataSourceListener, lVar);
                }
            }
            return lVar;
        }

        public l b(DataSourceListener dataSourceListener) {
            l lVar;
            synchronized (this.Ta) {
                lVar = this.Ta.get(dataSourceListener);
            }
            return lVar;
        }

        public l c(DataSourceListener dataSourceListener) {
            l lVarRemove;
            synchronized (this.Ta) {
                lVarRemove = this.Ta.remove(dataSourceListener);
                if (lVarRemove == null) {
                    lVarRemove = new l(dataSourceListener);
                }
            }
            return lVarRemove;
        }
    }

    private l(DataSourceListener dataSourceListener) {
        this.SY = (DataSourceListener) com.google.android.gms.common.internal.n.i(dataSourceListener);
    }

    @Override // com.google.android.gms.fitness.data.k
    public void onEvent(DataPoint dataPoint) throws RemoteException {
        this.SY.onEvent(dataPoint);
    }
}
