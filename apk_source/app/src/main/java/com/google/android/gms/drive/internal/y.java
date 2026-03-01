package com.google.android.gms.drive.internal;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Pair;
import com.google.android.gms.drive.events.ChangeEvent;
import com.google.android.gms.drive.events.ChangeListener;
import com.google.android.gms.drive.events.CompletionEvent;
import com.google.android.gms.drive.events.CompletionListener;
import com.google.android.gms.drive.events.DriveEvent;
import com.google.android.gms.drive.internal.ad;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class y extends ad.a {
    private final int NS;
    private final com.google.android.gms.drive.events.c OW;
    private final a OX;
    private final List<Integer> OY = new ArrayList();

    private static class a extends Handler {
        private final Context mContext;

        private a(Looper looper, Context context) {
            super(looper);
            this.mContext = context;
        }

        public void a(com.google.android.gms.drive.events.c cVar, DriveEvent driveEvent) {
            sendMessage(obtainMessage(1, new Pair(cVar, driveEvent)));
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Pair pair = (Pair) msg.obj;
                    com.google.android.gms.drive.events.c cVar = (com.google.android.gms.drive.events.c) pair.first;
                    DriveEvent driveEvent = (DriveEvent) pair.second;
                    switch (driveEvent.getType()) {
                        case 1:
                            if (!(cVar instanceof DriveEvent.Listener)) {
                                ((ChangeListener) cVar).onChange((ChangeEvent) driveEvent);
                                break;
                            } else {
                                ((DriveEvent.Listener) cVar).onEvent((ChangeEvent) driveEvent);
                                break;
                            }
                        case 2:
                            ((CompletionListener) cVar).onCompletion((CompletionEvent) driveEvent);
                            break;
                        default:
                            v.p("EventCallback", "Unexpected event: " + driveEvent);
                            break;
                    }
                default:
                    v.e(this.mContext, "EventCallback", "Don't know how to handle this event");
                    break;
            }
        }
    }

    public y(Looper looper, Context context, int i, com.google.android.gms.drive.events.c cVar) {
        this.NS = i;
        this.OW = cVar;
        this.OX = new a(looper, context);
    }

    public void bq(int i) {
        this.OY.add(Integer.valueOf(i));
    }

    public boolean br(int i) {
        return this.OY.contains(Integer.valueOf(i));
    }

    @Override // com.google.android.gms.drive.internal.ad
    public void c(OnEventResponse onEventResponse) throws RemoteException {
        DriveEvent driveEventIh = onEventResponse.ih();
        com.google.android.gms.common.internal.n.I(this.NS == driveEventIh.getType());
        com.google.android.gms.common.internal.n.I(this.OY.contains(Integer.valueOf(driveEventIh.getType())));
        this.OX.a(this.OW, driveEventIh);
    }
}
