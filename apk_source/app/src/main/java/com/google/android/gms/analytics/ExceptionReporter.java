package com.google.android.gms.analytics;

import android.content.Context;
import com.google.android.gms.analytics.HitBuilders;
import java.lang.Thread;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ExceptionReporter implements Thread.UncaughtExceptionHandler {
    private final Context mContext;
    private final Thread.UncaughtExceptionHandler xX;
    private final Tracker xY;
    private ExceptionParser xZ;

    public ExceptionReporter(Tracker tracker, Thread.UncaughtExceptionHandler originalHandler, Context context) {
        if (tracker == null) {
            throw new NullPointerException("tracker cannot be null");
        }
        if (context == null) {
            throw new NullPointerException("context cannot be null");
        }
        this.xX = originalHandler;
        this.xY = tracker;
        this.xZ = new StandardExceptionParser(context, new ArrayList());
        this.mContext = context.getApplicationContext();
        z.V("ExceptionReporter created, original handler is " + (originalHandler == null ? "null" : originalHandler.getClass().getName()));
    }

    Thread.UncaughtExceptionHandler dZ() {
        return this.xX;
    }

    public ExceptionParser getExceptionParser() {
        return this.xZ;
    }

    public void setExceptionParser(ExceptionParser exceptionParser) {
        this.xZ = exceptionParser;
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread t, Throwable e) {
        String description = "UncaughtException";
        if (this.xZ != null) {
            description = this.xZ.getDescription(t != null ? t.getName() : null, e);
        }
        z.V("Tracking Exception: " + description);
        this.xY.send(new HitBuilders.ExceptionBuilder().setDescription(description).setFatal(true).build());
        GoogleAnalytics.getInstance(this.mContext).dispatchLocalHits();
        if (this.xX != null) {
            z.V("Passing exception to original handler.");
            this.xX.uncaughtException(t, e);
        }
    }
}
