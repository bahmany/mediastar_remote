package com.google.android.gms.internal;

import com.google.android.gms.internal.pg;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ph<M extends pg<M>, T> {
    protected final boolean awA;
    protected final Class<T> awz;
    protected final int tag;
    protected final int type;

    private ph(int i, Class<T> cls, int i2, boolean z) {
        this.type = i;
        this.awz = cls;
        this.tag = i2;
        this.awA = z;
    }

    public static <M extends pg<M>, T extends pm> ph<M, T> a(int i, Class<T> cls, int i2) {
        return new ph<>(i, cls, i2, false);
    }

    private T m(List<po> list) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            po poVar = list.get(i);
            if (poVar.awK.length != 0) {
                a(poVar, arrayList);
            }
        }
        int size = arrayList.size();
        if (size == 0) {
            return null;
        }
        T tCast = this.awz.cast(Array.newInstance(this.awz.getComponentType(), size));
        for (int i2 = 0; i2 < size; i2++) {
            Array.set(tCast, i2, arrayList.get(i2));
        }
        return tCast;
    }

    private T n(List<po> list) {
        if (list.isEmpty()) {
            return null;
        }
        return this.awz.cast(u(pe.p(list.get(list.size() - 1).awK)));
    }

    int A(Object obj) {
        return this.awA ? B(obj) : C(obj);
    }

    protected int B(Object obj) {
        int iC = 0;
        int length = Array.getLength(obj);
        for (int i = 0; i < length; i++) {
            if (Array.get(obj, i) != null) {
                iC += C(Array.get(obj, i));
            }
        }
        return iC;
    }

    protected int C(Object obj) {
        int iGH = pp.gH(this.tag);
        switch (this.type) {
            case 10:
                return pf.b(iGH, (pm) obj);
            case 11:
                return pf.c(iGH, (pm) obj);
            default:
                throw new IllegalArgumentException("Unknown type " + this.type);
        }
    }

    protected void a(po poVar, List<Object> list) {
        list.add(u(pe.p(poVar.awK)));
    }

    void a(Object obj, pf pfVar) throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
        if (this.awA) {
            c(obj, pfVar);
        } else {
            b(obj, pfVar);
        }
    }

    protected void b(Object obj, pf pfVar) {
        try {
            pfVar.gz(this.tag);
            switch (this.type) {
                case 10:
                    int iGH = pp.gH(this.tag);
                    pfVar.b((pm) obj);
                    pfVar.w(iGH, 4);
                    return;
                case 11:
                    pfVar.c((pm) obj);
                    return;
                default:
                    throw new IllegalArgumentException("Unknown type " + this.type);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    protected void c(Object obj, pf pfVar) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
        int length = Array.getLength(obj);
        for (int i = 0; i < length; i++) {
            Object obj2 = Array.get(obj, i);
            if (obj2 != null) {
                b(obj2, pfVar);
            }
        }
    }

    final T l(List<po> list) {
        if (list == null) {
            return null;
        }
        return this.awA ? m(list) : n(list);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected Object u(pe peVar) {
        Class componentType = this.awA ? this.awz.getComponentType() : this.awz;
        try {
            switch (this.type) {
                case 10:
                    pm pmVar = (pm) componentType.newInstance();
                    peVar.a(pmVar, pp.gH(this.tag));
                    return pmVar;
                case 11:
                    pm pmVar2 = (pm) componentType.newInstance();
                    peVar.a(pmVar2);
                    return pmVar2;
                default:
                    throw new IllegalArgumentException("Unknown type " + this.type);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading extension field", e);
        } catch (IllegalAccessException e2) {
            throw new IllegalArgumentException("Error creating instance of class " + componentType, e2);
        } catch (InstantiationException e3) {
            throw new IllegalArgumentException("Error creating instance of class " + componentType, e3);
        }
    }
}
