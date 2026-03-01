package com.voicetechnology.rtspclient.headers;

import com.voicetechnology.rtspclient.HeaderMismatchException;
import com.voicetechnology.rtspclient.concepts.Header;

/* loaded from: classes.dex */
public class BaseStringHeader extends Header {
    public BaseStringHeader(String name) {
        super(name);
    }

    public BaseStringHeader(String name, String header) {
        super(header);
        try {
            checkName(name);
        } catch (HeaderMismatchException e) {
            setName(name);
        }
    }
}
