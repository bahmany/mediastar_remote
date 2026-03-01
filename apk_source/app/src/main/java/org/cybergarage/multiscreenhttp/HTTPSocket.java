package org.cybergarage.multiscreenhttp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Calendar;

/* loaded from: classes.dex */
public class HTTPSocket {
    private int timeOut = 2000;
    private Socket socket = null;
    private InputStream sockIn = null;
    private OutputStream sockOut = null;

    public HTTPSocket(Socket socket, int timeOut) throws SocketException {
        setTimeOut(timeOut);
        setSocket(socket);
        open();
    }

    public HTTPSocket(HTTPSocket socket, int timeOut) {
        setTimeOut(timeOut);
        setSocket(socket.getSocket());
        setInputStream(socket.getInputStream());
        setOutputStream(socket.getOutputStream());
    }

    public void finalize() throws IOException {
        close();
    }

    private void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public String getLocalAddress() {
        return getSocket().getLocalAddress().getHostAddress();
    }

    public int getLocalPort() {
        return getSocket().getLocalPort();
    }

    private void setInputStream(InputStream in) {
        this.sockIn = in;
    }

    public InputStream getInputStream() {
        return this.sockIn;
    }

    private void setOutputStream(OutputStream out) {
        this.sockOut = out;
    }

    private OutputStream getOutputStream() {
        return this.sockOut;
    }

    public boolean open() throws SocketException {
        Socket sock = getSocket();
        try {
            sock.setSoTimeout(getTimeOut());
            this.sockIn = sock.getInputStream();
            this.sockOut = sock.getOutputStream();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean close() throws IOException {
        try {
            if (this.sockIn != null) {
                this.sockIn.close();
            }
            if (this.sockOut != null) {
                this.sockOut.close();
            }
            getSocket().close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean post(HTTPResponse httpRes, byte[] content) throws IOException {
        httpRes.setDate(Calendar.getInstance());
        OutputStream out = getOutputStream();
        try {
            out.write(httpRes.getHeader().getBytes());
            out.write("\r\n".getBytes());
            out.write(content);
            out.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean post(HTTPResponse httpRes, InputStream in, long inLen) throws IOException {
        boolean z = false;
        httpRes.setDate(Calendar.getInstance());
        OutputStream out = getOutputStream();
        try {
            out.write(httpRes.getHeader().getBytes());
            out.write("\r\n".getBytes());
            int chunkSize = HTTP.getChunkSize();
            byte[] readBuf = new byte[chunkSize];
            long readCnt = 0;
            int readLen = in.read(readBuf);
            while (readLen > 0 && readCnt < inLen) {
                out.write(readBuf, 0, readLen);
                readCnt += readLen;
                readLen = in.read(readBuf);
            }
            out.flush();
            z = true;
            return true;
        } catch (Exception e) {
            return z;
        }
    }

    public boolean post(HTTPResponse httpRes, InputStream in) {
        return post(httpRes, httpRes.getContentInputStream(), httpRes.getContentLength());
    }

    public boolean post(HTTPResponse httpRes) {
        return httpRes.hasContentInputStream() ? post(httpRes, httpRes.getContentInputStream()) : post(httpRes, httpRes.getContent());
    }

    public void setTimeOut(int time) {
        this.timeOut = time;
    }

    public int getTimeOut() {
        return this.timeOut;
    }
}
