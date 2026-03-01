package com.voicetechnology.rtspclient.headers;

/* loaded from: classes.dex */
public class CSeqHeader extends BaseIntegerHeader {
    public static final String NAME = "CSeq";

    public CSeqHeader() {
        super(NAME);
    }

    public CSeqHeader(int cseq) {
        super(NAME, cseq);
    }

    public CSeqHeader(String line) {
        super(line);
    }
}
