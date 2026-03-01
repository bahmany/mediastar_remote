package com.sun.mail.iap;

import com.sun.mail.util.SocketFetcher;
import com.sun.mail.util.TraceInputStream;
import com.sun.mail.util.TraceOutputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Properties;
import java.util.Vector;

/* loaded from: classes.dex */
public class Protocol {
    private static final byte[] CRLF = {13, 10};
    private boolean connected;
    protected boolean debug;
    private volatile Vector handlers;
    protected String host;
    private volatile ResponseInputStream input;
    protected PrintStream out;
    private volatile DataOutputStream output;
    protected String prefix;
    protected Properties props;
    protected boolean quote;
    private Socket socket;
    private int tagCounter;
    private volatile long timestamp;
    private TraceInputStream traceInput;
    private TraceOutputStream traceOutput;

    public Protocol(String host, int port, boolean debug, PrintStream out, Properties props, String prefix, boolean isSSL) throws ProtocolException, IOException {
        this.connected = false;
        this.tagCounter = 0;
        this.handlers = null;
        try {
            this.host = host;
            this.debug = debug;
            this.out = out;
            this.props = props;
            this.prefix = prefix;
            this.socket = SocketFetcher.getSocket(host, port, props, prefix, isSSL);
            String s = props.getProperty("mail.debug.quote");
            this.quote = s != null && s.equalsIgnoreCase("true");
            initStreams(out);
            processGreeting(readResponse());
            this.timestamp = System.currentTimeMillis();
            this.connected = true;
        } finally {
            if (!this.connected) {
                disconnect();
            }
        }
    }

    private void initStreams(PrintStream out) throws IOException {
        this.traceInput = new TraceInputStream(this.socket.getInputStream(), out);
        this.traceInput.setTrace(this.debug);
        this.traceInput.setQuote(this.quote);
        this.input = new ResponseInputStream(this.traceInput);
        this.traceOutput = new TraceOutputStream(this.socket.getOutputStream(), out);
        this.traceOutput.setTrace(this.debug);
        this.traceOutput.setQuote(this.quote);
        this.output = new DataOutputStream(new BufferedOutputStream(this.traceOutput));
    }

    public Protocol(InputStream in, OutputStream out, boolean debug) throws IOException {
        this.connected = false;
        this.tagCounter = 0;
        this.handlers = null;
        this.host = "localhost";
        this.debug = debug;
        this.quote = false;
        this.out = System.out;
        this.traceInput = new TraceInputStream(in, System.out);
        this.traceInput.setTrace(debug);
        this.traceInput.setQuote(this.quote);
        this.input = new ResponseInputStream(this.traceInput);
        this.traceOutput = new TraceOutputStream(out, System.out);
        this.traceOutput.setTrace(debug);
        this.traceOutput.setQuote(this.quote);
        this.output = new DataOutputStream(new BufferedOutputStream(this.traceOutput));
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public synchronized void addResponseHandler(ResponseHandler h) {
        if (this.handlers == null) {
            this.handlers = new Vector();
        }
        this.handlers.addElement(h);
    }

    public synchronized void removeResponseHandler(ResponseHandler h) {
        if (this.handlers != null) {
            this.handlers.removeElement(h);
        }
    }

    public void notifyResponseHandlers(Response[] responses) {
        if (this.handlers != null) {
            for (Response r : responses) {
                if (r != null) {
                    int size = this.handlers.size();
                    if (size != 0) {
                        Object[] h = new Object[size];
                        this.handlers.copyInto(h);
                        for (int j = 0; j < size; j++) {
                            ((ResponseHandler) h[j]).handleResponse(r);
                        }
                    } else {
                        return;
                    }
                }
            }
        }
    }

    protected void processGreeting(Response r) throws ProtocolException {
        if (r.isBYE()) {
            throw new ConnectionException(this, r);
        }
    }

    protected ResponseInputStream getInputStream() {
        return this.input;
    }

    protected OutputStream getOutputStream() {
        return this.output;
    }

    protected synchronized boolean supportsNonSyncLiterals() {
        return false;
    }

    public Response readResponse() throws ProtocolException, IOException {
        return new Response(this);
    }

    protected ByteArray getResponseBuffer() {
        return null;
    }

    public String writeCommand(String command, Argument args) throws ProtocolException, IOException {
        StringBuilder sb = new StringBuilder("A");
        int i = this.tagCounter;
        this.tagCounter = i + 1;
        String tag = sb.append(Integer.toString(i, 10)).toString();
        this.output.writeBytes(String.valueOf(tag) + " " + command);
        if (args != null) {
            this.output.write(32);
            args.write(this);
        }
        this.output.write(CRLF);
        this.output.flush();
        return tag;
    }

    public synchronized Response[] command(String command, Argument args) {
        Response[] responses;
        Response r;
        Vector v = new Vector();
        boolean done = false;
        String tag = null;
        try {
            tag = writeCommand(command, args);
        } catch (LiteralException lex) {
            v.addElement(lex.getResponse());
            done = true;
        } catch (Exception ex) {
            v.addElement(Response.byeResponse(ex));
            done = true;
        }
        while (!done) {
            try {
                r = readResponse();
            } catch (ProtocolException e) {
            } catch (IOException ioex) {
                r = Response.byeResponse(ioex);
            }
            v.addElement(r);
            if (r.isBYE()) {
                done = true;
            }
            if (r.isTagged() && r.getTag().equals(tag)) {
                done = true;
            }
        }
        responses = new Response[v.size()];
        v.copyInto(responses);
        this.timestamp = System.currentTimeMillis();
        return responses;
    }

    public void handleResult(Response response) throws ProtocolException {
        if (!response.isOK()) {
            if (response.isNO()) {
                throw new CommandFailedException(response);
            }
            if (response.isBAD()) {
                throw new BadCommandException(response);
            }
            if (response.isBYE()) {
                disconnect();
                throw new ConnectionException(this, response);
            }
        }
    }

    public void simpleCommand(String cmd, Argument args) throws ProtocolException {
        Response[] r = command(cmd, args);
        notifyResponseHandlers(r);
        handleResult(r[r.length - 1]);
    }

    public synchronized void startTLS(String cmd) throws ProtocolException, IOException {
        simpleCommand(cmd, null);
        this.socket = SocketFetcher.startTLS(this.socket, this.props, this.prefix);
        initStreams(this.out);
    }

    protected synchronized void disconnect() {
        if (this.socket != null) {
            try {
                this.socket.close();
            } catch (IOException e) {
            }
            this.socket = null;
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();
        disconnect();
    }
}
