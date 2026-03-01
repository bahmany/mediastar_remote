package com.google.android.gms.tagmanager;

import android.content.Context;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.google.android.gms.analytics.ecommerce.Promotion;
import com.google.android.gms.internal.d;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
class dj extends dg {
    private static final String ID = com.google.android.gms.internal.a.UNIVERSAL_ANALYTICS.toString();
    private static final String arS = com.google.android.gms.internal.b.ACCOUNT.toString();
    private static final String arT = com.google.android.gms.internal.b.ANALYTICS_PASS_THROUGH.toString();
    private static final String arU = com.google.android.gms.internal.b.ENABLE_ECOMMERCE.toString();
    private static final String arV = com.google.android.gms.internal.b.ECOMMERCE_USE_DATA_LAYER.toString();
    private static final String arW = com.google.android.gms.internal.b.ECOMMERCE_MACRO_DATA.toString();
    private static final String arX = com.google.android.gms.internal.b.ANALYTICS_FIELDS.toString();
    private static final String arY = com.google.android.gms.internal.b.TRACK_TRANSACTION.toString();
    private static final String arZ = com.google.android.gms.internal.b.TRANSACTION_DATALAYER_MAP.toString();
    private static final String asa = com.google.android.gms.internal.b.TRANSACTION_ITEM_DATALAYER_MAP.toString();
    private static final List<String> asb = Arrays.asList("detail", ProductAction.ACTION_CHECKOUT, ProductAction.ACTION_CHECKOUT_OPTION, "click", ProductAction.ACTION_ADD, ProductAction.ACTION_REMOVE, ProductAction.ACTION_PURCHASE, ProductAction.ACTION_REFUND);
    private static Map<String, String> asc;
    private static Map<String, String> asd;
    private final DataLayer anS;
    private final Set<String> ase;
    private final df asf;

    public dj(Context context, DataLayer dataLayer) {
        this(context, dataLayer, new df(context));
    }

    dj(Context context, DataLayer dataLayer, df dfVar) {
        super(ID, new String[0]);
        this.anS = dataLayer;
        this.asf = dfVar;
        this.ase = new HashSet();
        this.ase.add("");
        this.ase.add("0");
        this.ase.add("false");
    }

    private Promotion M(Map<String, String> map) {
        Promotion promotion = new Promotion();
        String str = map.get("id");
        if (str != null) {
            promotion.setId(String.valueOf(str));
        }
        String str2 = map.get("name");
        if (str2 != null) {
            promotion.setName(String.valueOf(str2));
        }
        String str3 = map.get("creative");
        if (str3 != null) {
            promotion.setCreative(String.valueOf(str3));
        }
        String str4 = map.get("position");
        if (str4 != null) {
            promotion.setPosition(String.valueOf(str4));
        }
        return promotion;
    }

    private Product N(Map<String, Object> map) {
        Product product = new Product();
        Object obj = map.get("id");
        if (obj != null) {
            product.setId(String.valueOf(obj));
        }
        Object obj2 = map.get("name");
        if (obj2 != null) {
            product.setName(String.valueOf(obj2));
        }
        Object obj3 = map.get("brand");
        if (obj3 != null) {
            product.setBrand(String.valueOf(obj3));
        }
        Object obj4 = map.get("category");
        if (obj4 != null) {
            product.setCategory(String.valueOf(obj4));
        }
        Object obj5 = map.get("variant");
        if (obj5 != null) {
            product.setVariant(String.valueOf(obj5));
        }
        Object obj6 = map.get("coupon");
        if (obj6 != null) {
            product.setCouponCode(String.valueOf(obj6));
        }
        Object obj7 = map.get("position");
        if (obj7 != null) {
            product.setPosition(z(obj7).intValue());
        }
        Object obj8 = map.get("price");
        if (obj8 != null) {
            product.setPrice(y(obj8).doubleValue());
        }
        Object obj9 = map.get("quantity");
        if (obj9 != null) {
            product.setQuantity(z(obj9).intValue());
        }
        return product;
    }

    private Map<String, String> O(Map<String, d.a> map) {
        d.a aVar = map.get(arZ);
        if (aVar != null) {
            return c(aVar);
        }
        if (asc == null) {
            HashMap map2 = new HashMap();
            map2.put("transactionId", "&ti");
            map2.put("transactionAffiliation", "&ta");
            map2.put("transactionTax", "&tt");
            map2.put("transactionShipping", "&ts");
            map2.put("transactionTotal", "&tr");
            map2.put("transactionCurrency", "&cu");
            asc = map2;
        }
        return asc;
    }

    private Map<String, String> P(Map<String, d.a> map) {
        d.a aVar = map.get(asa);
        if (aVar != null) {
            return c(aVar);
        }
        if (asd == null) {
            HashMap map2 = new HashMap();
            map2.put("name", "&in");
            map2.put("sku", "&ic");
            map2.put("category", "&iv");
            map2.put("price", "&ip");
            map2.put("quantity", "&iq");
            map2.put("currency", "&cu");
            asd = map2;
        }
        return asd;
    }

