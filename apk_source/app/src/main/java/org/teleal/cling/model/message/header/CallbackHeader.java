package org.teleal.cling.model.message.header;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class CallbackHeader extends UpnpHeader<List<URL>> {
    public CallbackHeader() {
        setValue(new ArrayList());
    }

    public CallbackHeader(List<URL> urls) {
        this();
        getValue().addAll(urls);
    }

    public CallbackHeader(URL url) {
        this();
        getValue().add(url);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String s) throws InvalidHeaderException {
        if (s.length() != 0) {
            if (!s.contains("<") || !s.contains(">")) {
                throw new InvalidHeaderException("URLs not in brackets: " + s);
            }
            String s2 = s.replaceAll("<", "");
            String[] split = s2.split(">");
            try {
                ArrayList arrayList = new ArrayList();
                for (String str : split) {
                    String sp = str.trim();
                    if (!sp.startsWith("http://")) {
                        throw new InvalidHeaderException("Can't parse non-http callback URL: " + sp);
                    }
                    arrayList.add(new URL(sp));
                }
                setValue(arrayList);
            } catch (MalformedURLException ex) {
                throw new InvalidHeaderException("Can't parse callback URLs from '" + s2 + "': " + ex);
            }
        }
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public String getString() {
        StringBuilder s = new StringBuilder();
        for (URL url : getValue()) {
            s.append("<").append(url.toString()).append(">");
        }
        return s.toString();
    }
}
