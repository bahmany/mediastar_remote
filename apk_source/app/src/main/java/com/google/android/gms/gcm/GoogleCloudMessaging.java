package com.google.android.gms.gcm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import com.google.android.gms.common.GooglePlayServicesUtil;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class GoogleCloudMessaging {
    public static final String ERROR_MAIN_THREAD = "MAIN_THREAD";
    public static final String ERROR_SERVICE_NOT_AVAILABLE = "SERVICE_NOT_AVAILABLE";
    public static final String MESSAGE_TYPE_DELETED = "deleted_messages";
    public static final String MESSAGE_TYPE_MESSAGE = "gcm";
    public static final String MESSAGE_TYPE_SEND_ERROR = "send_error";
    static GoogleCloudMessaging adk;
    private PendingIntent adl;
    final BlockingQueue<Intent> adm = new LinkedBlockingQueue();
    private Handler adn = new Handler(Looper.getMainLooper()) { // from class: com.google.android.gms.gcm.GoogleCloudMessaging.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            GoogleCloudMessaging.this.adm.add((Intent) msg.obj);
        }
    };
    private Messenger ado = new Messenger(this.adn);
    private Context lB;

    private void a(String str, String str2, long j, int i, Bundle bundle) throws IOException {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException(ERROR_MAIN_THREAD);
        }
        if (str == null) {
            throw new IllegalArgumentException("Missing 'to'");
        }
        Intent intent = new Intent("com.google.android.gcm.intent.SEND");
        intent.putExtras(bundle);
        j(intent);
        intent.setPackage(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE);
        intent.putExtra("google.to", str);
        intent.putExtra("google.message_id", str2);
        intent.putExtra("google.ttl", Long.toString(j));
        intent.putExtra("google.delay", Integer.toString(i));
        this.lB.sendOrderedBroadcast(intent, "com.google.android.gtalkservice.permission.GTALK_SERVICE");
    }

    private void d(String... strArr) {
        String strE = e(strArr);
        Intent intent = new Intent("com.google.android.c2dm.intent.REGISTER");
        intent.setPackage(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE);
        intent.putExtra("google.messenger", this.ado);
        j(intent);
        intent.putExtra("sender", strE);
        this.lB.startService(intent);
    }

    public static synchronized GoogleCloudMessaging getInstance(Context context) {
        if (adk == null) {
            adk = new GoogleCloudMessaging();
            adk.lB = context;
        }
        return adk;
    }

    private void lL() {
        Intent intent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
        intent.setPackage(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE);
        this.adm.clear();
        intent.putExtra("google.messenger", this.ado);
        j(intent);
        this.lB.startService(intent);
    }

    public void close() {
        lM();
    }

    String e(String... strArr) {
        if (strArr == null || strArr.length == 0) {
            throw new IllegalArgumentException("No senderIds");
        }
        StringBuilder sb = new StringBuilder(strArr[0]);
        for (int i = 1; i < strArr.length; i++) {
            sb.append(',').append(strArr[i]);
        }
        return sb.toString();
    }

    public String getMessageType(Intent intent) {
        if (!"com.google.android.c2dm.intent.RECEIVE".equals(intent.getAction())) {
            return null;
        }
        String stringExtra = intent.getStringExtra("message_type");
        return stringExtra == null ? MESSAGE_TYPE_MESSAGE : stringExtra;
    }

    synchronized void j(Intent intent) {
        if (this.adl == null) {
            Intent intent2 = new Intent();
            intent2.setPackage("com.google.example.invalidpackage");
            this.adl = PendingIntent.getBroadcast(this.lB, 0, intent2, 0);
        }
        intent.putExtra("app", this.adl);
    }

    synchronized void lM() {
        if (this.adl != null) {
            this.adl.cancel();
            this.adl = null;
        }
    }

    public String register(String... senderIds) throws InterruptedException, IOException {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException(ERROR_MAIN_THREAD);
        }
        this.adm.clear();
        d(senderIds);
        try {
            Intent intentPoll = this.adm.poll(5000L, TimeUnit.MILLISECONDS);
            if (intentPoll == null) {
                throw new IOException(ERROR_SERVICE_NOT_AVAILABLE);
            }
            String stringExtra = intentPoll.getStringExtra("registration_id");
            if (stringExtra != null) {
                return stringExtra;
            }
            intentPoll.getStringExtra("error");
            String stringExtra2 = intentPoll.getStringExtra("error");
            if (stringExtra2 != null) {
                throw new IOException(stringExtra2);
            }
            throw new IOException(ERROR_SERVICE_NOT_AVAILABLE);
        } catch (InterruptedException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void send(String to, String msgId, long timeToLive, Bundle data) throws IOException {
        a(to, msgId, timeToLive, -1, data);
    }

    public void send(String to, String msgId, Bundle data) throws IOException {
        send(to, msgId, -1L, data);
    }

    public void unregister() throws InterruptedException, IOException {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException(ERROR_MAIN_THREAD);
        }
        lL();
        try {
            Intent intentPoll = this.adm.poll(5000L, TimeUnit.MILLISECONDS);
            if (intentPoll == null) {
                throw new IOException(ERROR_SERVICE_NOT_AVAILABLE);
            }
            if (intentPoll.getStringExtra("unregistered") != null) {
                return;
            }
            String stringExtra = intentPoll.getStringExtra("error");
            if (stringExtra == null) {
                throw new IOException(ERROR_SERVICE_NOT_AVAILABLE);
            }
            throw new IOException(stringExtra);
        } catch (InterruptedException e) {
            throw new IOException(e.getMessage());
        }
    }
}