    private void a(Tracker tracker, Map<String, d.a> map) {
        String strCZ = cZ("transactionId");
        if (strCZ == null) {
            bh.T("Cannot find transactionId in data layer.");
            return;
        }
        LinkedList linkedList = new LinkedList();
        try {
            Map<String, String> mapP = p(map.get(arX));
            mapP.put("&t", "transaction");
            for (Map.Entry<String, String> entry : O(map).entrySet()) {
                b(mapP, entry.getValue(), cZ(entry.getKey()));
            }
            linkedList.add(mapP);
            List<Map<String, String>> listDa = da("transactionProducts");
            if (listDa != null) {
                for (Map<String, String> map2 : listDa) {
                    if (map2.get("name") == null) {
                        bh.T("Unable to send transaction item hit due to missing 'name' field.");
                        return;
                    }
                    Map<String, String> mapP2 = p(map.get(arX));
                    mapP2.put("&t", "item");
                    mapP2.put("&ti", strCZ);
                    for (Map.Entry<String, String> entry2 : P(map).entrySet()) {
                        b(mapP2, entry2.getValue(), map2.get(entry2.getKey()));
                    }
                    linkedList.add(mapP2);
                }
            }
            Iterator it = linkedList.iterator();
            while (it.hasNext()) {
                tracker.send((Map) it.next());
            }
        } catch (IllegalArgumentException e) {
            bh.b("Unable to send transaction", e);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:47:0x011d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void b(com.google.android.gms.analytics.Tracker r8, java.util.Map<java.lang.String, com.google.android.gms.internal.d.a> r9) {
        /*
            Method dump skipped, instructions count: 459
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.tagmanager.dj.b(com.google.android.gms.analytics.Tracker, java.util.Map):void");
    }

    private void b(Map<String, String> map, String str, String str2) {
        if (str2 != null) {
            map.put(str, str2);
        }
    }

    private ProductAction c(String str, Map<String, Object> map) {
        ProductAction productAction = new ProductAction(str);
        Object obj = map.get("id");
        if (obj != null) {
            productAction.setTransactionId(String.valueOf(obj));
        }
        Object obj2 = map.get("affiliation");
        if (obj2 != null) {
            productAction.setTransactionAffiliation(String.valueOf(obj2));
        }
        Object obj3 = map.get("coupon");
        if (obj3 != null) {
            productAction.setTransactionCouponCode(String.valueOf(obj3));
        }
        Object obj4 = map.get("list");
        if (obj4 != null) {
            productAction.setProductActionList(String.valueOf(obj4));
        }
        Object obj5 = map.get("option");
        if (obj5 != null) {
            productAction.setCheckoutOptions(String.valueOf(obj5));
        }
        Object obj6 = map.get("revenue");
        if (obj6 != null) {
            productAction.setTransactionRevenue(y(obj6).doubleValue());
        }
        Object obj7 = map.get("tax");
        if (obj7 != null) {
            productAction.setTransactionTax(y(obj7).doubleValue());
        }
        Object obj8 = map.get("shipping");
        if (obj8 != null) {
            productAction.setTransactionShipping(y(obj8).doubleValue());
        }
        Object obj9 = map.get("step");
        if (obj9 != null) {
            productAction.setCheckoutStep(z(obj9).intValue());
        }
        return productAction;
    }

    private Map<String, String> c(d.a aVar) {
        Object objO = di.o(aVar);
        if (!(objO instanceof Map)) {
            return null;
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (Map.Entry entry : ((Map) objO).entrySet()) {
            linkedHashMap.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return linkedHashMap;
    }

    private String cZ(String str) {
        Object obj = this.anS.get(str);
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    private List<Map<String, String>> da(String str) {
        Object obj = this.anS.get(str);
        if (obj == null) {
            return null;
        }
        if (!(obj instanceof List)) {
            throw new IllegalArgumentException("transactionProducts should be of type List.");
        }
        Iterator it = ((List) obj).iterator();
        while (it.hasNext()) {
            if (!(it.next() instanceof Map)) {
                throw new IllegalArgumentException("Each element of transactionProducts should be of type Map.");
            }
        }
        return (List) obj;
    }

    private boolean f(Map<String, d.a> map, String str) {
        d.a aVar = map.get(str);
        if (aVar == null) {
            return false;
        }
        return di.n(aVar).booleanValue();
    }

    private Map<String, String> p(d.a aVar) {
        Map<String, String> mapC;
        if (aVar != null && (mapC = c(aVar)) != null) {
            String str = mapC.get("&aip");
            if (str != null && this.ase.contains(str.toLowerCase())) {
                mapC.remove("&aip");
            }
            return mapC;
        }
        return new HashMap();
    }

    private Double y(Object obj) {
        if (obj instanceof String) {
            try {
                return Double.valueOf((String) obj);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Cannot convert the object to Double: " + e.getMessage());
            }
        }
        if (obj instanceof Integer) {
            return Double.valueOf(((Integer) obj).doubleValue());
        }
        if (obj instanceof Double) {
            return (Double) obj;
        }
        throw new RuntimeException("Cannot convert the object to Double: " + obj.toString());
    }

    private Integer z(Object obj) {
        if (obj instanceof String) {
            try {
                return Integer.valueOf((String) obj);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Cannot convert the object to Integer: " + e.getMessage());
            }
        }
        if (obj instanceof Double) {
            return Integer.valueOf(((Double) obj).intValue());
        }
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        throw new RuntimeException("Cannot convert the object to Integer: " + obj.toString());
    }

    @Override // com.google.android.gms.tagmanager.dg
    public void E(Map<String, d.a> map) {
        Tracker trackerCR = this.asf.cR("_GTM_DEFAULT_TRACKER_");
        if (f(map, arU)) {
            b(trackerCR, map);
            return;
        }
        if (f(map, arT)) {
            trackerCR.send(p(map.get(arX)));
        } else if (f(map, arY)) {
            a(trackerCR, map);
        } else {
            bh.W("Ignoring unknown tag.");
        }
    }
}
