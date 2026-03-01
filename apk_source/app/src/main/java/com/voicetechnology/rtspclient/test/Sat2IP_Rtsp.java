package com.voicetechnology.rtspclient.test;

import android.util.Log;
import com.alitech.dvbtoip.DVBtoIP;
import com.voicetechnology.rtspclient.RTSPClient;
import com.voicetechnology.rtspclient.concepts.Client;
import com.voicetechnology.rtspclient.concepts.ClientListener;
import com.voicetechnology.rtspclient.concepts.Request;
import com.voicetechnology.rtspclient.concepts.Response;
import com.voicetechnology.rtspclient.transport.PlainTCP;
import java.net.URI;
import mktvsmart.screen.GMScreenGlobalInfo;

/* loaded from: classes.dex */
public class Sat2IP_Rtsp implements ClientListener {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$voicetechnology$rtspclient$concepts$Request$Method;
    private static boolean isTearDown;
    private boolean _isSucc;
    RTSPClient client;
    private EndOfFileListener eofListener;
    String mBaseUrl;
    String mQueryStr;
    private int port;
    private int rtp_port;
    private int streamId;

    public interface EndOfFileListener {
        void onEndOfFile();
    }

    static /* synthetic */ int[] $SWITCH_TABLE$com$voicetechnology$rtspclient$concepts$Request$Method() {
        int[] iArr = $SWITCH_TABLE$com$voicetechnology$rtspclient$concepts$Request$Method;
        if (iArr == null) {
            iArr = new int[Request.Method.valuesCustom().length];
            try {
                iArr[Request.Method.DESCRIBE.ordinal()] = 2;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[Request.Method.OPTIONS.ordinal()] = 1;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[Request.Method.PLAY.ordinal()] = 4;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[Request.Method.RECORD.ordinal()] = 5;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[Request.Method.SETUP.ordinal()] = 3;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[Request.Method.TEARDOWN.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            $SWITCH_TABLE$com$voicetechnology$rtspclient$concepts$Request$Method = iArr;
        }
        return iArr;
    }

    public static void main(String[] args) throws Exception {
        Sat2IP_Rtsp rtsp = new Sat2IP_Rtsp();
        rtsp.setup("rtsp://192.168.0.101:554/", "?src=1&fe=1&freq=3840&pol=h&msys=dvbs2&mtype=8psk&ro=0.35&plts=on&sr=27500&fec=2&pids=0,16,17,20,257,512,8190,8191,8191,650");
        System.out.println("get rtp port: " + rtsp.get_rtp_port());
    }

    public Sat2IP_Rtsp() {
        try {
            this.client = new RTSPClient();
            this.client.setTransport(new PlainTCP());
            this.client.setClientListener(this);
            this.port = 10022;
            this.rtp_port = 0;
            isTearDown = true;
            this.eofListener = null;
        } catch (Exception e) {
        }
    }

    public void set_eof_listener(EndOfFileListener l) {
        this.eofListener = l;
    }

    public int get_rtp_port() {
        return this.rtp_port;
    }

    public synchronized boolean setup_blocked(String baseUrl, String query) {
        try {
            this._isSucc = false;
            setup(baseUrl, query);
            wait(5000L);
        } catch (Exception e) {
        } catch (Throwable th) {
        }
        return this._isSucc;
    }

    public void setup(String baseUrl, String query) throws Exception {
        this.rtp_port = this.port;
        this.mBaseUrl = baseUrl;
        this.mQueryStr = query;
        Log.v("sat2ip", String.valueOf(baseUrl) + " query: " + query);
        this.client.setup(new URI(String.valueOf(this.mBaseUrl) + this.mQueryStr), nextPort());
        isTearDown = false;
    }

    public void teardown() {
        isTearDown = true;
        this.client.teardown();
    }

    private void handleRequestFailed(Client client) {
        client.teardown();
        this._isSucc = false;
        synchronized (this) {
            notify();
        }
    }

    private void handleSessionNotFound() {
        if (this.eofListener != null) {
            this.eofListener.onEndOfFile();
        }
    }

    private void handleRequestFailed_NoTearDown(Client client) {
        this._isSucc = false;
        synchronized (this) {
            notify();
        }
    }

    @Override // com.voicetechnology.rtspclient.concepts.ClientListener
    public void requestFailed(Client client, Request request, Throwable cause) {
        System.out.println("Request failed \n" + request);
        cause.printStackTrace();
        if (request.getMethod() != Request.Method.TEARDOWN) {
            handleRequestFailed(client);
        }
    }

    @Override // com.voicetechnology.rtspclient.concepts.ClientListener
    public void response(Client client, Request request, Response response) {
        try {
            if (request.getMethod() != Request.Method.OPTIONS) {
                System.out.println("Got response: " + response);
                System.out.println("for the request: \n" + request);
            } else {
                System.out.println("Got response for OPTIONS\n");
            }
        } catch (Throwable t) {
            generalError(client, t);
            return;
        }
        if (response.getStatusCode() == 200) {
            switch ($SWITCH_TABLE$com$voicetechnology$rtspclient$concepts$Request$Method()[request.getMethod().ordinal()]) {
                case 1:
                    if (!isTearDown) {
                        Thread.sleep(10000L);
                        client.options("*", new URI(this.mBaseUrl));
                        return;
                    }
                    return;
                case 2:
                default:
                    return;
                case 3:
                    if (GMScreenGlobalInfo.playType == 2) {
                        this.streamId = Integer.parseInt(response.getHeader("com.ses.streamID").getRawValue());
                        DVBtoIP.setSeed(this.streamId);
                        client.play(String.valueOf(this.mBaseUrl) + "stream=" + this.streamId);
                        return;
                    } else {
                        if (GMScreenGlobalInfo.playType == 1) {
                            client.play(this.mBaseUrl);
                            return;
                        }
                        return;
                    }
                case 4:
                    this._isSucc = true;
                    synchronized (this) {
                        notify();
                    }
                    Thread.sleep(10000L);
                    client.options("*", new URI(this.mBaseUrl));
                    return;
            }
            generalError(client, t);
            return;
        }
        if (response.getStatusCode() == 454) {
            if (request.getMethod() == Request.Method.OPTIONS) {
                handleSessionNotFound();
                return;
            } else {
                handleRequestFailed_NoTearDown(client);
                return;
            }
        }
        handleRequestFailed(client);
    }

    @Override // com.voicetechnology.rtspclient.concepts.ClientListener
    public void generalError(Client client, Throwable error) {
        error.printStackTrace();
    }

    @Override // com.voicetechnology.rtspclient.concepts.ClientListener
    public void mediaDescriptor(Client client, String descriptor) {
    }

    private int nextPort() {
        int i = this.port + 2;
        this.port = i;
        return i - 2;
    }
}
