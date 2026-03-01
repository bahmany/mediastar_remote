package javax.mail.search;

import javax.mail.Flags;
import javax.mail.Message;

/* loaded from: classes.dex */
public final class FlagTerm extends SearchTerm {
    private static final long serialVersionUID = -142991500302030647L;
    protected Flags flags;
    protected boolean set;

    public FlagTerm(Flags flags, boolean set) {
        this.flags = flags;
        this.set = set;
    }

    public Flags getFlags() {
        return (Flags) this.flags.clone();
    }

    public boolean getTestSet() {
        return this.set;
    }

    @Override // javax.mail.search.SearchTerm
    public boolean match(Message msg) {
        boolean z = true;
        try {
            Flags f = msg.getFlags();
            if (this.set) {
                if (!f.contains(this.flags)) {
                    z = false;
                }
            } else {
                Flags.Flag[] sf = this.flags.getSystemFlags();
                int i = 0;
                while (true) {
                    if (i < sf.length) {
                        if (f.contains(sf[i])) {
                            z = false;
                            break;
                        }
                        i++;
                    } else {
                        String[] s = this.flags.getUserFlags();
                        int i2 = 0;
                        while (true) {
                            if (i2 < s.length) {
                                if (f.contains(s[i2])) {
                                    z = false;
                                    break;
                                }
                                i2++;
                            }
                        }
                    }
                }
            }
            return z;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FlagTerm)) {
            return false;
        }
        FlagTerm ft = (FlagTerm) obj;
        return ft.set == this.set && ft.flags.equals(this.flags);
    }

    public int hashCode() {
        return this.set ? this.flags.hashCode() : this.flags.hashCode() ^ (-1);
    }
}
