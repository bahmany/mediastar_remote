package javax.mail;

/* loaded from: classes.dex */
public abstract class BodyPart implements Part {
    protected Multipart parent;

    public Multipart getParent() {
        return this.parent;
    }

    void setParent(Multipart parent) {
        this.parent = parent;
    }
}
