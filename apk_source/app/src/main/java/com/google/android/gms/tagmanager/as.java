package com.google.android.gms.tagmanager;

import android.content.Context;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.LinkedBlockingQueue;

/* loaded from: classes.dex */
class as extends Thread implements ar {
    private static as ape;
    private final LinkedBlockingQueue<Runnable> apd;
    private volatile at apf;
    private volatile boolean mClosed;
    private final Context mContext;
    private volatile boolean yU;

    private as(Context context) {
        super("GAThread");
        this.apd = new LinkedBlockingQueue<>();
        this.yU = false;
        this.mClosed = false;
        if (context != null) {
            this.mContext = context.getApplicationContext();
        } else {
            this.mContext = context;
        }
        start();
    }

    static as Y(Context context) {
        if (ape == null) {
            ape = new as(context);
        }
        return ape;
    }

    private String g(Throwable th) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);
        th.printStackTrace(printStream);
        printStream.flush();
        return new String(byteArrayOutputStream.toByteArray());
    }

    @Override // com.google.android.gms.tagmanager.ar
    public void b(Runnable runnable) {
        this.apd.add(runnable);
    }

    void b(final String str, final long j) {
        b(new Runnable() { // from class: com.google.android.gms.tagmanager.as.1
            @Override // java.lang.Runnable
            public void run() {
                if (as.this.apf == null) {
                    cy cyVarPu = cy.pu();
                    cyVarPu.a(as.this.mContext, this);
                    as.this.apf = cyVarPu.pv();
                }
                as.this.apf.f(j, str);
            }
        });
    }

    @Override // com.google.android.gms.tagmanager.ar
    public void cz(String str) {
        b(str, System.currentTimeMillis());
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        while (!this.mClosed) {
            try {
                try {
                    Runnable runnableTake = this.apd.take();
                    if (!this.yU) {
                        runnableTake.run();
                    }
                } catch (InterruptedException e) {
                    bh.U(e.toString());
                }
            } catch (Throwable th) {
                bh.T("Error on Google TagManager Thread: " + g(th));
                bh.T("Google TagManager is shutting down.");
                this.yU = true;
            }
        }
    }
}
