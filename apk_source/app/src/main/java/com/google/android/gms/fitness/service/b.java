package com.google.android.gms.fitness.service;

import android.os.RemoteException;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.k;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
class b implements SensorEventDispatcher {
    private final k Up;

    b(k kVar) {
        this.Up = (k) n.i(kVar);
    }

    @Override // com.google.android.gms.fitness.service.SensorEventDispatcher
    public void publish(DataPoint dataPoint) throws RemoteException {
        this.Up.onEvent(dataPoint);
    }

    @Override // com.google.android.gms.fitness.service.SensorEventDispatcher
    public void publish(List<DataPoint> dataPoints) throws RemoteException {
        Iterator<DataPoint> it = dataPoints.iterator();
        while (it.hasNext()) {
            publish(it.next());
        }
    }
}
