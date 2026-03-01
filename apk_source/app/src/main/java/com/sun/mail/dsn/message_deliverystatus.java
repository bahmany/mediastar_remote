package com.sun.mail.dsn;

import java.io.IOException;
import java.io.OutputStream;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import myjava.awt.datatransfer.DataFlavor;

/* loaded from: classes.dex */
public class message_deliverystatus implements DataContentHandler {
    ActivationDataFlavor ourDataFlavor = new ActivationDataFlavor(DeliveryStatus.class, "message/delivery-status", "Delivery Status");

    @Override // javax.activation.DataContentHandler
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{this.ourDataFlavor};
    }

    @Override // javax.activation.DataContentHandler
    public Object getTransferData(DataFlavor df, DataSource ds) throws IOException {
        if (this.ourDataFlavor.equals(df)) {
            return getContent(ds);
        }
        return null;
    }

    @Override // javax.activation.DataContentHandler
    public Object getContent(DataSource ds) throws IOException {
        try {
            return new DeliveryStatus(ds.getInputStream());
        } catch (MessagingException me) {
            throw new IOException("Exception creating DeliveryStatus in message/devliery-status DataContentHandler: " + me.toString());
        }
    }

    @Override // javax.activation.DataContentHandler
    public void writeTo(Object obj, String mimeType, OutputStream os) throws Exception {
        if (obj instanceof DeliveryStatus) {
            DeliveryStatus ds = (DeliveryStatus) obj;
            try {
                ds.writeTo(os);
                return;
            } catch (MessagingException me) {
                throw new IOException(me.toString());
            }
        }
        throw new IOException("unsupported object");
    }
}
