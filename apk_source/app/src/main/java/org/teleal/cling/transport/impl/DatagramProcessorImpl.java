package org.teleal.cling.transport.impl;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mina.proxy.handlers.http.HttpProxyConstants;
import org.teleal.cling.model.message.IncomingDatagramMessage;
import org.teleal.cling.model.message.OutgoingDatagramMessage;
import org.teleal.cling.model.message.UpnpHeaders;
import org.teleal.cling.model.message.UpnpRequest;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.transport.spi.DatagramProcessor;
import org.teleal.cling.transport.spi.UnsupportedDataException;
import org.teleal.common.http.Headers;

/* loaded from: classes.dex */
public class DatagramProcessorImpl implements DatagramProcessor {
    private static Logger log = Logger.getLogger(DatagramProcessor.class.getName());

    @Override // org.teleal.cling.transport.spi.DatagramProcessor
    public IncomingDatagramMessage read(InetAddress receivedOnAddress, DatagramPacket datagram) throws UnsupportedDataException {
        try {
            if (log.isLoggable(Level.FINER)) {
                log.finer("===================================== DATAGRAM BEGIN ============================================");
                log.finer(new String(datagram.getData()));
                log.finer("-===================================== DATAGRAM END =============================================");
            }
            ByteArrayInputStream is = new ByteArrayInputStream(datagram.getData());
            String[] startLine = Headers.readLine(is).split(" ");
            return startLine[0].startsWith("HTTP/1.") ? readResponseMessage(receivedOnAddress, datagram, is, Integer.valueOf(startLine[1]).intValue(), startLine[2], startLine[0]) : readRequestMessage(receivedOnAddress, datagram, is, startLine[0], startLine[2]);
        } catch (Exception ex) {
            throw new UnsupportedDataException("Could not parse headers: " + ex, ex);
        }
    }

    /* JADX WARN: Type inference failed for: r3v0, types: [org.teleal.cling.model.message.UpnpOperation] */
    @Override // org.teleal.cling.transport.spi.DatagramProcessor
    public DatagramPacket write(OutgoingDatagramMessage message) throws UnsupportedDataException, UnsupportedEncodingException {
        StringBuilder statusLine = new StringBuilder();
        ?? operation = message.getOperation();
        if (operation instanceof UpnpRequest) {
            UpnpRequest requestOperation = (UpnpRequest) operation;
            statusLine.append(requestOperation.getHttpMethodName()).append(" * ");
            statusLine.append("HTTP/1.").append(operation.getHttpMinorVersion()).append("\r\n");
        } else if (operation instanceof UpnpResponse) {
            UpnpResponse responseOperation = (UpnpResponse) operation;
            statusLine.append("HTTP/1.").append(operation.getHttpMinorVersion()).append(" ");
            statusLine.append(responseOperation.getStatusCode()).append(" ").append(responseOperation.getStatusMessage());
            statusLine.append("\r\n");
        } else {
            throw new UnsupportedDataException("Message operation is not request or response, don't know how to process: " + message);
        }
        StringBuilder messageData = new StringBuilder();
        messageData.append((CharSequence) statusLine);
        messageData.append(message.getHeaders().toString()).append("\r\n");
        if (log.isLoggable(Level.FINER)) {
            log.finer("Writing message data for: " + message);
            log.finer("---------------------------------------------------------------------------------");
            log.finer(messageData.toString().substring(0, messageData.length() - 2));
            log.finer("---------------------------------------------------------------------------------");
        }
        try {
            byte[] data = messageData.toString().getBytes("US-ASCII");
            log.fine("Writing new datagram packet with " + data.length + " bytes for: " + message);
            return new DatagramPacket(data, data.length, message.getDestinationAddress(), message.getDestinationPort());
        } catch (UnsupportedEncodingException ex) {
            throw new UnsupportedDataException("Can't convert message content to US-ASCII: " + ex.getMessage(), ex);
        }
    }

    protected IncomingDatagramMessage readRequestMessage(InetAddress receivedOnAddress, DatagramPacket datagram, ByteArrayInputStream is, String requestMethod, String httpProtocol) throws Exception {
        UpnpHeaders headers = new UpnpHeaders(is);
        UpnpRequest upnpRequest = new UpnpRequest(UpnpRequest.Method.getByHttpName(requestMethod));
        upnpRequest.setHttpMinorVersion(httpProtocol.toUpperCase().equals(HttpProxyConstants.HTTP_1_1) ? 1 : 0);
        IncomingDatagramMessage requestMessage = new IncomingDatagramMessage(upnpRequest, datagram.getAddress(), datagram.getPort(), receivedOnAddress);
        requestMessage.setHeaders(headers);
        return requestMessage;
    }

    protected IncomingDatagramMessage readResponseMessage(InetAddress receivedOnAddress, DatagramPacket datagram, ByteArrayInputStream is, int statusCode, String statusMessage, String httpProtocol) throws Exception {
        UpnpHeaders headers = new UpnpHeaders(is);
        UpnpResponse upnpResponse = new UpnpResponse(statusCode, statusMessage);
        upnpResponse.setHttpMinorVersion(httpProtocol.toUpperCase().equals(HttpProxyConstants.HTTP_1_1) ? 1 : 0);
        IncomingDatagramMessage responseMessage = new IncomingDatagramMessage(upnpResponse, datagram.getAddress(), datagram.getPort(), receivedOnAddress);
        responseMessage.setHeaders(headers);
        return responseMessage;
    }
}
