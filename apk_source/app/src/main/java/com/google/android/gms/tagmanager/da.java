package com.google.android.gms.tagmanager;

import com.google.android.gms.tagmanager.l;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
class da<K, V> implements k<K, V> {
    private final Map<K, V> ars = new HashMap();
    private final int art;
    private final l.a<K, V> aru;
    private int arv;

    da(int i, l.a<K, V> aVar) {
        this.art = i;
        this.aru = aVar;
    }

    @Override // com.google.android.gms.tagmanager.k
    public synchronized void e(K k, V v) {
        if (k == null || v == null) {
            throw new NullPointerException("key == null || value == null");
        }
        this.arv += this.aru.sizeOf(k, v);
        if (this.arv > this.art) {
            Iterator<Map.Entry<K, V>> it = this.ars.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<K, V> next = it.next();
                this.arv -= this.aru.sizeOf(next.getKey(), next.getValue());
                it.remove();
                if (this.arv <= this.art) {
                    break;
                }
            }
        }
        this.ars.put(k, v);
    }

    @Override // com.google.android.gms.tagmanager.k
    public synchronized V get(K key) {
        return this.ars.get(key);
    }
}
