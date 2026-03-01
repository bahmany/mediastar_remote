package com.sun.mail.imap.protocol;

import java.util.Vector;

/* loaded from: classes.dex */
public class MessageSet {
    public int end;
    public int start;

    public MessageSet() {
    }

    public MessageSet(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int size() {
        return (this.end - this.start) + 1;
    }

    public static MessageSet[] createMessageSets(int[] msgs) {
        Vector v = new Vector();
        int i = 0;
        while (i < msgs.length) {
            MessageSet ms = new MessageSet();
            ms.start = msgs[i];
            int j = i + 1;
            while (j < msgs.length && msgs[j] == msgs[j - 1] + 1) {
                j++;
            }
            ms.end = msgs[j - 1];
            v.addElement(ms);
            int i2 = j - 1;
            i = i2 + 1;
        }
        MessageSet[] msgsets = new MessageSet[v.size()];
        v.copyInto(msgsets);
        return msgsets;
    }

    public static String toString(MessageSet[] msgsets) {
        if (msgsets == null || msgsets.length == 0) {
            return null;
        }
        int i = 0;
        StringBuffer s = new StringBuffer();
        int size = msgsets.length;
        while (true) {
            int start = msgsets[i].start;
            int end = msgsets[i].end;
            if (end > start) {
                s.append(start).append(':').append(end);
            } else {
                s.append(start);
            }
            i++;
            if (i < size) {
                s.append(',');
            } else {
                return s.toString();
            }
        }
    }

    public static int size(MessageSet[] msgsets) {
        int count = 0;
        if (msgsets == null) {
            return 0;
        }
        for (MessageSet messageSet : msgsets) {
            count += messageSet.size();
        }
        return count;
    }
}
