package com.google.android.gms.tagmanager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/* loaded from: classes.dex */
class cy extends cx {
    private static cy arp;
    private static final Object yc = new Object();
    private Context arf;
    private at arg;
    private volatile ar arh;
    private bo arn;
    private Handler handler;
    private int ari = 1800000;
    private boolean arj = true;
    private boolean ark = false;
    private boolean connected = true;
    private boolean arl = true;
    private au arm = new au() { // from class: com.google.android.gms.tagmanager.cy.1
        @Override // com.google.android.gms.tagmanager.au
        public void z(boolean z) {
            cy.this.a(z, cy.this.connected);
        }
    };
    private boolean aro = false;

    private cy() {
    }

    private void eb() {
        this.arn = new bo(this);
        this.arn.z(this.arf);
    }

    private void ec() {
        this.handler = new Handler(this.arf.getMainLooper(), new Handler.Callback() { // from class: com.google.android.gms.tagmanager.cy.2
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                if (1 == msg.what && cy.yc.equals(msg.obj)) {
                    cy.this.dispatch();
                    if (cy.this.ari > 0 && !cy.this.aro) {
                        cy.this.handler.sendMessageDelayed(cy.this.handler.obtainMessage(1, cy.yc), cy.this.ari);
                    }
                }
                return true;
            }
        });
        if (this.ari > 0) {
            this.handler.sendMessageDelayed(this.handler.obtainMessage(1, yc), this.ari);
        }
    }

    public static cy pu() {
        if (arp == null) {
            arp = new cy();
        }
        return arp;
    }

    @Override // com.google.android.gms.tagmanager.cx
    synchronized void A(boolean z) {
        a(this.aro, z);
    }

    synchronized void a(Context context, ar arVar) {
        if (this.arf == null) {
            this.arf = context.getApplicationContext();
            if (this.arh == null) {
                this.arh = arVar;
            }
        }
    }

    synchronized void a(boolean z, boolean z2) {
        if (this.aro != z || this.connected != z2) {
            if ((z || !z2) && this.ari > 0) {
                this.handler.removeMessages(1, yc);
            }
            if (!z && z2 && this.ari > 0) {
                this.handler.sendMessageDelayed(this.handler.obtainMessage(1, yc), this.ari);
            }
            bh.V("PowerSaveMode " + ((z || !z2) ? "initiated." : "terminated."));
            this.aro = z;
            this.connected = z2;
        }
    }

    @Override // com.google.android.gms.tagmanager.cx
    public synchronized void dispatch() {
        if (this.ark) {
            this.arh.b(new Runnable() { // from class: com.google.android.gms.tagmanager.cy.3
                @Override // java.lang.Runnable
                public void run() {
                    cy.this.arg.dispatch();
                }
            });
        } else {
            bh.V("Dispatch call queued. Dispatch will run once initialization is complete.");
            this.arj = true;
        }
    }

    @Override // com.google.android.gms.tagmanager.cx
    synchronized void ee() {
        if (!this.aro && this.connected && this.ari > 0) {
            this.handler.removeMessages(1, yc);
            this.handler.sendMessage(this.handler.obtainMessage(1, yc));
        }
    }

    synchronized at pv() {
        if (this.arg == null) {
            if (this.arf == null) {
                throw new IllegalStateException("Cant get a store unless we have a context");
            }
            this.arg = new cb(this.arm, this.arf);
        }
        if (this.handler == null) {
            ec();
        }
        this.ark = true;
        if (this.arj) {
            dispatch();
            this.arj = false;
        }
        if (this.arn == null && this.arl) {
            eb();
        }
        return this.arg;
    }
}
