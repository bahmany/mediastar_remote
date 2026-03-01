package org.teleal.cling.model.message;

import java.io.ByteArrayInputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.common.http.Headers;

/* loaded from: classes.dex */
public class UpnpHeaders extends Headers {
    private static Logger log = Logger.getLogger(UpnpHeaders.class.getName());
    protected Map<UpnpHeader.Type, List<UpnpHeader>> parsedHeaders;

    public UpnpHeaders() {
    }

    public UpnpHeaders(Map<String, List<String>> headers) {
        super(headers);
    }

    public UpnpHeaders(ByteArrayInputStream inputStream) {
        super(inputStream);
    }

    protected void parseHeaders() throws IllegalAccessException, InstantiationException {
        this.parsedHeaders = new LinkedHashMap();
        log.fine("Parsing all HTTP headers for known UPnP headers: " + size());
        for (Map.Entry<String, List<String>> entry : entrySet()) {
            if (entry.getKey() != null) {
                UpnpHeader.Type type = UpnpHeader.Type.getByHttpName(entry.getKey());
                if (type == null) {
                    log.fine("Ignoring non-UPNP HTTP header: " + entry.getKey());
                } else {
                    for (String value : entry.getValue()) {
                        UpnpHeader upnpHeader = UpnpHeader.newInstance(type, value);
                        if (upnpHeader == null || upnpHeader.getValue() == null) {
                            log.fine("Ignoring known but non-parsable header (value violates the UDA specification?) '" + type.getHttpName() + "': " + value);
                        } else {
                            addParsedValue(type, upnpHeader);
                        }
                    }
                }
            }
        }
    }

    protected void addParsedValue(UpnpHeader.Type type, UpnpHeader value) {
        log.fine("Adding parsed header: " + value);
        List<UpnpHeader> list = this.parsedHeaders.get(type);
        if (list == null) {
            list = new LinkedList<>();
            this.parsedHeaders.put(type, list);
        }
        list.add(value);
    }

    public List<String> put(String key, List<String> values) {
        this.parsedHeaders = null;
        return super.put(key, values);
    }

    public void add(String key, String value) {
        this.parsedHeaders = null;
        super.add(key, value);
    }

    public List<String> remove(Object key) {
        this.parsedHeaders = null;
        return super.remove(key);
    }

    public void clear() {
        this.parsedHeaders = null;
        super.clear();
    }

    public boolean containsKey(UpnpHeader.Type type) throws IllegalAccessException, InstantiationException {
        if (this.parsedHeaders == null) {
            parseHeaders();
        }
        return this.parsedHeaders.containsKey(type);
    }

    public List<UpnpHeader> get(UpnpHeader.Type type) throws IllegalAccessException, InstantiationException {
        if (this.parsedHeaders == null) {
            parseHeaders();
        }
        return this.parsedHeaders.get(type);
    }

    public void add(UpnpHeader.Type type, UpnpHeader value) {
        super.add(type.getHttpName(), value.getString());
        if (this.parsedHeaders != null) {
            addParsedValue(type, value);
        }
    }

    public void remove(UpnpHeader.Type type) {
        super.remove(type.getHttpName());
        if (this.parsedHeaders != null) {
            this.parsedHeaders.remove(type);
        }
    }

    public UpnpHeader[] getAsArray(UpnpHeader.Type type) throws IllegalAccessException, InstantiationException {
        if (this.parsedHeaders == null) {
            parseHeaders();
        }
        if (this.parsedHeaders.get(type) != null) {
            return (UpnpHeader[]) this.parsedHeaders.get(type).toArray(new UpnpHeader[this.parsedHeaders.get(type).size()]);
        }
        return new UpnpHeader[0];
    }

    public UpnpHeader getFirstHeader(UpnpHeader.Type type) {
        if (getAsArray(type).length > 0) {
            return getAsArray(type)[0];
        }
        return null;
    }

    public <H extends UpnpHeader> H getFirstHeader(UpnpHeader.Type type, Class<H> cls) throws IllegalAccessException, InstantiationException {
        UpnpHeader[] asArray = getAsArray(type);
        if (asArray.length == 0) {
            return null;
        }
        for (UpnpHeader upnpHeader : asArray) {
            H h = (H) upnpHeader;
            if (cls.isAssignableFrom(h.getClass())) {
                return h;
            }
        }
        return null;
    }

    public void log() {
        if (log.isLoggable(Level.FINE)) {
            log.fine("############################ RAW HEADERS ###########################");
            for (Map.Entry<String, List<String>> entry : entrySet()) {
                log.fine("=== NAME : " + entry.getKey());
                for (String v : entry.getValue()) {
                    log.fine("VALUE: " + v);
                }
            }
            if (this.parsedHeaders != null && this.parsedHeaders.size() > 0) {
                log.fine("########################## PARSED HEADERS ##########################");
                for (Map.Entry<UpnpHeader.Type, List<UpnpHeader>> entry2 : this.parsedHeaders.entrySet()) {
                    log.fine("=== TYPE: " + entry2.getKey());
                    for (UpnpHeader upnpHeader : entry2.getValue()) {
                        log.fine("HEADER: " + upnpHeader);
                    }
                }
            }
            log.fine("####################################################################");
        }
    }
}
