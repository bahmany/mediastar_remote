package com.google.android.gms.internal;

import com.google.android.gms.internal.pc;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public final class pb {

    public static class a {
        public final pc avQ;
        public final List<Asset> avR;

        public a(pc pcVar, List<Asset> list) {
            this.avQ = pcVar;
            this.avR = list;
        }
    }

    private static int a(String str, pc.a.C0092a[] c0092aArr) {
        int i = 14;
        for (pc.a.C0092a c0092a : c0092aArr) {
            if (i != 14) {
                if (c0092a.type != i) {
                    throw new IllegalArgumentException("The ArrayList elements should all be the same type, but ArrayList with key " + str + " contains items of type " + i + " and " + c0092a.type);
                }
            } else if (c0092a.type == 9 || c0092a.type == 2 || c0092a.type == 6) {
                i = c0092a.type;
            } else if (c0092a.type != 14) {
                throw new IllegalArgumentException("Unexpected TypedValue type: " + c0092a.type + " for key " + str);
            }
        }
        return i;
    }

    static int a(List<Asset> list, Asset asset) {
        list.add(asset);
        return list.size() - 1;
    }

    public static a a(DataMap dataMap) {
        pc pcVar = new pc();
        ArrayList arrayList = new ArrayList();
        pcVar.avS = a(dataMap, arrayList);
        return new a(pcVar, arrayList);
    }

    private static pc.a.C0092a a(List<Asset> list, Object obj) {
        int i;
        int i2 = 0;
        pc.a.C0092a c0092a = new pc.a.C0092a();
        if (obj == null) {
            c0092a.type = 14;
            return c0092a;
        }
        c0092a.avW = new pc.a.C0092a.C0093a();
        if (obj instanceof String) {
            c0092a.type = 2;
            c0092a.avW.avY = (String) obj;
        } else if (obj instanceof Integer) {
            c0092a.type = 6;
            c0092a.avW.awc = ((Integer) obj).intValue();
        } else if (obj instanceof Long) {
            c0092a.type = 5;
            c0092a.avW.awb = ((Long) obj).longValue();
        } else if (obj instanceof Double) {
            c0092a.type = 3;
            c0092a.avW.avZ = ((Double) obj).doubleValue();
        } else if (obj instanceof Float) {
            c0092a.type = 4;
            c0092a.avW.awa = ((Float) obj).floatValue();
        } else if (obj instanceof Boolean) {
            c0092a.type = 8;
            c0092a.avW.awe = ((Boolean) obj).booleanValue();
        } else if (obj instanceof Byte) {
            c0092a.type = 7;
            c0092a.avW.awd = ((Byte) obj).byteValue();
        } else if (obj instanceof byte[]) {
            c0092a.type = 1;
            c0092a.avW.avX = (byte[]) obj;
        } else if (obj instanceof String[]) {
            c0092a.type = 11;
            c0092a.avW.awh = (String[]) obj;
        } else if (obj instanceof long[]) {
            c0092a.type = 12;
            c0092a.avW.awi = (long[]) obj;
        } else if (obj instanceof float[]) {
            c0092a.type = 15;
            c0092a.avW.awj = (float[]) obj;
        } else if (obj instanceof Asset) {
            c0092a.type = 13;
            c0092a.avW.awk = a(list, (Asset) obj);
        } else if (obj instanceof DataMap) {
            c0092a.type = 9;
            DataMap dataMap = (DataMap) obj;
            Set<String> setKeySet = dataMap.keySet();
            pc.a[] aVarArr = new pc.a[setKeySet.size()];
            Iterator<String> it = setKeySet.iterator();
            while (true) {
                int i3 = i2;
                if (!it.hasNext()) {
                    break;
                }
                String next = it.next();
                aVarArr[i3] = new pc.a();
                aVarArr[i3].name = next;
                aVarArr[i3].avU = a(list, dataMap.get(next));
                i2 = i3 + 1;
            }
            c0092a.avW.awf = aVarArr;
        } else {
            if (!(obj instanceof ArrayList)) {
                throw new RuntimeException("newFieldValueFromValue: unexpected value " + obj.getClass().getSimpleName());
            }
            c0092a.type = 10;
            ArrayList arrayList = (ArrayList) obj;
            pc.a.C0092a[] c0092aArr = new pc.a.C0092a[arrayList.size()];
            Object obj2 = null;
            int size = arrayList.size();
            int i4 = 0;
            int i5 = 14;
            while (i4 < size) {
                Object obj3 = arrayList.get(i4);
                pc.a.C0092a c0092aA = a(list, obj3);
                if (c0092aA.type != 14 && c0092aA.type != 2 && c0092aA.type != 6 && c0092aA.type != 9) {
                    throw new IllegalArgumentException("The only ArrayList element types supported by DataBundleUtil are String, Integer, Bundle, and null, but this ArrayList contains a " + obj3.getClass());
                }
                if (i5 == 14 && c0092aA.type != 14) {
                    i = c0092aA.type;
                } else {
                    if (c0092aA.type != i5) {
                        throw new IllegalArgumentException("ArrayList elements must all be of the sameclass, but this one contains a " + obj2.getClass() + " and a " + obj3.getClass());
                    }
                    obj3 = obj2;
                    i = i5;
                }
                c0092aArr[i4] = c0092aA;
                i4++;
                i5 = i;
                obj2 = obj3;
            }
            c0092a.avW.awg = c0092aArr;
        }
        return c0092a;
    }

    public static DataMap a(a aVar) {
        DataMap dataMap = new DataMap();
        for (pc.a aVar2 : aVar.avQ.avS) {
            a(aVar.avR, dataMap, aVar2.name, aVar2.avU);
        }
        return dataMap;
    }

    private static ArrayList a(List<Asset> list, pc.a.C0092a.C0093a c0093a, int i) {
        ArrayList arrayList = new ArrayList(c0093a.awg.length);
        for (pc.a.C0092a c0092a : c0093a.awg) {
            if (c0092a.type == 14) {
                arrayList.add(null);
            } else if (i == 9) {
                DataMap dataMap = new DataMap();
                pc.a[] aVarArr = c0092a.avW.awf;
                for (pc.a aVar : aVarArr) {
                    a(list, dataMap, aVar.name, aVar.avU);
                }
                arrayList.add(dataMap);
            } else if (i == 2) {
                arrayList.add(c0092a.avW.avY);
            } else {
                if (i != 6) {
                    throw new IllegalArgumentException("Unexpected typeOfArrayList: " + i);
                }
                arrayList.add(Integer.valueOf(c0092a.avW.awc));
            }
        }
        return arrayList;
    }

    private static void a(List<Asset> list, DataMap dataMap, String str, pc.a.C0092a c0092a) {
        int i = c0092a.type;
        if (i == 14) {
            dataMap.putString(str, null);
            return;
        }
        pc.a.C0092a.C0093a c0093a = c0092a.avW;
        if (i == 1) {
            dataMap.putByteArray(str, c0093a.avX);
            return;
        }
        if (i == 11) {
            dataMap.putStringArray(str, c0093a.awh);
            return;
        }
        if (i == 12) {
            dataMap.putLongArray(str, c0093a.awi);
            return;
        }
        if (i == 15) {
            dataMap.putFloatArray(str, c0093a.awj);
            return;
        }
        if (i == 2) {
            dataMap.putString(str, c0093a.avY);
            return;
        }
        if (i == 3) {
            dataMap.putDouble(str, c0093a.avZ);
            return;
        }
        if (i == 4) {
            dataMap.putFloat(str, c0093a.awa);
            return;
        }
        if (i == 5) {
            dataMap.putLong(str, c0093a.awb);
            return;
        }
        if (i == 6) {
            dataMap.putInt(str, c0093a.awc);
            return;
        }
        if (i == 7) {
            dataMap.putByte(str, (byte) c0093a.awd);
            return;
        }
        if (i == 8) {
            dataMap.putBoolean(str, c0093a.awe);
            return;
        }
        if (i == 13) {
            if (list == null) {
                throw new RuntimeException("populateBundle: unexpected type for: " + str);
            }
            dataMap.putAsset(str, list.get((int) c0093a.awk));
            return;
        }
        if (i == 9) {
            DataMap dataMap2 = new DataMap();
            for (pc.a aVar : c0093a.awf) {
                a(list, dataMap2, aVar.name, aVar.avU);
            }
            dataMap.putDataMap(str, dataMap2);
            return;
        }
        if (i != 10) {
            throw new RuntimeException("populateBundle: unexpected type " + i);
        }
        int iA = a(str, c0093a.awg);
        ArrayList<Integer> arrayListA = a(list, c0093a, iA);
        if (iA == 14) {
            dataMap.putStringArrayList(str, arrayListA);
            return;
        }
        if (iA == 9) {
            dataMap.putDataMapArrayList(str, arrayListA);
        } else if (iA == 2) {
            dataMap.putStringArrayList(str, arrayListA);
        } else {
            if (iA != 6) {
                throw new IllegalStateException("Unexpected typeOfArrayList: " + iA);
            }
            dataMap.putIntegerArrayList(str, arrayListA);
        }
    }

    private static pc.a[] a(DataMap dataMap, List<Asset> list) {
        Set<String> setKeySet = dataMap.keySet();
        pc.a[] aVarArr = new pc.a[setKeySet.size()];
        int i = 0;
        Iterator<String> it = setKeySet.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return aVarArr;
            }
            String next = it.next();
            Object obj = dataMap.get(next);
            aVarArr[i2] = new pc.a();
            aVarArr[i2].name = next;
            aVarArr[i2].avU = a(list, obj);
            i = i2 + 1;
        }
    }
}
