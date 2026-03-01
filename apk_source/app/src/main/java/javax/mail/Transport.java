package javax.mail;

import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;

/* loaded from: classes.dex */
public abstract class Transport extends Service {
    private Vector transportListeners;

    public abstract void sendMessage(Message message, Address[] addressArr) throws MessagingException;

    public Transport(Session session, URLName urlname) {
        super(session, urlname);
        this.transportListeners = null;
    }

    public static void send(Message msg) throws IllegalAccessException, NoSuchMethodException, InstantiationException, MessagingException, ClassNotFoundException, SecurityException, IllegalArgumentException, InvocationTargetException {
        msg.saveChanges();
        send0(msg, msg.getAllRecipients());
    }

    public static void send(Message msg, Address[] addresses) throws IllegalAccessException, NoSuchMethodException, InstantiationException, MessagingException, ClassNotFoundException, SecurityException, IllegalArgumentException, InvocationTargetException {
        msg.saveChanges();
        send0(msg, addresses);
    }

    private static void send0(Message msg, Address[] addresses) throws IllegalAccessException, NoSuchMethodException, InstantiationException, MessagingException, ClassNotFoundException, SecurityException, IllegalArgumentException, InvocationTargetException {
        Transport transport;
        if (addresses == null || addresses.length == 0) {
            throw new SendFailedException("No recipient addresses");
        }
        Hashtable protocols = new Hashtable();
        Vector invalid = new Vector();
        Vector validSent = new Vector();
        Vector validUnsent = new Vector();
        for (int i = 0; i < addresses.length; i++) {
            if (protocols.containsKey(addresses[i].getType())) {
                ((Vector) protocols.get(addresses[i].getType())).addElement(addresses[i]);
            } else {
                Vector w = new Vector();
                w.addElement(addresses[i]);
                protocols.put(addresses[i].getType(), w);
            }
        }
        int dsize = protocols.size();
        if (dsize == 0) {
            throw new SendFailedException("No recipient addresses");
        }
        Session s = msg.session != null ? msg.session : Session.getDefaultInstance(System.getProperties(), null);
        if (dsize == 1) {
            transport = s.getTransport(addresses[0]);
            try {
                transport.connect();
                transport.sendMessage(msg, addresses);
                return;
            } finally {
            }
        }
        MessagingException chainedEx = null;
        boolean sendFailed = false;
        Enumeration e = protocols.elements();
        while (e.hasMoreElements()) {
            Vector v = (Vector) e.nextElement();
            Address[] protaddresses = new Address[v.size()];
            v.copyInto(protaddresses);
            transport = s.getTransport(protaddresses[0]);
            if (transport == null) {
                for (Address address : protaddresses) {
                    invalid.addElement(address);
                }
            } else {
                try {
                    transport.connect();
                    transport.sendMessage(msg, protaddresses);
                } catch (SendFailedException sex) {
                    sendFailed = true;
                    if (chainedEx == null) {
                        chainedEx = sex;
                    } else {
                        chainedEx.setNextException(sex);
                    }
                    Address[] a = sex.getInvalidAddresses();
                    if (a != null) {
                        for (Address address2 : a) {
                            invalid.addElement(address2);
                        }
                    }
                    Address[] a2 = sex.getValidSentAddresses();
                    if (a2 != null) {
                        for (Address address3 : a2) {
                            validSent.addElement(address3);
                        }
                    }
                    Address[] c = sex.getValidUnsentAddresses();
                    if (c != null) {
                        for (Address address4 : c) {
                            validUnsent.addElement(address4);
                        }
                    }
                } catch (MessagingException mex) {
                    sendFailed = true;
                    if (chainedEx == null) {
                        chainedEx = mex;
                    } else {
                        chainedEx.setNextException(mex);
                    }
                } finally {
                }
            }
        }
        if (!sendFailed && invalid.size() == 0 && validUnsent.size() == 0) {
            return;
        }
        Address[] a3 = (Address[]) null;
        Address[] b = (Address[]) null;
        Address[] c2 = (Address[]) null;
        if (validSent.size() > 0) {
            a3 = new Address[validSent.size()];
            validSent.copyInto(a3);
        }
        if (validUnsent.size() > 0) {
            b = new Address[validUnsent.size()];
            validUnsent.copyInto(b);
        }
        if (invalid.size() > 0) {
            c2 = new Address[invalid.size()];
            invalid.copyInto(c2);
        }
        throw new SendFailedException("Sending failed", chainedEx, a3, b, c2);
    }

    public synchronized void addTransportListener(TransportListener l) {
        if (this.transportListeners == null) {
            this.transportListeners = new Vector();
        }
        this.transportListeners.addElement(l);
    }

    public synchronized void removeTransportListener(TransportListener l) {
        if (this.transportListeners != null) {
            this.transportListeners.removeElement(l);
        }
    }

    protected void notifyTransportListeners(int type, Address[] validSent, Address[] validUnsent, Address[] invalid, Message msg) {
        if (this.transportListeners != null) {
            TransportEvent e = new TransportEvent(this, type, validSent, validUnsent, invalid, msg);
            queueEvent(e, this.transportListeners);
        }
    }
}
