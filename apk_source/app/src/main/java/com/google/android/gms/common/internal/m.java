package com.google.android.gms.common.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public final class m {

    public static final class a {
        private final List<String> LY;
        private final Object LZ;

        private a(Object obj) {
            this.LZ = n.i(obj);
            this.LY = new ArrayList();
        }

        public a a(String str, Object obj) {
            this.LY.add(((String) n.i(str)) + "=" + String.valueOf(obj));
            return this;
        }

        public String toString() {
            StringBuilder sbAppend = new StringBuilder(100).append(this.LZ.getClass().getSimpleName()).append('{');
            int size = this.LY.size();
            for (int i = 0; i < size; i++) {
                sbAppend.append(this.LY.get(i));
                if (i < size - 1) {
                    sbAppend.append(", ");
                }
            }
            return sbAppend.append('}').toString();
        }
    }

    public static boolean equal(Object a2, Object b) {
        return a2 == b || (a2 != null && a2.equals(b));
    }

    public static a h(Object obj) {
        return new a(obj);
    }

    public static int hashCode(Object... objects) {
        return Arrays.hashCode(objects);
    }
}
