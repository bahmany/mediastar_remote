package com.google.android.gms.tagmanager;

import android.util.LruCache;
import com.google.android.gms.tagmanager.l;

/* loaded from: classes.dex */
class bb<K, V> implements k<K, V> {
    private LruCache<K, V> apx;

    /* renamed from: com.google.android.gms.tagmanager.bb$1 */
    class AnonymousClass1 extends LruCache<K, V> {
        final /* synthetic */ l.a apy;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass1(int i, l.a aVar) {
            super(i);
            aVar = aVar;
        }

        @Override // android.util.LruCache
        protected int sizeOf(K key, V value) {
            return aVar.sizeOf(key, value);
        }
    }

    bb(int i, l.a<K, V> aVar) {
        this.apx = new LruCache<K, V>(i) { // from class: com.google.android.gms.tagmanager.bb.1
            final /* synthetic */ l.a apy;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass1(int i2, l.a aVar2) {
                super(i2);
                aVar = aVar2;
            }

            @Override // android.util.LruCache
            protected int sizeOf(K key, V value) {
                return aVar.sizeOf(key, value);
            }
        };
    }

    @Override // com.google.android.gms.tagmanager.k
    public void e(K k, V v) {
        this.apx.put(k, v);
    }

    @Override // com.google.android.gms.tagmanager.k
    public V get(K key) {
        return this.apx.get(key);
    }
}
