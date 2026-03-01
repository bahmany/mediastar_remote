package com.google.android.gms.tagmanager;

import com.hisilicon.multiscreen.protocol.message.KeyInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class DataLayer {
    public static final String EVENT_KEY = "event";
    public static final Object OBJECT_NOT_PRESENT = new Object();
    static final String[] aov = "gtm.lifetime".toString().split("\\.");
    private static final Pattern aow = Pattern.compile("(\\d+)\\s*([smhd]?)");
    private final LinkedList<Map<String, Object>> aoA;
    private final c aoB;
    private final CountDownLatch aoC;
    private final ConcurrentHashMap<b, Integer> aox;
    private final Map<String, Object> aoy;
    private final ReentrantLock aoz;

    static final class a {
        public final String JH;
        public final Object wq;

        a(String str, Object obj) {
            this.JH = str;
            this.wq = obj;
        }

        public boolean equals(Object o) {
            if (!(o instanceof a)) {
                return false;
            }
            a aVar = (a) o;
            return this.JH.equals(aVar.JH) && this.wq.equals(aVar.wq);
        }

        public int hashCode() {
            return Arrays.hashCode(new Integer[]{Integer.valueOf(this.JH.hashCode()), Integer.valueOf(this.wq.hashCode())});
        }

        public String toString() {
            return "Key: " + this.JH + " value: " + this.wq.toString();
        }
    }

    interface b {
        void D(Map<String, Object> map);
    }

    interface c {

        public interface a {
            void g(List<a> list);
        }

        void a(a aVar);

        void a(List<a> list, long j);

        void cu(String str);
    }

    DataLayer() {
        this(new c() { // from class: com.google.android.gms.tagmanager.DataLayer.1
            @Override // com.google.android.gms.tagmanager.DataLayer.c
            public void a(c.a aVar) {
                aVar.g(new ArrayList());
            }

            @Override // com.google.android.gms.tagmanager.DataLayer.c
            public void a(List<a> list, long j) {
            }

            @Override // com.google.android.gms.tagmanager.DataLayer.c
            public void cu(String str) {
            }
        });
    }

    DataLayer(c persistentStore) {
        this.aoB = persistentStore;
        this.aox = new ConcurrentHashMap<>();
        this.aoy = new HashMap();
        this.aoz = new ReentrantLock();
        this.aoA = new LinkedList<>();
        this.aoC = new CountDownLatch(1);
        oc();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void F(Map<String, Object> map) {
        this.aoz.lock();
        try {
            this.aoA.offer(map);
            if (this.aoz.getHoldCount() == 1) {
                od();
            }
            G(map);
        } finally {
            this.aoz.unlock();
        }
    }

    private void G(Map<String, Object> map) {
        Long lH = H(map);
        if (lH == null) {
            return;
        }
        List<a> listJ = J(map);
        listJ.remove("gtm.lifetime");
        this.aoB.a(listJ, lH.longValue());
    }

    private Long H(Map<String, Object> map) {
        Object objI = I(map);
        if (objI == null) {
            return null;
        }
        return ct(objI.toString());
    }

    private Object I(Map<String, Object> map) {
        String[] strArr = aov;
        int length = strArr.length;
        int i = 0;
        Map<String, Object> map2 = map;
        while (i < length) {
            String str = strArr[i];
            if (!(map2 instanceof Map)) {
                return null;
            }
            i++;
            map2 = map2.get(str);
        }
        return map2;
    }

    private List<a> J(Map<String, Object> map) {
        ArrayList arrayList = new ArrayList();
        a(map, "", arrayList);
        return arrayList;
    }

    private void K(Map<String, Object> map) {
        synchronized (this.aoy) {
            for (String str : map.keySet()) {
                a(c(str, map.get(str)), this.aoy);
            }
        }
        L(map);
    }

    private void L(Map<String, Object> map) {
        Iterator<b> it = this.aox.keySet().iterator();
        while (it.hasNext()) {
            it.next().D(map);
        }
    }

    private void a(Map<String, Object> map, String str, Collection<a> collection) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String str2 = str + (str.length() == 0 ? "" : ".") + entry.getKey();
            if (entry.getValue() instanceof Map) {
                a((Map) entry.getValue(), str2, collection);
            } else if (!str2.equals("gtm.lifetime")) {
                collection.add(new a(str2, entry.getValue()));
            }
        }
    }

    static Long ct(String str) throws NumberFormatException {
        long j;
        Matcher matcher = aow.matcher(str);
        if (!matcher.matches()) {
            bh.U("unknown _lifetime: " + str);
            return null;
        }
        try {
            j = Long.parseLong(matcher.group(1));
        } catch (NumberFormatException e) {
            bh.W("illegal number in _lifetime value: " + str);
            j = 0;
        }
        if (j <= 0) {
            bh.U("non-positive _lifetime: " + str);
            return null;
        }
        String strGroup = matcher.group(2);
        if (strGroup.length() == 0) {
            return Long.valueOf(j);
        }
        switch (strGroup.charAt(0)) {
            case 'd':
                break;
            case 'h':
                break;
            case KeyInfo.KEYCODE_NEXT /* 109 */:
                break;
            case KeyInfo.KEYCODE_VOLUME_UP /* 115 */:
                break;
            default:
                bh.W("unknown units in _lifetime: " + str);
                break;
        }
        return null;
    }

    public static List<Object> listOf(Object... objects) {
        ArrayList arrayList = new ArrayList();
        for (Object obj : objects) {
            arrayList.add(obj);
        }
        return arrayList;
    }

    public static Map<String, Object> mapOf(Object... objects) {
        if (objects.length % 2 != 0) {
            throw new IllegalArgumentException("expected even number of key-value pairs");
        }
        HashMap map = new HashMap();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= objects.length) {
                return map;
            }
            if (!(objects[i2] instanceof String)) {
                throw new IllegalArgumentException("key is not a string: " + objects[i2]);
            }
            map.put((String) objects[i2], objects[i2 + 1]);
            i = i2 + 2;
        }
    }

    private void oc() {
        this.aoB.a(new c.a() { // from class: com.google.android.gms.tagmanager.DataLayer.2
            @Override // com.google.android.gms.tagmanager.DataLayer.c.a
            public void g(List<a> list) {
                for (a aVar : list) {
                    DataLayer.this.F(DataLayer.this.c(aVar.JH, aVar.wq));
                }
                DataLayer.this.aoC.countDown();
            }
        });
    }

    private void od() {
        int i = 0;
        do {
            int i2 = i;
            Map<String, Object> mapPoll = this.aoA.poll();
            if (mapPoll == null) {
                return;
            }
            K(mapPoll);
            i = i2 + 1;
        } while (i <= 500);
        this.aoA.clear();
        throw new RuntimeException("Seems like an infinite loop of pushing to the data layer");
    }

    void a(b bVar) {
        this.aox.put(bVar, 0);
    }

    void a(Map<String, Object> map, Map<String, Object> map2) {
        for (String str : map.keySet()) {
            Object obj = map.get(str);
            if (obj instanceof List) {
                if (!(map2.get(str) instanceof List)) {
                    map2.put(str, new ArrayList());
                }
                b((List) obj, (List) map2.get(str));
            } else if (obj instanceof Map) {
                if (!(map2.get(str) instanceof Map)) {
                    map2.put(str, new HashMap());
                }
                a((Map<String, Object>) obj, (Map<String, Object>) map2.get(str));
            } else {
                map2.put(str, obj);
            }
        }
    }

    void b(List<Object> list, List<Object> list2) {
        while (list2.size() < list.size()) {
            list2.add(null);
        }
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= list.size()) {
                return;
            }
            Object obj = list.get(i2);
            if (obj instanceof List) {
                if (!(list2.get(i2) instanceof List)) {
                    list2.set(i2, new ArrayList());
                }
                b((List) obj, (List) list2.get(i2));
            } else if (obj instanceof Map) {
                if (!(list2.get(i2) instanceof Map)) {
                    list2.set(i2, new HashMap());
                }
                a((Map<String, Object>) obj, (Map<String, Object>) list2.get(i2));
            } else if (obj != OBJECT_NOT_PRESENT) {
                list2.set(i2, obj);
            }
            i = i2 + 1;
        }
    }

    Map<String, Object> c(String str, Object obj) {
        HashMap map = new HashMap();
        String[] strArrSplit = str.toString().split("\\.");
        int i = 0;
        HashMap map2 = map;
        while (i < strArrSplit.length - 1) {
            HashMap map3 = new HashMap();
            map2.put(strArrSplit[i], map3);
            i++;
            map2 = map3;
        }
        map2.put(strArrSplit[strArrSplit.length - 1], obj);
        return map;
    }

    void cs(String str) throws InterruptedException {
        push(str, null);
        this.aoB.cu(str);
    }

    public Object get(String key) {
        synchronized (this.aoy) {
            Map<String, Object> map = this.aoy;
            String[] strArrSplit = key.split("\\.");
            int length = strArrSplit.length;
            Map<String, Object> map2 = map;
            int i = 0;
            while (i < length) {
                String str = strArrSplit[i];
                if (!(map2 instanceof Map)) {
                    return null;
                }
                Object obj = map2.get(str);
                if (obj == null) {
                    return null;
                }
                i++;
                map2 = obj;
            }
            return map2;
        }
    }

    public void push(String key, Object value) throws InterruptedException {
        push(c(key, value));
    }

    public void push(Map<String, Object> update) throws InterruptedException {
        try {
            this.aoC.await();
        } catch (InterruptedException e) {
            bh.W("DataLayer.push: unexpected InterruptedException");
        }
        F(update);
    }

    public void pushEvent(String eventName, Map<String, Object> update) throws InterruptedException {
        HashMap map = new HashMap(update);
        map.put(EVENT_KEY, eventName);
        push(map);
    }

    public String toString() {
        String string;
        synchronized (this.aoy) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Object> entry : this.aoy.entrySet()) {
                sb.append(String.format("{\n\tKey: %s\n\tValue: %s\n}\n", entry.getKey(), entry.getValue()));
            }
            string = sb.toString();
        }
        return string;
    }
}
