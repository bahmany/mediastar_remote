package mktvsmart.screen;

import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class GsSession {
    private static GsSession session;
    private Map<Object, Object> _objectContainer = new HashMap();

    private GsSession() {
    }

    public static GsSession getSession() {
        if (session != null) {
            return session;
        }
        session = new GsSession();
        return session;
    }

    public void put(Object key, Object value) {
        this._objectContainer.put(key, value);
    }

    public Object get(Object key) {
        return this._objectContainer.get(key);
    }

    public void cleanUpSession() {
        this._objectContainer.clear();
    }

    public void remove(Object key) {
        this._objectContainer.remove(key);
    }
}
