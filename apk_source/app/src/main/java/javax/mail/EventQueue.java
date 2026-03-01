package javax.mail;

import java.util.Vector;
import javax.mail.event.MailEvent;

/* loaded from: classes.dex */
class EventQueue implements Runnable {
    private QueueElement head = null;
    private QueueElement tail = null;
    private Thread qThread = new Thread(this, "JavaMail-EventQueue");

    static class QueueElement {
        MailEvent event;
        QueueElement next = null;
        QueueElement prev = null;
        Vector vector;

        QueueElement(MailEvent event, Vector vector) {
            this.event = null;
            this.vector = null;
            this.event = event;
            this.vector = vector;
        }
    }

    public EventQueue() {
        this.qThread.setDaemon(true);
        this.qThread.start();
    }

    public synchronized void enqueue(MailEvent event, Vector vector) {
        QueueElement newElt = new QueueElement(event, vector);
        if (this.head == null) {
            this.head = newElt;
            this.tail = newElt;
        } else {
            newElt.next = this.head;
            this.head.prev = newElt;
            this.head = newElt;
        }
        notifyAll();
    }

    private synchronized QueueElement dequeue() throws InterruptedException {
        QueueElement elt;
        while (this.tail == null) {
            wait();
        }
        elt = this.tail;
        this.tail = elt.prev;
        if (this.tail == null) {
            this.head = null;
        } else {
            this.tail.next = null;
        }
        elt.next = null;
        elt.prev = null;
        return elt;
    }

    @Override // java.lang.Runnable
    public void run() {
        while (true) {
            try {
                QueueElement qe = dequeue();
                if (qe != null) {
                    MailEvent e = qe.event;
                    Vector v = qe.vector;
                    for (int i = 0; i < v.size(); i++) {
                        try {
                            e.dispatch(v.elementAt(i));
                        } catch (Throwable t) {
                            if (t instanceof InterruptedException) {
                                return;
                            }
                        }
                    }
                } else {
                    return;
                }
            } catch (InterruptedException e2) {
                return;
            }
        }
    }

    void stop() {
        if (this.qThread != null) {
            this.qThread.interrupt();
            this.qThread = null;
        }
    }
}
