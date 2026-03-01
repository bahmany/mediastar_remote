package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.d;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
class di {
    private static final Object arJ = null;
    private static Long arK = new Long(0);
    private static Double arL = new Double(0.0d);
    private static dh arM = dh.z(0);
    private static String arN = new String("");
    private static Boolean arO = new Boolean(false);
    private static List<Object> arP = new ArrayList(0);
    private static Map<Object, Object> arQ = new HashMap();
    private static d.a arR = u(arN);

    public static d.a cU(String str) {
        d.a aVar = new d.a();
        aVar.type = 5;
        aVar.gA = str;
        return aVar;
    }

    private static dh cV(String str) {
        try {
            return dh.cT(str);
        } catch (NumberFormatException e) {
            bh.T("Failed to convert '" + str + "' to a number.");
            return arM;
        }
    }

    private static Long cW(String str) {
        dh dhVarCV = cV(str);
        return dhVarCV == arM ? arK : Long.valueOf(dhVarCV.longValue());
    }

    private static Double cX(String str) {
        dh dhVarCV = cV(str);
        return dhVarCV == arM ? arL : Double.valueOf(dhVarCV.doubleValue());
    }

    private static Boolean cY(String str) {
        return "true".equalsIgnoreCase(str) ? Boolean.TRUE : "false".equalsIgnoreCase(str) ? Boolean.FALSE : arO;
    }

    private static double getDouble(Object o) {
        if (o instanceof Number) {
            return ((Number) o).doubleValue();
        }
        bh.T("getDouble received non-Number");
        return 0.0d;
    }

    public static String j(d.a aVar) {
        return p(o(aVar));
    }

    public static dh k(d.a aVar) {
        return q(o(aVar));
    }

    public static Long l(d.a aVar) {
        return r(o(aVar));
    }

    public static Double m(d.a aVar) {
        return s(o(aVar));
    }

    public static Boolean n(d.a aVar) {
        return t(o(aVar));
    }

    public static Object o(d.a aVar) {
        int i = 0;
        if (aVar == null) {
            return arJ;
        }
        switch (aVar.type) {
            case 1:
                break;
            case 2:
                ArrayList arrayList = new ArrayList(aVar.gw.length);
                d.a[] aVarArr = aVar.gw;
                int length = aVarArr.length;
                while (i < length) {
                    Object objO = o(aVarArr[i]);
                    if (objO == arJ) {
                        break;
                    } else {
                        arrayList.add(objO);
                        i++;
                    }
                }
                break;
            case 3:
                if (aVar.gx.length == aVar.gy.length) {
                    HashMap map = new HashMap(aVar.gy.length);
                    while (i < aVar.gx.length) {
                        Object objO2 = o(aVar.gx[i]);
                        Object objO3 = o(aVar.gy[i]);
                        if (objO2 == arJ || objO3 == arJ) {
                            break;
                        } else {
                            map.put(objO2, objO3);
                            i++;
                        }
                    }
                    break;
                } else {
                    bh.T("Converting an invalid value to object: " + aVar.toString());
                    break;
                }
                break;
            case 4:
                bh.T("Trying to convert a macro reference to object");
                break;
            case 5:
                bh.T("Trying to convert a function id to object");
                break;
            case 6:
                break;
            case 7:
                StringBuffer stringBuffer = new StringBuffer();
                d.a[] aVarArr2 = aVar.gD;
                int length2 = aVarArr2.length;
                while (i < length2) {
                    String strJ = j(aVarArr2[i]);
                    if (strJ == arN) {
                        break;
                    } else {
                        stringBuffer.append(strJ);
                        i++;
                    }
                }
                break;
            case 8:
                break;
            default:
                bh.T("Failed to convert a value of type: " + aVar.type);
                break;
        }
        return arJ;
    }

    public static String p(Object obj) {
        return obj == null ? arN : obj.toString();
    }

    public static Object pC() {
        return arJ;
    }

    public static Long pD() {
        return arK;
    }

    public static Double pE() {
        return arL;
    }

    public static Boolean pF() {
        return arO;
    }

    public static dh pG() {
        return arM;
    }

    public static String pH() {
        return arN;
    }

    public static d.a pI() {
        return arR;
    }

    public static dh q(Object obj) {
        return obj instanceof dh ? (dh) obj : w(obj) ? dh.z(x(obj)) : v(obj) ? dh.a(Double.valueOf(getDouble(obj))) : cV(p(obj));
    }

    public static Long r(Object obj) {
        return w(obj) ? Long.valueOf(x(obj)) : cW(p(obj));
    }

    public static Double s(Object obj) {
        return v(obj) ? Double.valueOf(getDouble(obj)) : cX(p(obj));
    }

    public static Boolean t(Object obj) {
        return obj instanceof Boolean ? (Boolean) obj : cY(p(obj));
    }

    public static d.a u(Object obj) {
        boolean z = false;
        d.a aVar = new d.a();
        if (obj instanceof d.a) {
            return (d.a) obj;
        }
        if (obj instanceof String) {
            aVar.type = 1;
            aVar.gv = (String) obj;
        } else if (obj instanceof List) {
            aVar.type = 2;
            List list = (List) obj;
            ArrayList arrayList = new ArrayList(list.size());
            Iterator it = list.iterator();
            boolean z2 = false;
            while (it.hasNext()) {
                d.a aVarU = u(it.next());
                if (aVarU == arR) {
                    return arR;
                }
                boolean z3 = z2 || aVarU.gF;
                arrayList.add(aVarU);
                z2 = z3;
            }
            aVar.gw = (d.a[]) arrayList.toArray(new d.a[0]);
            z = z2;
        } else if (obj instanceof Map) {
            aVar.type = 3;
            Set<Map.Entry> setEntrySet = ((Map) obj).entrySet();
            ArrayList arrayList2 = new ArrayList(setEntrySet.size());
            ArrayList arrayList3 = new ArrayList(setEntrySet.size());
            boolean z4 = false;
            for (Map.Entry entry : setEntrySet) {
                d.a aVarU2 = u(entry.getKey());
                d.a aVarU3 = u(entry.getValue());
                if (aVarU2 == arR || aVarU3 == arR) {
                    return arR;
                }
                boolean z5 = z4 || aVarU2.gF || aVarU3.gF;
                arrayList2.add(aVarU2);
                arrayList3.add(aVarU3);
                z4 = z5;
            }
            aVar.gx = (d.a[]) arrayList2.toArray(new d.a[0]);
            aVar.gy = (d.a[]) arrayList3.toArray(new d.a[0]);
            z = z4;
        } else if (v(obj)) {
            aVar.type = 1;
            aVar.gv = obj.toString();
        } else if (w(obj)) {
            aVar.type = 6;
            aVar.gB = x(obj);
        } else {
            if (!(obj instanceof Boolean)) {
                bh.T("Converting to Value from unknown object type: " + (obj == null ? "null" : obj.getClass().toString()));
                return arR;
            }
            aVar.type = 8;
            aVar.gC = ((Boolean) obj).booleanValue();
        }
        aVar.gF = z;
        return aVar;
    }

    private static boolean v(Object obj) {
        return (obj instanceof Double) || (obj instanceof Float) || ((obj instanceof dh) && ((dh) obj).px());
    }

    private static boolean w(Object obj) {
        return (obj instanceof Byte) || (obj instanceof Short) || (obj instanceof Integer) || (obj instanceof Long) || ((obj instanceof dh) && ((dh) obj).py());
    }

    private static long x(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        bh.T("getInt64 received non-Number");
        return 0L;
    }
}
